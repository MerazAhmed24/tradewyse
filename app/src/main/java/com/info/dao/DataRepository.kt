package com.info.dao

import android.content.Context
import androidx.annotation.WorkerThread
import com.info.dao.PriceCallback
import com.info.model.StockPrice
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object DataRepository {
    @JvmStatic
    fun getStockPrice(context: Context, stockName: String, databaseCallback: PriceCallback<StockPrice>) {
        try {
            var dao = AppDatabase.getAppDataBase(context).stockPriceDao()
            dao.getStockPrice(stockName).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        databaseCallback.onPriceUpdate(it)
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }




    @WorkerThread
   suspend fun insertPrice(stockPrice: StockPrice, context: Context) {
        var dao = AppDatabase.getAppDataBase(context).stockPriceDao()
        dao.insert(stockPrice)
    }

    @JvmStatic
    fun getStockPriceSingle(context: Context, stockName: String): StockPrice? {
        var dao = AppDatabase.getAppDataBase(context).stockPriceDao()
        return dao.getStockPriceSingle(stockName)
    }

}