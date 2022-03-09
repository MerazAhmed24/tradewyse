package com.info.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocialChatStocksDetails {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("stockSymbol")
    @Expose
    private String stockSymbol;
    @SerializedName("stockName")
    @Expose
    private String stockName;
    @SerializedName("symbolCount")
    @Expose
    private String symbolCount;
    @SerializedName("summaryRecommendation")
    @Expose
    private String summaryRecommendation;
    @SerializedName("summaryBuy")
    @Expose
    private String summaryBuy;
    @SerializedName("summarySell")
    @Expose
    private String summarySell;
    @SerializedName("summaryNeutral")
    @Expose
    private String summaryNeutral;
    @SerializedName("oscillatorBuy")
    @Expose
    private String oscillatorBuy;
    @SerializedName("oscillatorSell")
    @Expose
    private String oscillatorSell;
    @SerializedName("oscillatorNeutral")
    @Expose
    private String oscillatorNeutral;
    @SerializedName("oscillatorRecommendation")
    @Expose
    private String oscillatorRecommendation;
    @SerializedName("movingAveragesRecommendation")
    @Expose
    private String movingAveragesRecommendation;
    @SerializedName("movingAveragesBuy")
    @Expose
    private String movingAveragesBuy;
    @SerializedName("movingAveragesSell")
    @Expose
    private String movingAveragesSell;
    @SerializedName("movingAveragesNeutral")
    @Expose
    private String movingAveragesNeutral;
    @SerializedName("zackRank")
    @Expose
    private String zackRank;
    @SerializedName("dividendDate")
    @Expose
    private String dividendDate;
    @SerializedName("dividend")
    @Expose
    private String dividend;
    @SerializedName("smaGenerationDate")
    @Expose
    private String smaGenerationDate;
    @SerializedName("rsiResult")
    @Expose
    private String rsiResult;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("modifiedOn")
    @Expose
    private Long modifiedOn;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;
    @SerializedName("a_50days")
    @Expose
    private String a50days;
    @SerializedName("a_10days")
    @Expose
    private String a10days;
    @SerializedName("a_200days")
    @Expose
    private String a200days;

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

    public String getSymbolCount() {
        return symbolCount;
    }

    public void setSymbolCount(String symbolCount) {
        this.symbolCount = symbolCount;
    }

    public String getSummaryRecommendation() {
        return summaryRecommendation;
    }

    public void setSummaryRecommendation(String summaryRecommendation) {
        this.summaryRecommendation = summaryRecommendation;
    }

    public String getSummaryBuy() {
        return summaryBuy;
    }

    public void setSummaryBuy(String summaryBuy) {
        this.summaryBuy = summaryBuy;
    }

    public String getSummarySell() {
        return summarySell;
    }

    public void setSummarySell(String summarySell) {
        this.summarySell = summarySell;
    }

    public String getSummaryNeutral() {
        return summaryNeutral;
    }

    public void setSummaryNeutral(String summaryNeutral) {
        this.summaryNeutral = summaryNeutral;
    }

    public String getOscillatorBuy() {
        return oscillatorBuy;
    }

    public void setOscillatorBuy(String oscillatorBuy) {
        this.oscillatorBuy = oscillatorBuy;
    }

    public String getOscillatorSell() {
        return oscillatorSell;
    }

    public void setOscillatorSell(String oscillatorSell) {
        this.oscillatorSell = oscillatorSell;
    }

    public String getOscillatorNeutral() {
        return oscillatorNeutral;
    }

    public void setOscillatorNeutral(String oscillatorNeutral) {
        this.oscillatorNeutral = oscillatorNeutral;
    }

    public String getOscillatorRecommendation() {
        return oscillatorRecommendation;
    }

    public void setOscillatorRecommendation(String oscillatorRecommendation) {
        this.oscillatorRecommendation = oscillatorRecommendation;
    }

    public String getMovingAveragesRecommendation() {
        return movingAveragesRecommendation;
    }

    public void setMovingAveragesRecommendation(String movingAveragesRecommendation) {
        this.movingAveragesRecommendation = movingAveragesRecommendation;
    }

    public String getMovingAveragesBuy() {
        return movingAveragesBuy;
    }

    public void setMovingAveragesBuy(String movingAveragesBuy) {
        this.movingAveragesBuy = movingAveragesBuy;
    }

    public String getMovingAveragesSell() {
        return movingAveragesSell;
    }

    public void setMovingAveragesSell(String movingAveragesSell) {
        this.movingAveragesSell = movingAveragesSell;
    }

    public String getMovingAveragesNeutral() {
        return movingAveragesNeutral;
    }

    public void setMovingAveragesNeutral(String movingAveragesNeutral) {
        this.movingAveragesNeutral = movingAveragesNeutral;
    }

    public String getZackRank() {
        return zackRank;
    }

    public void setZackRank(String zackRank) {
        this.zackRank = zackRank;
    }

    public String getDividendDate() {
        return dividendDate;
    }

    public void setDividendDate(String dividendDate) {
        this.dividendDate = dividendDate;
    }

    public String getDividend() {
        return dividend;
    }

    public void setDividend(String dividend) {
        this.dividend = dividend;
    }

    public String getSmaGenerationDate() {
        return smaGenerationDate;
    }

    public void setSmaGenerationDate(String smaGenerationDate) {
        this.smaGenerationDate = smaGenerationDate;
    }

    public String getRsiResult() {
        return rsiResult;
    }

    public void setRsiResult(String rsiResult) {
        this.rsiResult = rsiResult;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getA50days() {
        return a50days;
    }

    public void setA50days(String a50days) {
        this.a50days = a50days;
    }

    public String getA10days() {
        return a10days;
    }

    public void setA10days(String a10days) {
        this.a10days = a10days;
    }

    public String getA200days() {
        return a200days;
    }

    public void setA200days(String a200days) {
        this.a200days = a200days;
    }

}
