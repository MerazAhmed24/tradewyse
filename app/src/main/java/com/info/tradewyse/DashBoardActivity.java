package com.info.tradewyse;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.info.ComplexPreference.ComplexPreferences;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Analytics;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.DateTimeHelper;
import com.info.commons.FIleHelper;
import com.info.commons.PhotoUtility;
import com.info.commons.StringHelper;
import com.info.commons.TradWyseSession;
import com.info.dashboard.DashboardViewModel;
import com.info.dashboard.StockChangeDialogFragment;
import com.info.dialog.AddBannerDialog;
import com.info.dialog.NewAdBannerDialog;
import com.info.eventBus.MessageEvent;
import com.info.fragment.DashBoardFragment;
import com.info.interfaces.PhotoOptionSelectListener;
import com.info.logger.Logger;
import com.info.model.AdBannerModel;
import com.info.model.BasilPrivateChatModel;
import com.info.model.FirestoreAuthentication;
import com.info.model.FooterModel;
import com.info.model.LogoutResponse;
import com.info.model.NotificationCountModel;
import com.info.model.PublicChatModel;
import com.info.model.Stocks;
import com.info.model.Tips;
import com.info.model.User;
import com.info.model.UserSubscriptionDetail;
import com.info.tradewyse.databinding.ActivityDashBoardBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static okhttp3.MultipartBody.FORM;
import static okhttp3.MultipartBody.Part.createFormData;

public class DashBoardActivity extends BaseActivity
        implements View.OnClickListener {
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    Fragment fragment;
    private int visitCount;
    Context context;
    SimpleDraweeView imgProfile;
    ImageView imgSearch;
    DashBoardFragment dashBoardFragment;
    BroadcastReceiver logoutReceiver;
    public TradWyseSession tradWyseSession;
    public FirebaseAnalytics mFirebaseAnalytics;
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    AdBannerModel adBannerModel;
    private RelativeLayout imgMenu;
    private AppCompatImageView imgCross;
    LinearLayout bottomlinearLayout;
    PhotoUtility photoUtility;
    SimpleDraweeView imgProfileHeader;
    boolean checkForPendingIndent;
    private ReviewManager reviewManager;
    String groupId, groupTitle, groupDesc, groupImage;
    String basilPrivateGroupId, basilPrivateGroupTitle, basilPrivateGroupDesc, basilPrivateGroupImage, basilPrivateGroupCode;



    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private DashboardViewModel viewModel;
    ActivityDashBoardBinding activityDashBoardBinding;

    public static void CallDashboardActivity(Context mContext, boolean checkForPendingIndent) {
        Intent mIntent = new Intent(mContext, DashBoardActivity.class);
        mIntent.putExtra("checkForPendingIndent", checkForPendingIndent);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board);
        FooterModel footerModel = new FooterModel(true, false, false, false, false);
        activityDashBoardBinding.setFooterModel(footerModel);
        setToolBar(getString(R.string.app_name));
        Common.clearCache();

        reviewManager = ReviewManagerFactory.create(this);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        tradWyseSession = TradWyseSession.getSharedInstance(this);
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(this);
        imgSearch = findViewById(R.id.imgSearch);
        bottomlinearLayout = findViewById(R.id.bottomView);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toolbar = findViewById(R.id.toolbar);
        imgMenu = findViewById(R.id.imgMenu);
        imgCross = findViewById(R.id.img_cross);
        imgProfileHeader = findViewById(R.id.img_header_profile);

        imgSearch.setOnClickListener(this);
        imgMenu.setOnClickListener(this);
        imgCross.setOnClickListener(this);

        findViewById(R.id.chat_room_layout).setOnClickListener(this);
        findViewById(R.id.rl_notification).setOnClickListener(this);
        findViewById(R.id.user_search_layout).setOnClickListener(this);
        findViewById(R.id.al_stock_prediction_layout).setOnClickListener(this);
        findViewById(R.id.mentor).setOnClickListener(this);
        findViewById(R.id.rl_demo).setOnClickListener(this);
        findViewById(R.id.rl_suggestion).setOnClickListener(this);
        findViewById(R.id.rl_changepass).setOnClickListener(this);
        findViewById(R.id.rl_subscription).setOnClickListener(this);
//        findViewById(R.id.rl_use_trade_tips).setOnClickListener(this);
        findViewById(R.id.rl_learn_trade).setOnClickListener(this);
        findViewById(R.id.rl_faq).setOnClickListener(this);
        findViewById(R.id.rl_setting).setOnClickListener(this);
        findViewById(R.id.rl_logout).setOnClickListener(this);
        findViewById(R.id.imageview_edit_photo).setOnClickListener(this);
        findViewById(R.id.txt_visit_profile).setOnClickListener(this);
        checkIfPublicChatGroupIsAvailable();
        checkForPendingIndent = getIntent().getBooleanExtra("checkForPendingIndent", false);
        Common.BottomTabColorChange(this,bottomlinearLayout);
        context = this;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //    Log.d("onReceive","Logout in progress");
                //At this point you should start the login activity and finish this one
                DashBoardActivity.this.finish();

            }
        };
        registerReceiver(logoutReceiver, intentFilter);
        displayDashboardFragment();
        String userName = tradWyseSession.getUserName();
        getUserDetail(userName);
        getProfileImage();
        startCountAndReview();
        compositeDisposable.add(viewModel.getShowStockDialog().subscribe(success -> {
            new StockChangeDialogFragment().show(getSupportFragmentManager(), "stockChanges");
        }, error -> {
            Toast.makeText(this, "Error showing changed stocks", Toast.LENGTH_SHORT).show();
        }));
        viewModel.processIntent(this, getIntent());

        isSubscribedUser(tradWyseSession.getUserId());
        registerWithFireBaseToken();


        if (tradWyseSession.getIsMentor() != null && tradWyseSession.getIsMentor().equalsIgnoreCase("true")) {
            findViewById(R.id.mentor).setVisibility(View.GONE);
        } else {
            findViewById(R.id.mentor).setVisibility(View.VISIBLE);
        }

        //check to notification enable or not
       /* if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            //Do your Work
        } else {
            Toast.makeText(this, "Please Enable Your Notification", Toast.LENGTH_SHORT).show();
            //Ask for permission
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }*/

    }


    public void homeTabClicked(View v) {
    }

    public void chatTabClicked(View v) {
        ChatActivity.starChatActivity(DashBoardActivity.this,false);
        //TabbedChatActivity.CallTabbedChatActivity(DashBoardActivity.this, true);
    }

    public void servicesTabClicked(View v) {
        ServicesActivity.CallServicesActivity(DashBoardActivity.this);
    }

    public void notificationTabClicked(View v) {
        NotificationActivity.starNotificationActivity(this);
    }

    public void moreTabClicked(View v) {
        MoreTabActivity.startMoreTabActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        Constants.VisitedProfileUserNameList = new ArrayList<>();
        getNotificationCount();
        getActiveSimpleAdBannerDetails();
        if (checkForPendingIndent)
            checkforPendingIntent();
    }


    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Toasty.success(this,"onNewIntent Method called").show();
        viewModel.processIntent(this, intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        getAdBannerById(event.getAdBannerId(), event.getNotificationId());
        //Toast.makeText(context, "onEvent Method Called", Toast.LENGTH_SHORT).show();
    }

    public void getAdBannerById(String adBannerId, int notificationId) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, DashBoardActivity.this);
        Call<AdBannerModel> call = apiInterface.getSimpleAdBannerById(adBannerId);
        call.enqueue(new Callback<AdBannerModel>() {
            @Override
            public void onResponse(Call<AdBannerModel> call, Response<AdBannerModel> response) {
                if (response != null && response.body() != null) {
                    AdBannerModel AdBannerModelList = response.body();
                    if (AdBannerModelList != null) {
                        if (notificationId != 0) {
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            mNotificationManager.cancel(notificationId);
                        }
                        if (AdBannerModelList.isSingleBanner()) {
                            showPopup(AdBannerModelList);
                        } else {
                            showNewAdPopup(AdBannerModelList);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AdBannerModel> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getUserProfile: onFailure", t.fillInStackTrace());
            }
        });
    }

    private void getActiveSimpleAdBannerDetails() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, DashBoardActivity.this);
        Call<List<AdBannerModel>> call = apiInterface.getActiveSimpleAdBannerDetails();
        call.enqueue(new Callback<List<AdBannerModel>>() {
            @Override
            public void onResponse(Call<List<AdBannerModel>> call, Response<List<AdBannerModel>> response) {
                if (response != null && response.body() != null) {
                    List<AdBannerModel> AdBannerModelList = response.body();
                    if (AdBannerModelList.size() > 0) {
                        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(DashBoardActivity.this, Constants.ADD_BANNER_PREF, MODE_PRIVATE);
                        AdBannerModel adBannerModel = complexPreferences.getObject(Constants.ADD_BANNER_PREF_OBJ, AdBannerModel.class);
                        if (adBannerModel == null || !adBannerModel.getId().equalsIgnoreCase(AdBannerModelList.get(0).getId())) {
                            if (AdBannerModelList.get(0).isSingleBanner()) {
                                showPopup(AdBannerModelList.get(0));
                            } else {
                                showNewAdPopup(AdBannerModelList.get(0));
                            }
                        }
                        AdBannerModelList.get(0).setDisplayed(true);
                        complexPreferences.putObject(Constants.ADD_BANNER_PREF_OBJ, AdBannerModelList.get(0));
                        complexPreferences.commit();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AdBannerModel>> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getUserProfile: onFailure", t.fillInStackTrace());
            }
        });
    }

    private void showPopup(AdBannerModel adBannerModel) {
        new AddBannerDialog(this, adBannerModel) {
            @Override
            public void gotItButtonClicked(AdBannerModel adBannerModel) {

            }

            @Override
            public void closeButtonClicked(AdBannerModel adBannerModel) {

            }
        };
    }

    private void showNewAdPopup(AdBannerModel adBannerModel) {
        new NewAdBannerDialog(this, adBannerModel) {
            @Override
            public void gotItButtonClicked(AdBannerModel adBannerModel) {

            }

            @Override
            public void closeButtonClicked(AdBannerModel adBannerModel) {

            }
        };
    }

    private void getNotificationCount() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, DashBoardActivity.this);
        Call<NotificationCountModel> call = apiInterface.getCountOfAllUnreadNotificationActivityDetails();
        call.enqueue(new Callback<NotificationCountModel>() {
            @Override
            public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                if (response != null && response.body() != null) {
                    NotificationCountModel notificationCountModel = response.body();
                    if (notificationCountModel.getUnReadCount() > 0) {
                        (findViewById(R.id.notification_badge)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.nav_unread_badge)).setVisibility(View.VISIBLE);
                    } else {
                        (findViewById(R.id.notification_badge)).setVisibility(View.GONE);
                        (findViewById(R.id.nav_unread_badge)).setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationCountModel> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getUserProfile: onFailure", t.fillInStackTrace());
            }
        });
    }

    private void checkforPendingIntent() {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(DashBoardActivity.this, Constants.PENDING_DYNAMIC_LIST_PREF, MODE_PRIVATE);
        PendingDynamicLinkData pendingDynamicLinkData = complexPreferences.getObject(Constants.PENDING_DYNAMIC_LIST_PREF_OBJ, PendingDynamicLinkData.class);
        checkForPendingIndent = false;
        if (pendingDynamicLinkData != null)
            if (pendingDynamicLinkData.getLink() != null && pendingDynamicLinkData.getLink().getQueryParameter(Constants.TIP_ID) != null) {
                Logger.debug("SplashActivity", pendingDynamicLinkData.getLink().toString());
                Logger.debug("SplashActivity", pendingDynamicLinkData.getLink().getQueryParameter(Constants.TIP_ID));
                Intent i = new Intent(DashBoardActivity.this, TipDetailActivity.class);
                i.putExtra("selectedScreen", Constants.HOME);
                i.putExtra(Constants.TIP_ID, pendingDynamicLinkData.getLink().getQueryParameter(Constants.TIP_ID));
                startActivity(i);
            } else if (pendingDynamicLinkData.getLink() != null && pendingDynamicLinkData.getLink().getQueryParameter(Constants.SERVICE_ID) != null) {
                Logger.debug("SplashActivity", pendingDynamicLinkData.getLink().toString());
                Logger.debug("SplashActivity", pendingDynamicLinkData.getLink().getQueryParameter(Constants.SERVICE_ID));
                Intent i = new Intent(DashBoardActivity.this, ServiceDetailsScreen.class);
                i.putExtra("selectedScreen", Constants.HOME);
                i.putExtra("serviceSubscriptionPlanId", pendingDynamicLinkData.getLink().getQueryParameter(Constants.SERVICE_ID));
                startActivity(i);
            } else if (pendingDynamicLinkData.getLink().getQueryParameter(Constants.GOTO_SUBSCRIPTION_PAGE) != null) {
                Logger.debug("SplashActivity", pendingDynamicLinkData.getLink().toString());
                Logger.debug("SplashActivity", pendingDynamicLinkData.getLink().getQueryParameter(Constants.GOTO_SUBSCRIPTION_PAGE));
                Intent i = new Intent(DashBoardActivity.this, SubscriptionActivity.class);
                startActivity(i);
            }

    }

    public void getUserDetail(String userName) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, DashBoardActivity.this);
        Call<User> call = apiInterface.getAppUserByUserName(userName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (!response.body().getId().isEmpty()) {
                        User userResponse = response.body();
                        tradWyseSession.setUserImage(userResponse.getImage());
                        tradWyseSession.setUserIdString(userResponse.getId());
                        tradWyseSession.setIsMentor(userResponse.getIsMentor());
                        tradWyseSession.setFirstName(userResponse.getfName());
                        tradWyseSession.setLastName(userResponse.getlName());
                        tradWyseSession.setInternalSubscribedMember(userResponse.getInternalSubscribedUser());
                        getProfileImage();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getUserProfile: onFailure", t.fillInStackTrace());

            }
        });

    }


    private void getProfileImage() {
        String userId = tradWyseSession.getUserId();
        Common.setProfileImage(imgProfile, userId);
        Common.setProfileImage(imgProfileHeader, userId);
    }

    public void displayDashboardFragment() {
        // Pop off everything up to and including the current tab
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        dashBoardFragment = DashBoardFragment.newInstance("", "");
        // Add the new tab fragment
        fragmentManager.beginTransaction()
                .replace(R.id.container, dashBoardFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        this.moveTaskToBack(true);
    }

    private void setAppBarHeight() {
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight() + dpToPx(56)));
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgProfile:
                ProfileTabbedActivity.starProfileTabbedtActivityForResult(DashBoardActivity.this, true, 104, Constants.HOME);
                break;
            case R.id.imgSearch:
                SearchStocksActivity.startActivity(DashBoardActivity.this, dashBoardFragment.getStockList(), true, SearchStocksActivity.SEARCH_REQUEST_CODE, Constants.HOME);
                break;

            case R.id.imgMenu:
                drawerLayout.openDrawer(GravityCompat.END);
                break;

            case R.id.img_cross:
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.chat_room_layout:
                ProfileTabbedActivity.starProfileTabbedtActivityForResult(DashBoardActivity.this, true, true, 104, Constants.HOME);
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.rl_notification:
                NotificationActivity.starNotificationActivity(DashBoardActivity.this);
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.user_search_layout:
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.al_stock_prediction_layout:
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.mentor:
                startActivity(new Intent(DashBoardActivity.this, BecomeMentorActivity.class));
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.rl_subscription:
                Intent intent = SubscriptionActivity.Companion.newIntent(getApplicationContext());
                intent.putExtra(Constants.FROM, Constants.FROM_DASHBOARD);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.rl_learn_trade:
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.rl_faq:
                startActivity(new Intent(DashBoardActivity.this, FaqActivity.class));
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.rl_demo:
                Intent demotour = new Intent(DashBoardActivity.this, TourActivity.class);
                demotour.putExtra("fromWhere", "AfterLogin");
                startActivity(demotour);
                drawerLayout.closeDrawer(GravityCompat.END);
                break;


            case R.id.rl_setting:

                /*Intent starter = new Intent(DashBoardActivity.this, SettingActivity.class);
                DashBoardActivity.this.startActivity(starter);
                drawerLayout.closeDrawer(GravityCompat.END);*/
                break;

            case R.id.rl_logout:
                // Common.logout(DashBoardActivity.this);

                String loginInfoId = TradWyseSession.getSharedInstance(DashBoardActivity.this).getLoginInfoId();
                userLogout(loginInfoId);
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.imageview_edit_photo:
                takePhoto();
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.txt_visit_profile:
                ProfileTabbedActivity.starProfileTabbedtActivityForResult(DashBoardActivity.this, true, 104, Constants.HOME);
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.rl_changepass:
                startActivity(new Intent(DashBoardActivity.this, ChangePasswordActivity.class));
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.rl_suggestion:
                String suggestionurl = "https://tradetipsapp.com/contact/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(suggestionurl));
                startActivity(i);
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", "Detail AR activity");

        if (resultCode == RESULT_OK) {
            if (requestCode == SearchStocksActivity.SEARCH_REQUEST_CODE) {
                dashBoardFragment.refreshStockList();
            }

            if (requestCode == SearchStocksActivity.ADD_TIPS) {
                List<Stocks> stocksList = data.getParcelableArrayListExtra("selected_stocks");
                if (stocksList.size() > 0) {
                    AddTipsActivity.Companion.start(context, stocksList.get(0), SearchStocksActivity.TIPS_ADDED);
                }
            }

            if (requestCode == SearchStocksActivity.TIPS_ADDED) {
                Tips addedTips = data.getParcelableExtra("tips");
                dashBoardFragment.addNewTips(addedTips);
            }

            if (requestCode == 104) {
                String userName = tradWyseSession.getUserName();
                getUserDetail(userName);
                dashBoardFragment.setDefaultTips();
            }


            if (requestCode == 110 || requestCode == 111) {
                photoUtility.setResult(requestCode, resultCode, data);
                if (resultCode == RESULT_OK) {
                    uploadProfilePhoto();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (logoutReceiver != null)
            unregisterReceiver(logoutReceiver);
        super.onDestroy();
        compositeDisposable.dispose();
    }

    public void registerWithFireBaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull @NotNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("DashBoardActivity", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                String newToken = task.getResult();
                Log.e("newToken", newToken);
                TradWyseSession.getSharedInstance(DashBoardActivity.this).setFcmToken(newToken);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Autrefresh", "on restart of dashboard run..");
        if (!DateTimeHelper.isWeekend() && DateTimeHelper.isMarketHours() && dashBoardFragment.isUSMarketOpen()) {
            Log.d("Autrefresh", "call auto refresh  callon restart");
            if (dashBoardFragment != null) dashBoardFragment.refreshAllStockPrice();
        } else {
            Log.d("Autrefresh", "auto refresh not called.");
        }
    }


    public void isSubscribedUser(String userId) {
        TradWyseSession tradWyseSession = TradWyseSession.getSharedInstance(context);
        String token = tradWyseSession.getLoginAccessToken();
        Log.e("Token==", token);
        Log.e("userId==", tradWyseSession.getUserId());

        if (!Common.isNetworkAvailable(context)) {
            // if no network available then we will not call api.
            return;
        }
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<List<UserSubscriptionDetail>> call = apiInterface.getProductSubscriptionsByUserId(userId);
        showProgressDialog();
        call.enqueue(new Callback<List<UserSubscriptionDetail>>() {
            @Override
            public void onResponse(Call<List<UserSubscriptionDetail>> call, Response<List<UserSubscriptionDetail>> response) {
                dismissProgressDialog();
                Log.d("Response==", "" + response.body());
                Log.d("URL==", "" + call.request().url());
                if (response.body() != null) {
                    // we can parse the response and show the list of subscription.
                    List<UserSubscriptionDetail> userSubscriptionDetails = response.body();
                    if (userSubscriptionDetails.isEmpty()) {
                        tradWyseSession.setSubscribedMember(false);
                    } else {
                        for (int i = 0; i < userSubscriptionDetails.size(); i++) {
                            if (userSubscriptionDetails.get(i).getSubscriptionPlan().getSubscriptionStatus().equals("active")) {
                                tradWyseSession.setSubscribedMember(true);
                                break;
                            }
                        }
                    }
                    Log.d("IsSusbscribed", "" + tradWyseSession.isSubscribedMember());
                }
            }

            @Override
            public void onFailure(Call<List<UserSubscriptionDetail>> call, Throwable t) {
                dismissProgressDialog();
                Log.d(Constants.ON_FAILURE_TAG, "TipDetailActivity addTipComment: onFailure");
            }
        });
    }

    private void takePhoto() {
        try {
            photoUtility = new PhotoUtility.Builder(this)
                    .setImageView(imgProfileHeader)
                    .setOutPutFile(FIleHelper.createNewFile(this, FIleHelper.createFileName("profile")))
                    .build();
            photoUtility.setPhotoOptionSelectListener(new PhotoOptionSelectListener() {
                @Override
                public void requestPermissions(@NonNull String[] permissions, int requestCode) {
                    ActivityCompat.requestPermissions(DashBoardActivity.this, permissions, requestCode);
                }

                @Override
                public void startActivityForResult(Intent intent, int requestCode) {
                    ActivityCompat.startActivityForResult(DashBoardActivity.this, intent, requestCode, null);
                }
            });
            photoUtility.requestPermissionsCameraAndStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        photoUtility.setPermissionResult(requestCode, grantResults);
    }

    private void uploadProfilePhoto() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(FORM);
        String selectedFilePath = photoUtility.getSelectedFilePath();
        if (StringHelper.isEmpty(selectedFilePath)) return;
        File f = new File(selectedFilePath);
        if (f != null && f.exists()) {
            try {
                f = new Compressor(this).compressToFile(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(selectedFilePath), f);
            builder.addPart(createFormData("file", f.getName(), requestFile));
        } else {
            return;
        }
        String userName = TradWyseSession.getSharedInstance(this).getUserName();
        String userId = TradWyseSession.getSharedInstance(this).getUserId();
        builder.addFormDataPart("appUserName", userName);
        MultipartBody finalRequestBody = builder.build();
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        showProgressDialog("Uploading...");
        Call<ResponseBody> call = apiInterface.addProfilePhoto(finalRequestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    Toast.makeText(DashBoardActivity.this, "Profile photo uploaded successfully", Toast.LENGTH_LONG).show();
                    Common.evictImage(userId);
                    setResult(RESULT_OK);
                    Common.setProfileImage(imgProfile, userId);
                } else {
                    Toast.makeText(DashBoardActivity.this, "Unable to upload image at the moment.Please try again later", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                Toast.makeText(DashBoardActivity.this, "Unable to upload image at the moment.Please try again later", Toast.LENGTH_LONG).show();
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity uploadProfilePhoto: onFailure", t.fillInStackTrace());
            }
        });
    }


    public void userLogout(String deviceLoginId) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, DashBoardActivity.this);
        Call<LogoutResponse> call = apiInterface.logoutUser(deviceLoginId);
        showProgressDialog();
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                dismissProgressDialog();
                if (response.body() != null) {
                    if (response.body().getStatus().equals("true") && response.body().getTitle().equals("Success")) {
                        Common.logout(DashBoardActivity.this);
                    } else {
                        Common.logout(DashBoardActivity.this);
                        //Common.showMessage(context, response.body().getMessage(), response.body().getTitle());
                    }
                } else {
                    Toast.makeText(DashBoardActivity.this, "Something went wrong!please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                Toast.makeText(DashBoardActivity.this, "Something went wrong!please try again", Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                        context.getResources().getString(R.string.JustLetYouKnow));

            }
        });

    }

   // Review App code using in app play store rating
    public void startCountAndReview()
    {
        visitCount = tradWyseSession.getReviewCount();
        visitCount++;
        tradWyseSession.setReviewCount(visitCount);
        if (tradWyseSession.getReviewCount() == 25) {
            rateAppOnPlayStore();
            visitCount = 0;
            tradWyseSession.setReviewCount(visitCount);
        }
    }

    public void rateAppOnPlayStore() {
       // Toast.makeText(DashBoardActivity.this, "TestOk", Toast.LENGTH_SHORT).show();
        com.google.android.play.core.tasks.Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Getting the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                com.google.android.play.core.tasks.Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown.
                });
            }
        });

    }

    //code notification enable
    public boolean areNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (!manager.areNotificationsEnabled()) {
                return false;
            }
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (NotificationChannel channel : channels) {
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    return false;
                }
            }
            return true;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }

    public void checkIfPublicChatGroupIsAvailable() {
        if (TradwyseApplication.publicChatModel != null && TradwyseApplication.basilPrivateChatModel != null) {
            // For Strategy Chat Room
            groupId = TradwyseApplication.publicChatModel.getGroupId();
            groupImage = TradwyseApplication.publicChatModel.getGroupImage();
            groupTitle = TradwyseApplication.publicChatModel.getGroupTitle();
            groupDesc = TradwyseApplication.publicChatModel.getGroupDesc();

            // For Basil Private Chat Room
            basilPrivateGroupId = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupId();
            basilPrivateGroupImage = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupImage();
            basilPrivateGroupTitle = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupTitle();
            basilPrivateGroupDesc = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupDesc();
            basilPrivateGroupCode = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupCode();

        } else {
            getFirestoreAuthToken();
        }
    }

    public void getFirestoreAuthToken() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<FirestoreAuthentication> call = apiInterface.getFirestoreAuth();
        call.enqueue(new Callback<FirestoreAuthentication>() {
            @Override
            public void onResponse(Call<FirestoreAuthentication> call, Response<FirestoreAuthentication> response) {
                if (response != null && response.body() != null) {
                    FirestoreAuthentication firestoreAuthentication = response.body();
                    ComplexPreferences mComplexPreferences = ComplexPreferences.getComplexPreferences(DashBoardActivity.this, Constants.FIRESTORE_AUTH_PREF, MODE_PRIVATE);
                    mComplexPreferences.putObject(Constants.FIRESTORE_AUTH_PREF_OBJ, firestoreAuthentication);
                    mComplexPreferences.commit();
                    signInWithCustomToken(firestoreAuthentication.getAuthToken());
                } else {
                    Log.d(Constants.ON_FAILURE_TAG, "Dashboard getFirestoreAuthToken: No response ");
                }
            }

            @Override
            public void onFailure(Call<FirestoreAuthentication> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getFirestoreAuthToken: onFailure ", t.fillInStackTrace());
            }
        });
    }

    private void signInWithCustomToken(String FCMID) {
        FirebaseAuth.getInstance().signInWithCustomToken(FCMID).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkIfPublicChatGroupIsAvailableHere();
                    checkIfBasilPrivateChatGroupIsAvailableHere();
                } else {
                    Toast.makeText(DashBoardActivity.this, "Unable to signInWithCustomToken", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkIfPublicChatGroupIsAvailableHere() {
        TradwyseApplication.getFirestoreDb().collection(Constants.OPEN_GROUP)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_GROUP_ID)) {
                            TradwyseApplication.publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            checkIfPublicChatGroupIsAvailable();
                            break;
                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_GROUP_ID)) {
                            TradwyseApplication.publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            checkIfPublicChatGroupIsAvailable();
                            break;
                        }
                    }
                } else {
                    //Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void checkIfBasilPrivateChatGroupIsAvailableHere() {
        TradwyseApplication.getFirestoreDb().collection(Constants.BASIL_PRIVATE_GROUP)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_BASIL_PRIVATE_GROUP_ID)) {
                            TradwyseApplication.basilPrivateChatModel = new BasilPrivateChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION), document.getString(Constants.GROUP_CODE));
                            break;

                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_BASIL_PRIVATE_GROUP_ID)) {
                            TradwyseApplication.basilPrivateChatModel = new BasilPrivateChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION), document.getString(Constants.GROUP_CODE));
                            break;
                        }
                    }
                } else {
                    //Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
}

