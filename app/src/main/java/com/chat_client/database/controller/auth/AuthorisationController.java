package com.chat_client.database.controller.auth;

import com.chat_client.database.controller.DatabaseController;
import com.chat_client.util.entity.User;
import com.chat_client.util.json.JsonObjectFactory;

import org.zeromq.ZMQ;

import static com.chat_client.database.util.DatabaseCommand.AUTHORIZE_USER;

public class AuthorisationController implements DatabaseController {
    private final ZMQ.Socket databaseRequester;

    public AuthorisationController(ZMQ.Socket databaseRequester) {
        this.databaseRequester = databaseRequester;
    }

    @Override
    public boolean execute(String login, String password) throws Exception {
        User user = new User(login, password);
        String jsonString = JsonObjectFactory.getJsonString(AUTHORIZE_USER, user);
        databaseRequester.send(jsonString);

        String response = databaseRequester.recvStr();
        user = JsonObjectFactory.getObjectFromJson(response, User.class);

        return user.validation();
    }
}
