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
    private boolean isRun;
    private EditText messageField;
    private StringBuffer sendMessageBuffer = new StringBuffer();
    private StringBuffer receiveMessageBuffer = new StringBuffer(0);
    private ScrollView boardScrollView;
    private BroadcastReceiver broadcastReceiver;
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
                board.setText(receiveMessageBuffer);
            }

        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        Button sendButton = (Button) findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageBuffer.append(StringCleaner.messageTrim(messageField.getText().toString()));
                Intent intent = new Intent(ChatService.BROADCAST_ACTION);
                intent.putExtra(IntentExtraStrings.SEND_MESSAGE, sendMessageBuffer.toString());
                sendMessageBuffer.setLength(0);
                sendBroadcast(intent);

                messageField.setText("");
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
            Intent intent = new Intent(this, ChatService.class);
            intent.putExtra(IntentExtraStrings.LOGIN, getIntent().getStringExtra(IntentExtraStrings.LOGIN));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startService(intent);
            isRun = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
