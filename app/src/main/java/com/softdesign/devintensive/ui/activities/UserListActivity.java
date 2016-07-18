package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.ui.fragments.RetainFragment;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.softdesign.devintensive.utils.RoundedImageTransformation;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "UserListActivity";

    private static final String TAG_RETAIN_FRAGMENT = "retain_fragment";
    private RetainFragment mRetainFragment;

    private ImageView drawerUsrAvatar;
    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;

    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.user_list) RecyclerView mRecyclerView;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        mRetainFragment = (RetainFragment) getSupportFragmentManager().findFragmentByTag(TAG_RETAIN_FRAGMENT);
        if (mRetainFragment == null) {
            mRetainFragment = new RetainFragment();
            getSupportFragmentManager().beginTransaction().add(mRetainFragment, TAG_RETAIN_FRAGMENT).commit();
        }

        mDataManager = DataManager.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        setupToolBar();
        setupDrawer();

        if (savedInstanceState == null) {
            loadUsersListAndSetupAdapter();
        } else {
            setupUsersListAdapter(mRetainFragment.getUsersList());
        }
    }

    private void setupToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<UserListRes.UserData> filteredModelList = filter(mRetainFragment.getUsersList(), newText);
        mUsersAdapter.setFilter(filteredModelList);

        return false;
    }

    private List<UserListRes.UserData> filter(List<UserListRes.UserData> models, String query) {
        query = query.toLowerCase();

        final List<UserListRes.UserData> filteredModelList = new ArrayList<>();
        for (UserListRes.UserData model : models) {
            final String text = model.getFullName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupDrawer() {
        drawerUsrAvatar = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_avatar_img);
        TextView drawerUserFullName = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_user_name_txt);
        TextView drawerUserEmail = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_user_email_txt);

        drawerUserFullName.setText(mDataManager.getPreferencesManager().getUserFullName());
        drawerUserEmail.setText(mDataManager.getPreferencesManager().getUserEmail());

        insertDrawerAvatar(mDataManager.getPreferencesManager().loadUserAvatar());

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                if (item.getItemId() == R.id.user_profile_menu) {
                    Intent authIntent = new Intent(UserListActivity.this, MainActivity.class) ;
                    finish();
                    startActivity(authIntent);
                }

                if (item.getItemId() == R.id.team_menu) {

                }

                if (item.getItemId() == R.id.login_menu) {
                    Intent authIntent = new Intent(UserListActivity.this, AuthActivity.class) ;
                    finish();
                    startActivity(authIntent);
                }

                if (item.getItemId() == R.id.login_vk_menu) {
                    VKSdk.login(UserListActivity.this, null);
                }

                return false;
            }
        });
    }

    private void insertDrawerAvatar(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .resize(getResources().getDimensionPixelSize(R.dimen.drawer_header_avatar_size),
                        getResources().getDimensionPixelSize(R.dimen.drawer_header_avatar_size))
                .centerCrop()
                .transform(new RoundedImageTransformation())
                .placeholder(R.drawable.avatar_bg)
                .into(drawerUsrAvatar);
    }

    private void loadUsersListAndSetupAdapter() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {

            showProgress();

            Call<UserListRes> call = mDataManager.getUserList();
            call.enqueue(new Callback<UserListRes>() {
                @Override
                public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                    if (response.code() == 200) {
                        mRetainFragment.setUsersList(response.body().getData());
                        setupUsersListAdapter(mRetainFragment.getUsersList());
                    } else {
                        showSnackbar("Не удалось получить данные с сервера: " + response.code());
                    }

                    hideProgress();
                }

                @Override
                public void onFailure(Call<UserListRes> call, Throwable t) {
                    showSnackbar("Ошибка: " + t.getMessage());

                    hideProgress();
                }
            });
        } else {
            showSnackbar("Сеть на данный момент недоступна, попробуйте позже");
        }
    }

    private void setupUsersListAdapter(ArrayList<UserListRes.UserData> users) {
        mUsersAdapter = new UsersAdapter(users, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int adapterPosition) {
                UserDTO userDTO = new UserDTO(mUsersAdapter.getUser(adapterPosition));
                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);

                startActivity(profileIntent);
            }
        });
        mRecyclerView.setAdapter(mUsersAdapter);
    }
}
