package com.info.model

import com.google.gson.annotations.SerializedName

data class PinResponse(
        @SerializedName("id") val id: String,
        @SerializedName("tipId") val tipId: String,
        @SerializedName("userId") val userId: String,
        @SerializedName("isPin") val isPin: Boolean,
        @SerializedName("isHide") val isHide: Boolean,
        @SerializedName("userLiked") val userLiked: Boolean,
        @SerializedName("createdOn") val createdOn: Int,
        @SerializedName("modifiedOn") val modifiedOn: Int)