package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Outcome(
    @SerializedName("network_status")
    val networkStatus: String,
    @SerializedName("reason")
    val reason: Any,
    @SerializedName("risk_level")
    val riskLevel: String,
    @SerializedName("risk_score")
    val riskScore: Int,
    @SerializedName("rule")
    val rule: Any,
    @SerializedName("seller_message")
    val sellerMessage: String,
    @SerializedName("type")
    val type: String
)