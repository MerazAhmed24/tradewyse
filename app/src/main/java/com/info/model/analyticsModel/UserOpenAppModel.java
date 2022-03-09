package com.info.model.analyticsModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserOpenAppModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("appConnectionDate")
    @Expose
    private String appConnectionDate;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("modifiedOn")
    @Expose
    private Long modifiedOn;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppConnectionDate() {
        return appConnectionDate;
    }

    public void setAppConnectionDate(String appConnectionDate) {
        this.appConnectionDate = appConnectionDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }
}
