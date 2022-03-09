package com.info.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TipsSection implements Parcelable {

    private TipResponse tipResponse;
    List<SectorNews> sectorNewsList;
    int tipsType;
    long tipTime;
    int stockIndex;

    public TipsSection() {
    }

    protected TipsSection(Parcel in) {
        tipResponse = in.readParcelable(TipResponse.class.getClassLoader());
        sectorNewsList = in.createTypedArrayList(SectorNews.CREATOR);
        tipsType = in.readInt();
        tipTime = in.readLong();
        stockIndex = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(tipResponse, flags);
        dest.writeTypedList(sectorNewsList);
        dest.writeInt(tipsType);
        dest.writeLong(tipTime);
        dest.writeInt(stockIndex);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TipsSection> CREATOR = new Creator<TipsSection>() {
        @Override
        public TipsSection createFromParcel(Parcel in) {
            return new TipsSection(in);
        }

        @Override
        public TipsSection[] newArray(int size) {
            return new TipsSection[size];
        }
    };

    public TipResponse getTips() {
        return tipResponse;
    }

    public void setTips(TipResponse tips) {
        this.tipResponse = tips;
    }


    public int getTipsType() {
        return tipsType;
    }

    public void setTipsType(int tipsType) {
        this.tipsType = tipsType;
    }

    public List<SectorNews> getSectorNewsList() {
        return sectorNewsList;
    }

    public void setSectorNewsList(List<SectorNews> sectorNewsList) {
        this.sectorNewsList = sectorNewsList;
    }

    public long getTipTime() {
        return tipTime;
    }

    public void setTipTime(long tipTime) {
        this.tipTime = tipTime;
    }

    public int getStockIndex() {
        return stockIndex;
    }

    public void setStockIndex(int stockIndex) {
        this.stockIndex = stockIndex;
    }
}
