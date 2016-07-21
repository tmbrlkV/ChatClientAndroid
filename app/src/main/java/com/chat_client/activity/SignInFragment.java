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

public class SignInFragment extends Fragment {
    @BindView(R.id.loginText)
    protected EditText loginEditText;
    @BindView(R.id.passwordText)
    protected EditText passwordEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_main, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.goToSignUp)
    protected void toSignUp() {
        getFragmentManager().beginTransaction()
                .replace(R.id.mainFragmentField, new SignUpFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.signInButton)
    protected void signIn() {
        if (isNotValidDataInput()) {
            Toast.makeText(getActivity(), "Invalid input data",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = getSignInIntent();
            getActivity().startService(intent);
        }
    }

    private boolean isNotValidDataInput() {
        return passwordEditText == null || loginEditText == null || loginEditText.getText().length() == 0
                || passwordEditText.getText().length() == 0;
    }

    @NonNull
    private Intent getSignInIntent() {
        Intent intent = new Intent(getActivity(), DatabaseService.class);
        intent.putExtra(IntentExtraStrings.LOGIN, loginEditText.getText().toString().trim());
        intent.putExtra(IntentExtraStrings.PASSWORD, passwordEditText.getText().toString().trim());
        PendingIntent authorizeIntent = getActivity().createPendingResult(0, new Intent(), 0);
        intent.putExtra(IntentExtraStrings.DATABASE_ACTION, authorizeIntent);
        intent.putExtra(IntentExtraStrings.DATABASE_COMMAND, IntentExtraStrings.AUTHORIZE);
        return intent;
    }
}
