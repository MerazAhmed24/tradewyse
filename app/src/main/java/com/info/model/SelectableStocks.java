package com.info.model;

/**
 * Created by ankur on 3/4/19.
 */

public class SelectableStocks extends Stocks {
    private boolean isSelected = false;
    Stocks stocks;

    public SelectableStocks(Stocks stocks, boolean isSelected) {
        //String stockName, String companyName, float stockPrice, float stockChange, boolean stockStatus
        super(stocks.getId(),stocks.getStockName(), stocks.getCompanyName(), stocks.getStockPrice(), stocks.getStockChange(), stocks.getHigh(), stocks.getLow(), stocks.isStockStatus(),stocks.getSectionName());
        this.stocks = stocks;
        this.isSelected = isSelected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}