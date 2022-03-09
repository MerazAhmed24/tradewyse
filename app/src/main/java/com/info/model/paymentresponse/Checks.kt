package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class Checks(
    @SerializedName("address_line1_check")
    val addressLine1Check: Any,
    @SerializedName("address_postal_code_check")
    val addressPostalCodeCheck: Any,
    @SerializedName("cvc_check")
    val cvcCheck: String
)