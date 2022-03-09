package com.info.model
import com.google.gson.annotations.SerializedName

data class RefundRequestResponse(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Boolean = false,
    @SerializedName("title")
    var title: String=""
)