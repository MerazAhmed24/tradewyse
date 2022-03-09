package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SellStock implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("stockSymbol")
    @Expose
    private String stockSymbol;
    @SerializedName("stockName")
    @Expose
    private String stockName;
    @SerializedName("crossover")
    @Expose
    private String crossover;
    @SerializedName("crossoverDate")
    @Expose
    private String crossoverDate;
    @SerializedName("currentdate")
    @Expose
    private String currentdate;
    @SerializedName("closePrice")
    @Expose
    private String closePrice;
    @SerializedName("modifiedOn")
    @Expose
    private Long modifiedOn;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;

    protected SellStock(Parcel in) {
        id = in.readString();
        stockSymbol = in.readString();
        stockName = in.readString();
        crossover = in.readString();
        crossoverDate = in.readString();
        currentdate = in.readString();
        closePrice = in.readString();
        if (in.readByte() == 0) {
            modifiedOn = null;
        } else {
            modifiedOn = in.readLong();
        }
        if (in.readByte() == 0) {
            createdOn = null;
        } else {
            createdOn = in.readLong();
        }
    }

    public static final Creator<SellStock> CREATOR = new Creator<SellStock>() {
        @Override
        public SellStock createFromParcel(Parcel in) {
            return new SellStock(in);
        }

        @Override
        public SellStock[] newArray(int size) {
            return new SellStock[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getCrossover() {
        return crossover;
    }

    public void setCrossover(String crossover) {
        this.crossover = crossover;
    }

    public String getCrossoverDate() {
        return crossoverDate;
    }

    public void setCrossoverDate(String crossoverDate) {
        this.crossoverDate = crossoverDate;
    }

    public String getCurrentdate() {
        return currentdate;
    }

    public void setCurrentdate(String currentdate) {
        this.currentdate = currentdate;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(stockSymbol);
        dest.writeString(stockName);
        dest.writeString(crossover);
        dest.writeString(crossoverDate);
        dest.writeString(currentdate);
        dest.writeString(closePrice);
        if (modifiedOn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(modifiedOn);
        }
        if (createdOn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(createdOn);
        }
    }
}
