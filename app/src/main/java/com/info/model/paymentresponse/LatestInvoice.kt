package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class LatestInvoice(
    @SerializedName("account_country")
    val accountCountry: String,
    @SerializedName("account_name")
    val accountName: String,
    @SerializedName("amount_due")
    val amountDue: Int,
    @SerializedName("amount_paid")
    val amountPaid: Int,
    @SerializedName("amount_remaining")
    val amountRemaining: Int,
    @SerializedName("application_fee_amount")
    val applicationFeeAmount: Any,
    @SerializedName("attempt_count")
    val attemptCount: Int,
    @SerializedName("attempted")
    val attempted: Boolean,
    @SerializedName("auto_advance")
    val autoAdvance: Boolean,
    @SerializedName("billing_reason")
    val billingReason: String,
    @SerializedName("charge")
    val charge: String,
    @SerializedName("collection_method")
    val collectionMethod: String,
    @SerializedName("created")
    val created: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("custom_fields")
    val customFields: Any,
    @SerializedName("customer")
    val customer: String,
    @SerializedName("customer_address")
    val customerAddress: Any,
    @SerializedName("customer_email")
    val customerEmail: String,
    @SerializedName("customer_name")
    val customerName: Any,
    @SerializedName("customer_phone")
    val customerPhone: Any,
    @SerializedName("customer_shipping")
    val customerShipping: Any,
    @SerializedName("customer_tax_exempt")
    val customerTaxExempt: String,
    @SerializedName("customer_tax_ids")
    val customerTaxIds: List<Any>,
    @SerializedName("default_payment_method")
    val defaultPaymentMethod: Any,
    @SerializedName("default_source")
    val defaultSource: Any,
    @SerializedName("default_tax_rates")
    val defaultTaxRates: List<Any>,
    @SerializedName("deleted")
    val deleted: Any,
    @SerializedName("description")
    val description: Any,
    @SerializedName("discount")
    val discount: Any,
    @SerializedName("due_date")
    val dueDate: Any,
    @SerializedName("ending_balance")
    val endingBalance: Int,
    @SerializedName("footer")
    val footer: Any,
    @SerializedName("hosted_invoice_url")
    val hostedInvoiceUrl: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("invoice_pdf")
    val invoicePdf: String,
    @SerializedName("lines")
    val lines: Lines,
    @SerializedName("livemode")
    val livemode: Boolean,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("next_payment_attempt")
    val nextPaymentAttempt: Any,
    @SerializedName("number")
    val number: String,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("paid")
    val paid: Boolean,
    @SerializedName("payment_intent")
    val paymentIntent: PaymentIntent,
    @SerializedName("period_end")
    val periodEnd: Int,
    @SerializedName("period_start")
    val periodStart: Int,
    @SerializedName("post_payment_credit_notes_amount")
    val postPaymentCreditNotesAmount: Int,
    @SerializedName("pre_payment_credit_notes_amount")
    val prePaymentCreditNotesAmount: Int,
    @SerializedName("receipt_number")
    val receiptNumber: Any,
    @SerializedName("starting_balance")
    val startingBalance: Int,
    @SerializedName("statement_descriptor")
    val statementDescriptor: Any,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_transitions")
    val statusTransitions: StatusTransitions,
    @SerializedName("subscription")
    val subscription: String,
    @SerializedName("subscription_proration_date")
    val subscriptionProrationDate: Any,
    @SerializedName("subtotal")
    val subtotal: Int,
    @SerializedName("tax")
    val tax: Any,
    @SerializedName("tax_percent")
    val taxPercent: Any,
    @SerializedName("threshold_reason")
    val thresholdReason: Any,
    @SerializedName("total")
    val total: Int,
    @SerializedName("total_tax_amounts")
    val totalTaxAmounts: List<Any>,
    @SerializedName("transfer_data")
    val transferData: Any,
    @SerializedName("webhooks_delivered_at")
    val webhooksDeliveredAt: Int
)