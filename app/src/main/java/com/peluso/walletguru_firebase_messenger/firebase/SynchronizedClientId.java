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

    public SynchronizedClientId(Context context, String username) {
        this.context = context;
        this.username = username;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();

        //We need to react to changes in this constructor
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
    public void doesUserExist(String username, Function<Boolean, Void> func) {
        mDatabase.child("users").child(username).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // if the username exists then we can send a message to them
                        if (snapshot.exists()) {
                            func.apply(true);
                        } else {
                            func.apply(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        func.apply(false);
                    }
                }
        );
    }

}
