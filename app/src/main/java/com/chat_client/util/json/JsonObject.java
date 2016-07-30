package com.chat_client.util.json;


public class JsonObject {
    private String command;
    private Object object;

    public JsonObject() {}

    JsonObject(String command, Object object) {
        this.command = command;
        this.object = object;
    }

    public String getCommand() {
        return command;
    }

    public Object getObject() {
        return object;
    }
}
