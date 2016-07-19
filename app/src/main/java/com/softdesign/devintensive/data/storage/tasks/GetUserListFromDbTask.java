package com.softdesign.devintensive.data.storage.tasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;

import java.util.ArrayList;
import java.util.List;

public class GetUserListFromDbTask extends ChronosOperation<List<User>> {
    public static final String BY_NAME = "BY_NAME";

    private String mCriteria;
    private String mQuery;

    public GetUserListFromDbTask() {

    }

    public GetUserListFromDbTask(String userName, String criteria) {
        mQuery = userName;
        mCriteria = criteria;
    }

    @Nullable
    @Override
    public List<User> run() {
        final List<User> result ;

        if (mCriteria == null) {
            result = DataManager.getInstance().getUserListFromDb();
        } else {
            switch (mCriteria) {
                case BY_NAME:
                    result = DataManager.getInstance().getUserListByName(mQuery);
                    break;

                default:
                    result = new ArrayList<>();
                    break;
            }
        }

        return result;
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<List<User>>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<List<User>> {

    }
}
