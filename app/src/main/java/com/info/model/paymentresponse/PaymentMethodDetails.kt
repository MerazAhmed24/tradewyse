package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class PaymentMethodDetails(
    @SerializedName("ach_credit_transfer")
    val achCreditTransfer: Any,
    @SerializedName("ach_debit")
    val achDebit: Any,
    @SerializedName("acss_debit")
    val acssDebit: Any,
    @SerializedName("alipay")
    val alipay: Any,
    @SerializedName("au_becs_debit")
    val auBecsDebit: Any,
    @SerializedName("bancontact")
    val bancontact: Any,
    @SerializedName("bitcoin")
    val bitcoin: Any,
    @SerializedName("card")
    val card: Card,
    @SerializedName("card_present")
    val cardPresent: Any,
    @SerializedName("eps")
    val eps: Any,
    @SerializedName("fpx")
    val fpx: Any,
    @SerializedName("giropay")
    val giropay: Any,
    @SerializedName("ideal")
    val ideal: Any,
    @SerializedName("interac_present")
    val interacPresent: Any,
    @SerializedName("klarna")
    val klarna: Any,
    @SerializedName("multibanco")
    val multibanco: Any,
    @SerializedName("p24")
    val p24: Any,
    @SerializedName("sepa_credit_transfer")
    val sepaCreditTransfer: Any,
    @SerializedName("sepa_debit")
    val sepaDebit: Any,
    @SerializedName("sofort")
    val sofort: Any,
    @SerializedName("stripe_account")
    val stripeAccount: Any,
    @SerializedName("type")
    val type: String,
    @SerializedName("wechat")
    val wechat: Any
)