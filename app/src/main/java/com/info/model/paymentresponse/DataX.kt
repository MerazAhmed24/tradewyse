package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("discountable")
    val discountable: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("invoice_item")
    val invoiceItem: Any,
    @SerializedName("livemode")
    val livemode: Boolean,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("period")
    val period: Period,
    @SerializedName("plan")
    val plan: Plan,
    @SerializedName("price")
    val price: Price,
    @SerializedName("proration")
    val proration: Boolean,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("subscription")
    val subscription: String,
    @SerializedName("subscription_item")
    val subscriptionItem: String,
    @SerializedName("tax_amounts")
    val taxAmounts: List<Any>,
    @SerializedName("tax_rates")
    val taxRates: List<Any>,
    @SerializedName("type")
    val type: String,
    @SerializedName("unified_proration")
    val unifiedProration: Any
)