package com.chat_client.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chat_client.R;
import com.chat_client.service.ChatService;
import com.chat_client.util.IntentExtraStrings;
import com.chat_client.util.StringCleaner;
import com.chat_client.util.notification.NotificationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {
    @BindView(R.id.boardChatTextView)
    protected TextView board;
    @BindView(R.id.messageField)
    protected EditText messageField;
    @BindView(R.id.boardScrollView)
    protected ScrollView boardScrollView;
    @BindView(R.id.sendMessageButton)
    protected Button sendMessageButton;

    private StringBuffer sendMessageBuffer = new StringBuffer();
    private StringBuffer receiveMessageBuffer = new StringBuffer();

    private BroadcastReceiver broadcastReceiver;
    public final static String BROADCAST_ACTION = "com.chat_client.service";
    private static boolean isRun;
    private static boolean turnNotification = true;


    private void nullUserProtection() {
        String login = getIntent().getStringExtra(IntentExtraStrings.LOGIN);
        if (login == null) {
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nullUserProtection();
        setContentView(R.layout.chat_main);
        ButterKnife.bind(this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String receive = intent.getStringExtra(IntentExtraStrings.RECEIVE_MESSAGE);
                receiveMessageBuffer.append(receive);
                board.append(receiveMessageBuffer);
                receiveMessageBuffer.setLength(0);
            }

        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        boardScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                boardScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @OnClick(R.id.sendMessageButton)
    protected void sendMessage() {
        String message = StringCleaner.messageTrim(messageField.getText().toString());
        if (!message.equals("")) {
            sendMessageBuffer.append(message);
            Intent intent = new Intent(ChatService.BROADCAST_ACTION);
            intent.putExtra(IntentExtraStrings.SEND_MESSAGE, sendMessageBuffer.toString());
            sendMessageBuffer.setLength(0);
            sendBroadcast(intent);
            messageField.setText("");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isRun) {
            isRun = true;
            Intent intent = new Intent(this, ChatService.class);
            String login = getIntent().getStringExtra(IntentExtraStrings.LOGIN);
            intent.putExtra(IntentExtraStrings.LOGIN, login);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationUtils.getInstance(getApplicationContext()).cancelAll();
        Intent intent = new Intent(ChatService.BROADCAST_ACTION);
        intent.putExtra(IntentExtraStrings.PAUSE, false);
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(ChatService.BROADCAST_ACTION);
        intent.putExtra(IntentExtraStrings.PAUSE, true);
        intent.putExtra(IntentExtraStrings.NOTIFICATIONS, turnNotification);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isRun = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
        stopService(new Intent(this, ChatService.class));
        NotificationUtils.getInstance(getApplicationContext()).cancelAll();
        unregisterReceiver(broadcastReceiver);
    }

    public void turnOnMenuClick(MenuItem item) {
        if (!item.isChecked()) {
            turnNotification = true;
            item.setChecked(true);
        }
    }

    public void turnOffMenuClick(MenuItem item) {
        if (!item.isChecked()) {
            turnNotification = false;
            item.setChecked(true);
        }
    }
}
