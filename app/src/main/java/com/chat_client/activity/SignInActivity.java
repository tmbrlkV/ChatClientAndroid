package com.chat_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chat_client.R;

public class SignInActivity extends Activity {

    private EditText loginText;
    private EditText passwordText;
    private TextView board;
    private StringBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_main);

        loginText = (EditText) findViewById(R.id.loginEditText);
        passwordText = (EditText) findViewById(R.id.passwordEditText);
        board = (TextView) findViewById(R.id.boardChatTextView);

        signIn();
        signUp();
    }

    private void signUp() {
        Button signUpButton = (Button) findViewById(R.id.signUpMainLayoutButton);
        assert signUpButton != null;
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }

    private void signIn() {
        Button signInButton = (Button) findViewById(R.id.signInMainLayoutButton);

        assert signInButton != null;
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInvalidData()) {
                    Toast.makeText(SignInActivity.this, "Invalid input data",
                            Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(SignInActivity.this, ChatActivity.class));
                }
            }

            private boolean isInvalidData() {
                return passwordText == null || loginText == null || loginText.getText().length() == 0
                        || passwordText.getText().length() == 0;
            }
        });
    }
}
