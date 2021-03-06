package com.softdesign.devintensive.data.managers;

import android.content.Context;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.data.storage.models.UserOrder;
import com.softdesign.devintensive.data.storage.models.UserOrderDao;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DataManager {
    private static DataManager INSTANCE = null;
    private Picasso mPicasso;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DaoSession mDaoSession;

    public DataManager() {
        mContext = DevintensiveApplication.getContext();
        mPreferencesManager = new PreferencesManager();
        mRestService = ServiceGenerator.createService(RestService.class);
        mPicasso = new PicassoCache(mContext).getPicassoInstance();
        mDaoSession = DevintensiveApplication.getDaoSession();
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }

        return INSTANCE;
    }

    public Context getContext() {
        return mContext;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Picasso getPicasso() {
//        mPicasso.setIndicatorsEnabled(true);
        return mPicasso;
    }

    // region =============== Network ===============

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    public Call<ResponseBody> uploadPhoto(String userId, MultipartBody.Part file) {
        return mRestService.uploadPhoto(userId, file);
    }

    public Call<UserListRes> getUserListFromNetwork() {
        return mRestService.getUserList();
    }

    // endregion

    // region =============== Database ===============


    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public List<User> getAllUserListOrderedByRatingFromDb() {
        List<User> userList = new ArrayList<>();

        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .orderDesc(UserDao.Properties.Rating)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    public List<User> getUserListSortedByNameFromDb(String query) {

        List<User> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.Rating.gt(0),
                            UserDao.Properties.SearchName.like("%" + query.toUpperCase() + "%"))
//                    .orderDesc(UserDao.Properties.Rating)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    public List<User> getUserListByUserOrderedFromDb() {

        List<User> userList = new ArrayList<>();
        try {
            QueryBuilder<User> queryBuilder = mDaoSession.queryBuilder(User.class);
            queryBuilder.join(UserDao.Properties.RemoteId, UserOrder.class, UserOrderDao.Properties.UserRemoteId);
            queryBuilder.where(UserDao.Properties.Rating.gt(0));
            queryBuilder.orderRaw("USER_ORDER ASC");
            queryBuilder.distinct();
            userList = queryBuilder.list();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    public void saveUserListOrderInDb(List<User> userList) {

        try {
            mDaoSession.getUserOrderDao().deleteAll();

            for (int i = 0; i < userList.size(); i++) {
                mDaoSession.getUserOrderDao().insertInTx(new UserOrder(userList.get(i).getRemoteId(), i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // endregion
}
