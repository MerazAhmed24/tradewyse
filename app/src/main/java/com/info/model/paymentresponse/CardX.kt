package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class CardX(
    @SerializedName("installments")
    val installments: Any,
    @SerializedName("request_three_d_secure")
    val requestThreeDSecure: String
)