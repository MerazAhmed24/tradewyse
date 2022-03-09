package com.info.model;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ankur on 4/24/2019.
 */

public class Comment {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("commentDetails")
    @Expose
    private String commentDetails;
    @SerializedName("userType")
    @Expose
    private Object userType;
    @SerializedName("tipId")
    @Expose
    private String tipId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("imageDetails")
    @Expose
    private String imageDetails;
    @SerializedName("createdOn")
    @Expose
    private long createdOn;
    @SerializedName("modifiedOn")
    @Expose
    private long modifiedOn;
    @SerializedName("commentId")
    @Expose
    private Object commentId;
    @SerializedName("commentOnTip")
    @Expose
    private boolean commentOnTip;
    @SerializedName("commentOnComment")
    @Expose
    private boolean commentOnComment;

    @SerializedName("appUser")
    @Expose
    private User appUser;

    private Uri uri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentDetails() {
        return commentDetails;
    }

    public void setCommentDetails(String commentDetails) {
        this.commentDetails = commentDetails;
    }

    public Object getUserType() {
        return userType;
    }

    public void setUserType(Object userType) {
        this.userType = userType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageDetails() {
        return imageDetails;
    }

    public void setImageDetails(String imageDetails) {
        this.imageDetails = imageDetails;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Object getCommentId() {
        return commentId;
    }

    public void setCommentId(Object commentId) {
        this.commentId = commentId;
    }

    public boolean isCommentOnTip() {
        return commentOnTip;
    }

    public void setCommentOnTip(boolean commentOnTip) {
        this.commentOnTip = commentOnTip;
    }

    public boolean isCommentOnComment() {
        return commentOnComment;
    }

    public void setCommentOnComment(boolean commentOnComment) {
        this.commentOnComment = commentOnComment;
    }

    public User getAppUser() {
        return appUser;
    }

    public void setAppUser(User appUser) {
        this.appUser = appUser;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
