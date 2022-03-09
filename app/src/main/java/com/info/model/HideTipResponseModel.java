package com.info.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HideTipResponseModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("tipId")
    @Expose
    private String tipId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("isPin")
    @Expose
    private Boolean isPin;
    @SerializedName("isHide")
    @Expose
    private Boolean isHide;
    @SerializedName("userLiked")
    @Expose
    private Boolean userLiked;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;
    @SerializedName("modifiedOn")
    @Expose
    private Long modifiedOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipId() {
        return tipId;
    }

    public void setTipId(String tipId) {
        this.tipId = tipId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsPin() {
        return isPin;
    }

    public void setIsPin(Boolean isPin) {
        this.isPin = isPin;
    }

    public Boolean getIsHide() {
        return isHide;
    }

    public void setIsHide(Boolean isHide) {
        this.isHide = isHide;
    }

    public Boolean getUserLiked() {
        return userLiked;
    }

    public void setUserLiked(Boolean userLiked) {
        this.userLiked = userLiked;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

}