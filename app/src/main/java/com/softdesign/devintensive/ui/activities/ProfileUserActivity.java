package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileUserActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.user_photo_img) ImageView mProfileImage;
    @BindView(R.id.about_edit) EditText mUserBio;
    @BindView(R.id.user_info_rait_txt) TextView mUserRating;
    @BindView(R.id.user_info_code_lines_txt) TextView mUserCodeLines;
    @BindView(R.id.user_info_projects_txt) TextView mUserProjects;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.repositories_list) ListView mRepoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        ButterKnife.bind(this);

        setupToolBar();
        initProfileData();
    }

    private void setupToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initProfileData() {
        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final List<String> repositories = userDTO.getRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repositories);
        mRepoListView.setAdapter(repositoriesAdapter);
        mRepoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri repoUri = Uri.parse("https://" + repositories.get(position));
                Intent viewRepoIntent = new Intent(Intent.ACTION_VIEW, repoUri);
                startActivity(viewRepoIntent);
            }
        });

        mUserBio.setText(userDTO.getBio());
        mUserRating.setText(userDTO.getRating());
        mUserCodeLines.setText(userDTO.getCodeLines());
        mUserProjects.setText(userDTO.getProjects());

        mCollapsingToolbar.setTitle(userDTO.getFullName());

        String userPhoto = userDTO.getPhoto();
        if (userPhoto.trim().equals("")) {
            userPhoto = null;
        }

        Picasso.with(this)
                .load(userPhoto)
                .placeholder(R.drawable.user_bg)
                .error(R.drawable.user_bg)
                .into(mProfileImage);
    }
}
