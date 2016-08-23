package com.chat_client.database.controller;

import android.annotation.TargetApi;
import android.os.Build;

import com.chat.util.entity.User;
import com.chat.util.json.JsonObjectFactory;
import com.chat.util.json.JsonProtocol;
import com.chat_client.database.util.security.SecurityUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class DatabaseController {
    private Socket databaseRequester;

    public DatabaseController(Socket databaseRequester) {
        this.databaseRequester = databaseRequester;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean execute(String command, User user) throws Exception {
        String hashPassword = SecurityUtil.hash(user.getPassword());
        user.setPassword(hashPassword);
        String jsonString = JsonObjectFactory.getJsonString(new JsonProtocol<>(command, user));
        OutputStream outputStream = databaseRequester.getOutputStream();
        outputStream.write(jsonString.getBytes());
        outputStream.flush();

        InputStream reader = databaseRequester.getInputStream();
        Scanner scanner = new Scanner(reader);
        boolean isValid = false;
        int count = 0;
//        while (count++ < 2 && !isValid) {
        String line = scanner.nextLine().trim();
        System.out.println("Line: " + line);
        User newUser = JsonObjectFactory.getObjectFromJson(line, User.class);
        System.out.println(newUser.getId() + " " + newUser.getLogin() + " " + newUser.getPassword() + " " + newUser.validation());
        if (newUser.getLogin().equals(user.getLogin())
                && newUser.getPassword().equals(user.getPassword())
                && newUser.getId() != 0) {
            isValid = newUser.validation();
        }
//        }

        System.out.println("isValid: " + isValid);
        return isValid;
    }

}
