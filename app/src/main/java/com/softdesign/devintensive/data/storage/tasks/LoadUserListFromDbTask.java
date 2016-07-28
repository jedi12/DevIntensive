package com.softdesign.devintensive.data.storage.tasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;

import java.util.ArrayList;
import java.util.List;

public class LoadUserListFromDbTask extends ChronosOperation<List<User>> {
    public static final String NO_SORT = "NO_SORT";
    public static final String SORT_BY_NAME = "SORT_BY_NAME";
    public static final String SORT_BY_USERS_LIKED_ME = "SORT_BY_USERS_LIKED_ME";

    private String mCriteria;
    private String mQuery;

//    public LoadUserListFromDbTask() {
//
//    }

    public LoadUserListFromDbTask(String query, String criteria) {
        mQuery = query;
        mCriteria = criteria;
    }

    @Nullable
    @Override
    public List<User> run() {
        final List<User> result ;

        if (mCriteria == null || mCriteria.equals("")) {
            result = DataManager.getInstance().getAllUserListOrderedByRatingFromDb();
        } else {
            switch (mCriteria) {
                case NO_SORT:
                    result = DataManager.getInstance().getUserListByUserOrderedFromDb();
                    break;

                case SORT_BY_NAME:
                    result = DataManager.getInstance().getUserListSortedByNameFromDb(mQuery);
                    break;

//                case SORT_BY_USERS_LIKED_ME:
//                    result = DataManager.getInstance().getUserListSortedByUsersLikedMe();
//                    break;

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
