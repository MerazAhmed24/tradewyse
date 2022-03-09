package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TipClass implements Parcelable {

    @SerializedName("tip")
    @Expose
    private Tips tips = null;

    @SerializedName("pinCount")
    @Expose
    private int pinCount = 0;

    @SerializedName("likeCount")
    @Expose
    private int likeCount = 0;

    @SerializedName("commentCount")
    @Expose
    private int commentCount = 0;

    @SerializedName("userHideStatus")
    @Expose
    private boolean isUserHideStatus = false;

    @SerializedName("userPinStatus")
    @Expose
    private boolean isUserPinStatus = false;

    @SerializedName("userLikeStatus")
    @Expose
    private boolean isUserLikeStatus = false;

    protected TipClass(Parcel in) {
        tips = in.readParcelable(Tips.class.getClassLoader());
        pinCount = in.readInt();
        likeCount = in.readInt();
        commentCount = in.readInt();
        isUserHideStatus = in.readByte() != 0;
        isUserPinStatus = in.readByte() != 0;
        isUserLikeStatus = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(tips, flags);
        dest.writeInt(pinCount);
        dest.writeInt(likeCount);
        dest.writeInt(commentCount);
        dest.writeByte((byte) (isUserHideStatus ? 1 : 0));
        dest.writeByte((byte) (isUserPinStatus ? 1 : 0));
        dest.writeByte((byte) (isUserLikeStatus ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TipClass> CREATOR = new Creator<TipClass>() {
        @Override
        public TipClass createFromParcel(Parcel in) {
            return new TipClass(in);
        }

        @Override
        public TipClass[] newArray(int size) {
            return new TipClass[size];
        }
    };

    public Tips getTips() {
        return tips;
    }

    public void setTips(Tips tips) {
        this.tips = tips;
    }

    public int getPinCount() {
        return pinCount;
    }

    public void setPinCount(int pinCount) {
        this.pinCount = pinCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isUserHideStatus() {
        return isUserHideStatus;
    }

    public void setUserHideStatus(boolean userHideStatus) {
        isUserHideStatus = userHideStatus;
    }

    public boolean isUserPinStatus() {
        return isUserPinStatus;
    }

    public void setUserPinStatus(boolean userPinStatus) {
        isUserPinStatus = userPinStatus;
    }

    public boolean isUserLikeStatus() {
        return isUserLikeStatus;
    }

    public void setUserLikeStatus(boolean userLikeStatus) {
        isUserLikeStatus = userLikeStatus;
    }
}
