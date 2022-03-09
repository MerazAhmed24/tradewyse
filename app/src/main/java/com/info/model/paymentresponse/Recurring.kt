package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Recurring(
    @SerializedName("aggregate_usage")
    val aggregateUsage: Any,
    @SerializedName("interval")
    val interval: String,
    @SerializedName("interval_count")
    val intervalCount: Int,
    @SerializedName("trial_period_days")
    val trialPeriodDays: Any,
    @SerializedName("usage_type")
    val usageType: String
)