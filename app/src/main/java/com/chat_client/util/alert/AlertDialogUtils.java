package com.chat_client.util.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.chat_client.R;

public class AlertDialogUtils {
    private static AlertDialogUtils instance;

    private static Context context;
    private AlertDialog.Builder builder;

    private AlertDialogUtils(Context context) {
        AlertDialogUtils.context = context;
        builder = new AlertDialog.Builder(context);
    }

    public static AlertDialogUtils getInstance(Context context) {
        if (instance == null) {
            instance = new AlertDialogUtils(context);
        } else {
            AlertDialogUtils.context = context;
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public AlertDialog createWifiNotEnabledDialog() {
        builder.setMessage(R.string.wifi_dialog_message)
                .setTitle(R.string.wifi_dialog_title)
                .setCancelable(false);
        builder.setPositiveButton(R.string.wifi_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.wifi_dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(startMain);
            }
        });

        return builder.create();
    }

}
