package com.info.model;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.info.commons.Converter;

import java.util.ArrayList;
import java.util.List;

public class Tips implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("timeframe")
    @Expose
    private String timeframe;
    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("stockSuggestion")
    @Expose
    private String stockSuggestion;

    @SerializedName("createTipPrice")
    @Expose
    private String createTipPrice;

    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("entryPoint")
    @Expose
    private String entryPoint;
    @SerializedName("exitPoint")
    @Expose
    private String exitpoint;
    @SerializedName("stopPoint")
    @Expose
    private String stopPoint;
    @SerializedName("stockName")
    @Expose
    private String stockName;
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
    @SerializedName("comments")
    @Expose
    private List<Comment> comments = null;

    @SerializedName("appUser")
    @Expose
    private User appUser = null;

    private String companyName;
    private double stockPrice;
    private double stockChange;
    private double high;
    private double low;
    private Uri uri;

    protected Tips(Parcel in) {
        id = in.readString();
        timeframe = in.readString();
        comment = in.readString();
        stockSuggestion = in.readString();
        createTipPrice = in.readString();
        userId = in.readString();
        entryPoint = in.readString();
        exitpoint = in.readString();
        stopPoint = in.readString();
        stockName = in.readString();
        status = in.readString();
        imageDetails = in.readString();
        createdOn = in.readLong();
        modifiedOn = in.readLong();
        appUser = in.readParcelable(User.class.getClassLoader());
        companyName = in.readString();
        stockPrice = in.readDouble();
        stockChange = in.readDouble();
        high = in.readDouble();
        low = in.readDouble();
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(timeframe);
        dest.writeString(comment);
        dest.writeString(stockSuggestion);
        dest.writeString(createTipPrice);
        dest.writeString(userId);
        dest.writeString(entryPoint);
        dest.writeString(exitpoint);
        dest.writeString(stopPoint);
        dest.writeString(stockName);
        dest.writeString(status);
        dest.writeString(imageDetails);
        dest.writeLong(createdOn);
        dest.writeLong(modifiedOn);
        dest.writeParcelable(appUser, flags);
        dest.writeString(companyName);
        dest.writeDouble(stockPrice);
        dest.writeDouble(stockChange);
        dest.writeDouble(high);
        dest.writeDouble(low);
        dest.writeParcelable(uri, flags);
    }

    public static final Creator<Tips> CREATOR = new Creator<Tips>() {
        @Override
        public Tips createFromParcel(Parcel in) {
            return new Tips(in);
        }

        @Override
        public Tips[] newArray(int size) {
            return new Tips[size];
        }
    };

    public double getCreateTipPrice() {
        return Converter.parseStringToDouble(createTipPrice);
    }

    public void setCreateTipPrice(String createTipPrice) {
        this.createTipPrice = createTipPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getEntryPoint() {
        return Converter.parseStringToDouble(entryPoint);
    }
    public String getEntryPointAsItis() {
        return entryPoint;
    }
    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public double getExitpoint() {
        return Converter.parseStringToDouble(exitpoint);
    }
    public String getExitPointAsString() {
        return exitpoint;
    }
    public void setExitpoint(String exitpoint) {
        this.exitpoint = exitpoint;
    }

    public double getStopPoint() {
        return Converter.parseStringToDouble(stopPoint);
    }
    public String getStopPointAsString() {
        return stopPoint;
    }
    public void setStopPoint(String stopPoint) {
        this.stopPoint = stopPoint;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
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

    public User getAppUser() {
        return appUser;
    }

    public String getStockSuggestion() {
        return stockSuggestion;
    }


    public void setStockSuggestion(String stockSuggestion) {
        this.stockSuggestion = stockSuggestion;
    }

    public void setAppUser(User appUser) {
        this.appUser = appUser;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public double getStockChange() {
        return stockChange;
    }

    public void setStockChange(double stockChange) {
        this.stockChange = stockChange;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}

