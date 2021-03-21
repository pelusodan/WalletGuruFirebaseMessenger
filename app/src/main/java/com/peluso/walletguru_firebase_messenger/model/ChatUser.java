package com.peluso.walletguru_firebase_messenger.model;

public class ChatUser {

    public String username;
    public String clientId;
    public Integer stickerSentCountHeart;
    public Integer stickerSentCountHugs;
    public Integer stickerSentCountAngry;

    // we'll use a string to represent an array of characters here (so we don't have to deal w lists)
    public String received;

    public ChatUser() {
    }

    public ChatUser(String username, String clientId, Integer stickerSentCountHeart, Integer stickerSentCountHugs, Integer stickerSentCountAngry, String received) {
        this.username = username;
        this.clientId = clientId;
        this.stickerSentCountHeart = stickerSentCountHeart;
        this.stickerSentCountHugs = stickerSentCountHugs;
        this.stickerSentCountAngry = stickerSentCountAngry;
        this.received = received;
    }

    /*    public ChatUser(String username, String clientId, Integer stickerSentCount, String stickersReceived) {
        this.username = username;
        this.clientId = clientId;
        this.stickerSentCount = stickerSentCount;
        this.received = stickersReceived;
    }*/

    public ChatUser addSticker(String sticker) {
        received = received + "," + sticker;
        return this;
    }



    public ChatUser incrementStickerSentCount(String stickerSent) {
        switch (stickerSent)
        {
            case "\u2764\ufe0f":
                stickerSentCountHeart++;
                break;
            case "\ud83e\udd17":
                stickerSentCountHugs++;
                break;
            case "\ud83d\ude21":
                stickerSentCountAngry++;
                break;
            default:
                break;
        }
        return this;
    }
}
