package com.info.tradewyse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.info.adapter.TipsAdapter.context
import com.info.commons.Common
import com.info.commons.TradWyseSession
import kotlinx.android.synthetic.main.activity_payment_failure.*

class PaymentFailureActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_failure)
        var firstName = TradWyseSession.getSharedInstance(this).userName
        btnTryAgainPaymentFail.setOnClickListener {
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PaymentFailureActivity::class.java)
            context.startActivity(starter)
        }
    }
}
