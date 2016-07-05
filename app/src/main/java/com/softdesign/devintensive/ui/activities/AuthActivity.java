package com.softdesign.devintensive.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + "AuthActivity";

    EditText mAuthLogin;
    EditText mAuthPass;
    TextInputLayout mAuthLoginTil;
    TextInputLayout mAuthPassTil;
    Button mAuthLoginBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);

        mAuthLogin = (EditText) findViewById(R.id.auth_login);
        mAuthPass = (EditText) findViewById(R.id.auth_pass);
        mAuthLoginTil = (TextInputLayout) findViewById(R.id.auth_login_til);
        mAuthPassTil = (TextInputLayout) findViewById(R.id.auth_pass_till);
        mAuthLoginBtn = (Button) findViewById(R.id.auth_login_btn);
    }
}
