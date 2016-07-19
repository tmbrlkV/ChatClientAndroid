package com.chat_client.database.controller.auth;

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
    public boolean execute(User user, String command) throws Exception {
        String hashPassword = SecurityUtil.hash(user.getPassword());
        user.setPassword(hashPassword);
        String jsonString = JsonObjectFactory.getJsonString(command, user);
        OutputStream outputStream = databaseRequester.getOutputStream();
        outputStream.write(jsonString.getBytes());
        outputStream.flush();

        StringBuilder data = new StringBuilder();
        InputStream reader = databaseRequester.getInputStream();

        byte[] buffer = new byte[1024];
        System.out.println(jsonString);
        int read = reader.read(buffer);
        if (read > 0) {
            data.append(new String(buffer).trim());
            user = JsonObjectFactory.getObjectFromJson(data.toString(), User.class);
        }
        return user.validation();
    }
}
