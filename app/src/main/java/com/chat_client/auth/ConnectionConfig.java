package com.chat_client.auth;

import java.io.Serializable;

import static org.zeromq.ZMQ.*;

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
        receiver.connect("tcp://10.66.162.215:10000");
        receiver.subscribe("".getBytes());
    }

    private void sendInit(Context context) {
        sender = context.socket(PUSH);
        sender.connect("tcp://10.66.162.215:10001");
    }

    private void pollerInit() {
        poller = new Poller(0);
        poller.register(receiver, Poller.POLLIN);
    }

    private void databaseRequesterInit(Context context) {
        databaseRequester = context.socket(REQ);
        databaseRequester.connect("tcp://10.66.162.215:11000");
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
