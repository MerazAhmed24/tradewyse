package com.info.tradewyse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.info.api.APIClient
import com.info.commons.Common
import com.info.commons.Constants
import com.info.commons.StringHelper
import com.info.model.RefundRequestResponse
import com.info.model.SubscriptionPlan
import com.info.model.UserSubscriptionDetail
import kotlinx.android.synthetic.main.activity_subscription_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SubscriptionListActivity : BaseActivity() {
    lateinit var context: Context
    lateinit var list:List<SubscriptionPlan?>
    var productSubscriptionId:String?=null
    var select_subscription="Select Subscription"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription_list)
        context = applicationContext;
        setToolBarAddTip(getString(R.string.subscribtion))
        initializeViews()
    }

    private fun initializeViews(){

        spinner.setOnItemSelectedListener { view, position, id, item ->
           if(!item.equals(select_subscription)){
               productSubscriptionId=list[position]!!.id
           }

        }


        btnRequestRefund.setOnClickListener {
            var selectedIndex=spinner.selectedIndex
          if(selectedIndex!=0&&!StringHelper.isEmpty(productSubscriptionId)){
              var reason=edtReason.text.toString()
              if(!reason.isNullOrBlank()){
                  createRefundRequest(reason, tradWyseSession!!.userId, productSubscriptionId!!)
              }else{

                  //edtUserName.setError(getResources().getString(R.string.EnterUsername));
                  Common.showMessage(context, resources.getString(R.string.cancelRequestReason), resources.getString(R.string.InputRequired))
                  edtReason.requestFocus()
              }

          }else{
              Common.showMessage(context, resources.getString(R.string.SelectSubscriptionForCancel), resources.getString(R.string.SelectSubscription))
             // Toast.makeText(this@SubscriptionListActivity, "Please select Subscription", Toast.LENGTH_LONG).show()
          }

        }
    }

    private fun createRefundRequest(reason: String, userId: String, productSubscriptionId: String) {
        val apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context)
        val call = apiInterface.createRefundRequest(
                userId, reason, productSubscriptionId
        )
        showProgressDialog()
        call.enqueue(object : Callback<RefundRequestResponse?> {
            override fun onResponse(call: Call<RefundRequestResponse?>, response: Response<RefundRequestResponse?>) {
                var refundRequestResponse = response.body()
                dismissProgressDialog()
                refundRequestResponse?.let {
                    Toast.makeText(this@SubscriptionListActivity, refundRequestResponse.message, Toast.LENGTH_LONG).show()
                    finish()
                } ?: kotlin.run {
                    Toast.makeText(this@SubscriptionListActivity, "Unable to processed your request, please try again..", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<RefundRequestResponse?>, t: Throwable) {
                Toast.makeText(this@SubscriptionListActivity, "Unable to processed your request, please try again..", Toast.LENGTH_LONG).show()
                dismissProgressDialog()
            }
        })
    }

    companion object{
        fun newIntent(context: Context):Intent{
            var intent= Intent(context, SubscriptionListActivity::class.java)
            return intent
        }
    }


}
