package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Price(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("billing_scheme")
    val billingScheme: String,
    @SerializedName("created")
    val created: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("deleted")
    val deleted: Any,
    @SerializedName("id")
    val id: String,
    @SerializedName("livemode")
    val livemode: Boolean,
    @SerializedName("lookup_key")
    val lookupKey: Any,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("nickname")
    val nickname: Any,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("product")
    val product: String,
    @SerializedName("recurring")
    val recurring: Recurring,
    @SerializedName("tiers")
    val tiers: Any,
    @SerializedName("tiers_mode")
    val tiersMode: Any,
    @SerializedName("transform_quantity")
    val transformQuantity: Any,
    @SerializedName("type")
    val type: String,
    @SerializedName("unit_amount")
    val unitAmount: Int,
    @SerializedName("unit_amount_decimal")
    val unitAmountDecimal: Int
)