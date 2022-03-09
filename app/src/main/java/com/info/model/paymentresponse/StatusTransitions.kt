package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class StatusTransitions(
    @SerializedName("finalized_at")
    val finalizedAt: Int,
    @SerializedName("marked_uncollectible_at")
    val markedUncollectibleAt: Any,
    @SerializedName("paid_at")
    val paidAt: Int,
    @SerializedName("voided_at")
    val voidedAt: Any
)