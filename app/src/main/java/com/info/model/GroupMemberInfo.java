package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupMemberInfo implements Parcelable {

    private String id;
    private String documentReferenceId;
    private Long createdOn;
    private String messageSource;
    private String profileImageUrl;
    private String userId;
    private String userName;

    public GroupMemberInfo() {
    }

    public GroupMemberInfo(String id, String documentReferenceId, Long createdOn, String messageSource, String profileImageUrl, String userId, String userName) {
        this.id = id;
        this.documentReferenceId = documentReferenceId;
        this.createdOn = createdOn;
        this.messageSource = messageSource;
        this.profileImageUrl = profileImageUrl;
        this.userId = userId;
        this.userName = userName;
    }

    protected GroupMemberInfo(Parcel in) {
        id = in.readString();
        documentReferenceId = in.readString();
        if (in.readByte() == 0) {
            createdOn = null;
        } else {
            createdOn = in.readLong();
        }
        messageSource = in.readString();
        profileImageUrl = in.readString();
        userId = in.readString();
        userName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(documentReferenceId);
        if (createdOn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(createdOn);
        }
        dest.writeString(messageSource);
        dest.writeString(profileImageUrl);
        dest.writeString(userId);
        dest.writeString(userName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupMemberInfo> CREATOR = new Creator<GroupMemberInfo>() {
        @Override
        public GroupMemberInfo createFromParcel(Parcel in) {
            return new GroupMemberInfo(in);
        }

        @Override
        public GroupMemberInfo[] newArray(int size) {
            return new GroupMemberInfo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentReferenceId() {
        return documentReferenceId;
    }

    public void setDocumentReferenceId(String documentReferenceId) {
        this.documentReferenceId = documentReferenceId;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(String messageSource) {
        this.messageSource = messageSource;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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
}
