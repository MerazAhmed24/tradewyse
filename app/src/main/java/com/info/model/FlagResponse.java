package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FlagResponse implements Parcelable {

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("title")
    @Expose
    private String title;

    protected FlagResponse(Parcel in) {
        message = in.readString();
        status = in.readString();
        title = in.readString();
    }

    public static final Creator<FlagResponse> CREATOR = new Creator<FlagResponse>() {
        @Override
        public FlagResponse createFromParcel(Parcel in) {
            return new FlagResponse(in);
        }

        @Override
        public FlagResponse[] newArray(int size) {
            return new FlagResponse[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.message);
        parcel.writeString(this.status);
        parcel.writeString(this.title);
    }
}
