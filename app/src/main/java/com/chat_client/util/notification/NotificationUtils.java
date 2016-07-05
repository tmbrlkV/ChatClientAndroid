package com.chat_client.util.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.chat_client.R;
import com.chat_client.activity.ChatActivity;

public class NotificationUtils {
    private static NotificationUtils instance;

    private static Context context;
    private NotificationManager manager;
    private boolean turn = true;


    private NotificationUtils(Context context) {
        NotificationUtils.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static NotificationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationUtils(context);
        } else {
            NotificationUtils.context = context;
        }
        return instance;
    }

    public int createInfoNotification(String message) {
        if (!instance.turn) return -1;
        Intent notificationIntent = new Intent(context, ChatActivity.class); // HomeActivity opening by click
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setTicker(message)
                .setContentText(message)
                .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Chat")
                .setDefaults(Notification.DEFAULT_ALL);

        Notification notification = nb.build();
        manager.notify(0, notification);
        return 0;
    }

    public void cancelAll() {
        manager.cancelAll();
    }

    public void turnOff() {
        instance.turn = false;
    }

    public void turnOn() {
        instance.turn = true;
    }
}
