package com.chat_client.database.controller;

import android.annotation.TargetApi;
import android.os.Build;

import com.chat_client.database.util.security.SecurityUtil;
import com.chat_client.util.entity.User;
import com.chat_client.util.json.JsonObjectFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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

        StringBuilder data = new StringBuilder();
        InputStream reader = databaseRequester.getInputStream();

        byte[] buffer = new byte[1024];
        boolean isValid = false;
        while (!Thread.currentThread().isInterrupted()) {
            int read = reader.read(buffer);
            if (read > 0) {
                String databaseReply = new String(buffer).trim();
                data.append(databaseReply);
                user = JsonObjectFactory.getObjectFromJson(data.toString(), User.class);
                if (user != null) {
                    isValid = user.validation();
                    break;
                }
                data.setLength(0);
            }
        }
        return isValid;
    }

}
