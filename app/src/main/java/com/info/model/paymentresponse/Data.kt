package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("billing_thresholds")
    val billingThresholds: Any,
    @SerializedName("created")
    val created: Int,
    @SerializedName("deleted")
    val deleted: Any,
    @SerializedName("id")
    val id: String,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("plan")
    val plan: Plan,
    @SerializedName("price")
    val price: Price,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("subscription")
    val subscription: String,
    @SerializedName("tax_rates")
    val taxRates: List<Any>
)