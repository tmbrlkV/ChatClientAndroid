package com.chat_client.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chat_client.R;
import com.chat_client.auth.AuthorisationController;
import com.chat_client.auth.ConnectionConfig;

import org.zeromq.ZMQ;

public class SignUpActivity extends Activity {

    private EditText loginField;
    private EditText passwordRepeatField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_up_main);

        Button backButton = (Button) findViewById(R.id.backToSignInButton);
        loginField = (EditText) findViewById(R.id.loginSignUp);
        passwordField = (EditText) findViewById(R.id.passwordSignUp);
        passwordRepeatField = (EditText) findViewById(R.id.passwordSignUpRepeat);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });

        Button signUpButton = (Button) findViewById(R.id.signUpPageButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String login = loginField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();
                String passwordRepeat = passwordRepeatField.getText().toString().trim();

                if (login.equals("") || password.equals("") || passwordRepeat.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Invalid input data",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (!password.equals(passwordRepeat)) {
                        Toast.makeText(SignUpActivity.this, "Passwords don't match",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(new Runnable() {
                            @TargetApi(Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                try (ZMQ.Context context = ZMQ.context(1)) {
                                    ConnectionConfig config = new ConnectionConfig(context);
                                    SignUpController signUpController = new SignUpController(config.getDatabaseRequester());
                                    if (signUpController.register(login, password)) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SignUpActivity.this, "You've been registered",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SignUpActivity.this, "Registration failed",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }


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
