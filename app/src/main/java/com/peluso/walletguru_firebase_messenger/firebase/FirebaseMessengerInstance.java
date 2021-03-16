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

public class FirebaseMessengerInstance {

    private final static String TAG = FirebaseMessengerInstance.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAApDJ3YPQ:APA91bFT2iWlQYtj6H5T5Li_ArTCwaotJDMD8wk99Np79OJkXyUAxizH9_t6ixTDDPEBbzWq98XsbJ-1lnRRZ-tpW60twdTsBRrht99uKIsKFkQOCF5NRzV0ogKhoI8AL53veMcfU8GV";
    private String clientId;
    private ChatUser recipient;

    public FirebaseMessengerInstance(String clientId) {
        this.clientId = clientId;
    }


    public void setRecipient(ChatUser chatUser) {
        recipient = chatUser;
    }

    public void sendMessage(String message) {
        if (recipient != null) {
            try {
                // TODO: attempt to send the message to the recipient field
                new Thread(() -> sendMessageToDevice(recipient.clientId, message)).start();
            } catch (Exception e) {
                Log.e(TAG, e.getStackTrace().toString());
            }
        }
    }

    private void sendMessageToDevice(String clientId, String message) {
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

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            // If sending to a single client
            jPayload.put("to", clientId);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

            /***
             * The Payload object is now populated.
             * Send it to Firebase to send the message to the appropriate recipient.
             */
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

            Handler h = new Handler(Looper.getMainLooper());
            h.post(() -> {
                Log.e(TAG, "run: " + resp);
                //Toast.makeText(FCMActivity.this, resp, Toast.LENGTH_LONG).show();
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
