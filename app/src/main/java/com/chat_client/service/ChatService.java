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

public class ChatService extends Service {
    public static final String BROADCAST_ACTION = "com.chat_client.activity";
    private BroadcastReceiver broadcastServiceReceiver;
    private NotificationUtils notificationUtils;
    private String message;
    private StringBuffer messageAppender = new StringBuffer(0);

    private static Thread send;
    private static boolean isRun = true;
    private static boolean isPause;
    private static boolean turnNotification = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationUtils = NotificationUtils.getInstance(getApplicationContext());
        stopSenderThreadIfNotInterrupted();
        broadcastServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                message = intent.getStringExtra(IntentExtraStrings.SEND_MESSAGE);
                isPause = intent.getBooleanExtra(IntentExtraStrings.PAUSE, false);
                turnNotification = intent.getBooleanExtra(IntentExtraStrings.NOTIFICATIONS, true);
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastServiceReceiver, intentFilter);
    }

    private void stopSenderThreadIfNotInterrupted() {
        if (send != null && !send.isInterrupted()) {
            isRun = false;
            send.interrupt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastServiceReceiver);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, int startId) {
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try (ZMQ.Context context = ZMQ.context(1)) {
                    ConnectionConfig config = new ConnectionConfig(context);

                    ZMQ.Socket sender = config.getSender();
                    String login = intent.getStringExtra(IntentExtraStrings.LOGIN);
                    messageAppender.append(login).append(" has joined");
                    sender.send(messageAppender.toString());
                    messageAppender.setLength(0);

                    send = startSenderThread(login, config);
                    Thread receive = startReceiverThread(config);

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
                        messageAppender.append(login).append(": ").append(message);
                        sender.send(messageAppender.toString());
                        messageAppender.setLength(0);
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
                    if (stopReceiver()) break;
                    int events = poller.poll();
                    if (events > 0) {
                        String message = receiver.recvStr(0);
                        receiveMessageBuffer.append(message);
                        Intent intent = new Intent(ChatActivity.BROADCAST_ACTION);
                        intent.putExtra(IntentExtraStrings.RECEIVE_MESSAGE,
                                receiveMessageBuffer.toString());
                        sendBroadcast(intent);
                        notify(message);

                        receiveMessageBuffer.setLength(0);
                    }
                }
            }

            private boolean stopReceiver() {
                if (!isRun) {
                    isRun = true;
                    notificationUtils.cancelAll();
                    return true;
                }
                return false;
            }

            private void notify(String message) {
                if (isPause && turnNotification) {
                    notificationUtils.createInfoNotification(message);
                }
            }
        });
        receiver.start();
        return receiver;
    }
}
