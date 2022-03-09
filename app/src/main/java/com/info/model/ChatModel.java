package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

/**
 * Created by Amit Gupta on 18,January,2021
 */
public class ChatModel implements Parcelable {
    long createdDate;
    String documentReferenceId, message, messageId, messageType, profileImageUrl, userId, userName, caption, messageSource;
    private boolean isSelected;
    private boolean flag = false;
    private String flagedUserName;
    private String messageSocialReferenceId;
    private List<Map<String, Object>> flagMapList;

    public ChatModel(String documentReferenceId, long createdDate, String message, String messageId, String messageType, String profileImageUrl, String userId, String userName, String caption, String messageSource,
                     boolean flag, String flagedUserName, String messageSocialReferenceId, List<Map<String, Object>> flagMapList) {
        this.documentReferenceId = documentReferenceId;
        this.createdDate = createdDate;
        this.message = message;
        this.messageId = messageId;
        this.messageType = messageType;
        this.profileImageUrl = profileImageUrl;
        this.userId = userId;
        this.userName = userName;
        this.caption = caption;
        this.messageSource = messageSource;
        this.flag = flag;
        this.flagedUserName = flagedUserName;
        this.messageSocialReferenceId = messageSocialReferenceId;
        this.flagMapList = flagMapList;
    }

    protected ChatModel(Parcel in) {
        createdDate = in.readLong();
        documentReferenceId = in.readString();
        message = in.readString();
        messageId = in.readString();
        messageType = in.readString();
        profileImageUrl = in.readString();
        userId = in.readString();
        userName = in.readString();
        caption = in.readString();
        isSelected = in.readByte() != 0;
        flag = in.readByte() != 0;
        flagedUserName = in.readString();
        messageSource = in.readString();
        messageSocialReferenceId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createdDate);
        dest.writeString(documentReferenceId);
        dest.writeString(message);
        dest.writeString(messageId);
        dest.writeString(messageType);
        dest.writeString(profileImageUrl);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(caption);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (flag ? 1 : 0));
        dest.writeString(flagedUserName);
        dest.writeString(messageSource);
        dest.writeString(messageSocialReferenceId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatModel> CREATOR = new Creator<ChatModel>() {
        @Override
        public ChatModel createFromParcel(Parcel in) {
            return new ChatModel(in);
        }

        @Override
        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }
    };

    public String getDocumentReferenceId() {
        return documentReferenceId;
    }

    public void setDocumentReferenceId(String documentReferenceId) {
        this.documentReferenceId = documentReferenceId;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getFlagedUserName() {
        return flagedUserName;
    }

    public void setFlagedUserName(String flagedUserName) {
        this.flagedUserName = flagedUserName;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(String messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessageSocialReferenceId() {
        return messageSocialReferenceId;
    }

    public void setMessageSocialReferenceId(String messageSocialReferenceId) {
        this.messageSocialReferenceId = messageSocialReferenceId;
    }

    public List<Map<String, Object>> getFlagMapList() {
        return flagMapList;
    }

    public void setFlagMapList(List<Map<String, Object>> flagMapList) {
        this.flagMapList = flagMapList;
    }
}
