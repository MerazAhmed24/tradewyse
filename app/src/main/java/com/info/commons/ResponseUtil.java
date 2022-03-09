package com.info.commons;

import com.info.model.Stocks;

import java.util.ArrayList;
import java.util.List;

public class ResponseUtil {

    public static List<Stocks> parseSearchResult(String searchResult) {
        List<Stocks> searchResultList = new ArrayList<>();
        String[] responseEnter = searchResult.split("\n");

        for (int i = 0; i < responseEnter.length; i++) {

            String[] responseSemiComma = responseEnter[i].split(";");
            if (responseSemiComma.length > 0) {
                String[] responsePipe = responseSemiComma[0].split("\\|");
                if(responsePipe.length>=2){
                    Stocks stocks = new Stocks();
                    stocks.setStockName(responsePipe[0]);
                    stocks.setCompanyName(responsePipe[1]);
                    searchResultList.add(stocks);
                }

            }
        }
        return searchResultList;
    }
}
