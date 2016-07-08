package com.chat_client.util.alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

public class WifiAlertUtil extends BroadcastReceiver {
    private static AlertDialog dialog;

    public WifiAlertUtil() {
    }

    public WifiAlertUtil(AlertDialogUtils dialogUtils) {
        WifiAlertUtil.dialog = dialogUtils.createWifiNotEnabledDialog();
    }

    public void alert(Context context) {
        if (!isOnline(context)) {
            dialog.show();
        }
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private boolean isOnline(Context context) {
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
        if (dialog != null) {
            if (isOnline) {
                dismiss();
            } else {
                alert(context);
            }
        }
    }
}
