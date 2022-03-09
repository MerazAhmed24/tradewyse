package com.info.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class TipResponse:Parcelable {

    @SerializedName("pinCount")
    @Expose
    var pinCount = 0

    @SerializedName("likeCount")
    @Expose
    var likeCount = 0

    @SerializedName("commentCount")
    @Expose
    var commentCount = 0

    @SerializedName("userHideStatus")
    @Expose
    var isUserHideStatus = false

    @SerializedName("userPinStatus")
    @Expose
    var isUserPinStatus = false

    @SerializedName("userLikeStatus")
    @Expose
    var isUserLikeStatus = false

    @SerializedName("tip")
    @Expose
    var tip: Tips? = null

}