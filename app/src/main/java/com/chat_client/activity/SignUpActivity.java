package com.chat_client.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chat_client.R;
import com.chat_client.auth.ConnectionConfig;
import com.chat_client.registration.SignUpController;

import org.zeromq.ZMQ;

public class SignUpActivity extends Activity {
    private static final String LOGIN_TEXT = "loginTextSignUp";
    private static final String PASSWORD_TEXT = "passwordTextSignUp";
    private static final String PASSWORD_REPEAT_TEXT = "passwordRepeatTextSignUp";
    private EditText loginText;
    private EditText passwordRepeatText;
    private EditText passwordText;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_up_main);
        preferences  = PreferenceManager.getDefaultSharedPreferences(this);
        Button backButton = (Button) findViewById(R.id.backToSignInButton);
        loginText = (EditText) findViewById(R.id.loginSignUp);
        passwordText = (EditText) findViewById(R.id.passwordSignUp);
        passwordRepeatText = (EditText) findViewById(R.id.passwordSignUpRepeat);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Button signUpButton = (Button) findViewById(R.id.signUpPageButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String login = loginText.getText().toString().trim();
                final String password = passwordText.getText().toString().trim();
                String passwordRepeat = passwordRepeatText.getText().toString().trim();

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
                                    SignUpController signUpController =
                                            new SignUpController(config.getDatabaseRequester());
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
        loadPreferences();
    }

    private void loadPreferences() {
        loginText.setText(preferences.getString(LOGIN_TEXT, ""));
        passwordText.setText(preferences.getString(PASSWORD_TEXT, ""));
        passwordRepeatText.setText(preferences.getString(PASSWORD_REPEAT_TEXT, ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_TEXT, loginText.getText().toString());
        editor.putString(PASSWORD_TEXT, passwordText.getText().toString());
        editor.putString(PASSWORD_REPEAT_TEXT, passwordRepeatText.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.edit().clear().apply();
    }

}
