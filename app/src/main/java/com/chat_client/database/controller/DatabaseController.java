package com.chat_client.database.controller;

import android.annotation.TargetApi;
import android.os.Build;

import com.chat_client.database.util.security.SecurityUtil;
import com.chat_client.util.entity.User;
import com.chat_client.util.json.JsonObjectFactory;

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
        String jsonString = JsonObjectFactory.getJsonString(command, user);
        OutputStream outputStream = databaseRequester.getOutputStream();
        outputStream.write(jsonString.getBytes());
        outputStream.flush();

        InputStream reader = databaseRequester.getInputStream();
        Scanner scanner = new Scanner(reader);
        boolean isValid = false;
        int count = 0;
        while (count++ < 2 && !isValid) {
            String line = scanner.nextLine();
            User newUser = JsonObjectFactory.getObjectFromJson(line, User.class);
            if (newUser != null && newUser.equals(user) && newUser.getId() != 0) {
                isValid = newUser.validation();
            }
        }
        return isValid;
    }

}
