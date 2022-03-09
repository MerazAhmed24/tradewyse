package com.info.tradewyse;

import static com.info.config.Config.title;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.FIleHelper;
import com.info.commons.PhotoUtility;
import com.info.commons.StringHelper;
import com.info.commons.TradWyseSession;
import com.info.interfaces.PhotoOptionSelectListener;
import com.info.logger.Logger;
import com.info.model.LogoutResponse;
import com.info.model.User;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static okhttp3.MultipartBody.FORM;
import static okhttp3.MultipartBody.Part.createFormData;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    TextView txtEditPhoto;
    PhotoUtility photoUtility;
    SimpleDraweeView imgProfile;
    TradWyseSession tradWyseSession;
    public String userName = "";
    TextView txtUserName;
    View view15, view4;
    RelativeLayout rlSubscription, rlBookTimeWithMentor, rlPlayAppOVerView, rlFAQ, rlConctactUs,
            rlBecomeMentor, rlLogout, optionYourSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        setToolBar(getResources().getString(R.string.settings));
        tradWyseSession = TradWyseSession.getSharedInstance(this);
        imgProfile = findViewById(R.id.imgProfile);
        txtEditPhoto = findViewById(R.id.txtEditPhoto);
        txtUserName = findViewById(R.id.txtUserName);
        rlSubscription = findViewById(R.id.optionSubs);
        optionYourSubs = findViewById(R.id.optionYourSubs);
        rlBookTimeWithMentor = findViewById(R.id.optionAppointment);
        rlPlayAppOVerView = findViewById(R.id.optionTour);
        view4 = findViewById(R.id.view4);
        view15 = findViewById(R.id.view15);
        rlFAQ = findViewById(R.id.optionFAQ);
        rlConctactUs = findViewById(R.id.optionContactUs);
        rlBecomeMentor = findViewById(R.id.optionBecomeMentor);
        rlLogout = findViewById(R.id.optionLogout);
        rlConctactUs.setOnClickListener(this);
        rlFAQ.setOnClickListener(this);
        rlPlayAppOVerView.setOnClickListener(this);
        rlBookTimeWithMentor.setOnClickListener(this);
        rlSubscription.setOnClickListener(this);
        rlBecomeMentor.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
        optionYourSubs.setOnClickListener(this);
        if (!tradWyseSession.isSubscribedMember()) {
            optionYourSubs.setVisibility(View.VISIBLE);
            rlSubscription.setVisibility(View.GONE);
            view15.setVisibility(View.GONE);
            view4.setVisibility(View.VISIBLE);
        } else {
            optionYourSubs.setVisibility(View.GONE);
            rlSubscription.setVisibility(View.VISIBLE);
            //rlSubscription.setVisibility(View.VISIBLE);
            view15.setVisibility(View.GONE);
            view4.setVisibility(View.VISIBLE);
        }
        String isMentorString = tradWyseSession.getIsMentor();
        if (!StringHelper.isEmpty(isMentorString) && isMentorString.equalsIgnoreCase("true")) {
            rlBecomeMentor.setVisibility(View.GONE);
            (findViewById(R.id.view9)).setVisibility(View.GONE);
        } else {
            rlBecomeMentor.setVisibility(View.VISIBLE);
            (findViewById(R.id.view9)).setVisibility(View.VISIBLE);
        }

        // set on-click listener
        txtEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(SettingActivity.this, "You clicked me.", Toast.LENGTH_SHORT).show();
                takePhoto();
            }
        });
        userName = tradWyseSession.getUserName();
        txtUserName.setText(userName);
        setToolBarAddTip(getString(R.string.settings));
        getUserDetail(userName);
    }

    public void getUserDetail(String userName) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, SettingActivity.this);
        Call<User> call = apiInterface.getAppUserByUserName(userName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (!response.body().getId().isEmpty()) {
                        User userResponse = response.body();
                        if (userResponse.getUserName().equalsIgnoreCase(tradWyseSession.getUserName())) {
                            tradWyseSession.setUserImage(userResponse.getImage());
                            tradWyseSession.setInternalSubscribedMember(userResponse.getInternalSubscribedUser());
                        }
                        getProfileImage(userResponse.getId());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity getUserDetail: onFailure");
            }
        });

    }

    private void takePhoto() {
        try {
            photoUtility = new PhotoUtility.Builder(this)
                    .setImageView(imgProfile)
                    .setOutPutFile(FIleHelper.createNewFile(this, FIleHelper.createFileName("profile")))
                    .build();
            photoUtility.setPhotoOptionSelectListener(new PhotoOptionSelectListener() {
                @Override
                public void requestPermissions(@NonNull String[] permissions, int requestCode) {
                    ActivityCompat.requestPermissions(SettingActivity.this, permissions, requestCode);
                }

                @Override
                public void startActivityForResult(Intent intent, int requestCode) {
                    ActivityCompat.startActivityForResult(SettingActivity.this, intent, requestCode, null);
                }
            });

            photoUtility.requestPermissionsCameraAndStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProfileImage(String userId) {
        //Common.downloadImage(userName,userImage,this,imgProfile);
        Common.setProfileImage(imgProfile, userId);
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
                    Toast.makeText(SettingActivity.this, "Profile photo uploaded successfully", Toast.LENGTH_LONG).show();
                    Common.evictImage(userId);
                    setResult(RESULT_OK);
                } else {
                    Toast.makeText(SettingActivity.this, "Unable to upload image at the moment.Please try again later", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                Toast.makeText(SettingActivity.this, "Unable to upload image at the moment.Please try again later", Toast.LENGTH_LONG).show();
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity uploadProfilePhoto: onFailure");
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("Onactivityresult", "onactivity result profile activity");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 110 || requestCode == 111) {
            photoUtility.setResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                uploadProfilePhoto();
            }
        } else if (requestCode == Constants.REQUEST_CODE_FOLLOW_SCREEN) {
            Log.d("Onactivityresult", "onactivity result profile req code follow screen");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        photoUtility.setPermissionResult(requestCode, grantResults);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.optionSubs:
                Log.v("Click", "btn click");
                Intent intent = SubscriptionActivity.Companion.newIntent(getApplicationContext());
                intent.putExtra(Constants.FROM,Constants.FROM_SETTING);
                startActivity(intent);
                //Toast.makeText(SettingActivity.this, "This Option is Coming soon!", Toast.LENGTH_LONG).show();
                // Statements
                break; // break is optional
            case R.id.optionAppointment:
                Log.v("Click", "btn click");
                Toast.makeText(SettingActivity.this, "This Option is Coming soon!", Toast.LENGTH_LONG).show();
                // Statements
                break; // break is optional
            case R.id.optionTour:
                Log.v("Click", "btn click");
                // Statements
                Intent tour = new Intent(SettingActivity.this, TourActivity.class);
                tour.putExtra("fromWhere", "AfterLogin");
                startActivity(tour);
                break; // break is optional
            case R.id.optionFAQ:
                openFaqActivity();
                Logger.verbose("Click", "btn click");
                break; // break is optional
            case R.id.optionContactUs:
                Toast.makeText(SettingActivity.this, "This Option is Coming soon!", Toast.LENGTH_LONG).show();
                // Statements
                break; // break is optional
            case R.id.optionBecomeMentor:
                startActivity(new Intent(SettingActivity.this, BecomeMentorActivity.class));
                Log.v("Click", "btn click");
                // Statements
                break; // break is optional
            case R.id.optionLogout:
                // Statements
                String loginInfoId=TradWyseSession.getSharedInstance(SettingActivity.this).getLoginInfoId();
                userLogout(loginInfoId);

                break; // break is optional
            case R.id.optionYourSubs:
                //startActivity(SubscriptionListActivity.Companion.newIntent(this));
                break;
        }
    }

    private void openFaqActivity() {
        startActivity(new Intent(SettingActivity.this, FaqActivity.class));
    }


    public void userLogout(String deviceLoginId) {

        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, SettingActivity.this);
        Call<LogoutResponse> call = apiInterface.logoutUser(deviceLoginId);
        showProgressDialog();
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                dismissProgressDialog();
                if (response.body() != null) {
                    if (response.body().getStatus().equals("true") && response.body().getTitle().equals("Success")) {
                        Common.logout(SettingActivity.this);
                    }
                    else
                    {
                        Common.showMessage(SettingActivity.this, response.body().getMessage(), response.body().getTitle());
                    }
                }
                else
                {
                    Toast.makeText(SettingActivity.this, "Something went wrong!please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                Toast.makeText(SettingActivity.this,"Something went wrong!please try again",Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                Common.showOfflineMemeDialog(SettingActivity.this, getResources().getString(R.string.memeMsg),
                        getResources().getString(R.string.JustLetYouKnow));

            }
        });

    }

}
