package com.chat_client.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.chat_client.activity.ChatActivity;
import com.chat_client.database.util.ConnectionConfig;
import com.chat_client.util.IntentExtraStrings;

import org.zeromq.ZMQ;

public class ChatService extends Service {
    private ZMQ.Context context = ZMQ.context(1);
    private BroadcastReceiver broadcastReceiver;
    private String message;
    public static final String BROADCAST_ACTION = "com.chat_client.activity";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                message = intent.getStringExtra(IntentExtraStrings.SEND_MESSAGE);
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        context.close();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectionConfig config = new ConnectionConfig(context);

                    ZMQ.Socket sender = config.getSender();
                    String login = intent.getStringExtra(IntentExtraStrings.LOGIN);
                    sender.send(login + " has joined");

                    Thread send = startSenderThread(login, config);
                    Thread receive = startReceiverThread(config);

                    send.join();
                    receive.join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }


    private Thread startSenderThread(final String login, final ConnectionConfig config) {
        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                ZMQ.Socket sender = config.getSender();
                while (!Thread.currentThread().isInterrupted()) {
                    if (message != null) {
                        sender.send(login + ": " + message);
                        message = null;
                    }
                }
            }
        });
        send.start();
        return send;
    }

    private Thread startReceiverThread(final ConnectionConfig config) {
        Thread receiver = new Thread(new Runnable() {
            private StringBuffer receiveMessageBuffer = new StringBuffer();

            @Override
            public void run() {
                ZMQ.Socket receiver = config.getReceiver();
                ZMQ.Poller poller = config.getPoller();
                while (!Thread.currentThread().isInterrupted()) {
                    int events = poller.poll();
                    if (events > 0) {
                        String message = receiver.recvStr(0);
                        receiveMessageBuffer.append("\n").append(message);
                        Intent intent = new Intent(ChatActivity.BROADCAST_ACTION);
                        intent.putExtra(IntentExtraStrings.RECEIVE_MESSAGE, receiveMessageBuffer.toString());
                        sendBroadcast(intent);

                        receiveMessageBuffer.setLength(0);
                    }
                }
            }
        });
        receiver.start();
        return receiver;
    }
}
