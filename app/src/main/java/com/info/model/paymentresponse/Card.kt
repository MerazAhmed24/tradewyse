package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Card(
    @SerializedName("brand")
    val brand: String,
    @SerializedName("checks")
    val checks: Checks,
    @SerializedName("country")
    val country: String,
    @SerializedName("description")
    val description: Any,
    @SerializedName("exp_month")
    val expMonth: Int,
    @SerializedName("exp_year")
    val expYear: Int,
    @SerializedName("fingerprint")
    val fingerprint: String,
    @SerializedName("funding")
    val funding: String,
    @SerializedName("iin")
    val iin: Any,
    @SerializedName("installments")
    val installments: Any,
    @SerializedName("issuer")
    val issuer: Any,
    @SerializedName("last4")
    val last4: String,
    @SerializedName("moto")
    val moto: Any,
    @SerializedName("network")
    val network: String,
    @SerializedName("three_d_secure")
    val threeDSecure: Any,
    @SerializedName("wallet")
    val wallet: Any
)