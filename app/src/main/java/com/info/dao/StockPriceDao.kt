package com.info.dao

import androidx.room.*
import com.info.model.StockPrice
import io.reactivex.Flowable
@Dao
interface StockPriceDao {

    @Query("SELECT * FROM stockprice WHERE stock_name =:stockName")
    fun getStockPrice(stockName: String): Flowable<StockPrice>

    @Query("SELECT * FROM stockprice WHERE stock_name =:stockName")
    fun getStockPriceSingle(stockName: String):StockPrice

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stockPrice: StockPrice)

}