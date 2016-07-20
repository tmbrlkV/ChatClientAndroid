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
import com.chat_client.activity.MainActivity;
import com.chat_client.database.util.SocketConnection;
import com.chat_client.util.entity.IntentExtraStrings;
import com.chat_client.util.notification.NotificationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
                try {
                    SocketConnection keeper = (SocketConnection) getApplicationContext();
                    Socket activeSocket = keeper.getActiveSocket();
                    String login = intent.getStringExtra(IntentExtraStrings.LOGIN);
                    messageAppender.append(login).append(" has joined");
                    writeTo(activeSocket);
                    messageAppender.setLength(0);

                    send = startSenderThread(login);
                    Thread receive = startReceiverThread();

                    send.join();
                    receive.join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return START_NOT_STICKY;
    }

    private void writeTo(Socket activeSocket) {
        try {
            OutputStream outputStream = activeSocket.getOutputStream();
            outputStream.write(messageAppender.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Thread startSenderThread(final String login) {
        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                SocketConnection keeper = (SocketConnection) getApplicationContext();
                final Socket activeSocket = keeper.getActiveSocket();
                while (!Thread.currentThread().isInterrupted()) {
                    if (message != null) {
                        messageAppender.append(login).append(": ").append(message);
                        writeTo(activeSocket);
                        messageAppender.setLength(0);
                        message = null;
                    }
                }
            }
        });
        send.start();
        return send;
    }

    private Thread startReceiverThread() {
        Thread receiver = new Thread(new Runnable() {
            private StringBuffer receiveMessageBuffer = new StringBuffer();

            @Override
            public void run() {
                SocketConnection keeper = (SocketConnection) getApplicationContext();
                Socket activeSocket = keeper.getActiveSocket();
                System.out.println(activeSocket);
                while (!Thread.currentThread().isInterrupted()) {
                    if (stopReceiver()) break;
                    try {
                        if (!activeSocket.isClosed()) {
                            readFrom(activeSocket);
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        turnNotification = false;
                    }
                }
            }

            private void readFrom(Socket activeSocket) throws IOException {
                InputStream inputStream = activeSocket.getInputStream();
                byte[] message = new byte[300];
                int readBytes = inputStream.read(message);
                if (readBytes > 0) {
                    String asStringMessage = new String(message);
                    receiveMessageBuffer.append(asStringMessage);
                    Intent intent = new Intent(ChatActivity.BROADCAST_ACTION);
                    intent.putExtra(IntentExtraStrings.RECEIVE_MESSAGE,
                            receiveMessageBuffer.toString());
                    sendBroadcast(intent);
                    notify(asStringMessage);

                    receiveMessageBuffer.setLength(0);
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
