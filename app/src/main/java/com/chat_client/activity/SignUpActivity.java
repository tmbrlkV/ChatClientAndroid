package com.chat_client.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chat_client.R;
import com.chat_client.service.DatabaseService;
import com.chat_client.util.IntentExtraStrings;

public class SignUpActivity extends Activity {
    private static final String LOGIN_TEXT = "loginTextSignUp";
    private static final String PASSWORD_TEXT = "passwordTextSignUp";
    private static final String PASSWORD_REPEAT_TEXT = "passwordRepeatTextSignUp";
    private EditText loginText;
    private EditText passwordRepeatText;
    private EditText passwordText;
    private SharedPreferences preferences;
    private PendingIntent registerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_up_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginText = (EditText) findViewById(R.id.loginSignUp);
        passwordText = (EditText) findViewById(R.id.passwordSignUp);
        passwordRepeatText = (EditText) findViewById(R.id.passwordSignUpRepeat);

        Button backButton = (Button) findViewById(R.id.backToSignInButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button signUpButton = (Button) findViewById(R.id.signUpPageButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String login = loginText.getText().toString().trim();
                final String password = passwordText.getText().toString().trim();
                String passwordRepeat = passwordRepeatText.getText().toString().trim();

                if (isValidInput(login, password, passwordRepeat)) {
                    Toast.makeText(SignUpActivity.this, "Invalid input data",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(passwordRepeat)) {
                    Toast.makeText(SignUpActivity.this, "Passwords don't match",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = getIntent();
                    startService(intent);
                }

            }

            @NonNull
            private Intent getIntent() {
                Intent intent = new Intent(SignUpActivity.this, DatabaseService.class);
                intent.putExtra(IntentExtraStrings.LOGIN, loginText.getText().toString().trim());
                intent.putExtra(IntentExtraStrings.PASSWORD, passwordText.getText().toString().trim());
                registerIntent = createPendingResult(0, new Intent(), 0);
                intent.putExtra(IntentExtraStrings.REGISTER, registerIntent);
                return intent;
            }

            private boolean isValidInput(String login, String password, String passwordRepeat) {
                return login.equals("") || password.equals("") || passwordRepeat.equals("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean signedUp = data.getBooleanExtra(IntentExtraStrings.VALID, false);
        if (signedUp) {
            startActivity(new Intent(this, SignInActivity.class));
            Toast.makeText(SignUpActivity.this, "Registration success",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SignUpActivity.this, "Registration failed",
                    Toast.LENGTH_SHORT).show();
        }
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
