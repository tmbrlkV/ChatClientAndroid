package com.chat_client.database.controller.registration;

import com.chat_client.database.controller.DatabaseController;
import com.chat_client.database.util.security.SecurityUtil;
import com.chat_client.util.entity.User;
import com.chat_client.util.json.JsonObjectFactory;

import org.zeromq.ZMQ;

public class SignUpController implements DatabaseController {
    private final ZMQ.Socket databaseRequester;
    private static final String REGISTER_USER = "newUser";

    public SignUpController(ZMQ.Socket databaseRequester) {
        this.databaseRequester = databaseRequester;
    }

    @Override
    public boolean execute(String login, String password) throws Exception {
        User user = new User(login, SecurityUtil.hash(password));
        String jsonString = JsonObjectFactory.getJsonString(REGISTER_USER, user);
        databaseRequester.send(jsonString);

        String response = databaseRequester.recvStr();
        user = JsonObjectFactory.getObjectFromJson(response, User.class);

        return user.validation();
    }
}
