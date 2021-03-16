package com.peluso.walletguru_firebase_messenger.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.peluso.walletguru_firebase_messenger.R;
import com.peluso.walletguru_firebase_messenger.firebase.SynchronizedClientId;

import java.util.function.Function;

public class MainActivity extends AppCompatActivity {

    private EditText username_input;
    private Button username_check_button;
    private final static String TAG = MainActivity.class.getSimpleName();
    private SynchronizedClientId synchronizedClientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Test code to make a username in the Firebase database
        synchronizedClientId = new SynchronizedClientId(this, "username501");
        // this call should update the database with the correct username and client id
        synchronizedClientId.getToken();
        initViews();
    }

    private void initViews() {
        username_input = findViewById(R.id.username_input);
        username_check_button = findViewById(R.id.submit_check_button);
        username_check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronizedClientId.doesUserExist(username_input.getText().toString(), new Function<Boolean, Void>() {
                    @Override
                    public Void apply(Boolean aBoolean) {
                        // TODO: ideally once you verify a user exists you can open a new activity
                        // and begin a chat with them. For now just showing in toast
                        if (aBoolean) {
                            // user exists
                            Log.e(TAG, "User exists");
                            Toast.makeText(getApplicationContext(), username_input.getText().toString() + " exists!", Toast.LENGTH_LONG).show();
                        } else {
                            // user does not exist
                            Log.e(TAG, "User doesn't exist");
                            Toast.makeText(getApplicationContext(), username_input.getText().toString() + " does not exist!", Toast.LENGTH_LONG).show();
                        }
                        return null;
                    }
                });
            }
        });
    }

}