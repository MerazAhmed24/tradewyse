package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.info.commons.StringHelper;

public class Stocks implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("stockSymbol")
    @Expose
    private String stockName;

    @SerializedName("stockName")
    @Expose
    private String companyName;

    private double stockPrice;
    private double stockChange;
    private double high;
    private double low;
    private boolean stockStatus;
    private String sectionName;
    private long createdOn;


    public Stocks(
            String id,
            String stockName,
                  String companyName,
                  double stockPrice,
                  double stockChange,
                  double high,
                  double low,
                  boolean stockStatus,
                  String sectionName) {
        this.id=id;
        this.stockName = stockName;
        this.companyName = companyName;
        this.stockPrice = stockPrice;
        this.stockChange = stockChange;
        this.high=high;
        this.low=low;
        this.stockStatus = stockStatus;
        this.sectionName=sectionName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getStockName() {
        if(stockName!=null){
            stockName=stockName.trim();
        }
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getCompanyName() {
        if(!StringHelper.isEmpty(companyName)){
            companyName=companyName.replace("(The)","");
        }
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

    public boolean isStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(boolean stockStatus) {
        this.stockStatus = stockStatus;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stockName);
        dest.writeString(this.id);
        dest.writeString(this.companyName);
        dest.writeDouble(this.stockPrice);
        dest.writeDouble(this.stockChange);
        dest.writeDouble(this.high);
        dest.writeDouble(this.low);
        dest.writeByte(this.stockStatus ? (byte) 1 : (byte) 0);
    }

    public Stocks() {
    }

    protected Stocks(Parcel in) {
        this.stockName = in.readString();
        this.id=in.readString();
        this.companyName = in.readString();
        this.stockPrice = in.readDouble();
        this.stockChange = in.readDouble();
        this.high = in.readDouble();
        this.low = in.readDouble();
        this.stockStatus = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Stocks> CREATOR = new Parcelable.Creator<Stocks>() {
        @Override
        public Stocks createFromParcel(Parcel source) {
            return new Stocks(source);
        }

        @Override
        public Stocks[] newArray(int size) {
            return new Stocks[size];
        }
    };
}
