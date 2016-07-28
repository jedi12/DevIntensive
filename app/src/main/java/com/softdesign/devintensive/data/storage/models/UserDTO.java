package com.softdesign.devintensive.data.storage.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;

import java.util.ArrayList;
import java.util.List;

public class UserDTO implements Parcelable {

    private String mPhoto;
    private String mFullName;
    private String mRating;
    private String mCodeLines;
    private String mProjects;
    private String mBio;
    private String mUserId;
    private boolean mLiked;
    private List<String> mRepositories;

    public UserDTO(User userData, String myUserRemoteId) {
        List<String> repoLink = new ArrayList<>();

        mPhoto = userData.getPhoto();
        mFullName = userData.getFullName();
        mRating = String.valueOf(userData.getRating());
        mCodeLines = String.valueOf(userData.getCodeLines());
        mProjects = String.valueOf(userData.getProjects());
        mBio = userData.getBio();
        mUserId = userData.getRemoteId();

        for (Repository gitLink : userData.getRepositories()) {
            repoLink.add(gitLink.getRepositoryName());
        }

        mRepositories = repoLink;

        for (LikeList likeList : userData.getLikesBy()) {
            if (likeList.getUserLikeRemoteId().equals(myUserRemoteId)) {
                mLiked = true;
            }
        }
    }

    protected UserDTO(Parcel in) {
        mPhoto = in.readString();
        mFullName = in.readString();
        mRating = in.readString();
        mCodeLines = in.readString();
        mProjects = in.readString();
        mBio = in.readString();
        mUserId = in.readString();
        mLiked = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            mRepositories = new ArrayList<String>();
            in.readList(mRepositories, String.class.getClassLoader());
        } else {
            mRepositories = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPhoto);
        dest.writeString(mFullName);
        dest.writeString(mRating);
        dest.writeString(mCodeLines);
        dest.writeString(mProjects);
        dest.writeString(mBio);
        dest.writeString(mUserId);
        dest.writeByte((byte) (mLiked ? 0x01 : 0x00));
        if (mRepositories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mRepositories);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserDTO> CREATOR = new Parcelable.Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    public String getPhoto() {
        return mPhoto;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getRating() {
        return mRating;
    }

    public String getCodeLines() {
        return mCodeLines;
    }

    public String getProjects() {
        return mProjects;
    }

    public String getBio() {
        return mBio;
    }

    public String getUserId() {
        return mUserId;
    }

    public boolean isLiked() {
        return mLiked;
    }

    public List<String> getRepositories() {
        return mRepositories;
    }
}
