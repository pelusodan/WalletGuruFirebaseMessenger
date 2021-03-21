package com.peluso.walletguru_firebase_messenger.firebase;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.peluso.walletguru_firebase_messenger.model.ChatUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Function;

public class FirebaseMessengerInstance {

    private final static String TAG = FirebaseMessengerInstance.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAApDJ3YPQ:APA91bFT2iWlQYtj6H5T5Li_ArTCwaotJDMD8wk99Np79OJkXyUAxizH9_t6ixTDDPEBbzWq98XsbJ-1lnRRZ-tpW60twdTsBRrht99uKIsKFkQOCF5NRzV0ogKhoI8AL53veMcfU8GV";
    private String clientId;
    private ChatUser recipient;

    public FirebaseMessengerInstance(String clientId) {
        this.clientId = clientId;
    }


    /**
     * Setting the recipient of the messages - REQUIRED before
     *
     * @param chatUser the user you'd like to send a message to
     */
    public void setRecipient(ChatUser chatUser) {
        recipient = chatUser;
    }

    /**
     * Send a message to the recipient as defined 'setRecipent()' function
     *
     * @param message            the text to send to the recipient
     * @param incrementSentCount a higher-order function to notify the activity if the message was successful
     */
    public void sendMessage(String message, Function<Boolean, Void> incrementSentCount) {
        if (recipient != null) {
            try {
                new Thread(() -> sendMessageToDevice(recipient.clientId, message, incrementSentCount)).start();

            } catch (Exception e) {
                Log.e(TAG, e.getStackTrace().toString());
            }
        } else {
            Log.e(TAG, "Cannot send a message before declaring a recipient");
        }
    }

    /**
     * Copied from the Database Demo, with some slight changes
     *
     * @param clientId           client to send to
     * @param message            messsage to send
     * @param incrementSentCount higher-order function to let calling Activity know the sending was successful/failed
     */
    private void sendMessageToDevice(String clientId, String message, Function<Boolean, Void> incrementSentCount) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "New Message");
            jNotification.put("body", message);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            jdata.put("title", "data title");
            jdata.put("content", "data content");

            jPayload.put("to", clientId);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            // Notify the caller that they should increment the 'sent' count
            incrementSentCount.apply(true);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(() -> {
                Log.e(TAG, "run: " + resp);
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            // Notify the caller that we should not increment the 'sent' count
            incrementSentCount.apply(false);
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
