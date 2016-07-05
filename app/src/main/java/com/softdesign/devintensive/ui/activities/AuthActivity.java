package com.softdesign.devintensive.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + "AuthActivity";

    @BindView(R.id.auth_login) EditText mAuthLogin;
    @BindView(R.id.auth_pass) EditText mAuthPass;
    @BindView(R.id.auth_login_til) TextInputLayout mAuthLoginTil;
    @BindView(R.id.auth_pass_till) TextInputLayout mAuthPassTil;
    @BindView(R.id.auth_login_btn) Button mAuthLoginBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);
        ButterKnife.bind(this);
    }
}
