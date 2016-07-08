package com.chat_client.service;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.chat_client.database.controller.DatabaseController;
import com.chat_client.database.controller.auth.AuthorisationController;
import com.chat_client.database.controller.registration.SignUpController;
import com.chat_client.database.util.ConnectionConfig;
import com.chat_client.util.entity.IntentExtraStrings;

import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
                try (ZMQ.Context context = ZMQ.context(1)) {
                    ConnectionConfig config = ConnectionConfig.getInstance(context, DatabaseService.this);

                    DatabaseController controller;
                    Bundle extras = intent.getExtras();
                    // TODO: 7/1/16 to strategy pattern
                    if (extras.containsKey(IntentExtraStrings.AUTHORIZE)) {
                        controller = new AuthorisationController(config.getDatabaseRequester());
                        pendingIntent = intent.getParcelableExtra(IntentExtraStrings.AUTHORIZE);
                    } else if (extras.containsKey(IntentExtraStrings.REGISTER)) {
                        controller = new SignUpController(config.getDatabaseRequester());
                        pendingIntent = intent.getParcelableExtra(IntentExtraStrings.REGISTER);
                    } else {
                        return;
                    }

                    String login = intent.getStringExtra(IntentExtraStrings.LOGIN);
                    String password = intent.getStringExtra(IntentExtraStrings.PASSWORD);
                    boolean authorization = controller.execute(login, password);


                    intent.putExtra(IntentExtraStrings.VALID, authorization);
                    pendingIntent.send(DatabaseService.this, 0, intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
