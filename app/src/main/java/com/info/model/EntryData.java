package com.info.model;

public class EntryData {
    String lineName;
    int lineColor;

    public EntryData(String lineName, int lineColor) {
        this.lineName = lineName;
        this.lineColor = lineColor;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }
}
