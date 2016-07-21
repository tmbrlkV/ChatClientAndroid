package com.chat_client.util.entity;

import java.util.Date;

public class ChatMessage {
    private String message;
    private boolean layout;
    private Date date;

    public ChatMessage(String receivedMessage, String currentLogin) {
        String senderLogin = receivedMessage.split(": ")[0];
        layout = senderLogin.equals(currentLogin);
        message = receivedMessage;
        date = new Date();
    }

    public boolean getLayout() {
        return layout;
    }

    public String getMessage() {
        return message;
    }

    public Date getTime() {
        return date;
    }
}
