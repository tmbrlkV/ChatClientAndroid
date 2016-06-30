package com.chat_client.registration;

import com.chat_client.entity.User;
import com.chat_client.json.JsonObjectFactory;

import org.zeromq.ZMQ;

import static com.chat_client.util.DatabaseCommand.REGISTER_USER;

public class SignUpController {
    private ZMQ.Socket databaseRequester;

    public SignUpController(ZMQ.Socket databaseRequester) {
        this.databaseRequester = databaseRequester;
    }

    public boolean register(String login, String password) throws Exception {
        User user = new User(login, password);
        String jsonString = JsonObjectFactory.getJsonString(REGISTER_USER, user);
        databaseRequester.send(jsonString);

        String response = databaseRequester.recvStr();
        user = JsonObjectFactory.getObjectFromJson(response, User.class);

        return user.validation();
    }
}
