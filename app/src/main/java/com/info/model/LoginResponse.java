package com.info.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ankur on 6/1/2019.
 */

public class LoginResponse implements Serializable {
  /*  @SerializedName("accessToken")
    @Expose
    private String accessToken;

    @SerializedName("tokenType")
    @Expose
    private String tokenType;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private boolean status;




    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }*/

    @SerializedName("accessToken")
    @Expose
    private String accessToken;
    @SerializedName("accessTokenStreamIO")
    @Expose
    private String accessTokenStreamIO;

    @SerializedName("accessTokenStreamIOWithID")
    @Expose
    private String accessTokenStreamIOWithID;

    @SerializedName("tokenType")
    @Expose
    private String tokenType;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("createdOn")
    @Expose
    private String createdOn;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("savedStocks")
    @Expose
    private String savedStocks;
    @SerializedName("savedMentors")
    @Expose
    private String savedMentors;
    @SerializedName("modifiedOn")
    @Expose
    private String modifiedOn;
    @SerializedName("socialId")
    @Expose
    private Object socialId;
    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("isMentor")
    @Expose
    private String isMentor;

    @SerializedName("deviceDetails")
    @Expose
    private List<DeviceDetail> deviceDetails = null;

    @SerializedName("deviceLimitExceeded")
    @Expose
    private Boolean deviceLimitExceeded;

    @SerializedName("loginInfo")
    @Expose
    private LoginInfo loginInfo;


    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public List<DeviceDetail> getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(List<DeviceDetail> deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    public Boolean getDeviceLimitExceeded() {
        return deviceLimitExceeded;
    }

    public void setDeviceLimitExceeded(Boolean deviceLimitExceeded) {
        this.deviceLimitExceeded = deviceLimitExceeded;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /*public String getAccessTokenStreamIO() {
        return accessTokenStreamIO;
    }

    public void setAccessTokenStreamIO(String accessTokenStreamIO) {
        this.accessTokenStreamIO = accessTokenStreamIO;
    }*/

    public String getAccessTokenStreamIOWithID() {
        return accessTokenStreamIOWithID;
    }

    public void setAccessTokenStreamIOWithID(String accessTokenStreamIOWithID) {
        this.accessTokenStreamIOWithID = accessTokenStreamIOWithID;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSavedStocks() {
        return savedStocks;
    }

    public void setSavedStocks(String savedStocks) {
        this.savedStocks = savedStocks;
    }

    public String getSavedMentors() {
        return savedMentors;
    }

    public void setSavedMentors(String savedMentors) {
        this.savedMentors = savedMentors;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Object getSocialId() {
        return socialId;
    }

    public void setSocialId(Object socialId) {
        this.socialId = socialId;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getIsMentor() {
        return isMentor;
    }

    public void setIsMentor(String isMentor) {
        this.isMentor = isMentor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

