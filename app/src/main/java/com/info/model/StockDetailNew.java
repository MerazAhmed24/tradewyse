package com.info.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ankur on 6/20/2019.
 */

public class StockDetailNew {
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("sector")
    @Expose
    private String sector;
    @SerializedName("securityType")
    @Expose
    private String securityType;
    @SerializedName("bidPrice")
    @Expose
    private double bidPrice;
    @SerializedName("bidSize")
    @Expose
    private int bidSize;
    @SerializedName("askPrice")
    @Expose
    private double askPrice;
    @SerializedName("askSize")
    @Expose
    private int askSize;
    @SerializedName("lastUpdated")
    @Expose
    private int lastUpdated;
    @SerializedName("lastSalePrice")
    @Expose
    private double lastSalePrice;
    @SerializedName("lastSaleSize")
    @Expose
    private int lastSaleSize;
    @SerializedName("lastSaleTime")
    @Expose
    private int lastSaleTime;
    @SerializedName("volume")
    @Expose
    private int volume;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public int getBidSize() {
        return bidSize;
    }

    public void setBidSize(int bidSize) {
        this.bidSize = bidSize;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public int getAskSize() {
        return askSize;
    }

    public void setAskSize(int askSize) {
        this.askSize = askSize;
    }

    public int getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(int lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public double getLastSalePrice() {
        return lastSalePrice;
    }

    public void setLastSalePrice(double lastSalePrice) {
        this.lastSalePrice = lastSalePrice;
    }

    public int getLastSaleSize() {
        return lastSaleSize;
    }

    public void setLastSaleSize(int lastSaleSize) {
        this.lastSaleSize = lastSaleSize;
    }

    public int getLastSaleTime() {
        return lastSaleTime;
    }

    public void setLastSaleTime(int lastSaleTime) {
        this.lastSaleTime = lastSaleTime;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
