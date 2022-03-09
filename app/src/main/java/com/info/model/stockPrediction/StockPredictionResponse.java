
package com.info.model.stockPrediction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StockPredictionResponse implements Serializable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("object")
    @Expose
    private List<StockPrediction> stockPrediction = null;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;
    @SerializedName("subscribed")
    @Expose
    private boolean subscribed;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public List<StockPrediction> getStockPrediction() {
        return stockPrediction;
    }

    public void setStockPrediction(List<StockPrediction> stockPrediction) {
        this.stockPrediction = stockPrediction;
    }

    public boolean isSubscribed() {
        return subscribed;
    }
}
