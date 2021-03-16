package com.peluso.walletguru_firebase_messenger.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.peluso.walletguru_firebase_messenger.R;
import com.peluso.walletguru_firebase_messenger.firebase.FirebaseMessengerInstance;
import com.peluso.walletguru_firebase_messenger.firebase.SynchronizedClientId;
import com.peluso.walletguru_firebase_messenger.model.ChatUser;

public class MainActivity extends AppCompatActivity {

    private EditText username_input;
    private Button username_check_button;
    private EditText message_input;
    private Button send_button;
    private final static String TAG = MainActivity.class.getSimpleName();
    private SynchronizedClientId synchronizedClientId;
    private FirebaseMessengerInstance firebaseMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Test code to make a username in the Firebase database
        synchronizedClientId = new SynchronizedClientId(this, "username500", s -> {
            createFirebaseMessagingInstance(s);
            return null;
        });
        // this call should update the database with the correct username and client id
        synchronizedClientId.getToken();
        initViews();
    }

    // once we have the user's client ID, we can init the messaging service
    private void createFirebaseMessagingInstance(String clientId) {
        firebaseMessenger = new FirebaseMessengerInstance(clientId);
    }

    private void initViews() {
        username_input = findViewById(R.id.username_input);
        username_check_button = findViewById(R.id.submit_check_button);
        username_check_button.setOnClickListener(v -> synchronizedClientId.doesUserExist(username_input.getText().toString(), chatUser -> {
            // TODO: ideally once you verify a user exists you can open a new activity
            // and begin a chat with them. For now just showing in toast
            // NOTE: this also sets the recipient in our firebase messaging instance
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
        send_button = findViewById(R.id.send_message_button);
        send_button.setOnClickListener(v -> {
            firebaseMessenger.sendMessage(message_input.getText().toString());
        });
    }

    private void setRecipient(ChatUser chatUser) {
        // this function sets our Firebase instance to be sending messages to THIS user
        firebaseMessenger.setRecipient(chatUser);
    }

}