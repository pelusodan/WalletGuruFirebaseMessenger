package com.peluso.walletguru_firebase_messenger.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.peluso.walletguru_firebase_messenger.R;
import com.peluso.walletguru_firebase_messenger.firebase.FirebaseMessengerInstance;
import com.peluso.walletguru_firebase_messenger.firebase.FirebaseRealtimeDatabaseInstance;
import com.peluso.walletguru_firebase_messenger.model.ChatUser;

import java.util.concurrent.CountDownLatch;

import static com.peluso.walletguru_firebase_messenger.view.LoginActivityKt.UNAME_KEY;

public class MainActivity extends AppCompatActivity {

    private EditText username_input;
    private Button username_check_button;
    private final static String TAG = MainActivity.class.getSimpleName();
    private FirebaseRealtimeDatabaseInstance firebase;
    private FirebaseMessengerInstance firebaseMessenger;
    private String myUsername;
    private TextView sentHeartCount;
    private TextView sentHugCount;
    private TextView sentAngryCount;
    private TextView receivedStickers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // here we grab the declared username from the intent and put the intent in our database
        myUsername = getIntent().getStringExtra(UNAME_KEY);

        // Test code to make a username in the Firebase database
        firebase = new FirebaseRealtimeDatabaseInstance(this, myUsername, s -> {
            createFirebaseMessagingInstance(s);
            return null;
        });
        // this call should update the database with the correct username and client id
        firebase.getToken();
        getHistory();
        initViews();
    }

    // once we have the user's client ID, we can init the messaging service
    private void createFirebaseMessagingInstance(String clientId) {
        firebaseMessenger = new FirebaseMessengerInstance(clientId);
    }

    private void getHistory() {
        sentHeartCount = findViewById(R.id.sent_heart_count);
        sentHugCount = findViewById(R.id.sent_hug_count);
        sentAngryCount = findViewById(R.id.sent_angry_count);
        receivedStickers = findViewById(R.id.received_stickers);

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

    private void initViews() {
        username_input = findViewById(R.id.username_input);
        username_check_button = findViewById(R.id.submit_check_button);
        username_check_button.setOnClickListener(v -> firebase.doesUserExist(username_input.getText().toString(), chatUser -> {
            /***
             *  Currently, this function is set up so when you check it sets the recepient
             *  as the user you'd like to send to. We should probably change the UI so
             *  we tell the user they need to first confirm before sending a message
             *
             *  or we could make this so that when you click 'send message' it automatically
             *  verifies the other user and skips this step
             */
            if (chatUser != null) {
                // user exists
                Log.e(TAG, "User exists");
                Toast.makeText(getApplicationContext(), chatUser.username + " exists!", Toast.LENGTH_LONG).show();
                setRecipient(chatUser);
            } else {
                // user does not exist
                Log.e(TAG, "User doesn't exist");
                Toast.makeText(getApplicationContext(), username_input.getText().toString() + " does not exist!", Toast.LENGTH_LONG).show();
            }
            return null;
        }));

        ImageButton love_button = findViewById(R.id.heartButton);
        love_button.setOnClickListener(v -> {
            clickedEmoji("\u2764\ufe0f");
        });

        ImageButton hugs_button = findViewById(R.id.hugButton);
        hugs_button.setOnClickListener(v -> {
            clickedEmoji("\ud83e\udd17");
        });

        ImageButton angry_button = findViewById(R.id.angryButton);
        angry_button.setOnClickListener(v -> {
            clickedEmoji("\ud83d\ude21");
        });
    }

    private void setRecipient(ChatUser chatUser) {
        // this function sets our Firebase instance to be sending messages to THIS user
        firebaseMessenger.setRecipient(chatUser);
    }

    private void clickedEmoji(String emojiClicked) {
        // "message EMO  \uD83E\uDD2A\uD83D\uDE18\uD83D\uDE0D\uD83D\uDE02\uD83D\uDE01\uD83E\uDD17 "

        firebaseMessenger.sendMessage(emojiClicked, aBoolean -> {
                // we check if the handler was able to send the message successfully, and increment if so
                // update received user's "received" history
                if (aBoolean) {
                    firebase.addEmojiReceived(username_input.getText().toString(), emojiClicked);
                    Log.e("message EMO ", emojiClicked);
                    firebase.incrementEmojiSentCount(emojiClicked);
                }
                return null;
            });
    }

}