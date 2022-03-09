package com.info.tradewyse;

import static com.info.tradewyse.TradwyseApplication.browserPackageName;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textview.MaterialTextView;
import com.info.ComplexPreference.ComplexPreferences;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.model.FooterModel;
import com.info.model.NotificationCountModel;
import com.info.model.userServiceResponse.ServiceSubscriptionPlan;
import com.info.tradewyse.databinding.ActivityServiceDetailsBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsScreen extends BaseActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private AppCompatImageView imgCross;
    private AppCompatImageView imgService;
    private MaterialTextView txtServiceType, titleTv;
    private MaterialTextView txtDescription;
    private MaterialTextView txtPrice;
    private MaterialTextView txtPurchase;
    private AppCompatImageView imgShare;
    private LinearLayout llChild, layoutMain;
    private ServiceSubscriptionPlan serviceSubscriptionPlan;
    private TradWyseSession tradWyseSession;
    private MaterialTextView txtSubscribed;
    private String serviceSubscriptionPlanId;
    private String isMentor;
    String fileUri;
    File mydir;
    LinearLayout bottomLinearLayout;
    private int REQUEST_PERMISSION = 112;
    private String selectedScreen = "";
    ActivityServiceDetailsBinding serviceDetailsBinding;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_service_details);
        if (getIntent().getStringExtra("selectedScreen") != null)
            selectedScreen = getIntent().getStringExtra("selectedScreen");

        FooterModel footerModel = null;

        if (selectedScreen.equalsIgnoreCase(Constants.HOME)) {
            footerModel = new FooterModel(true, false, false, false, false);
        } else if (selectedScreen.equalsIgnoreCase(Constants.MORE_TAB)) {
            footerModel = new FooterModel(false, false, false, false, true);
        } else if (selectedScreen.equalsIgnoreCase(Constants.NOTIFICATION)) {
            footerModel = new FooterModel(false, false, false, true, false);
        } else if (selectedScreen.equalsIgnoreCase(Constants.SERVICES)) {
            footerModel = new FooterModel(false, false, true, false, false);
        } else {
            footerModel = new FooterModel(false, false, false, false, false);
        }

        serviceDetailsBinding.setFooterModel(footerModel);
        getNotificationCount();
        initView();
        initListeners();
        Common.BottomTabColorChange(this,bottomLinearLayout);
    }

    void initView() {
        titleTv = findViewById(R.id.title_tv);
        progressBar = findViewById(R.id.progress);
        imgCross = findViewById(R.id.img_cross);
        imgService = findViewById(R.id.img_service);
        txtServiceType = findViewById(R.id.txt_service_type);
        txtDescription = findViewById(R.id.description);
        txtPrice = findViewById(R.id.txt_price);
        txtPurchase = findViewById(R.id.btn_purchase);
        imgShare = findViewById(R.id.img_share);
        llChild = findViewById(R.id.ll_child);
        layoutMain = findViewById(R.id.layoutMain);
        txtSubscribed = findViewById(R.id.txt_subscribed);
        bottomLinearLayout = findViewById(R.id.bottomView);


        tradWyseSession = TradWyseSession.getSharedInstance(ServiceDetailsScreen.this);

        if (getIntent() != null) {
            Intent intent = getIntent();
            serviceSubscriptionPlanId = intent.getStringExtra("serviceSubscriptionPlanId");
            isMentor = tradWyseSession.getIsMentor();
            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "serviceSubscriptionPlanModel", MODE_PRIVATE);
            serviceSubscriptionPlan = complexPreferences.getObject("serviceSubscriptionPlanModelObject", ServiceSubscriptionPlan.class);

            if (serviceSubscriptionPlan != null) {
                serviceMentorSubscriptionPlanDetails(serviceSubscriptionPlanId, isMentor);
                //updateUi(serviceSubscriptionPlan, isMentor);
                llChild.setVisibility(View.VISIBLE);
            } else {
                serviceMentorSubscriptionPlanDetails(serviceSubscriptionPlanId, isMentor);
                llChild.setVisibility(View.GONE);
            }
        }
    }


    void initListeners() {
        imgCross.setOnClickListener(this);
        imgShare.setOnClickListener(this);
        txtPurchase.setOnClickListener(this);
    }

    public void serviceMentorSubscriptionPlanDetails(String serviceSubscriptionPlanId, String isMentor) {
        progressBar.setVisibility(View.VISIBLE);
        llChild.setVisibility(View.GONE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, ServiceDetailsScreen.this);
        Call<ServiceSubscriptionPlan> call = apiInterface.serviceMentorSubscriptionPlanDetails(serviceSubscriptionPlanId);
        call.enqueue(new Callback<ServiceSubscriptionPlan>() {
            @Override
            public void onResponse(@NotNull Call<ServiceSubscriptionPlan> call, @NotNull Response<ServiceSubscriptionPlan> response) {
                progressBar.setVisibility(View.GONE);
                layoutMain.setVisibility(View.VISIBLE);
                if (response.body() != null) {

                    if (serviceSubscriptionPlan == null)
                        serviceSubscriptionPlan = response.body();

                    if (serviceSubscriptionPlan != null) {
                        updateUi(serviceSubscriptionPlan, isMentor);
                        llChild.setVisibility(View.VISIBLE);
                    } else {
                        llChild.setVisibility(View.GONE);
                    }
                } else {
                    llChild.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServiceSubscriptionPlan> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                llChild.setVisibility(View.GONE);
                Log.d("error:", "" + t.toString());
                Log.d(Constants.ON_FAILURE_TAG, "ServiceFragment getAllServiceSubscriptionPlanForMentor: onFailure");
                Common.showOfflineMemeDialog(ServiceDetailsScreen.this, getResources().getString(R.string.memeMsg),
                        getResources().getString(R.string.JustLetYouKnow));

            }
        });
    }

    private void updateUi(ServiceSubscriptionPlan serviceSubscriptionPlan, String isMentor) {

        txtServiceType.setText("" + serviceSubscriptionPlan.getServiceTypeMaster().getServiceType());
        titleTv.setText("Service Details");

        String text = serviceSubscriptionPlan.getDescriptionOne();


        txtDescription.setText(text);

        double roundOff = Math.round(serviceSubscriptionPlan.getPrice() * 100.0) / 100.0;

        if (roundOff % 1 == 0) {
            int price = (int) roundOff;
            txtPrice.setText(price > 0 ? ("$" + price) : "Free");
        } else {
            txtPrice.setText(roundOff > 0 ? ("$" + roundOff) : "Free");
        }


        if (serviceSubscriptionPlan.getImageDetails() != null) {
            Picasso.get().load(APIClient.BASE_SERVER_URL_IMAGE + serviceSubscriptionPlan.getImageDetails()).placeholder(R.drawable.placeholder).into(imgService);
        }

        imgService.setOnClickListener(v-> {
            FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(this, APIClient.BASE_SERVER_URL_IMAGE + serviceSubscriptionPlan.getImageDetails(), Constants.ImageMessageType);
        });

        imgShare.setVisibility(View.VISIBLE);

        imgShare.setOnClickListener(v -> requestStoragePermission());

        if (isMentor != null && isMentor.equalsIgnoreCase("true")) {

            imgShare.setVisibility(View.VISIBLE);

            if ((!serviceSubscriptionPlan.getSubscribed()) && serviceSubscriptionPlan.getSuggested()) {
                txtPurchase.setText("View");
                //txtPurchase.setVisibility(View.VISIBLE);
            } else if (roundOff > 0 && !serviceSubscriptionPlan.getSubscribed() && !serviceSubscriptionPlan.getSuggested()) {
                txtPurchase.setText("Purchase");
            } else {
                txtPurchase.setText("View");
            }

            if (tradWyseSession.getUserId().equalsIgnoreCase(serviceSubscriptionPlan.getMentorId())) {
                txtPurchase.setText("View");
            }

            // MACD Tips service always free for mentor only.
            if (serviceSubscriptionPlan.getServiceTypeMaster().getServiceType().equalsIgnoreCase("MACDTips")) {
                txtPurchase.setText("View");
                txtPrice.setText("Free");
            }

        } else {

            if ((!serviceSubscriptionPlan.getSubscribed()) && (!serviceSubscriptionPlan.getSuggested())) {
                if (roundOff > 0 && !serviceSubscriptionPlan.getSubscribed()) {
                    txtPurchase.setText("Purchase");
                } else {
                    txtPurchase.setText("View");
                }
            } else if ((serviceSubscriptionPlan.getSubscribed()) || serviceSubscriptionPlan.getSuggested()) {
                txtPurchase.setText("View");
            }

        }

        if (serviceSubscriptionPlan.getSubscribed()) {
            txtSubscribed.setText("Subscribed");
            txtSubscribed.setVisibility(View.GONE);
        } else if (serviceSubscriptionPlan.getSuggested()) {
            txtSubscribed.setText("Suggested");
            txtSubscribed.setVisibility(View.GONE);
        } else {
            txtSubscribed.setVisibility(View.GONE);
        }

        if (txtPurchase.getText().toString().equals("View") && (serviceSubscriptionPlan.getDescriptionTwo() == null
                || serviceSubscriptionPlan.getDescriptionTwo().equalsIgnoreCase(""))) {
            txtPurchase.setVisibility(View.GONE);
        } else {
            txtPurchase.setVisibility(View.VISIBLE);
        }

    }

   @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cross:
                setResult(ProfileTabbedActivity.REQUEST_CODE_FOR_SERVICE_DETAILS, new Intent());
                finish();
                break;
            case R.id.img_share:
                requestStoragePermission();
                break;
            case R.id.btn_purchase:
                if (txtPurchase.getText().toString().equals("Purchase")) {
                    if (tradWyseSession.isSubscribedMember()) {
                        startActivity(new Intent(ServiceDetailsScreen.this, CheckoutActivity.class)
                                .putExtra(Constants.FROM, Constants.FROM_SERVICE_LIST)
                                .putExtra("id", serviceSubscriptionPlan.getId())
                                .putExtra("description", serviceSubscriptionPlan.getTitle())
                                .putExtra("price", serviceSubscriptionPlan.getPrice() + ""));
                    } else {
                        Common.becomeMemberDialog(ServiceDetailsScreen.this, getString(R.string.just_letting_you_know_description), getString(R.string.just_letting_you_know), serviceSubscriptionPlan);
                    }
                } else if (txtPurchase.getText().toString().equals("View")) {
                    String browser = "";
                    for (int i = 0; i < browserPackageName.size(); i++) {
                        if (browserPackageName.get(i).equals("com.android.chrome")) {
                            browser = browserPackageName.get(i);
                            break;
                        } else {
                            browser = browserPackageName.get(0);
                        }
                    }

                    try {
                        CustomTabsIntent customTabsIntent = Common.getCustomTabBuilder(ServiceDetailsScreen.this).build();
                        customTabsIntent.intent.setPackage(browser);
                        customTabsIntent.launchUrl(ServiceDetailsScreen.this, Uri.parse(serviceSubscriptionPlan.getDescriptionTwo()));
                    } catch(Exception e) {
                        Toast.makeText(this, "The link is not working, Please try again later.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    public void requestStoragePermission() {
        Dexter.withContext(ServiceDetailsScreen.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Common.prepareServiceShareData(serviceSubscriptionPlan, ServiceDetailsScreen.this);

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(ServiceDetailsScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.style_progress_dialog);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }


    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    public void homeTabClicked(View v) {
        DashBoardActivity.CallDashboardActivity(this, false);
        finish();
    }

    public void chatTabClicked(View v) {
        ChatActivity.starChatActivity(this,false);
        //TabbedChatActivity.CallTabbedChatActivity(this, true);
        finish();
    }

    public void servicesTabClicked(View v) {
        ServicesActivity.CallServicesActivity(this);
        finish();
    }

    public void notificationTabClicked(View v) {
        NotificationActivity.starNotificationActivity(this);
        finish();
    }

    public void moreTabClicked(View v) {
        MoreTabActivity.startMoreTabActivity(this);
        finish();
    }

    private void getNotificationCount() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<NotificationCountModel> call = apiInterface.getCountOfAllUnreadNotificationActivityDetails();
        call.enqueue(new Callback<NotificationCountModel>() {
            @Override
            public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                if (response != null && response.body() != null) {
                    NotificationCountModel notificationCountModel = response.body();
                    if (notificationCountModel.getUnReadCount() > 0) {
                        (findViewById(R.id.nav_unread_badge)).setVisibility(View.VISIBLE);
                    } else {
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

}
