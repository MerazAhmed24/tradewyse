package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SectorNews implements Parcelable {

    @SerializedName("avg10days")
    @Expose
    private String avg10days;
    @SerializedName("avg200days")
    @Expose
    private String avg200days;
    @SerializedName("avg50days")
    @Expose
    private String avg50days;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("stock_name")
    @Expose
    private String stockName;
    @SerializedName("sma_generation_date")
    @Expose
    private String smaGenerationDate;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("news_publish_date")
    @Expose
    private String newsPublishDate;
    @SerializedName("news_sector_sentiment")
    @Expose
    private String newsSectorSentiment;
    @SerializedName("news_source_name")
    @Expose
    private String newsSourceName;
    @SerializedName("news_summary")
    @Expose
    private String newsSummary;
    @SerializedName("news_title")
    @Expose
    private String newsTitle;
    @SerializedName("sector_name")
    @Expose
    private String sectorName;
    @SerializedName("sector_news")
    @Expose
    private String sectorNews;

    @SerializedName("stock_sentiment")
    @Expose
    private String stockSentiment;

    @SerializedName("overall_sector_sentiment")
    @Expose
    private String overallSectorSentiment;


    @SerializedName("sector_news_url")
    @Expose
    private String sectorNewsURL;


    @SerializedName("rsiResult")
    @Expose
    private String rsiResult;

    @SerializedName("fiveDayAgoRsi")
    @Expose
    private String fiveDayAgoRsi;

    @SerializedName("currentRsi")
    @Expose
    private String currentRsi;





    private String companyName;
    private double stockPrice;
    private double stockChange;
    private double high;
    private double low;


    public String getAvg10days() {
        return avg10days;
    }

    public void setAvg10days(String avg10days) {
        this.avg10days = avg10days;
    }

    public String getAvg200days() {
        return avg200days;
    }

    public void setAvg200days(String avg200days) {
        this.avg200days = avg200days;
    }

    public String getAvg50days() {
        return avg50days;
    }

    public void setAvg50days(String avg50days) {
        this.avg50days = avg50days;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getSmaGenerationDate() {
        return smaGenerationDate;
    }

    public void setSmaGenerationDate(String smaGenerationDate) {
        this.smaGenerationDate = smaGenerationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewsPublishDate() {
        return newsPublishDate;
    }

    public void setNewsPublishDate(String newsPublishDate) {
        this.newsPublishDate = newsPublishDate;
    }

    public String getNewsSectorSentiment() {
        return newsSectorSentiment;
    }

    public void setNewsSectorSentiment(String newsSectorSentiment) {
        this.newsSectorSentiment = newsSectorSentiment;
    }

    public String getNewsSourceName() {
        return newsSourceName;
    }

    public void setNewsSourceName(String newsSourceName) {
        this.newsSourceName = newsSourceName;
    }

    public String getNewsSummary() {
        return newsSummary;
    }

    public void setNewsSummary(String newsSummary) {
        this.newsSummary = newsSummary;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public String getSectorNews() {
        return sectorNews;
    }

    public void setSectorNews(String sectorNews) {
        this.sectorNews = sectorNews;
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

    public String getSectorNewsURL() {
        return sectorNewsURL;
    }

    public void setSectorNewsURL(String sectorNewsURL) {
        this.sectorNewsURL = sectorNewsURL;
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

    public String getStockSentiment() {
        return stockSentiment;
    }

    public void setStockSentiment(String stockSentiment) {
        this.stockSentiment = stockSentiment;
    }

    public String getOverallSectorSentiment() {
        return overallSectorSentiment;
    }

    public void setOverallSectorSentiment(String overallSectorSentiment) {
        this.overallSectorSentiment = overallSectorSentiment;
    }

    public String getRsiResult() {
        return rsiResult;
    }

    public void setRsiResult(String rsiResult) {
        this.rsiResult = rsiResult;
    }

    public String getFiveDayAgoRsi() {
        return fiveDayAgoRsi;
    }

    public void setFiveDayAgoRsi(String fiveDayAgoRsi) {
        this.fiveDayAgoRsi = fiveDayAgoRsi;
    }

    public String getCurrentRsi() {
        return currentRsi;
    }

    public void setCurrentRsi(String currentRsi) {
        this.currentRsi = currentRsi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avg10days);
        dest.writeString(this.avg200days);
        dest.writeString(this.avg50days);
        dest.writeString(this.result);
        dest.writeString(this.stockName);
        dest.writeString(this.smaGenerationDate);
        dest.writeString(this.id);
        dest.writeString(this.newsPublishDate);
        dest.writeString(this.sectorNewsURL);
        dest.writeString(this.sectorNews);
        dest.writeString(this.newsSectorSentiment);
        dest.writeString(this.newsSourceName);
        dest.writeString(this.newsSummary);
        dest.writeString(this.newsTitle);
        dest.writeString(this.sectorName);
        dest.writeString(this.overallSectorSentiment);
        dest.writeString(this.companyName);
        dest.writeString(this.stockSentiment);
        dest.writeDouble(this.stockPrice);
        dest.writeDouble(this.stockChange);
        dest.writeDouble(this.high);
        dest.writeDouble(this.low);
        dest.writeString(this.rsiResult);
        dest.writeString(this.fiveDayAgoRsi);
        dest.writeString(this.currentRsi);
    }

    public SectorNews() {
    }

    protected SectorNews(Parcel in) {
        this.avg10days = in.readString();
        this.avg200days = in.readString();
        this.avg50days = in.readString();
        this.result = in.readString();
        this.stockName = in.readString();
        this.smaGenerationDate =  in.readString();
        this.id = in.readString();
        this.newsPublishDate = in.readString();
        this.sectorNewsURL = in.readString();
        this.sectorNews = in.readString();
        this.newsSectorSentiment = in.readString();
        this.newsSourceName = in.readString();
        this.newsSummary = in.readString();
        this.newsTitle = in.readString();
        this.sectorName = in.readString();
        this.overallSectorSentiment=in.readString();
        this.companyName = in.readString();
        this.stockSentiment=in.readString();
        this.stockPrice = in.readDouble();
        this.stockChange = in.readDouble();
        this.high = in.readDouble();
        this.low = in.readDouble();

        this.rsiResult = in.readString();
        this.currentRsi=in.readString();
        this.fiveDayAgoRsi = in.readString();
    }

    public static final Parcelable.Creator<SectorNews> CREATOR = new Parcelable.Creator<SectorNews>() {
        @Override
        public SectorNews createFromParcel(Parcel source) {
            return new SectorNews(source);
        }

        @Override
        public SectorNews[] newArray(int size) {
            return new SectorNews[size];
        }
    };
}
