package com.chat_client.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chat_client.R;
import com.chat_client.service.DatabaseService;
import com.chat_client.util.entity.IntentExtraStrings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends Activity {
    @BindView(R.id.loginSignUpEditText)
    protected EditText loginText;
    @BindView(R.id.passwordSignUpEditText)
    protected EditText passwordText;
    @BindView(R.id.passwordSignUpEditTextRepeat)
    protected EditText passwordRepeatText;

    @BindView(R.id.signUpPageButton)
    protected Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_up_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick(R.id.signUpPageButton)
    protected void signUp() {
        final String login = loginText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();
        String passwordRepeat = passwordRepeatText.getText().toString().trim();

        if (isInputValid(login, password, passwordRepeat)) {
            Toast.makeText(SignUpActivity.this, "Invalid input data",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordRepeat)) {
            Toast.makeText(SignUpActivity.this, "Passwords don't match",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = getSignUpIntent();
            startService(intent);
        }
    }

    private boolean isInputValid(String login, String password, String passwordRepeat) {
        return login.equals("") || password.equals("") || passwordRepeat.equals("");
    }

    @NonNull
    private Intent getSignUpIntent() {
        Intent intent = new Intent(SignUpActivity.this, DatabaseService.class);
        intent.putExtra(IntentExtraStrings.LOGIN, loginText.getText().toString().trim());
        intent.putExtra(IntentExtraStrings.PASSWORD, passwordText.getText().toString().trim());
        PendingIntent registerIntent = createPendingResult(0, new Intent(), 0);
        intent.putExtra(IntentExtraStrings.REGISTER, registerIntent);
        return intent;
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
}
