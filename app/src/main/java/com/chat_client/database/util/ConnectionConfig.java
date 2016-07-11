package com.chat_client.database.util;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import org.zeromq.ZMQ;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

public class ConnectionConfig implements Serializable {
    private ZMQ.Socket receiver;
    private ZMQ.Socket sender;
    private ZMQ.Socket databaseRequester;
    private ZMQ.Poller poller;
    private final Properties properties;
    private static ConnectionConfig instance;

    private final static class ConnectionProperties {
        private static Context androidContext;

        private ConnectionProperties() {
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        private static Properties getProperties() {
            Properties properties = new Properties();
            try (InputStream open = androidContext.getAssets().open("server.properties")) {
                properties.load(open);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return properties;
        }
    }

    public static ConnectionConfig getInstance(ZMQ.Context context, Context androidContext) {
        if (instance == null) {
            instance = new ConnectionConfig(context, androidContext);
        } else {
           ConnectionProperties.androidContext = androidContext;
        }
        return instance;
    }

    private ConnectionConfig(ZMQ.Context context, Context androidContext) {
        ConnectionProperties.androidContext = androidContext;
        properties = ConnectionProperties.getProperties();
        init(context);
    }

    private void init(ZMQ.Context context) {
        receiveInit(context);
        sendInit(context);
        databaseRequesterInit(context);
        pollerInit();
    }

    private void receiveInit(ZMQ.Context context) {
        receiver = context.socket(ZMQ.SUB);
        receiver.connect(properties.getProperty("chat_service_receiver"));
        receiver.subscribe("".getBytes());
    }

    private void sendInit(ZMQ.Context context) {
        sender = context.socket(ZMQ.PUSH);
        sender.connect(properties.getProperty("chat_service_sender"));
    }

    private void pollerInit() {
        poller = new ZMQ.Poller(0);
        poller.register(receiver, ZMQ.Poller.POLLIN);
    }

    private void databaseRequesterInit(ZMQ.Context context) {
        databaseRequester = context.socket(ZMQ.REQ);
        System.out.println(properties.getProperty("database_service_requester"));
        databaseRequester.connect(properties.getProperty("database_service_requester"));
    }

    public ZMQ.Socket getDatabaseRequester() {
        return databaseRequester;
    }

    public ZMQ.Socket getReceiver() {
        return receiver;
    }

    public ZMQ.Poller getPoller() {
        return poller;
    }

    public ZMQ.Socket getSender() {
        return sender;
    }
}