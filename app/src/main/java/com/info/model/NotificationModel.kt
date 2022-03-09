package com.info.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Amit Gupta on 15,June,2021
 */
data class NotificationModel(
    @SerializedName("notificationId") val notificationId: String,
    @SerializedName("notificationText") val notificationText: String,
    @SerializedName("notificationType") val notificationType: String,
    @SerializedName("tipId") val tipId: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("createdOn") val createdOn: Long,
    @SerializedName("modifiedOn") val modifiedOn: Long,
    @SerializedName("expireTime") val expireTime: String,
    @SerializedName("read") var read: Boolean
)
