package com.chat_client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.chat_client.R;
import com.chat_client.util.alert.AlertDialogUtils;
import com.chat_client.util.alert.WifiAlertUtil;
import com.chat_client.util.entity.IntentExtraStrings;

public class MainActivity extends AppCompatActivity {
    private WifiAlertUtil wifiAlertUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiAlertUtil = new WifiAlertUtil(new AlertDialogUtils(this));
        setContentView(R.layout.main_activity);
        getFragmentManager().beginTransaction()
                .add(R.id.mainFragmentField, new SignInFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiAlertUtil.alert(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wifiAlertUtil.dismiss();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                moveTaskToBack(true);
            } else {
                getFragmentManager().popBackStack();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
        String command = (String) extras.get(IntentExtraStrings.DATABASE_COMMAND);
        assert command != null;
        if (command.equals(IntentExtraStrings.AUTHORIZE)) {
            authorization(data);
        }
        if (command.equals(IntentExtraStrings.REGISTER)){
            registration(data);
        }
    }

    private void authorization(Intent data) {
        boolean authorized = data.getBooleanExtra(IntentExtraStrings.VALID, false);
        if (authorized) {
            Intent newIntent = new Intent(this, ChatActivity.class);
            newIntent.putExtra(IntentExtraStrings.LOGIN, data.getStringExtra(IntentExtraStrings.LOGIN));
            startActivity(newIntent);
        } else {
            Toast.makeText(this, "Authorization failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void registration(Intent data) {
        boolean signedUp = data.getBooleanExtra(IntentExtraStrings.VALID, false);
        if (signedUp) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.mainFragmentField, new SignInFragment())
                    .commit();
            Toast.makeText(this, "Registration success",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration failed",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
