package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Period(
    @SerializedName("end")
    val end: Int,
    @SerializedName("start")
    val start: Int
)