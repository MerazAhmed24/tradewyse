package com.info.tradewyse

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.info.api.APIClient
import com.info.commons.Common
import com.info.commons.Constants
import com.info.commons.TradWyseSession
import com.info.model.CardDetailsModel
import com.info.model.Subscription
import com.info.model.ValidateCouponModel
import com.info.model.paymentresponse.StripePaymentResponse
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.view.CardInputWidget
import kotlinx.android.synthetic.main.activity_confirmation_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.sendbird.android.constant.StringSet.core
import com.sendbird.android.constant.StringSet.core
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.google.android.play.core.internal.v

import com.sendbird.android.constant.StringSet.core

class ConfirmationActivity : BaseActivity() {

    private lateinit var cardInputWidget: CardInputWidget;
    private lateinit var stripe: Stripe
    private lateinit var subscription: Subscription
    lateinit var cardDetailsModel: CardDetailsModel;
    var priceFloat = 0.0f
    var couponCode = ""
    var actualPriceFloat = 0.0f
    var from = "";
    var price = "";
    var id = "";
    var name = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation_details)
        setToolBarAddTip(getString(R.string.confirm_detail))
        initView()
        initListeners()

        layoutRoot.getViewTreeObserver().addOnGlobalLayoutListener(OnGlobalLayoutListener {
            val r = Rect()
            layoutRoot.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = layoutRoot.getRootView().getHeight()
            //Log.e("screenHeight", screenHeight.toString())
            val heightDiff: Int = screenHeight - (r.bottom - r.top)
            //Log.e("heightDiff", heightDiff.toString())
            val visible = heightDiff > screenHeight / 3
            //Log.e("visible", visible.toString())
            if (visible) {

                btnSubscribe.visibility = View.GONE;
                bottomTitle.visibility = View.GONE;
            } else {

                btnSubscribe.visibility = View.VISIBLE;
                bottomTitle.visibility = View.VISIBLE;

            }
        })

        layoutMoneyBackG.setOnClickListener(View.OnClickListener {
            Common.showMessageWithCenterText(
                this,
                getString(R.string.special_offer_dialog_msg),
                getString(R.string.money_back_guarantee)
            )
        })
    }


    private fun initView() {

        stripe = Stripe(this, PaymentConfiguration.getInstance(this).publishableKey)

        if (intent != null) {
            from = intent.getStringExtra(Constants.FROM) as String;

            if (from == Constants.FROM_SUBSCRIPTION_LIST) {
                subscription = intent.getParcelableExtra("subscription")!!
                price = subscription.subscriptionPrice;
                id = subscription.id
            } else if (from == Constants.FROM_SERVICE_LIST) {
                price = intent.getStringExtra("price") as String
                id = intent.getStringExtra("serviceId") as String
            }

            cardDetailsModel = intent.getSerializableExtra("cardDetails") as CardDetailsModel
            name = intent.getStringExtra("name") as String

            if (price.equals("1000")) {
                layoutPromoCode.visibility = View.GONE
            } else {
                layoutPromoCode.visibility = View.VISIBLE
            }
            promo_code.isEnabled = true
            apply.isEnabled = true
            updateUI()
        }
    }

    private fun updateUI() {

        var maskCardNumber: String? = null
        if (cardDetailsModel.cardNumber.count() == 16) {
            //maskCardNumber = maskString(cardDetailsModel.cardNumber, 0, 12, 'x')
            maskCardNumber = cardDetailsModel.cardNumber.substring(cardDetailsModel.cardNumber.count() - 4 )
        }

        name_on_card.text = cardDetailsModel.cardName
        card_no.text = maskCardNumber
        expiry_date.text = cardDetailsModel.expiryDate
        card_cvc.text = cardDetailsModel.cvcValue
        zip_code.text = cardDetailsModel.postalCodeValue
        email_id.text = cardDetailsModel.email

        //  subscription_type.text=subscription.subscriptionType+" Total"

        /*if (price.contains(".")) {
            price = price.substring(0, price.indexOf("."))
        }*/

        if (from == Constants.FROM_SUBSCRIPTION_LIST) {
            priceFloat = price.toFloat()
            actualPriceFloat = priceFloat;
        } else {
            priceFloat = price.toFloat()
            actualPriceFloat = priceFloat;
        }

        priceFloat = (Math.round("$priceFloat".toDouble() * 100.0) / 100.0).toFloat()
        actualPriceFloat = (Math.round("$actualPriceFloat".toDouble() * 100.0) / 100.0).toFloat()

        subscription_amount.text = "$"+String.format("%.2f", priceFloat)
        pay_amount.text = "$"+String.format("%.2f", priceFloat)
        discount_ll.visibility = View.GONE

    }


    @Throws(java.lang.Exception::class)
    private fun maskString(strText: String?, start: Int, end: Int, maskChar: Char): String? {
        var start = start
        var end = end
        if (strText == null || strText == "") return ""
        if (start < 0) start = 0
        if (end > strText.length) end = strText.length
        if (start > end) throw java.lang.Exception("End index cannot be greater than start index")
        val maskLength = end - start
        if (maskLength == 0) return strText
        val sbMaskString = StringBuilder(maskLength)
        for (i in 0 until maskLength) {
            sbMaskString.append(maskChar)
        }

        var maskString = (strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength))

        val s1: String = maskString.substring(0, 4)
        val s2: String = maskString.substring(4, 8)
        val s3: String = maskString.substring(8, 12)
        val s4: String = maskString.substring(12, 16)

        return "$s1-$s2-$s3-$s4"
    }


    private fun initListeners() {
        btnSubscribe.setOnClickListener(View.OnClickListener {
            addPaymentMethod()
        })

        edit_payment_info.setOnClickListener(View.OnClickListener {
            finish();
        })

        apply.setOnClickListener(View.OnClickListener {
            if (promo_code.text.toString().trim { it <= ' ' }.isEmpty()) {
                Common.showMessage(this, getString(R.string.enter_promo_code), getString(R.string.InputRequired))
            } else {
                callValidateCouponApi(promo_code.text.toString())
                hideKeyboard()
            }
        })

    }


    private fun addPaymentMethod() {

        cardInputWidget = CardInputWidget(this);
        cardInputWidget.setCardNumber(cardDetailsModel.cardNumber)
        cardInputWidget.setCvcCode(cardDetailsModel.cvcValue)
        cardInputWidget.setExpiryDate(cardDetailsModel.expiryMonth, cardDetailsModel.expiryYears);
        //cardInputWidget.usZipCodeRequired=true

        val card: PaymentMethodCreateParams.Card? = cardInputWidget.paymentMethodCard

        card?.let {
            showProgressDialog()
            val billingDetails = PaymentMethod.BillingDetails.Builder()
                .setName(cardDetailsModel.cardName)
                .setEmail(cardDetailsModel.email)
                .build()

            var paymentMethodCreateParams =
                PaymentMethodCreateParams.create(card = card, billingDetails = billingDetails)
            stripe.createPaymentMethod(
                paymentMethodCreateParams = paymentMethodCreateParams,
                callback = object : ApiResultCallback<PaymentMethod> {

                    override fun onError(e: Exception) {
                        this@ConfirmationActivity.dismissProgressDialog()
                        Common.showMessage(
                            this@ConfirmationActivity,
                            e.localizedMessage,
                            resources.getString(R.string.messageAlert)
                        )
                    }

                    override fun onSuccess(result: PaymentMethod) {
                        Log.d("success==", "" + result)
                        Log.d("success==", "" + result.id)
                        result.id?.let {
                            if (from == Constants.FROM_SUBSCRIPTION_LIST) {
                                createStripePayment(it, id, couponCode)
                            } else if (from == Constants.FROM_SERVICE_LIST) {
                                createServiceSubscriptionPayment(it, id, couponCode);
                            }
                        } ?: run {
                            this@ConfirmationActivity.dismissProgressDialog()
                            PaymentFailureActivity.start(this@ConfirmationActivity)
                            this@ConfirmationActivity.finish()
                        }
                    }
                })

        } ?: run {
            Common.showMessage(
                this,
                resources.getString(R.string.enterCard),
                resources.getString(R.string.messageAlert)
            )
        }
    }


    //send the payment info to our server.
    private fun createStripePayment(
        paymentId: String,
        subscriptionPlanId: String,
        couponId: String
    ) {
        var apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this)
        var userName = TradWyseSession.getSharedInstance(this).userName
        apiInterface.createStripePayment(userName, paymentId, subscriptionPlanId, couponId)
            .enqueue(object : Callback<StripePaymentResponse> {
                override fun onFailure(call: Call<StripePaymentResponse>, t: Throwable) {
                    Log.e("onFailure", t.localizedMessage)
                    this@ConfirmationActivity.dismissProgressDialog()
                    PaymentFailureActivity.start(this@ConfirmationActivity)
                    this@ConfirmationActivity.finish()
                }

                override fun onResponse(
                    call: Call<StripePaymentResponse>,
                    response: Response<StripePaymentResponse>
                ) {
                    val tempResponse = response.body()
                    if (tempResponse != null) {
                        //if (tempResponse.status.equals("active", true)) {
                            this@ConfirmationActivity.dismissProgressDialog()
//                            PaymentSuccessActivity.start(this@ConfirmationActivity)
                            val intent = Intent(this@ConfirmationActivity,PaymentSuccessActivity::class.java)
                            intent.putExtra("price",priceFloat)
                            intent.putExtra("name",name)
                            intent.putExtra("from",from)
                            startActivity(intent)
                            this@ConfirmationActivity.finish()
                       /* }else{
                            this@ConfirmationActivity.dismissProgressDialog()
                            PaymentFailureActivity.start(this@ConfirmationActivity)
                            this@ConfirmationActivity.finish()
                        }*/
                    }else{
                        this@ConfirmationActivity.dismissProgressDialog()
                        PaymentFailureActivity.start(this@ConfirmationActivity)
                        this@ConfirmationActivity.finish()
                    }
                }
            })
    }


    private fun createServiceSubscriptionPayment(
        paymentId: String,
        serviceSubscriptionPlanId: String,
        couponId: String
    ) {
        var apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this)
        var userName = TradWyseSession.getSharedInstance(this).userName
        apiInterface.createServiceSubscriptionPayment(
            userName,
            paymentId,
            serviceSubscriptionPlanId,
            couponId
        ).enqueue(object : Callback<StripePaymentResponse> {
            override fun onFailure(call: Call<StripePaymentResponse>, t: Throwable) {
                Log.e("onFailure", t.localizedMessage)
                this@ConfirmationActivity.dismissProgressDialog()
                PaymentFailureActivity.start(this@ConfirmationActivity)
                this@ConfirmationActivity.finish()
            }

            override fun onResponse(
                call: Call<StripePaymentResponse>,
                response: Response<StripePaymentResponse>
            ) {
                this@ConfirmationActivity.dismissProgressDialog()
                val intent = Intent(this@ConfirmationActivity,PaymentSuccessActivity::class.java)
                intent.putExtra("price",priceFloat)
                intent.putExtra("name",name)
                intent.putExtra("from",from)
                startActivity(intent)
                this@ConfirmationActivity.finish()
            }
        })
    }


    fun callValidateCouponApi(couponId: String?) {
        val apiInterface =
            APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this@ConfirmationActivity)
        val call = apiInterface.validateCoupon(couponId)
        showProgressDialog()
        call.enqueue(object : Callback<ValidateCouponModel?> {
            override fun onResponse(
                call: Call<ValidateCouponModel?>,
                response: Response<ValidateCouponModel?>
            ) {
                dismissProgressDialog()
                var model = response.body()
                if (model != null) {
                    if (model.valid != null && model.valid) {
                        promo_code.isEnabled = false
                        apply.isEnabled = false
                        if (couponId != null) {
                            couponCode = couponId
                        }
                        priceFloat = actualPriceFloat
                        var discount = ((priceFloat * model?.percentOff!!) / 100).toFloat()
                        priceFloat = (priceFloat - discount).toFloat()
                        pay_amount.text = "$"+String.format("%.2f", priceFloat)
                        discount_ll.visibility = View.VISIBLE
                        discount_amount.text = "-$"+String.format("%.2f", discount)

                    } else {
                        Common.showMessage(
                            this@ConfirmationActivity,
                            resources.getString(R.string.please_enter_a_valid_promo_code),
                            resources.getString(R.string.invalid_promo_code)
                        )
                    }
                } else {
                    Common.showMessage(
                        this@ConfirmationActivity,
                        resources.getString(R.string.please_enter_a_valid_promo_code),
                        resources.getString(R.string.invalid_promo_code)
                    )
                }
            }

            override fun onFailure(call: Call<ValidateCouponModel?>, t: Throwable) {
                Log.e("Failure===", "===" + t.message);
                //Toast.makeText(this@ConfirmationActivity, "Something went wrong!please try again", Toast.LENGTH_LONG).show()
                dismissProgressDialog()
                Common.showMessage(
                    this@ConfirmationActivity,
                    resources.getString(R.string.please_enter_a_valid_promo_code),
                    resources.getString(R.string.invalid_promo_code)
                )
            }
        })
    }


}