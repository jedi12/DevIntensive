package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + "AuthActivity";

    private DataManager mDataManager;

    @BindView(R.id.auth_login) EditText mAuthLogin;
    @BindView(R.id.auth_pass) EditText mAuthPass;
    @BindView(R.id.auth_login_til) TextInputLayout mAuthLoginTil;
    @BindView(R.id.auth_pass_till) TextInputLayout mAuthPassTil;
    @BindView(R.id.auth_login_btn) Button mAuthLoginBtn;
    @BindView(R.id.aurh_remember_txt) TextView mRememberPass;
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.aurh_remember_txt)
    protected void rememberPass() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    @OnClick(R.id.auth_login_btn)
    protected void signIn() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mAuthLogin.getText().toString(), mAuthPass.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body());
                    } else if (response.code() == 404) {
                        showSnackbar("Неверный логин или пароль");
                    } else {
                        showSnackbar("Всё пропало Шеф!!!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    showSnackbar("Ошибка: " + t.getMessage());
                }
            });
        } else {
            showSnackbar("Сеть на данный момент недоступна, попробуйте позже");
        }
    }

    protected void loginSuccess(UserModelRes userModel) {
        showSnackbar(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto()));
        mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar()));
        mDataManager.getPreferencesManager().saveUserFullName(userModel.getData().getUser().getFirstName() + " " + userModel.getData().getUser().getSecondName());
        saveUserValues(userModel);
        saveUserData(userModel);

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void saveUserValues(UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRaiting(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };

        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserData(UserModelRes userModel) {
        List<String> userData = new ArrayList<>();
        userData.add(userModel.getData().getUser().getContacts().getPhone());
        userData.add(userModel.getData().getUser().getContacts().getEmail());
        userData.add(userModel.getData().getUser().getContacts().getVk());
        userData.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        userData.add(userModel.getData().getUser().getPublicInfo().getBio());

        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }
}
