package com.chat_client.auth;

import com.chat_client.entity.User;
import com.chat_client.json.JsonObjectFactory;

import org.zeromq.ZMQ;

public class AuthorisationController {
    private final ZMQ.Socket databaseRequester;

    public AuthorisationController(ZMQ.Socket databaseRequester) {
        this.databaseRequester = databaseRequester;
    }

    public boolean authorization(String login, String password) throws Exception {

        User user = new User(login, password);
        String jsonString = JsonObjectFactory.getJsonString("getUserByLoginPassword", user);
        databaseRequester.send(jsonString);

        String response = databaseRequester.recvStr();
        user = JsonObjectFactory.getObjectFromJson(response, User.class);

        return user.validation();
    }
}
