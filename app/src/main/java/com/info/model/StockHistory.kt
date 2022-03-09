package com.info.model


import com.google.gson.annotations.SerializedName

data class StockHistory(
    @SerializedName("Close")
    val close: Float,
    @SerializedName("Date")
    val date: String,
    @SerializedName("High")
    val high: Float,
    @SerializedName("Low")
    val low: Float,
    @SerializedName("Open")
    val open: Float,
    @SerializedName("")
    val x: String
)