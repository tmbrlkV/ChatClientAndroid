package com.chat_client.auth;

import com.chat_client.entity.User;
import com.chat_client.json.JsonObjectFactory;

import org.zeromq.ZMQ;

import static com.chat_client.util.DatabaseCommand.AUTHORIZE_USER;

public class AuthorisationController {
    private final ZMQ.Socket databaseRequester;

    public AuthorisationController(ZMQ.Socket databaseRequester) {
        this.databaseRequester = databaseRequester;
    }

    public boolean authorization(String login, String password) throws Exception {
        User user = new User(login, password);
        String jsonString = JsonObjectFactory.getJsonString(AUTHORIZE_USER, user);
        databaseRequester.send(jsonString);

        String response = databaseRequester.recvStr();
        user = JsonObjectFactory.getObjectFromJson(response, User.class);

        return user.validation();
    }
}
