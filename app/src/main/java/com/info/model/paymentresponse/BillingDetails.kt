package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class BillingDetails(
    @SerializedName("address")
    val address: Address,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: Any
)