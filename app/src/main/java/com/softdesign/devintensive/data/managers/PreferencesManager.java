package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.widget.EditText;

import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferencesManager {
    private SharedPreferences mSharedPreferences;

    private static final String[] USER_FIELDS = {ConstantManager.EDIT_PHONE_KEY, ConstantManager.EDIT_MAIL_KEY,
            ConstantManager.EDIT_VK_KEY, ConstantManager.EDIT_GIT_KEY, ConstantManager.EDIT_BIO_KEY};

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
}
