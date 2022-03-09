package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class PaymentMethodOptions(
    @SerializedName("card")
    val card: CardX
)