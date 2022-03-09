package com.info.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class TipOuterResponse : Parcelable {

    @SerializedName("tipCrossoverResponse")
    @Expose
    var tipCrossoverResponse: List<TipResponse?>? = null

    @SerializedName("buyStockCrossover")
    @Expose
    var buyStock: List<BuyStock?>? = null

    @SerializedName("sellStockCrossover")
    @Expose
    var sellStock: List<SellStock?>? = null

    @SerializedName("buyMacDGif")
    @Expose
    var buyMacDGif: String? = null

    @SerializedName("sellMacDGif")
    @Expose
    var sellMacDGif: String? = null

    @SerializedName("tipCrossoverResponseAfterMacD")
    @Expose
    var tipCrossoverResponseAfterMacD : List<TipResponse?>? = null

}