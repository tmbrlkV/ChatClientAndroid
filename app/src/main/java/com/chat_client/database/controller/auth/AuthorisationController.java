package com.chat_client.database.controller.auth;

import com.chat_client.database.controller.DatabaseController;
import com.chat_client.database.controller.security.SecurityController;
import com.chat_client.util.entity.User;
import com.chat_client.util.json.JsonObjectFactory;

import org.zeromq.ZMQ;

public class AuthorisationController implements DatabaseController {
    private final ZMQ.Socket databaseRequester;
    private static final String AUTHORIZE_USER = "getUserByLoginPassword";

    public AuthorisationController(ZMQ.Socket databaseRequester) {
        this.databaseRequester = databaseRequester;
    }

    @Override
    public boolean execute(String login, String password) throws Exception {
        User user = new User(login, SecurityController.hash(password));
        String jsonString = JsonObjectFactory.getJsonString(AUTHORIZE_USER, user);
        databaseRequester.send(jsonString);

        String response = databaseRequester.recvStr();
        user = JsonObjectFactory.getObjectFromJson(response, User.class);

        return user.validation();
    }
}
