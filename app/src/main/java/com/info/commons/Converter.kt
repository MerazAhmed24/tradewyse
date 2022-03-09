package com.info.commons

import android.util.Log
import com.info.model.SectorNews
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object  Converter {
    @JvmStatic
    fun parseStringToInt(numberString: String): Int {
        return numberString.toIntOrNull() ?: 0
    }

    @JvmStatic
    fun parseStringToDouble(numberString: String): Double {
        return numberString.toDoubleOrNull() ?: 0.00
    }

    @JvmStatic
    fun parseStringToFloat(numberString: String): Float {
        return numberString.toFloatOrNull() ?: 0.00f
    }

    @JvmStatic
    fun formatAmount(value:Float): String {
        val nf = NumberFormat.getNumberInstance(Locale.US)
        val decimalFormat = nf as DecimalFormat
        decimalFormat.applyPattern("0.00")
        return decimalFormat.format(value)
    }

    @JvmStatic
    fun calculateSentimentValue(newsList: List<SectorNews>): SectorNews {

        val stockName = newsList[0].stockName
        val st = ArrayList<String>()
        st.add("AAPL")
        st.add("GOOG")
        st.add("MSFT")
        st.add("JPM")
        var sectorNews: SectorNews?=null
        if (!st.contains(stockName.toUpperCase())) {
            return newsList[0]
        }
        var newsListFiltered= newsList.sortedWith(compareBy(nullsLast<String>()) {it.stockSentiment})
        newsListFiltered= newsListFiltered.sortedBy { DateTimeHelperElapsed.getPublishDate(it.newsPublishDate) }.reversed()

        if(newsListFiltered.size>=5){
            newsListFiltered=newsListFiltered.subList(0,4)
        }
        if(newsListFiltered.isNotEmpty()){
             sectorNews = newsListFiltered[0]
        }

        if (sectorNews == null) {
            sectorNews = newsList[0]
        }

        return sectorNews
    }
}