package com.info.model
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class StockPrice(
    @PrimaryKey
    @ColumnInfo(name="stock_name")
    var stockName: String,
    @ColumnInfo(name="company_name")
    var companyName: String?,
    @ColumnInfo(name = "stock_price")
    var stockPrice: Double?,
    @ColumnInfo(name = "stock_change")
    var stockChange: Double?

)