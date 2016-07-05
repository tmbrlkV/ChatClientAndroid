package com.chat_client.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chat_client.R;
import com.chat_client.service.ChatService;
import com.chat_client.util.IntentExtraStrings;
import com.chat_client.util.StringCleaner;

public class ChatActivity extends Activity {
    private TextView board;
    private EditText messageField;
    private StringBuffer sendMessageBuffer = new StringBuffer();
    private StringBuffer receiveMessageBuffer = new StringBuffer(0);
    private ScrollView boardScrollView;
    private BroadcastReceiver broadcastReceiver;

    public static boolean isRun;
    public final static String BROADCAST_ACTION = "com.chat_client.service";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        board = (TextView) findViewById(R.id.boardChatTextView);
        messageField = (EditText) findViewById(R.id.editTextMessage);
        boardScrollView = (ScrollView) findViewById(R.id.boardScrollView);
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

        Button sendButton = (Button) findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        boardScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                boardScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
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
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(ChatService.BROADCAST_ACTION);
        intent.putExtra(IntentExtraStrings.PAUSE, false);
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(ChatService.BROADCAST_ACTION);
        intent.putExtra(IntentExtraStrings.PAUSE, true);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isRun = false;
        stopService(new Intent(this, ChatService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
        stopService(new Intent(this, ChatService.class));
        unregisterReceiver(broadcastReceiver);
    }
}
