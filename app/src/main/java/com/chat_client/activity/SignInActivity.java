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
import com.chat_client.util.IntentExtraStrings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends Activity {
    @BindView(R.id.loginEditText)
    protected EditText loginEditText;
    @BindView(R.id.passwordEditText)
    protected EditText passwordEditText;
    @BindView(R.id.signUpMainLayoutButton)
    protected Button signUpMainLayoutButton;
    @BindView(R.id.signInMainLayoutButton)
    protected Button signInMainLayoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.signUpMainLayoutButton)
    protected void startSignUp() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.signInMainLayoutButton)
    protected void signIn() {
        if (isNotValidDataInput()) {
            Toast.makeText(SignInActivity.this, "Invalid input data",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = getSignInIntent();
            startService(intent);
        }
    }

    @NonNull
    private Intent getSignInIntent() {
        Intent intent = new Intent(SignInActivity.this, DatabaseService.class);
        intent.putExtra(IntentExtraStrings.LOGIN, loginEditText.getText().toString().trim());
        intent.putExtra(IntentExtraStrings.PASSWORD, passwordEditText.getText().toString().trim());
        PendingIntent authorizeIntent = createPendingResult(0, new Intent(), 0);
        intent.putExtra(IntentExtraStrings.AUTHORIZE, authorizeIntent);
        return intent;
    }

    private boolean isNotValidDataInput() {
        return passwordEditText == null || loginEditText == null || loginEditText.getText().length() == 0
                || passwordEditText.getText().length() == 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean authorized = data.getBooleanExtra(IntentExtraStrings.VALID, false);
        if (authorized) {
            Intent intent = new Intent(SignInActivity.this, ChatActivity.class);
            intent.putExtra(IntentExtraStrings.LOGIN, loginEditText.getText().toString().trim());
            startActivity(intent);
        } else {
            Toast.makeText(SignInActivity.this, "Authorization failed",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
