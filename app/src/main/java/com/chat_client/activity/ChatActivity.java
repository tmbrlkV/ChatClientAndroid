package com.chat_client.activity;

import android.app.Activity;
import android.os.Bundle;

import com.chat_client.R;
import com.chat_client.auth.ConnectionConfig;

import org.zeromq.ZMQ;

public class ChatActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
//
//        ConnectionConfig config = (ConnectionConfig) getIntent().getSerializableExtra("config");
//        String login = getIntent().getStringExtra("login");
//        ZMQ.Socket sender = config.getSender();
//        sender.send(login + " has joined");
//
//        Thread send = startSenderThread(login, config);
//        Thread receive = startReceiverThread(config);
//
//        try {
//            send.join();
//            receive.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

//    private static Thread startSenderThread(final String login, final ConnectionConfig config) {
//        Thread send = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ZMQ.Socket sender = config.getSender();
//                while (!Thread.currentThread().isInterrupted()) {
//                    System.out.println(login);
//                }
//            }
//        });
//        send.start();
//        return send;
//    }
//
//    private static Thread startReceiverThread(final ConnectionConfig config) {
//        Thread receive = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ZMQ.Socket receiver = config.getReceiver();
//                ZMQ.Poller poller = config.getPoller();
//                while (!Thread.currentThread().isInterrupted()) {
//                    int events = poller.poll();
//                    if (events > 0) {
//                        String message = receiver.recvStr(0);
//                        System.out.println(message);
//                    }
//                }
//            }
//        });
//        receive.start();
//        return receive;
//    }
}
