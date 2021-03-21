package com.peluso.walletguru_firebase_messenger.model;

public class ChatUser {

    public String username;
    public String clientId;
    public Integer stickerSentCount;
    // we'll use a string to represent an array of characters here (so we don't have to deal w lists)
    public String received;

    public ChatUser() {
    }

    public ChatUser(String username, String clientId, Integer stickerSentCount, String stickersReceived) {
        this.username = username;
        this.clientId = clientId;
        this.stickerSentCount = stickerSentCount;
        this.received = stickersReceived;
    }

    public ChatUser addSticker(String sticker) {
        received = received + "," + sticker;
        return this;
    }



    public ChatUser incrementStickerSentCount() {
        stickerSentCount++;
        return this;
    }
}
