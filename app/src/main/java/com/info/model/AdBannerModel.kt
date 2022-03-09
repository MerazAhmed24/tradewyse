package com.info.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Amit Gupta on 21,June,2021
 */
data class AdBannerModel(
    @SerializedName("id") val id: String,
    @SerializedName("image") val image: String,
    @SerializedName("title") val title: String,
    @SerializedName("shortDescription") val shortDescription: String,
    @SerializedName("longDescription") val longDescription: String,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("createdOn") val createdOn: String,
    @SerializedName("modifiedOn") val modifiedOn: String,
    @SerializedName("image1") val image1: String,
    @SerializedName("image2") val image2: String,
    @SerializedName("image3") val image3: String,
    @SerializedName("title1") val title1: String,
    @SerializedName("title2") val title2: String,
    @SerializedName("title3") val title3: String,
    @SerializedName("shortDescription1") val shortDescription1: String,
    @SerializedName("shortDescription2") val shortDescription2: String,
    @SerializedName("shortDescription3") val shortDescription3: String,
    @SerializedName("longDescription1") val longDescription1: String,
    @SerializedName("longDescription2") val longDescription2: String,
    @SerializedName("longDescription3") val longDescription3: String,
    @SerializedName("isSingleBanner") val isSingleBanner: Boolean,
    var isDisplayed: Boolean
)
