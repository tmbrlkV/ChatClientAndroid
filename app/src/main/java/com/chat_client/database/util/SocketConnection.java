package com.chat_client.database.util;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.chat_client.client.NioClient;
import com.chat_client.client.ResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

public class SocketConnection extends Application {
    private Thread clientThread;
    private NioClient client;
    private ResponseHandler handler = new ResponseHandler();

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
                try {
                    Properties properties = ConnectionProperties.getProperties(getApplicationContext());
                    String host = properties.getProperty("butler_service_address");
                    int port = Integer.parseInt(properties.getProperty("butler_service_port"));
                    client = new NioClient(InetAddress.getByName(host), port);
                    clientThread = new Thread(client);
                    clientThread.setDaemon(true);
                    clientThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            client.send(message.getBytes(), handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {
        String read = handler.waitForResponse().trim();
        System.out.println(read);
        return read;
    }

    public void close() {
        clientThread.interrupt();
    }
}