package com.peluso.walletguru_firebase_messenger.model;

public class ChatUser {

    public String username;
    public String clientId;

    public ChatUser() {

    }

    public ChatUser(String username, String clientId) {
        this.username = username;
        this.clientId = clientId;
    }
}
