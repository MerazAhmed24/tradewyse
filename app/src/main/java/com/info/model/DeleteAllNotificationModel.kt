package com.info.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Amit Gupta on 17,June,2021
 */
data class DeleteAllNotificationModel(
    @SerializedName("NotificationId") val notificationId: String,
    @SerializedName("deleteStatus") val deleteStatus: Boolean,
    @SerializedName("message") val message: String
)
