package com.peluso.walletguru_firebase_messenger.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.peluso.walletguru_firebase_messenger.R;
import com.peluso.walletguru_firebase_messenger.firebase.FirebaseMessengerInstance;
import com.peluso.walletguru_firebase_messenger.firebase.FirebaseRealtimeDatabaseInstance;

public class HistoryActivity  extends AppCompatActivity {

    private FirebaseRealtimeDatabaseInstance firebase;
    private String myUsername;
    private TextView sentHeartCount;
    private TextView sentHugCount;
    private TextView sentAngryCount;
    private TextView receivedStickers;
    private FirebaseMessengerInstance firebaseMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // here we grab the declared username from the intent and put the intent in our database
        myUsername = getIntent().getExtras().getString("UNAME_KEY");

        // Test code to make a username in the Firebase database
        firebase = new FirebaseRealtimeDatabaseInstance(this, myUsername, s -> {
            createFirebaseMessagingInstance(s);
            return null;
        });

        // this call should update the database with the correct username and client id
        firebase.getToken();

        sentHeartCount = findViewById(R.id.sent_heart_count);
        sentHugCount = findViewById(R.id.sent_hug_count);
        sentAngryCount = findViewById(R.id.sent_angry_count);
        receivedStickers = findViewById(R.id.received_stickers);
        receivedStickers.setMovementMethod(new ScrollingMovementMethod());

        getHistory();
    }

    // once we have the user's client ID, we can init the messaging service
    private void createFirebaseMessagingInstance(String clientId) {
        firebaseMessenger = new FirebaseMessengerInstance(clientId);
    }

    private void getHistory() {
        firebase.doesUserExist(myUsername, chatUser -> {
            if (chatUser != null) {
                sentHeartCount.setText(String.format(getResources().getString(R.string.sent_count), chatUser.stickerSentCountHeart));
                sentHugCount.setText(String.format(getResources().getString(R.string.sent_count), chatUser.stickerSentCountHugs));
                sentAngryCount.setText(String.format(getResources().getString(R.string.sent_count), chatUser.stickerSentCountAngry));
                receivedStickers.setText(chatUser.received);
            }
            return null;
        });
    }
}
