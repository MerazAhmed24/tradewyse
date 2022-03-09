package com.info.tradewyse

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.info.api.APIClient
import com.info.commons.*
import com.info.interfaces.PhotoOptionSelectListener
import com.info.model.FooterModel
import com.info.model.LogoutResponse
import com.info.model.NotificationCountModel
import com.info.tradewyse.NotificationActivity.Companion.starNotificationActivity
import com.info.tradewyse.SubscriptionActivity.Companion.newIntent
import com.info.tradewyse.databinding.ActivityTabbedMoreBinding
import id.zelory.compressor.Compressor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Companion.FORM
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

/**
 * Created by Amit Gupta on 22,July,2021
 */
class MoreTabActivity : BaseActivity() {

    companion object {
        @JvmStatic
        fun startMoreTabActivity(context: Context) {
            val intent = Intent(context, MoreTabActivity::class.java)
            (context as Activity).startActivity(intent)
        }
    }

    lateinit var activityTabbedMoreBinding: ActivityTabbedMoreBinding
    lateinit var photoUtility: PhotoUtility



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTabbedMoreBinding = DataBindingUtil.setContentView(this, R.layout.activity_tabbed_more)
        val footerModel = FooterModel(false, false, false, false, true)
        activityTabbedMoreBinding.footerModel = footerModel
        setToolBarAddTip(tradWyseSession.userName)
        //code change bottom color for test build
        val bottomLinearLayout = findViewById<LinearLayout>(R.id.bottomView)
        Common.BottomTabColorChange(this, bottomLinearLayout)

        // Check user is mentor or not.
        val isMentorString = tradWyseSession.isMentor
        if (!StringHelper.isEmpty(isMentorString) && isMentorString.equals("true", ignoreCase = true)) {
            activityTabbedMoreBinding.navDrawerItem.mentor.visibility = View.GONE;
        } else {
            //activityTabbedMoreBinding.navDrawerItem.mentor.visibility = View.VISIBLE;
            activityTabbedMoreBinding.navDrawerItem.mentor.visibility = View.GONE;
        }

        getNotificationCount()

        if (tradWyseSession.isSubscribedMember()) {
            activityTabbedMoreBinding.navDrawerItem.txtSubscription.setText("My Subscription")
        } else {
            activityTabbedMoreBinding.navDrawerItem.txtSubscription.setText("Upgrade to Premium")
        }

        activityTabbedMoreBinding.navDrawerItem.gettingStarted.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MoreTabActivity, WebViewActivity::class.java)
                .putExtra("title", getString(R.string.getting_started))
                .putExtra("url", APIClient.NEW_USER_IN_APP_NOTIFICATION_REDIRECTION))
        })

        activityTabbedMoreBinding.navDrawerItem.mentor.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MoreTabActivity, BecomeMentorActivity::class.java))
        })
        activityTabbedMoreBinding.navDrawerItem.rlSubscription.setOnClickListener(View.OnClickListener {
            val intent = newIntent(this@MoreTabActivity)
            intent.putExtra(Constants.FROM, Constants.FROM_DASHBOARD)
            startActivity(intent)
        })
        activityTabbedMoreBinding.navDrawerItem.rlFaq.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MoreTabActivity, FaqActivity::class.java))
        })
        activityTabbedMoreBinding.navDrawerItem.rlChangepass.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MoreTabActivity, ChangePasswordActivity::class.java))
        })
        activityTabbedMoreBinding.navDrawerItem.rlSuggestion.setOnClickListener(View.OnClickListener {
            val suggestionurl = "https://tradetipsapp.com/contact/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(suggestionurl)
            startActivity(i)
        })
        activityTabbedMoreBinding.navDrawerItem.rlDemo.setOnClickListener(View.OnClickListener {
            val demotour = Intent(this@MoreTabActivity, TourActivity::class.java)
            demotour.putExtra("fromWhere", "AfterLogin")
            startActivity(demotour)
        })
        activityTabbedMoreBinding.navDrawerItem.rlLogout.setOnClickListener(View.OnClickListener {

            // Statements
          /*  val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
            builder.setMessage(resources.getString(R.string.are_you_sure_want_to_logout))
            builder.setTitle(resources.getString(R.string.app_name))
            builder.setNegativeButton(
                "No"
            ) { dialog, i -> dialog.dismiss() }
            builder.setPositiveButton(
                "Yes"
            ) { dialog, id -> */
                val loginInfoId = TradWyseSession.getLoginInfoId()
                userLogout(loginInfoId)
          //  }
                /*
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show() */
        })
        activityTabbedMoreBinding.navHeaderItem.imageviewEditPhoto.setOnClickListener(View.OnClickListener {
            takePhoto()
        })

        activityTabbedMoreBinding.navHeaderItem.txtVisitProfile.setOnClickListener(View.OnClickListener {
            ProfileTabbedActivity.starProfileTabbedtActivityForResult(this@MoreTabActivity, true, 104, Constants.MORE_TAB)
        })
        Common.setProfileImage(activityTabbedMoreBinding.navHeaderItem.imgHeaderProfile, tradWyseSession.userId)
    }

    fun homeTabClicked(v: View?) {
        DashBoardActivity.CallDashboardActivity(this, false)
        finish()
    }

    fun chatTabClicked(v: View?) {
        ChatActivity.starChatActivity(this, false)
        //TabbedChatActivity.CallTabbedChatActivity(this@MoreTabActivity, true)
        finish()
    }

    fun servicesTabClicked(v: View?) {
        ServicesActivity.CallServicesActivity(this@MoreTabActivity)
    }

    fun notificationTabClicked(v: View?) {
        starNotificationActivity(this)
        finish()
    }

    fun moreTabClicked(v: View?) {

    }

    fun userLogout(deviceLoginId: String?) {
        val apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this@MoreTabActivity)
        val call = apiInterface.logoutUser(deviceLoginId)
        showProgressDialog()
        call.enqueue(object : Callback<LogoutResponse?> {
            override fun onResponse(call: Call<LogoutResponse?>, response: Response<LogoutResponse?>) {
                dismissProgressDialog()
                if (response.body() != null) {
                    if (response.body()!!.status == "true" && response.body()!!.title == "Success") {
                        Common.logout(this@MoreTabActivity)
                    } else {
                        Common.logout(this@MoreTabActivity)
                    }
                } else {
                    Toast.makeText(this@MoreTabActivity, "Something went wrong!please try again", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LogoutResponse?>, t: Throwable) {
                Toast.makeText(this@MoreTabActivity, "Something went wrong!please try again", Toast.LENGTH_LONG).show()
                dismissProgressDialog()
                Common.showOfflineMemeDialog(
                    this@MoreTabActivity, this@MoreTabActivity.getResources().getString(R.string.memeMsg),
                    this@MoreTabActivity.getResources().getString(R.string.JustLetYouKnow)
                )
            }
        })
    }

    private fun takePhoto() {
        try {
            photoUtility = PhotoUtility.Builder(this)
                .setImageView(activityTabbedMoreBinding.navHeaderItem.imgHeaderProfile)
                .setOutPutFile(FIleHelper.createNewFile(this, FIleHelper.createFileName("profile")))
                .build()
            photoUtility.setPhotoOptionSelectListener(object : PhotoOptionSelectListener {
                override fun requestPermissions(permissions: Array<String>, requestCode: Int) {
                    ActivityCompat.requestPermissions(this@MoreTabActivity, permissions, requestCode)
                }

                override fun startActivityForResult(intent: Intent, requestCode: Int) {
                    ActivityCompat.startActivityForResult(this@MoreTabActivity, intent, requestCode, null)
                }
            })
            photoUtility.requestPermissionsCameraAndStorage()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ActivityResult", "Detail AR activity")
        if (resultCode == RESULT_OK) {
            if (requestCode == 110 || requestCode == 111) {
                photoUtility!!.setResult(requestCode, resultCode, data)
                if (resultCode == RESULT_OK) {
                    uploadProfilePhoto()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        photoUtility.setPermissionResult(requestCode, grantResults)
    }

    private fun uploadProfilePhoto() {
        val builder = MultipartBody.Builder()
        builder.setType(FORM)
        val selectedFilePath = photoUtility.getSelectedFilePath()
        if (StringHelper.isEmpty(selectedFilePath)) return
        var f: File? = File(selectedFilePath)
        if (f != null && f.exists()) {
            try {
                f = Compressor(this).compressToFile(f)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val requestFile = f?.let { RequestBody.create(selectedFilePath?.let { it.toMediaTypeOrNull() }, it) }
            requestFile?.let { MultipartBody.Part.Companion.createFormData("file", f!!.name, it) }?.let { builder.addPart(it) }
        } else {
            return
        }
        val userName = TradWyseSession.getSharedInstance(this).userName
        val userId = TradWyseSession.getSharedInstance(this).userId
        builder.addFormDataPart("appUserName", userName)
        val finalRequestBody: MultipartBody = builder.build()
        val apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this)
        showProgressDialog("Uploading...")
        val call = apiInterface.addProfilePhoto(finalRequestBody)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                dismissProgressDialog()
                if (response.isSuccessful) {

                    Toast.makeText(this@MoreTabActivity, "Profile photo uploaded successfully", Toast.LENGTH_LONG).show()
                    Common.evictImage(userId)
                    setResult(RESULT_OK)

                } else {
                    Toast.makeText(
                        this@MoreTabActivity,
                        "Unable to upload image at the moment.Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                dismissProgressDialog()
                Toast.makeText(this@MoreTabActivity, "Unable to upload image at the moment.Please try again later", Toast.LENGTH_LONG)
                    .show()
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity uploadProfilePhoto: onFailure", t.fillInStackTrace())
            }
        })
    }

    private fun getNotificationCount() {
        val apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this@MoreTabActivity)
        val call = apiInterface.countOfAllUnreadNotificationActivityDetails
        call.enqueue(object : Callback<NotificationCountModel?> {
            override fun onResponse(call: Call<NotificationCountModel?>, response: Response<NotificationCountModel?>) {
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
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getUserProfile: onFailure", t.fillInStackTrace())
            }
        })
    }
}