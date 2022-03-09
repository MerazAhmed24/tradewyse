package com.info.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockChartModel {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("minute")
    @Expose
    private String minute;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("high")
    @Expose
    private double high;
    @SerializedName("low")
    @Expose
    private double low;
    @SerializedName("average")
    @Expose
    private double average;

    @SerializedName("close")
    @Expose
    private double close;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
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

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }





}
