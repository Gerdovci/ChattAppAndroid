package com.chatt.gerdovci.chattapp;

/**
 * Created by gerdovci on 2016-12-07.
 */

public class ConversationListItems {

    String myPhoto;
    String myChattroomId;
    String myMessage;

    public ConversationListItems(String photo, String chattroomId, String message) {
        myPhoto = photo;
        myChattroomId = chattroomId;
        myMessage = message;
    }


    public String getMessage() {
        return myMessage;
    }

    public void setMessage(String myMessage) {
        this.myMessage = myMessage;
    }

    public String getChattroomId() {
        return myChattroomId;
    }

    public void setChattroomId(String myChattroomId) {
        this.myChattroomId = myChattroomId;
    }

    public String getPhoto() {
        return myPhoto;
    }

    public void setPhoto(String myPhoto) {
        this.myPhoto = myPhoto;
    }
}
