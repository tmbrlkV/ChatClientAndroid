package com.chat_client.database.controller.registration;

import com.chat_client.database.controller.DatabaseController;
import com.chat_client.database.security.SecurityController;
import com.chat_client.util.entity.User;
import com.chat_client.util.json.JsonObjectFactory;

import org.zeromq.ZMQ;

import java.security.MessageDigest;

import static com.chat_client.database.util.DatabaseCommand.REGISTER_USER;

public class SignUpController implements DatabaseController {
    private final ZMQ.Socket databaseRequester;

    public SignUpController(ZMQ.Socket databaseRequester) {
        this.databaseRequester = databaseRequester;
    }

    @Override
    public boolean execute(String login, String password) throws Exception {
        User user = new User(login, SecurityController.hash(password));
        String jsonString = JsonObjectFactory.getJsonString(REGISTER_USER, user);
        databaseRequester.send(jsonString);

        String response = databaseRequester.recvStr();
        user = JsonObjectFactory.getObjectFromJson(response, User.class);

        return user.validation();
    }
}
