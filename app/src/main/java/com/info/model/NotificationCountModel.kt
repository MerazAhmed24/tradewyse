package com.info.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Amit Gupta
 */
data class NotificationCountModel(
    @SerializedName("unReadCount") val unReadCount: Int
)