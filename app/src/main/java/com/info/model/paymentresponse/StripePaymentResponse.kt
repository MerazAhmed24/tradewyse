package com.info.model.paymentresponse


import com.google.gson.annotations.SerializedName

data class StripePaymentResponse(
    @SerializedName("application_fee_percent")
    val applicationFeePercent: Any,
    @SerializedName("billing_cycle_anchor")
    val billingCycleAnchor: Int,
    @SerializedName("billing_thresholds")
    val billingThresholds: Any,
    @SerializedName("cancel_at")
    val cancelAt: Any,
    @SerializedName("cancel_at_period_end")
    val cancelAtPeriodEnd: Boolean,
    @SerializedName("canceled_at")
    val canceledAt: Any,
    @SerializedName("collection_method")
    val collectionMethod: String,
    @SerializedName("created")
    val created: Int,
    @SerializedName("current_period_end")
    val currentPeriodEnd: Int,
    @SerializedName("current_period_start")
    val currentPeriodStart: Int,
    @SerializedName("customer")
    val customer: String,
    @SerializedName("days_until_due")
    val daysUntilDue: Any,
    @SerializedName("default_payment_method")
    val defaultPaymentMethod: Any,
    @SerializedName("default_source")
    val defaultSource: Any,
    @SerializedName("default_tax_rates")
    val defaultTaxRates: List<Any>,
    @SerializedName("discount")
    val discount: Any,
    @SerializedName("ended_at")
    val endedAt: Any,
    @SerializedName("id")
    val id: String,
    @SerializedName("items")
    val items: Items,
    @SerializedName("latest_invoice")
    val latestInvoice: LatestInvoice,
    @SerializedName("livemode")
    val livemode: Boolean,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("next_pending_invoice_item_invoice")
    val nextPendingInvoiceItemInvoice: Any,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("pause_collection")
    val pauseCollection: Any,
    @SerializedName("pending_invoice_item_interval")
    val pendingInvoiceItemInterval: Any,
    @SerializedName("pending_setup_intent")
    val pendingSetupIntent: Any,
    @SerializedName("pending_update")
    val pendingUpdate: Any,
    @SerializedName("plan")
    val plan: Plan,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("schedule")
    val schedule: Any,
    @SerializedName("start_date")
    val startDate: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("tax_percent")
    val taxPercent: Any,
    @SerializedName("transfer_data")
    val transferData: Any,
    @SerializedName("trial_end")
    val trialEnd: Any,
    @SerializedName("trial_start")
    val trialStart: Any
)