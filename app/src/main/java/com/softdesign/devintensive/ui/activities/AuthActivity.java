package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.ext.MessageEvent;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    private static final String NETWORK_NOT_AVAILABLE = "NETWORK_NOT_AVAILABLE";
    private static final String USERLIST_LOADED_AND_SAVED = "USERLIST_LOADED_AND_SAVED";
    private static final String USER_NOT_AUTHORIZED = "USER_NOT_AUTHORIZED";
    private static final String RESPONSE_NOT_OK = "RESPONSE_NOT_OK";
    private static final String SERVER_ERROR = "SERVER_ERROR";
    private static final String LOGIN_OR_PASSWORD_INCORRECT = "LOGIN_OR_PASSWORD_INCORRECT";

    private DataManager mDataManager;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;

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
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();

        showSplash();
        loadUserListFromServerAndSaveInDbOnBackground();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        hideSplash();
        hideProgress();

        switch (event.message) {
            case NETWORK_NOT_AVAILABLE:
                startUserMainActivity();

                break;

            case USERLIST_LOADED_AND_SAVED:
                startUserMainActivity();

                break;

            case USER_NOT_AUTHORIZED:
                showSnackbar("Необходима авторизация");

                break;

            case LOGIN_OR_PASSWORD_INCORRECT:
                showSnackbar("Неверный логин или пароль");

                break;

            case RESPONSE_NOT_OK:
                startUserMainActivity();

                break;

            case SERVER_ERROR:
                startUserMainActivity();

                break;
        }
    }

    private void startUserMainActivity() {
        Intent loginIntent = new Intent(AuthActivity.this, MainActivity.class);
        finish();
        startActivity(loginIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
        showProgress();

        if (!NetworkStatusChecker.isNetworkAvailable(AuthActivity.this)) {
            EventBus.getDefault().post(new MessageEvent(NETWORK_NOT_AVAILABLE));
            return;
        }

        Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mAuthLogin.getText().toString(), mAuthPass.getText().toString()));
        call.enqueue(new Callback<UserModelRes>() {
            @Override
            public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                if (response.code() == 200) {
                    loginSuccess(response.body());
                    //EventBus.getDefault().post(new MessageEvent(USERLIST_LOADED_AND_SAVED));
                } else if (response.code() == 404) {
                    EventBus.getDefault().post(new MessageEvent(LOGIN_OR_PASSWORD_INCORRECT));
                } else {
                    Log.e(TAG, "Network error: " + response.message());
                    EventBus.getDefault().post(new MessageEvent(RESPONSE_NOT_OK));
                }
            }

            @Override
            public void onFailure(Call<UserModelRes> call, Throwable t) {
                Log.e(TAG, "Network failure: " + t.getMessage());
                EventBus.getDefault().post(new MessageEvent(SERVER_ERROR));
            }
        });
    }

    protected void loginSuccess(UserModelRes userModel) {
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto()));
        mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar()));
        mDataManager.getPreferencesManager().saveUserFullName(userModel.getData().getUser().getFirstName() + " " + userModel.getData().getUser().getSecondName());

        saveUserValues(userModel);
        saveUserData(userModel);

        loadUserListFromServerAndSaveInDbOnBackground();
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

    private void loadUserListFromServerAndSaveInDbOnBackground() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadUserListFromServerSaveInDb();
            }
        }, AppConfig.START_DELAY);
    }

    private void loadUserListFromServerSaveInDb() {
        if (!NetworkStatusChecker.isNetworkAvailable(AuthActivity.this)) {
            EventBus.getDefault().post(new MessageEvent(NETWORK_NOT_AVAILABLE));
            return;
        }

        Call<UserListRes> call = mDataManager.getUserListFromNetwork();
        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                try {
                    if (response.code() == 200) {

                        List<Repository> allRepositories = new ArrayList<>();
                        List<User> allUsers = new ArrayList<>();

                        for (UserListRes.UserData userRes : response.body().getData()) {
                            allRepositories.addAll(getRepoListFromUserRes(userRes));
                            allUsers.add(new User(userRes));
                        }

                        mRepositoryDao.insertOrReplaceInTx(allRepositories);
                        mUserDao.insertOrReplaceInTx(allUsers);

                        EventBus.getDefault().post(new MessageEvent(USERLIST_LOADED_AND_SAVED));

                    } else if (response.code() == 401) {
                        EventBus.getDefault().post(new MessageEvent(USER_NOT_AUTHORIZED));

                    } else {
                        Log.e(TAG, "Network error: " + response.message());
                        EventBus.getDefault().post(new MessageEvent(RESPONSE_NOT_OK));
                    }
                } catch (NullPointerException e){
                    e.printStackTrace();
                    showSnackbar("Что-то пошло не так");
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                Log.e(TAG, "Network failure: " + t.getMessage());
                EventBus.getDefault().post(new MessageEvent(SERVER_ERROR));
            }
        });
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();

        List<Repository> repositories = new ArrayList<>();
        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }

        return repositories;
    }
}
