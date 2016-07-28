package com.softdesign.devintensive.data.storage.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(active = true, nameInDb = "LIKES")
public class LikeList {

    @Id
    private Long id;

    @NotNull
    private String userRemoteId;

    private String userLikeRemoteId;

    /** Used for active entity operations. */
    @Generated(hash = 698341182)
    private transient LikeListDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public LikeList(String likedBy, String userId) {
        userLikeRemoteId = likedBy;
        userRemoteId = userId;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 811191376)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLikeListDao() : null;
    }

    public String getUserLikeRemoteId() {
        return this.userLikeRemoteId;
    }

    public void setUserLikeRemoteId(String userLikeRemoteId) {
        this.userLikeRemoteId = userLikeRemoteId;
    }

    public String getUserRemoteId() {
        return this.userRemoteId;
    }

    public void setUserRemoteId(String userRemoteId) {
        this.userRemoteId = userRemoteId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 402497153)
    public LikeList(Long id, @NotNull String userRemoteId, String userLikeRemoteId) {
        this.id = id;
        this.userRemoteId = userRemoteId;
        this.userLikeRemoteId = userLikeRemoteId;
    }

    @Generated(hash = 1622007685)
    public LikeList() {
    }
}
