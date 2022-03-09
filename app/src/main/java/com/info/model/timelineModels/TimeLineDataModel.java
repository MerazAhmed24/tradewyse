
package com.info.model.timelineModels;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.info.model.TipResponse;

public class TimeLineDataModel {

    @SerializedName("activityType")
    @Expose
    private String activityType;
    @SerializedName("followUserName")
    @Expose
    private String followUserName;
    @SerializedName("followUserId")
    @Expose
    private String followUserId;
    @SerializedName("modifiedTime")
    @Expose
    private String modifiedTime;
    @SerializedName("tweetText")
    @Expose
    private String tweetText;
    @SerializedName("tweetCount")
    @Expose
    private Integer tweetCount;
    @SerializedName("tipResponse")
    @Expose
    private TipResponse tipResponse;
    @SerializedName("userFollowing")
    @Expose
    private Boolean userFollowing;

    public String getFollowUserId() {
        return followUserId;
    }

    private Bitmap followUserImageBitmap;

    private boolean alreadyCalledGetImageApi = false;

    private int section;

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public boolean isAlreadyCalledGetImageApi() {
        return alreadyCalledGetImageApi;
    }

    public void setAlreadyCalledGetImageApi(boolean alreadyCalledGetImageApi) {
        this.alreadyCalledGetImageApi = alreadyCalledGetImageApi;
    }

    public Bitmap getFollowUserImageBitmap() {
        return followUserImageBitmap;
    }

    public void setFollowUserImageBitmap(Bitmap followUserImageBitmap) {
        this.followUserImageBitmap = followUserImageBitmap;
    }

    public TimeLineDataModel(String activityType, String modifiedTime, int section) {
        this.activityType = activityType;
        this.modifiedTime = modifiedTime;
        this.section = section;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getFollowUserName() {
        return followUserName;
    }

    public void setFollowUserName(String followUserName) {
        this.followUserName = followUserName;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public Integer getTweetCount() {
        return tweetCount;
    }

    public void setTweetCount(Integer tweetCount) {
        this.tweetCount = tweetCount;
    }

    public TipResponse getTipResponse() {
        return tipResponse;
    }

    public void setTipResponse(TipResponse tipResponse) {
        this.tipResponse = tipResponse;
    }

    public Boolean getUserFollowing() {
        return userFollowing;
    }

    public void setUserFollowing(Boolean userFollowing) {
        this.userFollowing = userFollowing;
    }

}
