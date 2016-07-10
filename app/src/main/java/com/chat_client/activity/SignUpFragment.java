package com.chat_client.activity;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.chat_client.R;
import com.chat_client.service.DatabaseService;
import com.chat_client.util.entity.IntentExtraStrings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpFragment extends Fragment {
    @BindView(R.id.createLoginText)
    protected EditText loginText;
    @BindView(R.id.createPasswordText)
    protected EditText passwordText;
    @BindView(R.id.createPasswordRepeatText)
    protected EditText passwordRepeatText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sing_up_main, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.signUpButton)
    protected void signUp() {
        final String login = loginText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();
        String passwordRepeat = passwordRepeatText.getText().toString().trim();

        if (isInputValid(login, password, passwordRepeat)) {
            Toast.makeText(getActivity(), "Invalid input data",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordRepeat)) {
            Toast.makeText(getActivity(), "Passwords don't match",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = getSignUpIntent();
            getActivity().startService(intent);
        }
    }

    private boolean isInputValid(String login, String password, String passwordRepeat) {
        return login.equals("") || password.equals("") || passwordRepeat.equals("");
    }

    @NonNull
    private Intent getSignUpIntent() {
        Intent intent = new Intent(getActivity(), DatabaseService.class);
        intent.putExtra(IntentExtraStrings.LOGIN, loginText.getText().toString().trim());
        intent.putExtra(IntentExtraStrings.PASSWORD, passwordText.getText().toString().trim());
        PendingIntent registerIntent = getActivity().createPendingResult(0, new Intent(), 0);
        intent.putExtra(IntentExtraStrings.REGISTER, registerIntent);
        return intent;
    }
}
