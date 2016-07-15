package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;

import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {
    private SharedPreferences mSharedPreferences;

    private static final String[] USER_FIELDS = {
            ConstantManager.EDIT_PHONE_KEY,
            ConstantManager.EDIT_MAIL_KEY,
            ConstantManager.EDIT_VK_KEY,
            ConstantManager.EDIT_GIT_KEY,
            ConstantManager.EDIT_BIO_KEY};

    private static final String[] USER_VALUES = {
            ConstantManager.USER_RATING_VALUE,
            ConstantManager.USER_CODE_LINES_VALUE,
            ConstantManager.USER_PROJECT_VALUE
    };

    public PreferencesManager() {
        mSharedPreferences = DevintensiveApplication.getSharedPreferences();
    }

    public void saveUserProfileData(List<String> userFields) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i < USER_FIELDS.length; i++) {
            editor.putString(USER_FIELDS[i], userFields.get(i));
        }
        editor.apply();
    }

    public List<String> loadUserProfileData() {
        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.EDIT_PHONE_KEY, ""));
        userFields.add(mSharedPreferences.getString(ConstantManager.EDIT_MAIL_KEY, ""));
        userFields.add(mSharedPreferences.getString(ConstantManager.EDIT_VK_KEY, ""));
        userFields.add(mSharedPreferences.getString(ConstantManager.EDIT_GIT_KEY, ""));
        userFields.add(mSharedPreferences.getString(ConstantManager.EDIT_BIO_KEY, ""));

        return userFields;
    }

    public void saveUserProfileValues(int[] userValues) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i < USER_VALUES.length; i++) {
            editor.putString(USER_VALUES[i], String.valueOf(userValues[i]));
        }
        editor.apply();
    }

    public List<String> loadUserProfileValues() {
        List<String> userValues = new ArrayList<>();
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_RATING_VALUE, "0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_CODE_LINES_VALUE, "0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_PROJECT_VALUE, "0"));

        return userValues;
    }

    public void saveUserPhoto(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    public Uri loadUserPhoto() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,
                "android.resource://com.softdesign.devintensive/drawable/user_bg"));
    }

    public void saveUserAvatar(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_AVATAR_KEY, uri.toString());
        editor.apply();
    }

    public Uri loadUserAvatar() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_AVATAR_KEY,
                "android.resource://com.softdesign.devintensive/drawable/avatar_bg"));
    }

    public void saveAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }

    public String getAuthToken() {
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN_KEY, "");
    }

    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY, userId);
        editor.apply();
    }

    public String getUserId() {
        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY, "");
    }

    public void saveUserFullName(String userFullName) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_FULL_NAME_KEY, userFullName);
        editor.apply();
    }

    public String getUserFullName() {
        return mSharedPreferences.getString(ConstantManager.USER_FULL_NAME_KEY, "");
    }

    public String getUserEmail() {
        return mSharedPreferences.getString(ConstantManager.EDIT_MAIL_KEY, "");
    }
}
