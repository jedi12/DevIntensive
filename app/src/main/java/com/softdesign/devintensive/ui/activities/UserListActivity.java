package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.data.storage.tasks.LoadUserListFromDbTask;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.ui.fragments.RetainFragment;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedImageTransformation;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKSdk;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListActivity extends BaseActivity {

    private static final String TAG = ConstantManager.TAG_PREFIX + "UserListActivity";

    private static final String TAG_RETAIN_FRAGMENT = "retain_fragment";
    private RetainFragment mRetainFragment;

    private final ChronosConnector mChronosConnector = new ChronosConnector();

    private ImageView drawerUsrAvatar;
    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private List<User> mUsers;
    private MenuItem mSearchItem;
    private int mCurrTask;
    private String mSortCriteria;

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
        mChronosConnector.onCreate(this, savedInstanceState);

        mRetainFragment = (RetainFragment) getSupportFragmentManager().findFragmentByTag(TAG_RETAIN_FRAGMENT);
        if (mRetainFragment == null) {
            mRetainFragment = new RetainFragment();
            getSupportFragmentManager().beginTransaction().add(mRetainFragment, TAG_RETAIN_FRAGMENT).commit();
        }

        mDataManager = DataManager.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPosition = viewHolder.getAdapterPosition();
                final int toPosition = target.getAdapterPosition();

                mUsers.add(toPosition, mUsers.remove(fromPosition));
                mUsersAdapter.notifyItemMoved(fromPosition, toPosition);

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mUsers.remove(position);
                mUsersAdapter.notifyDataSetChanged();
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        setupToolBar();
        setupDrawer();

        mSortCriteria = mDataManager.getPreferencesManager().getSortCriteria();
        if (savedInstanceState == null) {
            mCurrTask = mChronosConnector.runOperation(new LoadUserListFromDbTask(null, mSortCriteria), false);
        } else {
            mUsers = mRetainFragment.getUsersList();
            showUsers(mUsers);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChronosConnector.onResume();
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        mChronosConnector.onSaveInstanceState(outState);
        mRetainFragment.setUsersList(mUsers);
        mDataManager.saveUserListOrderInDb(mUsers);
        if (mSortCriteria == null || mSortCriteria.equals("")) {
            mDataManager.getPreferencesManager().saveSortCriteria(LoadUserListFromDbTask.NO_SORT);
        } else {
            mDataManager.getPreferencesManager().saveSortCriteria(mSortCriteria);
        }
    }

    @Override
    protected void onPause() {
        mChronosConnector.onPause();
        super.onPause();
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
                .fit()
                .centerCrop()
                .transform(new RoundedImageTransformation())
                .placeholder(R.drawable.avatar_bg)
                .into(drawerUsrAvatar);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        mSearchItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setQueryHint("Введите имя пользователя");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showUserByQuery(newText);

                return true;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    private void showUserByQuery(String query) {
        if (mChronosConnector.isOperationRunning(mCurrTask)) {
            mChronosConnector.cancelOperation(mCurrTask, true);
        }
        mCurrTask = mChronosConnector.runOperation(new LoadUserListFromDbTask(query, LoadUserListFromDbTask.SORT_BY_NAME), false);
    }

    private void showUsers(List<User> users) {
        mUsers = users;
        mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int position) {
                UserDTO userDTO = new UserDTO(mUsers.get(position));
                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);

                startActivity(profileIntent);
            }
        });
        mRecyclerView.swapAdapter(mUsersAdapter, false);

    }

    public void onOperationFinished(final LoadUserListFromDbTask.Result result) {
        if (result.isSuccessful()) {
            mUsers = result.getOutput();

            if (mUsers.isEmpty()) {
                showSnackbar("Список пользователей пустой");
                return;
            }
            showUsers(result.getOutput());
        } else {
            showSnackbar(result.getErrorMessage());
        }
    }
}
