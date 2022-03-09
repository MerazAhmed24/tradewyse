
package com.info.model.timelineModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppUser {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fName")
    @Expose
    private String fName;
    @SerializedName("mName")
    @Expose
    private String mName;
    @SerializedName("lName")
    @Expose
    private String lName;
    @SerializedName("userType")
    @Expose
    private String userType;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("createdOn")
    @Expose
    private String createdOn;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("savedStocks")
    @Expose
    private String savedStocks;
    @SerializedName("savedMentors")
    @Expose
    private String savedMentors;
    @SerializedName("modifiedOn")
    @Expose
    private String modifiedOn;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("socialId")
    @Expose
    private Object socialId;
    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("isMentor")
    @Expose
    private Boolean isMentor;
    @SerializedName("passwordResetToken")
    @Expose
    private Object passwordResetToken;
    @SerializedName("passwordCreatedOn")
    @Expose
    private Object passwordCreatedOn;
    @SerializedName("isEmailAuthenticate")
    @Expose
    private Boolean isEmailAuthenticate;
    @SerializedName("emailAuthToken")
    @Expose
    private Object emailAuthToken;
    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getMName() {
        return mName;
    }

    public void setMName(String mName) {
        this.mName = mName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Object getSocialId() {
        return socialId;
    }

    public void setSocialId(Object socialId) {
        this.socialId = socialId;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Boolean getIsMentor() {
        return isMentor;
    }

    public void setIsMentor(Boolean isMentor) {
        this.isMentor = isMentor;
    }

    public Object getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(Object passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public Object getPasswordCreatedOn() {
        return passwordCreatedOn;
    }

    public void setPasswordCreatedOn(Object passwordCreatedOn) {
        this.passwordCreatedOn = passwordCreatedOn;
    }

    public Boolean getIsEmailAuthenticate() {
        return isEmailAuthenticate;
    }

    public void setIsEmailAuthenticate(Boolean isEmailAuthenticate) {
        this.isEmailAuthenticate = isEmailAuthenticate;
    }

    public Object getEmailAuthToken() {
        return emailAuthToken;
    }

    public void setEmailAuthToken(Object emailAuthToken) {
        this.emailAuthToken = emailAuthToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

}
