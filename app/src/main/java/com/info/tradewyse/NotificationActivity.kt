package com.info.tradewyse

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.info.ComplexPreference.ComplexPreferences
import com.info.adapter.NotificationAdapter
import com.info.api.APIClient
import com.info.commons.Common
import com.info.commons.Constants
import com.info.interfaces.ShowHideProgressDialogInterface
import com.info.logger.Logger
import com.info.model.*
import com.info.tradewyse.databinding.ActivityNotificationBinding
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.test.*
import kotlinx.android.synthetic.main.toolbar_notification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Amit Gupta
 */
class NotificationActivity : BaseActivity(), ShowHideProgressDialogInterface,


    NotificationAdapter.NotificationListener {
    companion object {
        @JvmStatic
        fun starNotificationActivity(context: Context) {
            val intent = Intent(context, NotificationActivity::class.java)
            (context as Activity).startActivity(intent)
        }
    }

    lateinit var activityNotificationBinding: ActivityNotificationBinding
    var offset: Int = 0
    var limit: Int = 30
    val TAG: String = "NotificationActivity"
    var notificationList = arrayListOf<NotificationModel>()
    var notificationBundleList = arrayListOf<NotificationBundle>()
    lateinit var notificationAdapter: NotificationAdapter
    var isPermissionAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNotificationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_notification)
        val footerModel = FooterModel(false, false, false, true, false)
        activityNotificationBinding.setFooterModel(footerModel)
        swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            getNotificationList()
        }
        //code change bottom color for test build
        val bottomLinearLayout = findViewById<LinearLayout>(R.id.bottomView)
        Common.BottomTabColorChange(this, bottomLinearLayout)

        action_clear_all.setOnClickListener {
            showConfirmationDialog()
        }
        getFirestoreAuthToken()
        setNotificationAdapter()
        getNotificationList()
    }

    override fun onResume() {
        super.onResume()
        getNotificationCount()
    }

    fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this@NotificationActivity, R.style.MyAlertDialogStyle)
        builder.setMessage("This will clear all notifications forever.")
        builder.setTitle("Just So You Know")
        builder.setPositiveButton("Ok") { dialog, id ->
            dialog.dismiss()
            callClearAllNotificationApi()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun callClearAllNotificationApi() {
        showDialog()
        val userName = tradWyseSession.userId
        val apiInterface =
            APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this@NotificationActivity)
        var call = apiInterface.deleteAllNotificationForLoginUser(userName)
        call.enqueue(object : Callback<List<DeleteAllNotificationModel>> {
            override fun onResponse(
                call: Call<List<DeleteAllNotificationModel>>,
                response: Response<List<DeleteAllNotificationModel>>
            ) {
                if (response.isSuccessful) {
                    var array = response.body()
                    notificationList.clear()
                    notificationAdapter.notificationBundleList =
                        notificationList as ArrayList<NotificationBundle>
                    notifyAdapter(true)
                    offset = 0
                    findViewById<View>(R.id.nav_unread_badge).visibility = View.INVISIBLE
                } else {
                    Logger.error(TAG, "Unable to delete notification list")
                }
                hideDialog()
            }

            override fun onFailure(call: Call<List<DeleteAllNotificationModel>>, t: Throwable) {
                hideDialog()
            }

        })
    }

    private fun setNotificationAdapter() {
        notificationAdapter =
            NotificationAdapter(
                notificationBundleList as ArrayList<NotificationBundle>,
                this@NotificationActivity,
                this@NotificationActivity
            )
        notification_rv.adapter = notificationAdapter
        notification_rv.layoutManager = LinearLayoutManager(this@NotificationActivity)
    }

    private fun getNotificationList() {
        swipeRefreshLayout!!.isRefreshing = true
        if (!Common.isNetworkAvailable(this))
        {
            Common.showOfflineMemeDialog(this@NotificationActivity, resources.getString(R.string.memeMsg),
                    resources.getString(R.string.JustLetYouKnow))
            empty_ll.visibility = View.VISIBLE

        }
        val userName = tradWyseSession.userName
        val apiInterface =
            APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this@NotificationActivity)
        var call = apiInterface.getAllUnreadBundleNotificationActivityMessage(userName);
        call.enqueue(object : Callback<List<NotificationBundle>> {
            override fun onResponse(
                call: Call<List<NotificationBundle>>,
                response: Response<List<NotificationBundle>>
            ) {
                if (response.isSuccessful) {
                    var array = response.body()
                    if (array != null && array!!.size > 0) {
                        if (offset == 0) {
                            notificationBundleList = array as ArrayList<NotificationBundle>
                            getNotificationCount()
                        } else {
                            notificationBundleList.addAll(array as Array<out NotificationBundle>)
                        }
                        notificationAdapter.notificationBundleList =
                            notificationBundleList as ArrayList<NotificationBundle>
                        offset = offset + limit
                        //findViewById<View>(R.id.nav_unread_badge).visibility = View.VISIBLE

                    } else {
                        findViewById<View>(R.id.nav_unread_badge).visibility = View.INVISIBLE
                    }
                    notifyAdapter(true)
                } else {
                    Logger.error(TAG, "Unable to get notification list")
                    findViewById<View>(R.id.nav_unread_badge).visibility = View.INVISIBLE
                }
                swipeRefreshLayout!!.isRefreshing = false
            }

            override fun onFailure(call: Call<List<NotificationBundle>>, t: Throwable) {
                //Toasty.error(this@NotificationActivity, "Notification List not found").show()
                swipeRefreshLayout!!.isRefreshing = false
                empty_ll.visibility = View.VISIBLE


            }
        })
    }

    override fun showDialog() {
        showProgressDialog("Please wait...")
    }

    override fun hideDialog() {
        dismissProgressDialog()
    }

    override fun notifyAdapter(needToNotify: Boolean) {
        if (needToNotify)
            notificationAdapter.notifyDataSetChanged()

        if (notificationAdapter.notificationBundleList.isEmpty()) {
            empty_ll.visibility = View.VISIBLE
            notification_rv.visibility = View.GONE
            action_clear_all.visibility = View.GONE
        } else {
            empty_ll.visibility = View.GONE
            notification_rv.visibility = View.VISIBLE
            action_clear_all.visibility = View.VISIBLE
        }

    }

    private fun deleteNotificationById(position: Int) {
        var notificationModel: NotificationBundle
        notificationModel = notificationBundleList[position]
        showDialog()
        val apiInterface =
            APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this@NotificationActivity)

        var call: Call<List<NotificationModel>>? = null
        if (notificationModel.bundleNotificationId != null) {
            val replace: String = notificationModel.notificationId?.replace("[", "").toString()
            val replace1 = replace.replace("]", "")
            println(replace1)
            val myList: List<String> =
                ArrayList<String>(Arrays.asList(replace1.split(",").toString()))
            println(myList)

            call = apiInterface.deleteNotificationDetailsById(
                replace1,
                notificationModel.bundleNotificationId
            )
        } else {
            call = apiInterface.deleteNotificationDetailsById(notificationModel.notificationId)
        }


        call?.enqueue(object : Callback<List<NotificationModel>> {
            override fun onResponse(
                call: Call<List<NotificationModel>>,
                response: Response<List<NotificationModel>>
            ) {
                if (response.isSuccessful) {
                    var array = response.body()
                    if (array != null && array!!.size > 0) {
                        hideDialog()
                        notificationBundleList.removeAt(position)
                        notificationAdapter.notifyItemRemoved(position)
                        notifyAdapter(false)
                    }
                }
            }

            override fun onFailure(call: Call<List<NotificationModel>>, t: Throwable) {
                hideDialog()
            }
        })
    }

    private fun markNotificationRead(notificationModel: NotificationBundle, position: Int) {
        showDialog()
        val apiInterface =
            APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this@NotificationActivity)
        var call: Call<List<NotificationModel>>? = null
        if (notificationModel.bundleNotificationId != null) {
            val replace: String = notificationModel.notificationId?.replace("[", "").toString()
            val replace1 = replace.replace("]", "")
            println(replace1)
            val myList: List<String> =
                ArrayList<String>(Arrays.asList(replace1.split(",").toString()))
            println(myList)

            call = apiInterface.createNotificationAsReadByNotificationId(
                replace1,
                notificationModel.bundleNotificationId
            )
        } else {
            call =
                apiInterface.createNotificationAsReadByNotificationId(notificationModel.notificationId)
        }

        call?.enqueue(object : Callback<List<NotificationModel>> {
            override fun onResponse(
                call: Call<List<NotificationModel>>,
                response: Response<List<NotificationModel>>
            ) {
                if (response.isSuccessful) {
                    var array = response.body()
                    if (array != null && array!!.size > 0) {
                        hideDialog()
                        notificationBundleList[position].read = array!![0].read
                        notificationAdapter.notifyItemChanged(position)
                        //getNotificationList()
                        notifyAdapter(false)
                    }
                    takeRequiredAction(notificationModel, position)
                }
            }

            override fun onFailure(call: Call<List<NotificationModel>>, t: Throwable) {
                hideDialog()
            }
        })
    }

    private fun takeRequiredAction(notificationModel: NotificationBundle, position: Int) {
        if (notificationModel.notificationType == "Comment" || notificationModel.notificationType == "Tip") {
            startActivity(
                Intent(this@NotificationActivity, TipDetailActivity::class.java)
                    .putExtra("tipId", notificationModel.tipId)
                    .putExtra("selectedScreen", Constants.NOTIFICATION)
            )
            // redirect to the comment detail page
        } else if (notificationModel.notificationType == "NewSignUpUser") {
            startActivity(
                Intent(this@NotificationActivity, WebViewActivity::class.java)
                    .putExtra("title", "TradeTips")
                    .putExtra("url", APIClient.NEW_USER_IN_APP_NOTIFICATION_REDIRECTION)
            )

        } else if (notificationModel.notificationType == "SocialChat") {
            val isBasilPrivateRoom: Boolean =
                notificationModel.groupId.equals("Test", ignoreCase = true) ||
                        notificationModel.groupId.equals("Production", ignoreCase = true)

            if (isPermissionAvailable) {
                TradwyseApplication.getFirestoreDb()
                    .collection(if (isBasilPrivateRoom) Constants.BASIL_PRIVATE_GROUP else Constants.OPEN_GROUP)
                    .document(notificationModel.groupId!!)
                    .collection("messages")
                    .document(notificationModel.notificationReferenceId!!)
                    .get()
                    .addOnCompleteListener {
                        //if (it.isSuccessful) {
                        if (it.result.get(Constants.MessageId) != null) {

                            startActivity(
                                Intent(this@NotificationActivity, ChatReplyActivity::class.java)
                                    .putExtra(Constants.GROUP_ID, notificationModel.groupId)
                                    .putExtra(Constants.FROM_NOTIFICATION_CLICK, true)
                                    .putExtra(Constants.FROM_PUSH_NOTIFICATION_CLICK, false)
                                    .putExtra(Constants.MessageId, notificationModel.messageId)
                                    .putExtra(Constants.GROUP_NAME, notificationModel.groupId)
                                    .putExtra(
                                        Constants.POSTED_BY_USERNAME,
                                        notificationModel.postedByUserName
                                    )
                                    .putExtra(
                                        Constants.MESSAGE_REFERENCE_ID,
                                        notificationModel.notificationReferenceId
                                    )
                                    .putExtra("isBasilPrivateRoom", isBasilPrivateRoom)
                            )

                        } else {
                            Common.showMessage(
                                this@NotificationActivity,
                                resources.getString(R.string.this_post_has_been_deleted),
                                resources.getString(R.string.messageAlert)
                            )
                        }
                        //}
                    }
            } else {
                getFirestoreAuthToken()
            }

        } else if (notificationModel.notificationType == "FollowUser") {
            // don’t redirect anywhere
        } else if (notificationModel.notificationType == "stockChange") {
            // don’t redirect anywhere
        } else if (notificationModel.notificationType == "prediction") {
            // don’t redirect anywhere
        }
    }

    fun homeTabClicked(v: View?) {
        DashBoardActivity.CallDashboardActivity(this, false)
        finish()
    }

    fun chatTabClicked(v: View?) {
        ChatActivity.starChatActivity(this, false)
        //TabbedChatActivity.CallTabbedChatActivity(this@NotificationActivity, true)
        finish()
    }

    fun servicesTabClicked(v: View?) {
        ServicesActivity.CallServicesActivity(this@NotificationActivity)
    }

    fun notificationTabClicked(v: View?) {
        //Toast.makeText(this, "Notification button clicked", Toast.LENGTH_SHORT).show()
    }

    fun moreTabClicked(v: View?) {
        MoreTabActivity.startMoreTabActivity(this)
        finish()
    }

    private fun getNotificationCount() {
        val apiInterface =
            APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this@NotificationActivity)
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

    override fun onNotificationClick(position: Int, type: String) {
        if (type.equals("markRead"))
            markNotificationRead(notificationBundleList[position], position)
        else
            takeRequiredAction(notificationBundleList[position], position)
    }

    override fun onDeleteClick(position: Int) {
        deleteNotificationById(position)
    }

    fun getFirestoreAuthToken() {
        val apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this)
        val call = apiInterface.firestoreAuth
        call.enqueue(object : Callback<FirestoreAuthentication?> {
            override fun onResponse(
                call: Call<FirestoreAuthentication?>,
                response: Response<FirestoreAuthentication?>
            ) {
                if (response != null && response.body() != null) {
                    val firestoreAuthentication = response.body()
                    val mComplexPreferences = ComplexPreferences.getComplexPreferences(
                        this@NotificationActivity,
                        Constants.FIRESTORE_AUTH_PREF,
                        MODE_PRIVATE
                    )
                    mComplexPreferences.putObject(
                        Constants.FIRESTORE_AUTH_PREF_OBJ,
                        firestoreAuthentication
                    )
                    mComplexPreferences.commit()
                    signInWithCustomToken(firestoreAuthentication!!.authToken)
                } else {
                    Log.d(Constants.ON_FAILURE_TAG, "Dashboard getFirestoreAuthToken: No response ")
                }
            }

            override fun onFailure(call: Call<FirestoreAuthentication?>, t: Throwable) {
                Log.d(
                    Constants.ON_FAILURE_TAG,
                    "Dashboard getFirestoreAuthToken: onFailure ",
                    t.fillInStackTrace()
                )
            }
        })
    }

    private fun signInWithCustomToken(FCMID: String) {
        FirebaseAuth.getInstance().signInWithCustomToken(FCMID).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isPermissionAvailable = true
            } else {
                Toast.makeText(
                    this@NotificationActivity,
                    "Unable to signInWithCustomToken",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}