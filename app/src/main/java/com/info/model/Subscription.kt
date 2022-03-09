package com.info.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subscription(
        @SerializedName("createdOn")
        val createdOn: Long,
        @SerializedName("id")
        val id: String,
        @SerializedName("modifiedOn")
        val modifiedOn: Long,
        @SerializedName("subscriptionPrice")
        val subscriptionPrice: String,
        @SerializedName("subscriptionStatus")
        val subscriptionStatus: String,
        @SerializedName("subscriptionType")
        val subscriptionType: String,
        @SerializedName("planSubscribedByUser")
        val planSubscribedByUser: Boolean,

        var isSelected_: Boolean

) : Parcelable {

    var isSelected: Boolean
        get() = isSelected_
        set(value) {
            isSelected_ = value
        }

}
