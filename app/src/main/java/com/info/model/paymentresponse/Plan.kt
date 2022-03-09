package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Plan(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("aggregate_usage")
    val aggregateUsage: Any,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("amount_decimal")
    val amountDecimal: Int,
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
    @SerializedName("interval")
    val interval: String,
    @SerializedName("interval_count")
    val intervalCount: Int,
    @SerializedName("livemode")
    val livemode: Boolean,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("nickname")
    val nickname: Any,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("product")
    val product: String,
    @SerializedName("tiers")
    val tiers: Any,
    @SerializedName("tiers_mode")
    val tiersMode: Any,
    @SerializedName("transform_usage")
    val transformUsage: Any,
    @SerializedName("trial_period_days")
    val trialPeriodDays: Any,
    @SerializedName("usage_type")
    val usageType: String
)