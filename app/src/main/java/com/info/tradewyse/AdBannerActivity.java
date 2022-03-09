package com.info.tradewyse;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.info.CustomToast.Toasty;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Constants;
import com.info.dialog.AddBannerDialog;
import com.info.model.AdBannerModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdBannerActivity extends BaseActivity {
    private static final String TAG = "AdBannerActivity";
    boolean isFromNotificationClick = false;
    String adbannerId;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_graph_stock_prediction);
        adbannerId = getIntent().getStringExtra("adbannerId");
        isFromNotificationClick = getIntent().getBooleanExtra("fromNotificationClick", false);
        if (adbannerId == null) {
            Toasty.error(this,"AdBanner Id not found...").show();
            finishActivity();
        }/*else
            getAdBannerById(adbannerId, 0);*/
    }

    @Override
    public void onBackPressed() {
        if (isFromNotificationClick) {
            redirectionToDashBoard();
        } else {
            finishActivity();
        }
    }

    public void onBackPress(View v) {
        //handleBackPress();
        if (isFromNotificationClick) {
            redirectionToDashBoard();
        } else
            onBackPressed();
    }

    public void finishActivity() {
        AdBannerActivity.this.finish();
    }

    private void redirectionToDashBoard() {
        Intent mIntent = new Intent(AdBannerActivity.this, DashBoardActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mIntent);
    }

}
