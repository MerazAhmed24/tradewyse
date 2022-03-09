package com.info.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationBundle {

    @SerializedName("modifiedOn")
    @Expose
    var modifiedOn: String? = null

    @SerializedName("read")
    @Expose
    var read: Boolean? = null

    @SerializedName("notificationText")
    @Expose
    var notificationText: String? = null

    @SerializedName("notificationId")
    @Expose
    var notificationId: String? = null

    @SerializedName("notificationType")
    @Expose
    var notificationType: String? = null

    @SerializedName("userId")
    @Expose
    var userId: String? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: String? = null

    @SerializedName("bundleNotificationId")
    @Expose
    var bundleNotificationId: String? = null

    @SerializedName("tipId")
    @Expose
    var tipId: String? = null

    @SerializedName("groupId")
    @Expose
    var groupId: String? = null

    @SerializedName("messageId")
    @Expose
    var messageId: String? = null

    @SerializedName("notificationReferenceId")
    @Expose
    var notificationReferenceId: String? = null

    @SerializedName("postedByUserName")
    @Expose
    var postedByUserName: String? = null
}