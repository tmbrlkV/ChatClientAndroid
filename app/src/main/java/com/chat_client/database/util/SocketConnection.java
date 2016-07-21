package com.chat_client.database.util;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

public class SocketConnection extends Application {
    private Socket socket;

    private static final class ConnectionProperties {
        private ConnectionProperties() {
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        private static Properties getProperties(Context context) {
            Properties properties = new Properties();
            try (InputStream open = context.getApplicationContext()
                    .getAssets().open("server.properties")) {
                properties.load(open);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return properties;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                socket = socketInit();
            }
        });
        thread.start();
    }

    public Socket getActiveSocket() {
        if (socket.isClosed()) {
            socket = socketInit();
        }
        return socket;
    }

    private Socket socketInit() {
        Socket socket = new Socket();
        Properties properties = ConnectionProperties
                .getProperties(SocketConnection.this);
        String address = (String) properties.get("butler_service_address");
        int port = Integer.parseInt((String) properties.get("butler_service_port"));
        try {
            socket.connect(new InetSocketAddress(address, port));
            socket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}