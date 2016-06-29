package com.chat_client.activity;

import com.chat_client.entity.User;
import com.chat_client.json.JsonObjectFactory;

import org.zeromq.ZMQ;

public class SignUpController {
    private ZMQ.Socket databaseRequester;

    public SignUpController(ZMQ.Socket databaseRequester) {
        this.databaseRequester = databaseRequester;
    }

    public boolean register(String login, String password) throws Exception {
        User user = new User(login, password);
        String jsonString = JsonObjectFactory.getJsonString("newUser", user);
        databaseRequester.send(jsonString);

        String response = databaseRequester.recvStr();
        user = JsonObjectFactory.getObjectFromJson(response, User.class);

        return user.validation();
    }
}
