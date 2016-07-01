package com.chat_client.database.controller;

public interface DatabaseController {
    boolean execute(String login, String password) throws Exception;
}
