package com.chat_client.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.BaseKeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.chat_client.R;
import com.chat_client.service.ChatService;
import com.chat_client.util.ChatArrayAdapter;
import com.chat_client.util.IntentExtraStrings;
import com.chat_client.util.StringCleaner;
import com.chat_client.util.entity.ChatMessage;
import com.chat_client.util.notification.NotificationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {
    @BindView(R.id.messageField)
    protected EditText messageField;

    @BindView(R.id.sendMessageButton)
    protected Button sendMessageButton;
    @BindView(R.id.boardListView)
    protected ListView boardListView;

    private BroadcastReceiver broadcastReceiver;
    public final static String BROADCAST_ACTION = "com.chat_client.service";
    private static boolean isRun;
    private static boolean turnNotification = true;
    private ChatArrayAdapter adapter;


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

        adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_message_layout);
        boardListView.setAdapter(adapter);

        final String currentLogin = getIntent().getStringExtra(IntentExtraStrings.LOGIN);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String receive = intent.getStringExtra(IntentExtraStrings.RECEIVE_MESSAGE);
                ChatMessage message = new ChatMessage(receive, currentLogin);
                adapter.add(message);
            }

        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @OnClick(R.id.sendMessageButton)
    protected void sendMessage() {
        String message = StringCleaner.messageTrim(messageField.getText().toString());
        if (!message.equals("")) {
            Intent intent = new Intent(ChatService.BROADCAST_ACTION);
            intent.putExtra(IntentExtraStrings.SEND_MESSAGE, message);
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
