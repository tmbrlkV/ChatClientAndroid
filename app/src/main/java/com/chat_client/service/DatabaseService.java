package com.chat_client.service;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.chat_client.database.controller.auth.DatabaseController;
import com.chat_client.database.util.SocketConnection;
import com.chat_client.util.entity.IntentExtraStrings;
import com.chat_client.util.entity.User;

import java.net.Socket;

public class DatabaseService extends Service {
    private PendingIntent pendingIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    SocketConnection keeper = (SocketConnection) getApplicationContext();
                    Socket activeSocket = keeper.getActiveSocket();
                    DatabaseController controller = new DatabaseController(activeSocket);
                    pendingIntent = intent.getParcelableExtra(IntentExtraStrings.DATABASE_ACTION);

                    String command = intent.getStringExtra(IntentExtraStrings.DATABASE_COMMAND);
                    String login = intent.getStringExtra(IntentExtraStrings.LOGIN);
                    String password = intent.getStringExtra(IntentExtraStrings.PASSWORD);

                    boolean authorization = controller.execute(new User(login, password), command);
                    intent.putExtra(IntentExtraStrings.VALID, authorization);
                    pendingIntent.send(DatabaseService.this, 0, intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
