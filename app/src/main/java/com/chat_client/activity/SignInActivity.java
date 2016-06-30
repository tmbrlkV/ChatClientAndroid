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
import com.chat_client.auth.AuthorisationController;
import com.chat_client.auth.ConnectionConfig;

import org.zeromq.ZMQ;

public class SignInActivity extends Activity {
    private static final String LOGIN_TEXT = "loginTextSignIn";
    private static final String PASSWORD_TEXT = "passwordTextSignIn";
    private EditText loginText;
    private EditText passwordText;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginText = (EditText) findViewById(R.id.loginEditText);
        passwordText = (EditText) findViewById(R.id.passwordEditText);

        signIn();
        signUp();
    }

    private void signUp() {
        Button signUpButton = (Button) findViewById(R.id.signUpMainLayoutButton);
        assert signUpButton != null;
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void signIn() {
        Button signInButton = (Button) findViewById(R.id.signInMainLayoutButton);

        assert signInButton != null;
        signInButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (isInvalidData()) {
                    Toast.makeText(SignInActivity.this, "Invalid input data",
                            Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try (ZMQ.Context context = ZMQ.context(1)) {
                                ConnectionConfig config = new ConnectionConfig(context);
                                String login = loginText.getText().toString().trim();
                                String password = passwordText.getText().toString().trim();
                                AuthorisationController authorizationController =
                                        new AuthorisationController(config.getDatabaseRequester());
                                if (authorizationController.authorization(login, password)) {
                                    Intent intent = new Intent(SignInActivity.this, ChatActivity.class);
                                    intent.putExtra("login", login);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    authFailed();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        private void authFailed() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignInActivity.this, "Authentication failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                }
            }

            private boolean isInvalidData() {
                return passwordText == null || loginText == null || loginText.getText().length() == 0
                        || passwordText.getText().length() == 0;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginText.setText(preferences.getString(LOGIN_TEXT, ""));
        passwordText.setText(preferences.getString(PASSWORD_TEXT, ""));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_TEXT, loginText.getText().toString());
        editor.putString(PASSWORD_TEXT, passwordText.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.edit().clear().apply();
    }
}
