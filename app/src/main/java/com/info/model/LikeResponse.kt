package com.info.model
import com.google.gson.annotations.SerializedName
data class LikeResponse(
    @SerializedName("createdOn")
    val createdOn: Long,
    @SerializedName("id")
    val id: String,
    @SerializedName("isPin")
    val isPin: Boolean,
    @SerializedName("modifiedOn")
    val modifiedOn: Long,
    @SerializedName("tipId")
    val tipId: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userLiked")
    val userLiked: Boolean
)