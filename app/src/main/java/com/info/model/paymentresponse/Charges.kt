package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Charges(
    @SerializedName("data")
    val `data`: List<DataXX>,
    @SerializedName("has_more")
    val hasMore: Boolean,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("request_options")
    val requestOptions: Any,
    @SerializedName("request_params")
    val requestParams: Any,
    @SerializedName("url")
    val url: String
)