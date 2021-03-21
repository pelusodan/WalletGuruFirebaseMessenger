package com.peluso.walletguru_firebase_messenger.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.peluso.walletguru_firebase_messenger.R;
import com.peluso.walletguru_firebase_messenger.firebase.FirebaseMessengerInstance;
import com.peluso.walletguru_firebase_messenger.firebase.FirebaseRealtimeDatabaseInstance;
import com.peluso.walletguru_firebase_messenger.model.ChatUser;

import java.util.function.Function;

import static com.peluso.walletguru_firebase_messenger.view.LoginActivityKt.UNAME_KEY;

public class MainActivity extends AppCompatActivity {

    private EditText username_input;
    private Button username_check_button;
    private EditText message_input;
    private Button send_button;
    private final static String TAG = MainActivity.class.getSimpleName();
    private FirebaseRealtimeDatabaseInstance firebase;
    private FirebaseMessengerInstance firebaseMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // here we grab the declared username from the intent and put the intent in our database
        String username = getIntent().getStringExtra(UNAME_KEY);

        // Test code to make a username in the Firebase database
        firebase = new FirebaseRealtimeDatabaseInstance(this, username, s -> {
            createFirebaseMessagingInstance(s);
            return null;
        });
        // this call should update the database with the correct username and client id
        firebase.getToken();
        initViews();
    }

    // once we have the user's client ID, we can init the messaging service
    private void createFirebaseMessagingInstance(String clientId) {
        firebaseMessenger = new FirebaseMessengerInstance(clientId);
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
        message_input = findViewById(R.id.message_edit_text);

        Button love_button = findViewById(R.id.loveEmoji);
        love_button.setOnClickListener(v -> {
            clickedEmoji(love_button.getText().toString());
        });

        Button flex_button = findViewById(R.id.flexEmoji);
        flex_button.setOnClickListener(v -> {
            //clickedEmoji(flex_button.getText().toString());
            clickedEmoji("\uD83E\uDD17");

        });


        send_button = findViewById(R.id.send_message_button);
        send_button.setOnClickListener(v -> {
            // for testing lets see if we can add an emoji
            //firebase.addEmojiReceived("p");
            //firebase.incrementEmojiSentCount();
            firebaseMessenger.sendMessage(message_input.getText().toString(), aBoolean -> {
                // we check if the handler was able to send the message successfully, and increment if so
                // update received user's "received" history
                if (aBoolean) {


                    firebase.addEmojiReceived(username_input.getText().toString(), message_input.getText().toString());
                    Log.e("message EMO ", message_input.getText().toString());
                    firebase.incrementEmojiSentCount();

                }
                return null;
            });
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
                    firebase.incrementEmojiSentCount();
                }
                return null;
            });
    }

}