package com.info.tradewyse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.info.adapter.TipsAdapter.context
import com.info.api.APIClient
import com.info.commons.Common
import com.info.commons.Constants
import com.info.commons.StringHelper
import com.info.commons.TradWyseSession
import com.info.model.UserSubscriptionDetail
import kotlinx.android.synthetic.main.activity_confirmation_details.*
import kotlinx.android.synthetic.main.activity_payment_success.*
import kotlinx.android.synthetic.main.toolbar_add_tip.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentSuccessActivity : BaseActivity() {

    var price = 0.0f;
    var name = "";
    var from = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)
        var firstName = TradWyseSession.getSharedInstance(this).userName
        btnLoginToUnlockSubs.setOnClickListener {


            val intent=Intent(this, DashBoardActivity::class.java);
            intent.putExtra(Constants.FROM, Constants.FROM_DASHBOARD)
            startActivity(intent)
            finishAffinity()

        }

        if(intent != null) {
            price = intent.getFloatExtra("price", 0.0f).toFloat();
            name = intent.getStringExtra("name").toString();
            from = intent.getStringExtra(Constants.FROM) as String;
        }

        price = (Math.round("$price".toDouble() * 100.0) / 100.0).toFloat()
        txtAmount.text = "$"+String.format("%.2f", price)

        if (from == Constants.FROM_SERVICE_LIST) {
            txtDurationMsg.text = "$name";
        } else {
            txtDurationMsg.text = StringHelper.capitalFirstLetter("$name") + " TradeTips Subscription";
        }

        textViewMoneyBackGaurantee.setOnClickListener(View.OnClickListener {
            Common.showMessageWithCenterText(this, getString(R.string.special_offer_dialog_msg), getString(R.string.money_back_guarantee))
        })

    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PaymentSuccessActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val intent=Intent(this, DashBoardActivity::class.java);
        intent.putExtra(Constants.FROM, Constants.FROM_DASHBOARD)
        startActivity(intent)
        finishAffinity()
    }

}
