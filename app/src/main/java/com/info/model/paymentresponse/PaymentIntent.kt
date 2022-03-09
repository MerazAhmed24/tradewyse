package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class PaymentIntent(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("amount_capturable")
    val amountCapturable: Int,
    @SerializedName("amount_received")
    val amountReceived: Int,
    @SerializedName("application")
    val application: Any,
    @SerializedName("application_fee_amount")
    val applicationFeeAmount: Any,
    @SerializedName("canceled_at")
    val canceledAt: Any,
    @SerializedName("cancellation_reason")
    val cancellationReason: Any,
    @SerializedName("capture_method")
    val captureMethod: String,
    @SerializedName("charges")
    val charges: Charges,
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("confirmation_method")
    val confirmationMethod: String,
    @SerializedName("created")
    val created: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("customer")
    val customer: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("invoice")
    val invoice: String,
    @SerializedName("last_payment_error")
    val lastPaymentError: Any,
    @SerializedName("livemode")
    val livemode: Boolean,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("next_action")
    val nextAction: Any,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("on_behalf_of")
    val onBehalfOf: Any,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("payment_method_options")
    val paymentMethodOptions: PaymentMethodOptions,
    @SerializedName("payment_method_types")
    val paymentMethodTypes: List<String>,
    @SerializedName("receipt_email")
    val receiptEmail: Any,
    @SerializedName("review")
    val review: Any,
    @SerializedName("setup_future_usage")
    val setupFutureUsage: String,
    @SerializedName("shipping")
    val shipping: Any,
    @SerializedName("source")
    val source: Any,
    @SerializedName("statement_descriptor")
    val statementDescriptor: Any,
    @SerializedName("statement_descriptor_suffix")
    val statementDescriptorSuffix: Any,
    @SerializedName("status")
    val status: String,
    @SerializedName("transfer_data")
    val transferData: Any,
    @SerializedName("transfer_group")
    val transferGroup: Any
)