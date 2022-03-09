package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class FraudDetails(
    @SerializedName("stripe_report")
    val stripeReport: Any,
    @SerializedName("user_report")
    val userReport: Any
)