package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("city")
    val city: Any,
    @SerializedName("country")
    val country: Any,
    @SerializedName("line1")
    val line1: Any,
    @SerializedName("line2")
    val line2: Any,
    @SerializedName("postal_code")
    val postalCode: Any,
    @SerializedName("state")
    val state: Any
)