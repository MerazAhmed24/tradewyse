
package com.info.model.stockPrediction;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockPrediction implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("stockName")
    @Expose
    private String stockName;
    @SerializedName("normalizedPrice")
    @Expose
    private String normalizedPrice;
    @SerializedName("newsAffected")
    @Expose
    private String newsAffected;
    @SerializedName("dates")
    @Expose
    private String dates;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;
    @SerializedName("modifiedOn")
    @Expose
    private Long modifiedOn;
    @SerializedName("vxxaffected")
    @Expose
    private String vxxaffected;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getNormalizedPrice() {
        return normalizedPrice;
    }

    public void setNormalizedPrice(String normalizedPrice) {
        this.normalizedPrice = normalizedPrice;
    }

    public String getNewsAffected() {
        return newsAffected;
    }

    public void setNewsAffected(String newsAffected) {
        this.newsAffected = newsAffected;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getVxxaffected() {
        return vxxaffected;
    }

    public void setVxxaffected(String vxxaffected) {
        this.vxxaffected = vxxaffected;
    }

}
