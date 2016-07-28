package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserLikeRes {

    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("data")
    @Expose
    public Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        @SerializedName("homeTask")
        @Expose
        public int homeTask;
        @SerializedName("projects")
        @Expose
        public int projects;
        @SerializedName("linesCode")
        @Expose
        public int linesCode;
        @SerializedName("likesBy")
        @Expose
        public List<String> likesBy = new ArrayList<String>();
        @SerializedName("rait")
        @Expose
        public int rait;
        @SerializedName("updated")
        @Expose
        public String updated;
        @SerializedName("rating")
        @Expose
        public int rating;

    }
}

