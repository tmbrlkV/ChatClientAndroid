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
import com.chat_client.util.entity.Message;
import com.chat_client.util.json.JsonObjectFactory;
import com.chat_client.util.json.JsonProtocol;
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

    private boolean isPause;
    private boolean turnNotification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationUtils = NotificationUtils.getInstance(getApplicationContext());
        notificationUtils.cancelAll();
        isPause = false;
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
                    String login = intent.getStringExtra(IntentExtraStrings.LOGIN);
                    Thread send = startSenderThread(login);
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


    private Thread startSenderThread(final String login) {
        Thread send = new Thread(new Runnable() {
            private OutputStream outputStream;

            @Override
            public void run() {
                SocketConnection keeper = (SocketConnection) getApplicationContext();
                final Socket activeSocket = keeper.getActiveSocket();
                message = "has joined";
                while (!Thread.currentThread().isInterrupted()) {
                    if (message != null) {
                        writeTo(activeSocket);
                        message = null;
                    }
                }
            }

            private void writeTo(Socket activeSocket) {
                try {
                    if (outputStream == null) {
                        outputStream = activeSocket.getOutputStream();
                    }

                    JsonProtocol<Message<String>> jsonMessage =
                            new JsonProtocol<>("message", new Message<>(login, message));
                    jsonMessage.setFrom("login");
                    jsonMessage.setTo("1");
                    String toSend = JsonObjectFactory.getJsonString(jsonMessage);
                    outputStream.write(toSend.getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    System.err.println(e.getMessage() + " sender thread");
                    Thread.currentThread().interrupt();
                }
            }
        });
        send.start();
        return send;
    }

    private Thread startReceiverThread() {
        Thread receiver = new Thread(new Runnable() {
            private InputStream inputStream;
            private StringBuffer receiveMessageBuffer = new StringBuffer();

            @Override
            public void run() {
                SocketConnection keeper = (SocketConnection) getApplicationContext();
                Socket activeSocket = keeper.getActiveSocket();
                while (!Thread.currentThread().isInterrupted()) {
                    readFrom(activeSocket);
                }
            }

            private void readFrom(Socket activeSocket) {
                try {
                    if (inputStream == null) {
                        inputStream = activeSocket.getInputStream();
                    }
                    byte[] message = new byte[300];
                    int readBytes = inputStream.read(message);
                    if (readBytes > 0) {
                        String asStringMessage = new String(message).trim();
                        JsonProtocol<Message> jsonMessage = JsonObjectFactory
                                .getObjectFromJson(asStringMessage, JsonProtocol.class);
                        if (jsonMessage != null) {
                            Message attachment = jsonMessage.getAttachment();
                            asStringMessage = attachment.getLogin() + ": "
                                    + attachment.getContent();
                            receiveMessageBuffer.append(asStringMessage);
                            Intent intent = new Intent(ChatActivity.BROADCAST_ACTION);
                            intent.putExtra(IntentExtraStrings.RECEIVE_MESSAGE,
                                    receiveMessageBuffer.toString());
                            sendBroadcast(intent);
                            notify(asStringMessage);
                            receiveMessageBuffer.setLength(0);
                        }
                    } else if (readBytes == -1) {
                        goBackToMainActivity();
                        Thread.currentThread().interrupt();
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage() + " receiver thread");
                    Thread.currentThread().interrupt();
                }
            }

            private void goBackToMainActivity() {
                Intent intent = new Intent(ChatService.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                stopService(new Intent(ChatService.this, ChatService.class));
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