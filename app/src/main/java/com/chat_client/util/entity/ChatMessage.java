package com.chat_client.util.entity;

public class ChatMessage {
    private String message;
    private boolean layout;

    public ChatMessage(String receivedMessage, String currentLogin) {
        String senderLogin = receivedMessage.split(": ")[0];
        layout = senderLogin.equals(currentLogin);
        message = receivedMessage;
    }

    public boolean getLayout() {
        return layout;
    }

    public String getMessage() {
        return message;
    }
}
