package com.chat_client.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.chat_client.activity.ChatActivity;
import com.chat_client.database.util.ConnectionConfig;
import com.chat_client.util.IntentExtraStrings;
import com.chat_client.util.notification.NotificationUtils;

import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;

public class ChatService extends Service {
    private BroadcastReceiver broadcastServiceReceiver;
    private NotificationUtils notificationUtils;
    private String message;
    public static final String BROADCAST_ACTION = "com.chat_client.activity";
    private static List<Thread> threads = new ArrayList<>();
    private static boolean isRun = true;
    private static boolean isPause;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!threads.isEmpty()) {
            isRun = false;
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }
        broadcastServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                message = intent.getStringExtra(IntentExtraStrings.SEND_MESSAGE);
                isPause = intent.getBooleanExtra("pause", false);
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastServiceReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastServiceReceiver);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, int startId) {
        notificationUtils = NotificationUtils.getInstance(getApplicationContext());
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try (ZMQ.Context context = ZMQ.context(1)) {
                    ConnectionConfig config = new ConnectionConfig(context);

                    ZMQ.Socket sender = config.getSender();
                    String login = intent.getStringExtra(IntentExtraStrings.LOGIN);
                    sender.send(login + " has joined");

                    Thread send = startSenderThread(login, config);
                    Thread receive = startReceiverThread(config);

                    threads.add(send);

                    send.join();
                    receive.join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return START_NOT_STICKY;
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
                    if (!isRun) {
                        isRun = true;
                        notificationUtils.cancelAll();
                        break;
                    }
                    int events = poller.poll();
                    if (events > 0) {
                        String message = receiver.recvStr(0);
                        receiveMessageBuffer.append("\n").append(message);
                        Intent intent = new Intent(ChatActivity.BROADCAST_ACTION);
                        intent.putExtra(IntentExtraStrings.RECEIVE_MESSAGE,
                                receiveMessageBuffer.toString());
                        sendBroadcast(intent);
                        if (isPause) {
                            notificationUtils.createInfoNotification(message);
                        }
                        receiveMessageBuffer.setLength(0);
                    }
                }
            }
        });
        receiver.start();
        return receiver;
    }
}
