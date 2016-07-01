package com.chat_client.database.util;


import java.io.Serializable;

import static org.zeromq.ZMQ.Context;
import static org.zeromq.ZMQ.PUSH;
import static org.zeromq.ZMQ.Poller;
import static org.zeromq.ZMQ.REQ;
import static org.zeromq.ZMQ.SUB;
import static org.zeromq.ZMQ.Socket;

public class ConnectionConfig implements Serializable {
    private Socket receiver;
    private Socket sender;
    private Socket databaseRequester;
    private Poller poller;

    public ConnectionConfig(Context context) {
        init(context);
    }

    private void init(Context context) {
        receiveInit(context);
        sendInit(context);
        pollerInit();
        databaseRequesterInit(context);
    }

    private void receiveInit(Context context) {
        receiver = context.socket(SUB);
        receiver.connect(ConnectionProperties.CHAT_SERVICE_RECEIVER_URL);
        receiver.subscribe("".getBytes());
    }

    private void sendInit(Context context) {
        sender = context.socket(PUSH);
        sender.connect(ConnectionProperties.CHAT_SERVICE_SENDER_URL);
    }

    private void pollerInit() {
        poller = new Poller(0);
        poller.register(receiver, Poller.POLLIN);
    }

    private void databaseRequesterInit(Context context) {
        databaseRequester = context.socket(REQ);
        databaseRequester.connect(ConnectionProperties.DATABASE_SERVICE_REQUESTER_URL);
    }

    public Socket getDatabaseRequester() {
        return databaseRequester;
    }

    public Socket getReceiver() {
        return receiver;
    }

    public Poller getPoller() {
        return poller;
    }

    public Socket getSender() {
        return sender;
    }
}
