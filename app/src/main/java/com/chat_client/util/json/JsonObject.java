package com.chat_client.util.json;


import com.chat_client.util.entity.User;

public class JsonObject {
    private String command;
    private User user;

    public JsonObject() {}

    JsonObject(String command, User user) {
        this.command = command;
        this.user = user;
    }

    public String getCommand() {
        return command;
    }

    public Object getUser() {
        return user;
    }
}
