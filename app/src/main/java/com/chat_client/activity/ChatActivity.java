package com.chat_client.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chat_client.R;
import com.chat_client.auth.ConnectionConfig;
import com.chat_client.util.StringCleaner;

import org.zeromq.ZMQ;

public class ChatActivity extends Activity {
    private TextView board;
    private EditText messageField;
    private StringBuffer sendMessageBuffer = new StringBuffer();
    private StringBuffer receiveMessageBuffer = new StringBuffer(0);
    private ScrollView boardScrollView;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        board = (TextView) findViewById(R.id.boardChatTextView);
        messageField = (EditText) findViewById(R.id.editTextMessage);
        boardScrollView = (ScrollView) findViewById(R.id.boardScrollView);
        Button sendButton = (Button) findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageBuffer.append(StringCleaner.messageTrim(messageField.getText().toString()));
                messageField.setText("");
            }

        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try (ZMQ.Context context = ZMQ.context(1)) {
                    ConnectionConfig config = new ConnectionConfig(context);

                    ZMQ.Socket sender = config.getSender();
                    String login = getIntent().getStringExtra("login");
                    sender.send(login + " has joined");

                    Thread send = startSenderThread(login, config);
                    Thread receive = startReceiverThread(config);

                    send.join();
                    receive.join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Thread startSenderThread(final String login, final ConnectionConfig config) {
        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                ZMQ.Socket sender = config.getSender();
                while (!Thread.currentThread().isInterrupted()) {
                    if (sendMessageBuffer.length() != 0) {
                        sender.send(login + ": " + sendMessageBuffer);
                        sendMessageBuffer.setLength(0);
                    }
                }
            }
        });
        send.start();
        return send;
    }

    private Thread startReceiverThread(final ConnectionConfig config) {
        Thread receive = new Thread(new Runnable() {

            @Override
            public void run() {
                ZMQ.Socket receiver = config.getReceiver();
                ZMQ.Poller poller = config.getPoller();
                while (!Thread.currentThread().isInterrupted()) {
                    int events = poller.poll();
                    if (events > 0) {
                        String message = receiver.recvStr(0);
                        if (receiveMessageBuffer.length() == 0) {
                            receiveMessageBuffer.append(board.getText());
                        }
                        receiveMessageBuffer.append("\n").append(message);
                        receive();
                    }
                }
            }
        });
        receive.start();
        return receive;
    }

    private void receive() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                board.setText(receiveMessageBuffer);
                boardScrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                scrollTo(0, boardScrollView.getMaxScrollAmount());

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
