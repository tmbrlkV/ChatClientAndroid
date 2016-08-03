package com.chat_client.database.controller;

import android.annotation.TargetApi;
import android.os.Build;

import com.chat_client.client.NioClient;
import com.chat_client.database.util.SocketConnection;
import com.chat_client.database.util.security.SecurityUtil;
import com.chat_client.util.entity.User;
import com.chat_client.util.json.JsonObjectFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseController {
    private SocketConnection keeper;

    public DatabaseController(SocketConnection keeper) {
        this.keeper = keeper;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean execute(String command, User user) throws Exception {
        String hashPassword = SecurityUtil.hash(user.getPassword());
        user.setPassword(hashPassword);
        String jsonString = JsonObjectFactory.getJsonString(command, user);
        keeper.send(jsonString);

        String read = keeper.read();
        System.out.println(read);
        boolean isValid = false;
        User newUser = JsonObjectFactory.getObjectFromJson(read, User.class);
        if (newUser != null && newUser.equals(user) && newUser.getId() != 0) {
            isValid = newUser.validation();
        }
        System.out.println(isValid);
        return isValid;
    }

}
