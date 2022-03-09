package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
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

    @SerializedName("userName")
    @Expose
    private String userName;

    @SerializedName("isMentor")
    @Expose
    private String isMentor;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("internalSubscribedUser")
    @Expose
    private boolean internalSubscribedUser;

    @SerializedName("twitterId")
    @Expose
    private String twitterId;

    protected User(Parcel in) {
        id = in.readString();
        fName = in.readString();
        mName = in.readString();
        lName = in.readString();
        userName = in.readString();
        isMentor = in.readString();
        image = in.readString();
        internalSubscribedUser = in.readByte() != 0;
        twitterId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(fName);
        dest.writeString(mName);
        dest.writeString(lName);
        dest.writeString(userName);
        dest.writeString(isMentor);
        dest.writeString(image);
        dest.writeByte((byte) (internalSubscribedUser ? 1 : 0));
        dest.writeString(twitterId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsMentor() {
        return isMentor;
    }

    public void setIsMentor(String isMentor) {
        this.isMentor = isMentor;
    }

    public boolean getInternalSubscribedUser() {
        return internalSubscribedUser;
    }

    public void setInternalSubscribedUser(boolean internalSubscribedUser) {
        this.internalSubscribedUser = internalSubscribedUser;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }


    public User() {
    }

}
