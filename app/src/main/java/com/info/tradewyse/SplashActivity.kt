package com.info.tradewyse

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.info.ComplexPreference.ComplexPreferences
import com.info.CustomToast.Toasty
import com.info.commons.Constants
import com.info.commons.TradWyseSession
import com.info.logger.Logger
import com.info.service.MyFirebaseMessagingService.*

class SplashActivity : AppCompatActivity() {
    var animationView: LottieAnimationView? = null
    private val mAppUpdateManager: AppUpdateManager? = null

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> popupSnackbarForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(
                        this
                    )
                    else -> Log.d(":state: %s", installState.installStatus().toString() + "")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TradWyseSession.getSharedInstance(this)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
        animationView = findViewById<View>(R.id.animation_view) as LottieAnimationView


        initilizeHandler()
        createAdBannerNotificationChannel()
        createStockPredectionNotificationChannel()
        createGeneralNotificationChannel()
        createSocialChatNotificationChannel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    private fun initilizeHandler() {
        Handler().postDelayed({
            initializeFirebaseDynamicLink()
            checkForAppUpdate()
        }, 3150)
    }

    private fun initializeFirebaseDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                Logger.debug("SplashActivity", "Dynamic link is working")

                if (pendingDynamicLinkData != null && pendingDynamicLinkData.link != null) {
                    if (TradWyseSession.isLoggedIn()) {
                        if (pendingDynamicLinkData.link!!.getQueryParameter(Constants.TIP_ID) != null) {
                            Logger.debug("SplashActivity", pendingDynamicLinkData.link.toString())
                            Logger.debug(
                                "SplashActivity",
                                pendingDynamicLinkData.link!!.getQueryParameter(Constants.TIP_ID)
                            )
                            val i = Intent(this@SplashActivity, TipDetailActivity::class.java)
                            i.putExtra(
                                Constants.TIP_ID,
                                pendingDynamicLinkData.link!!.getQueryParameter(Constants.TIP_ID)
                            )
                            i.putExtra("selectedScreen", Constants.HOME)
                            startActivity(i)
                            finish()

                        }
                        else if (pendingDynamicLinkData.link!!.getQueryParameter(Constants.SERVICE_ID) != null) {

                            Logger.debug("SplashActivity", pendingDynamicLinkData.link.toString())
                            Logger.debug("SplashActivity", pendingDynamicLinkData.link!!.getQueryParameter(Constants.SERVICE_ID))

                            val i = Intent(this@SplashActivity, ServiceDetailsScreen::class.java)
                            i.putExtra("selectedScreen", Constants.HOME)
                            i.putExtra("serviceSubscriptionPlanId", pendingDynamicLinkData.link!!.getQueryParameter(Constants.SERVICE_ID))
                            startActivity(i)
                            finish()

                        }
                        else if (pendingDynamicLinkData.link!!.getQueryParameter(Constants.GOTO_SUBSCRIPTION_PAGE) != null) {
                            Logger.debug("SplashActivity", pendingDynamicLinkData.link.toString())
                            Logger.debug(
                                "SplashActivity",
                                pendingDynamicLinkData.link!!.getQueryParameter(Constants.GOTO_SUBSCRIPTION_PAGE)
                            )
                            val i = Intent(this@SplashActivity, SubscriptionActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                    } else {
                        val complexPreference = ComplexPreferences.getComplexPreferences(
                            this,
                            Constants.PENDING_DYNAMIC_LIST_PREF,
                            MODE_PRIVATE
                        );
                        complexPreference.putObject(
                            Constants.PENDING_DYNAMIC_LIST_PREF_OBJ,
                            pendingDynamicLinkData
                        );
                        complexPreference.commit()
                        //PendingDynamicLinkData
                        LoginActivity.CallLoginActivity(this, true);
                        finish()

                    }
                } else {
                    redirectToNext()
                }

            }
    }

    private fun redirectToNext() {
        if (TradWyseSession.isLoggedIn()) {
            val i = Intent(this@SplashActivity, DashBoardActivity::class.java)
            startActivity(i)
        } else {
            val i = Intent(this@SplashActivity, SignUpActivity::class.java)
            i.putExtra("fromWhere", "BeforeLogin")
            startActivity(i)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
        finish()
    }


    private fun checkForAppUpdate() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.
                try {
                    val installType = when {
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
                        else -> null
                    }
                    if (installType == AppUpdateType.FLEXIBLE) appUpdateManager.registerListener(
                        appUpdatedListener
                    )

                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        //installType!!,
                        AppUpdateType.IMMEDIATE,
                        this,
                        APP_UPDATE_REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    Log.e("error", e.message + " error")
                    redirectToNext()
                }
            } else {
                //redirectToNext()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                    this,
                    "App Update failed, please try again on the next app launch.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(
            findViewById(R.id.animation_view),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("RESTART") { appUpdateManager.completeUpdate() }
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackbar.show()
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }

                //Check if Immediate update is required
                try {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            APP_UPDATE_REQUEST_CODE
                        )
                    }
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            }
    }

    companion object {
        private const val APP_UPDATE_REQUEST_CODE = 1991
    }

    private fun createAdBannerNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.ad_banner_channel_name)
            val descriptionText = getString(R.string.ad_banner_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_AD_BANNER, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createStockPredectionNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.stock_prediction_channel_name)
            val descriptionText = getString(R.string.stock_prediction_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_STOCK_PREDICTION, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createGeneralNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.general_notification_channel_name)
            val descriptionText = getString(R.string.general_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID_GENERAL_NOTIFICATION, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createSocialChatNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.social_chat_reply_channel_name)
            val descriptionText = getString(R.string.social_chat_reply_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID_SOCIAL_CHAT, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}