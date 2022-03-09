package com.info.tradewyse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import com.info.api.APIClient
import com.info.commons.Common
import com.info.commons.Constants
import com.info.commons.TradWyseSession
import com.info.model.CardDetailsModel
import com.info.model.Subscription
import com.info.model.paymentresponse.StripePaymentResponse
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.view.CardInputWidget
import kotlinx.android.synthetic.main.activity_checkout2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class CheckoutActivity : BaseActivity() {

    private lateinit var stripe: Stripe
    private lateinit var subscription: Subscription
    private lateinit var cardInputWidget: CardInputWidget
    private lateinit var card: Card
    lateinit var emailId: String;
    private var isFromSpecialOfferActivity: Boolean = false;

    var name: String = ""
    var price: String = ""
    var serviceId: String = ""
    var from: String = ""
    var priceFloat: Double = 0.0
    var subsPrice : String = "";
    var servicePrice : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout2)
        //  setToolBarAddTip(getString(R.string.subscribtion))
        setToolBarAddTip(getString(R.string.purchase))
        //   isFromSpecialOfferActivity = intent.getBooleanExtra("isFromSpecialOfferActivity", false);
        initializeView()
        /*val text = "<font color=#2b2b2b>Credit card </font> <font color=#69738E>Secured by </font><font color=#2b2b2b><b>stripe </b></font>"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tv_text.setText(Html.fromHtml(text, 0))
        }*/
    }

    companion object {
        @JvmStatic
        fun start(context: Context, subscription: Subscription?, isFromSpecialOfferActivity: Boolean, from: String) {
            val starter = Intent(context, CheckoutActivity::class.java)
            starter.putExtra("subscription", subscription)
            starter.putExtra(Constants.FROM, from)
            starter.putExtra("isFromSpecialOfferActivity", isFromSpecialOfferActivity)
            context.startActivity(starter)
        }
    }

    private fun initializeView() {
        //test comment

        if (intent != null) {

            from=intent.getStringExtra(Constants.FROM) as String;

            if (intent.getStringExtra(Constants.FROM) == Constants.FROM_SUBSCRIPTION_LIST) {

                subscription = intent.getParcelableExtra("subscription")!!

                name = subscription.subscriptionType
                price = subscription.subscriptionPrice
                //priceFloat = price.toDouble() / 100

            } else if (intent.getStringExtra(Constants.FROM) == Constants.FROM_SPECIAL_OFFER) {

                subscription = intent.getParcelableExtra("subscription")!!
                price = subscription.subscriptionPrice
                //priceFloat = price.toDouble() / 100
                name="24-Hours";
                from=Constants.FROM_SUBSCRIPTION_LIST;
            }
            else if (intent.getStringExtra(Constants.FROM) == Constants.FROM_SERVICE_LIST)
            {
                serviceId=intent.getStringExtra("id") as String;
                price=intent.getStringExtra("price") as String;
                //priceFloat=price.toDouble();
                name=intent.getStringExtra("description") as String;
                textViewServiceType.text = "$name";

            } else if (intent.getStringExtra(Constants.FROM) == Constants.FROM_PROFILE_TABBED_ACTIVITY) {

                subscription = intent.getParcelableExtra("subscription")!!
                subsPrice = intent.getStringExtra("subsPrice")!!
                servicePrice = intent.getStringExtra("servicePrice")!!
                price = intent.getStringExtra("price") as String;
            }
        }

        var stringAmount = price.toFloat()
//        txtSubscriptionName.text = "$$stringAmount $name"
        //textViewAmount.text = "$stringAmount"
        stringAmount = (Math.round("$stringAmount".toDouble() * 100.0) / 100.0).toFloat()
        textViewAmount.text = "$stringAmount";

        stripe = Stripe(this, PaymentConfiguration.getInstance(this).publishableKey)
        btnSubscribe.setOnClickListener {
            moveToConfirmationScreen()
        }

        TradWyseSession.getSharedInstance(this).firstName
        emailId = TradWyseSession.getSharedInstance(this).emailId
        Log.d("emailId", "" + emailId);
        setRegex(emailId)

//        cvcEditText.setHint("CVV")

    }


    fun setRegex(email: String) {
        var p = """^([^@]{3})([^@]+)([^@]{3}@)""".toRegex()

        if (email.contains("@")) {
            var array = email.split('@');

            if (array[0].length == 1) {
                p = """^([^@]{1})([^@]+)([^@]{0}@)""".toRegex()
            } else if (array[0].length == 2) {
                p = """^([^@]{1})([^@]+)([^@]{0}@)""".toRegex()
            } else if (array[0].length == 3) {
                p = """^([^@]{1})([^@]+)([^@]{1}@)""".toRegex()
            } else if (array[0].length == 4) {
                p = """^([^@]{1})([^@]+)([^@]{1}@)""".toRegex()
            } else if (array[0].length == 5 || array[0].length == 6) {
                p = """^([^@]{2})([^@]+)([^@]{2}@)""".toRegex()
            }


            txt_email.text = email.toLowerCase().replace(p, {
                it.groupValues[1] + "*".repeat(it.groupValues[2].length) + it.groupValues[3]
            });

        }

    }

    private fun moveToConfirmationScreen() {

        var cardNumber = cardNumberEditText.cardNumber;
        var isValidDate = expiryDateEditText.isDateValid;
        var cvcValue = cvcEditText.editText?.text.toString();
        var postalcodeValue = postalCodeEditText.editText?.text.toString();
        val name1 = edtName.editText?.text.toString()
        val email = edtEmail.editText?.text.toString()


        Log.d("cardNumber", "" + cardNumber);
        Log.d("isValidDate", "" + isValidDate);
        Log.d("date", "" + expiryDateEditText.text.toString());
        Log.d("cvcValue", "" + cvcValue);
        Log.d("postalCodeEditText", "" + postalcodeValue);
        Log.d("name", "" + name1);
        Log.d("email", "" + email);


        if (validate()) {

            var expiryDate = expiryDateEditText.text.toString().split("/").toTypedArray();

            var month = expiryDate[0].toInt();
            var year = expiryDate[1].toInt();


            var cardDetailsModel = CardDetailsModel()
            cardDetailsModel.cardName = name1
            cardDetailsModel.cardNumber = cardNumber;
            cardDetailsModel.cvcValue = cvcValue
            cardDetailsModel.expiryMonth = month
            cardDetailsModel.expiryYears = year
            cardDetailsModel.postalCodeValue = postalcodeValue;
            cardDetailsModel.email = email;
            cardDetailsModel.expiryDate = expiryDateEditText.text.toString()

            val intent=Intent(this, ConfirmationActivity::class.java);

            intent.putExtra("cardDetails", cardDetailsModel)
            intent.putExtra(Constants.FROM,from)
            intent.putExtra("price",price)
            intent.putExtra("serviceId",serviceId)
            intent.putExtra("name",name)

            if (from==Constants.FROM_SUBSCRIPTION_LIST || from == Constants.FROM_SPECIAL_OFFER)
            {
                intent.putExtra("subscription", subscription)
            }

             startActivity(intent)

        }

    }

    private fun validate(): Boolean {

        if (edtName.editText?.text.toString().trim { it <= ' ' }.isEmpty()) {
            Common.showMessage(this, "Enter name as it appears on card.", getString(R.string.required))
            edtName.editText?.requestFocus()
            return false
        }
        else if(cardNumberEditText.text.toString().trim { it <= ' '}.isEmpty())
        {
            Common.showMessage(this, "Enter credit card number.", resources.getString(R.string.required))
            return false
        }
        else if (!cardNumberEditText.isCardNumberValid) {
            Common.showMessage(this, "Invalid credit card number.", resources.getString(R.string.messageAlert))
            return false
        }
        else if (expiryDateEditText.text.toString().trim {it<= ' '}.isEmpty())
        {
            Common.showMessage(this, "Enter card expiry date.", resources.getString(R.string.required))
            return false

        }
        else if (!expiryDateEditText.isDateValid) {
            Common.showMessage(this, "Invalid expiry date.", resources.getString(R.string.messageAlert))
            return false
        } else if (cvcEditText.editText?.text.toString().trim { it <= ' ' }.isEmpty()) {
            Common.showMessage(this, "Enter CVV (security code) number.", resources.getString(R.string.required))
            return false
        } else if (postalCodeEditText.editText?.text.toString().trim { it <= ' ' }.isEmpty()) {
            Common.showMessage(this, "Enter your billing zip code.", resources.getString(R.string.required))
            return false
        }

        else if (edtEmail.editText?.text.toString().trim { it <= ' ' }.isEmpty()) {
            Common.showMessage(this, "Enter your TradeTips account email.", resources.getString(R.string.required))
            edtEmail.requestFocus()
            return false
        }

        else if (!edtEmail.editText?.text.toString().trim().equals(emailId,ignoreCase = true)) {
            Common.showMessage(this, "Please enter your TradeTips account email.", "Alert")
            edtEmail.requestFocus()
            return false
        } else {
            return true
        }

    }
}