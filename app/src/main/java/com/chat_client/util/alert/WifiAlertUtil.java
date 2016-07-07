package com.chat_client.util.alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

public class WifiAlertUtil extends BroadcastReceiver {
    private static AlertDialog dialog;
    private static WifiAlertUtil instance;
    private static Context context;

    public WifiAlertUtil() {
    }

    public static WifiAlertUtil getInstance(AlertDialogUtils dialogUtils) {
        if (instance == null) {
            instance = new WifiAlertUtil(dialogUtils);
        } else {
            context = dialogUtils.getContext();
            dialog = dialogUtils.createWifiNotEnabledDialog();
        }
        return instance;
    }

    private WifiAlertUtil(AlertDialogUtils dialogUtils) {
        context = dialogUtils.getContext();
        dialog = dialogUtils.createWifiNotEnabledDialog();
    }


    public void alert() {
        dismissAlertDialog();
        wifiDisabledDialog();
    }

    public void dismiss() {
        dismissAlertDialog();
    }

    private void dismissAlertDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void wifiDisabledDialog() {
        if (!isOnline() && dialog != null) {
            dialog.show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        if (isOnline) {
            dismiss();
        } else {
            alert();
        }
    }
}
