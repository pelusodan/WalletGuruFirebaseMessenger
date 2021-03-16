package com.peluso.walletguru_firebase_messenger.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.peluso.walletguru_firebase_messenger.model.ChatUser;

import java.util.function.Function;

/***
 *
 * This class is responsible for updating the database with the user's proper client id based  on their username
 *
 * This is so we can chat with other users based on their username
 */
public class SynchronizedClientId {

    private final static String TAG = SynchronizedClientId.class.getSimpleName();
    private final DatabaseReference mDatabase;
    private String username;
    private Context context;
    private Function<String, Void> clientIdFunc;

    /**
     * Constructor for creating this helper class
     *
     * @param context      for displaying messages and updated information
     * @param username     you need a username to access the database and send messages
     * @param clientIdFunc pass in a function which will use your clientId to begin the firebase messaging system
     */
    public SynchronizedClientId(Context context, String username, Function<String, Void> clientIdFunc) {
        this.context = context;
        this.username = username;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.clientIdFunc = clientIdFunc;
        mDatabase.child("users").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.e(TAG, snapshot.child("users").getChildren().toString());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                }
        );
    }

    public void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e("Token", s);
                submitTokenToDatabase(s);
                clientIdFunc.apply(s);
            }
        });
    }

    private void submitTokenToDatabase(String token) {
        // add the user to the db from calculated token
        ChatUser user = new ChatUser(username, token);
        mDatabase.child("users").child(user.username).setValue(user);
    }


    /**
     * Before messaging a user, this function will check if they exist in the database
     *
     * @param username to check if this user exists
     * @param func     we need to pass a function which will be called so we can get our boolean asynchronusly
     */
    public void doesUserExist(String username, Function<ChatUser, Void> func) {
        mDatabase.child("users").child(username).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // if the username exists then we return their client ID to them
                        if (snapshot.exists()) {
                            ChatUser user = snapshot.getValue(ChatUser.class);
                            func.apply(user);
                        } else {
                            func.apply(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        func.apply(null);
                    }
                }
        );
    }

}
