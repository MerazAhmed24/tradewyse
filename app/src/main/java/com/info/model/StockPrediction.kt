package com.info.model
import com.google.gson.annotations.SerializedName
data class StockPrediction(
    @SerializedName("dates")
    val dates: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("newsAffected")
    val newsAffected: String?,
    @SerializedName("normalizedPrice")
    val normalizedPrice: String?,
    @SerializedName("stockName")
    val stockName: String?,
    @SerializedName("vxxaffected")
    val vxxaffected: String?
)