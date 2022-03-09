package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class DataXX(
    @SerializedName("alternate_statement_descriptors")
    val alternateStatementDescriptors: Any,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("amount_refunded")
    val amountRefunded: Int,
    @SerializedName("application")
    val application: Any,
    @SerializedName("application_fee")
    val applicationFee: Any,
    @SerializedName("application_fee_amount")
    val applicationFeeAmount: Any,
    @SerializedName("authorization_code")
    val authorizationCode: Any,
    @SerializedName("balance_transaction")
    val balanceTransaction: String,
    @SerializedName("billing_details")
    val billingDetails: BillingDetails,
    @SerializedName("calculated_statement_descriptor")
    val calculatedStatementDescriptor: String,
    @SerializedName("captured")
    val captured: Boolean,
    @SerializedName("created")
    val created: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("customer")
    val customer: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("destination")
    val destination: Any,
    @SerializedName("dispute")
    val dispute: Any,
    @SerializedName("disputed")
    val disputed: Boolean,
    @SerializedName("failure_code")
    val failureCode: Any,
    @SerializedName("failure_message")
    val failureMessage: Any,
    @SerializedName("fraud_details")
    val fraudDetails: FraudDetails,
    @SerializedName("id")
    val id: String,
    @SerializedName("invoice")
    val invoice: String,
    @SerializedName("level3")
    val level3: Any,
    @SerializedName("livemode")
    val livemode: Boolean,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("on_behalf_of")
    val onBehalfOf: Any,
    @SerializedName("order")
    val order: Any,
    @SerializedName("outcome")
    val outcome: Outcome,
    @SerializedName("paid")
    val paid: Boolean,
    @SerializedName("payment_intent")
    val paymentIntent: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("payment_method_details")
    val paymentMethodDetails: PaymentMethodDetails,
    @SerializedName("receipt_email")
    val receiptEmail: String,
    @SerializedName("receipt_number")
    val receiptNumber: Any,
    @SerializedName("receipt_url")
    val receiptUrl: String,
    @SerializedName("refunded")
    val refunded: Boolean,
    @SerializedName("refunds")
    val refunds: Refunds,
    @SerializedName("review")
    val review: Any,
    @SerializedName("shipping")
    val shipping: Any,
    @SerializedName("source")
    val source: Any,
    @SerializedName("source_transfer")
    val sourceTransfer: Any,
    @SerializedName("statement_descriptor")
    val statementDescriptor: Any,
    @SerializedName("statement_descriptor_suffix")
    val statementDescriptorSuffix: Any,
    @SerializedName("status")
    val status: String,
    @SerializedName("transfer")
    val transfer: Any,
    @SerializedName("transfer_data")
    val transferData: Any,
    @SerializedName("transfer_group")
    val transferGroup: Any
)