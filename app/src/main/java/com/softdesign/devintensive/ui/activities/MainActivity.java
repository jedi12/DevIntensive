package com.softdesign.devintensive.ui.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.ImageHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";

    private DataManager mDataManager;
    private int mCurrentEditMode = 0;

    private ImageView mCallImg;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDraver;
    private NavigationView mNavigationView;
    private FloatingActionButton mFab;
    private EditText mUserPhone;
    private EditText mUserMail;
    private EditText mUserVk;
    private EditText mUserGit;
    private EditText mUserBio;

    private List<EditText> mUserInfoViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate");

        mDataManager = DataManager.getInstance();

        mCallImg = (ImageView) findViewById(R.id.call_img);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDraver = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mUserPhone = (EditText) findViewById(R.id.phone_edit);
        mUserMail = (EditText) findViewById(R.id.email_edit);
        mUserVk = (EditText) findViewById(R.id.vk_profile_edit);
        mUserGit = (EditText) findViewById(R.id.repo_edit);
        mUserBio = (EditText) findViewById(R.id.about_edit);

        ImageView imageView = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_avatar);
        Bitmap bitmap = ImageHelper.getRoundedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
        bitmap = ImageHelper.getRoundedBitmap(bitmap);
        imageView.setImageBitmap(bitmap);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        mFab.setOnClickListener(this);

        setToolBar();
        setupDrawer();
        loadUserInfoValue();

        if (savedInstanceState == null) {

        } else {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDraver.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDraver.isDrawerOpen(GravityCompat.START)) {
            mNavigationDraver.closeDrawer(GravityCompat.START);
        } else if (mCurrentEditMode == 1) {
            changeEditMode(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        loadUserInfoValue();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserInfoValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
        changeEditMode(mCurrentEditMode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                } else {
                    changeEditMode(0);
                }
                break;
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDraver.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * переключает режим редактирования
     *
     * @param mode если 1 - режим редактирования, если 0 - режим просмотра
     */
    private void changeEditMode(int mode) {
        if (mode == 1) {
            mCurrentEditMode = 1;
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
        } else {
            mCurrentEditMode = 0;
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                saveUserInfoValue();
            }
        }
    }

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }


}
