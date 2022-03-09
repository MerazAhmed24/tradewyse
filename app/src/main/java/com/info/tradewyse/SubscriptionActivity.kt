package com.info.tradewyse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.info.adapter.SubscriptionAdapter
import com.info.api.APIClient
import com.info.commons.Common
import com.info.commons.Constants
import com.info.commons.StringHelper
import com.info.commons.TradWyseSession
import com.info.model.FooterModel
import com.info.model.NotificationCountModel
import com.info.model.Subscription
import com.info.model.userServiceResponse.ServiceSubscriptionPlan
import com.info.tradewyse.MoreTabActivity.Companion.startMoreTabActivity
import com.info.tradewyse.NotificationActivity.Companion.starNotificationActivity
import com.info.tradewyse.databinding.ActivitySubscriptionBinding
import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.android.synthetic.main.toolbar_add_tip.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SubscriptionActivity : BaseActivity(), View.OnClickListener {
    lateinit var context: Context;
    lateinit var firstName: String;
    var subscriptionArrayList = ArrayList<Subscription>();
    lateinit var subscriptionBinding: ActivitySubscriptionBinding
    var pos: Int = 0;
    var from = "";
    lateinit var serviceSubscriptionPlan: ServiceSubscriptionPlan;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        subscriptionBinding = DataBindingUtil.setContentView(this, R.layout.activity_subscription)
        val footerModel = FooterModel(false, false, false, false, false)
        getNotificationCount()
        subscriptionBinding.setFooterModel(footerModel)
        context = applicationContext;
        // setToolBarAddTip(getString(R.string.make_a_tip))
        setToolBarAddTip(getString(R.string.subscription))
        initializeViews()
    }

    private fun initializeViews() {

        firstName = TradWyseSession.getSharedInstance(this).userName

        if (intent != null) {
            from = intent.getStringExtra(Constants.FROM).toString()

            if (intent.getParcelableExtra<Parcelable?>("serviceSubscriptionPlan") != null) {
                serviceSubscriptionPlan = intent.getParcelableExtra("serviceSubscriptionPlan")!!
            }

            if (from == Constants.FROM_DASHBOARD || from == Constants.FROM_SETTING) {
                setTabThreeData()
            } else if (from == Constants.FROM_GRAPH_STOCK_PREDICTION) {
                setTabTwoData()
            } else if (from == Constants.FROM_PROFILE_TABBED_ACTIVITY) {
                setTabOneData()
            }
        }
        tv_money_back_guarantee.setOnClickListener(this)
        txt_one.setOnClickListener(this)
        txt_two.setOnClickListener(this)
        txt_three.setOnClickListener(this)
        special_offer.setOnClickListener(this)
        ll_see_our.setOnClickListener(this)
        btn_continue_special_offer.setOnClickListener(this)

        progress.visibility = View.VISIBLE
        val apiService = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this)
        var call = apiService.allSubscriptionPlan
        call.enqueue(object : Callback<List<Subscription>> {
            override fun onResponse(
                call: Call<List<Subscription>>,
                response: Response<List<Subscription>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        var adapter = SubscriptionAdapter(
                            it as ArrayList<Subscription>,
                            this@SubscriptionActivity
                        )
                        subscriptionArrayList = it as ArrayList<Subscription>;
                        recSubscriptionList.adapter = adapter
                        recSubscriptionList.layoutManager =
                            GridLayoutManager(this@SubscriptionActivity, 2)

                        // Check the first plan is purchased or not for continue button enable/disable.
                        if (subscriptionArrayList.get(0).planSubscribedByUser) {
                            btn_continue_special_offer.isEnabled = false;
                            btn_continue_special_offer.setTextColor(
                                ContextCompat.getColor(
                                    this@SubscriptionActivity,
                                    R.color.textColor
                                )
                            )
                        } else {
                            btn_continue_special_offer.isEnabled = true;
                            btn_continue_special_offer.setTextColor(
                                ContextCompat.getColor(
                                    this@SubscriptionActivity,
                                    R.color.text_color_white
                                )
                            )
                        }

                        adapter.setOnSubscriptionListener(object :
                            SubscriptionAdapter.SubscriptionListener {
                            override fun onSubscriptionClick(position: Int) {
                                pos = position;
                                if (subscriptionArrayList.get(position).planSubscribedByUser) {
                                    btn_continue_special_offer.isEnabled = false;
                                    btn_continue_special_offer.setTextColor(
                                        ContextCompat.getColor(
                                            this@SubscriptionActivity,
                                            R.color.textColor
                                        )
                                    )
                                } else {
                                    btn_continue_special_offer.isEnabled = true;
                                    btn_continue_special_offer.setTextColor(
                                        ContextCompat.getColor(
                                            this@SubscriptionActivity,
                                            R.color.text_color_white
                                        )
                                    )
                                }
                            }
                        })

                    }
                }
                progress.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<Subscription>>, t: Throwable) {
                progress.visibility = View.GONE
            }
        })
    }

    companion object {
        fun newIntent(context: Context): Intent {
            var intent = Intent(context, SubscriptionActivity::class.java)
            return intent
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_money_back_guarantee-> Common.showMessageWithCenterText(
                this,
                getString(R.string.special_offer_dialog_msg),
                getString(R.string.money_back_guarantee)
            )
            R.id.txt_one -> setTabOneData()
            R.id.txt_two -> setTabTwoData()
            R.id.txt_three -> setTabThreeData()
            R.id.special_offer -> moveToSpecialOfferScreen()
            R.id.ll_see_our -> Common.showMessageWithCenterText(
                this,
                getString(R.string.special_offer_dialog_msg),
                getString(R.string.money_back_guarantee)
            )
            R.id.btn_continue_special_offer -> {

                CheckoutActivity.start(
                    this@SubscriptionActivity, subscriptionArrayList[pos],
                    false, Constants.FROM_SUBSCRIPTION_LIST
                )
            }
        }
    }

    private fun moveToSpecialOfferScreen() {
        startActivity(Intent(context, SpecialOfferActivity::class.java).putExtra("name", firstName))
    }

    private fun setTabOneData() {
        detail.text = getString(R.string.for_service, StringHelper.capitalFirstLetter(firstName))
        select_plan.visibility = View.GONE
        what_you_get.visibility = View.VISIBLE
    }


    private fun setTabTwoData() {
        detail.text = getString(R.string.for_prediction, StringHelper.capitalFirstLetter(firstName))
        select_plan.visibility = View.GONE
        what_you_get.visibility = View.VISIBLE
    }

    private fun setTabThreeData() {
        detail.text = getString(R.string.for_subscriber, StringHelper.capitalFirstLetter(firstName))
        select_plan.visibility = View.VISIBLE
        what_you_get.visibility = View.GONE
    }

    fun homeTabClicked(v: View?) {
        DashBoardActivity.CallDashboardActivity(this, false)
        finish()
    }

    fun chatTabClicked(v: View?) {
        ChatActivity.starChatActivity(this, false)
        //TabbedChatActivity.CallTabbedChatActivity(this, true)
        finish()
    }

    fun servicesTabClicked(v: View?) {
        ServicesActivity.CallServicesActivity(this)
        finish()
    }

    fun notificationTabClicked(v: View?) {
        starNotificationActivity(this)
        finish()
    }

    fun moreTabClicked(v: View?) {
        startMoreTabActivity(this)
        finish()
    }

    private fun getNotificationCount() {
        val apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this)
        val call = apiInterface.countOfAllUnreadNotificationActivityDetails
        call.enqueue(object : Callback<NotificationCountModel?> {
            override fun onResponse(
                call: Call<NotificationCountModel?>,
                response: Response<NotificationCountModel?>
            ) {
                if (response != null && response.body() != null) {
                    val notificationCountModel = response.body()
                    if (notificationCountModel!!.unReadCount > 0) {
                        findViewById<View>(R.id.nav_unread_badge).visibility = View.VISIBLE
                    } else {
                        findViewById<View>(R.id.nav_unread_badge).visibility = View.INVISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<NotificationCountModel?>, t: Throwable) {
                Log.d(
                    Constants.ON_FAILURE_TAG,
                    "Dashboard getUserProfile: onFailure",
                    t.fillInStackTrace()
                )
            }
        })
    }

}
