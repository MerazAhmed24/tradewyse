package com.info.tradewyse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.felipecsl.gifimageview.library.GifImageView;
import com.info.adapter.MACDBuyAdapter;
import com.info.adapter.MACDDetailAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.DateTimeHelperElapsed;
import com.info.dialog.WhatIsMacdDialog;
import com.info.model.BuyStock;
import com.info.model.FooterModel;
import com.info.model.NotificationCountModel;
import com.info.model.SellStock;
import com.info.tradewyse.databinding.ActivityMacdAlertDetailsBinding;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class MacdAlertDetailsActivity extends BaseActivity {

    private String from;
    private ArrayList<BuyStock> buyStockArrayList = new ArrayList<>();
    private ArrayList<SellStock> sellStockArrayList = new ArrayList<>();
    private String gifFile;
    private RelativeLayout txtMACDSell, txtMACDBuy;
    private ImageView imageViewBuy, imageViewPlayPause;
    private TextView txtDateTime, txtSignals, txtBuyTitleDetail, textViewImagePress;
    private RecyclerView recyclerViewMacdAlert;
    private GifImageView gifImageViewBottomImage;
    private ActivityMacdAlertDetailsBinding binding;
    boolean isFromNotificationClick = false;
    private LinearLayout layoutBlank;
    private RelativeLayout layoutTitles;
    private CardView layoutBottomImage;
    private ProgressDialog progressDialog;
    LinearLayout bottomlinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_macd_alert_details);
        FooterModel footerModel = new FooterModel(true, false, false, false, false);
        binding.setFooterModel(footerModel);

        if (getIntent() != null) {
            isFromNotificationClick = getIntent().getBooleanExtra("fromNotificationClick", false);
            from = getIntent().getStringExtra("from");
            gifFile = getIntent().getStringExtra("gifFile");
            if (from.equals("Buy")) {
                buyStockArrayList = getIntent().getParcelableArrayListExtra("dataList");
            } else {
                sellStockArrayList = getIntent().getParcelableArrayListExtra("dataList");
            }
        }

        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("MACD Tips Details");

        txtMACDSell = findViewById(R.id.txtMACDSell);
        txtMACDBuy = findViewById(R.id.txtMACDBuy);
        imageViewBuy = findViewById(R.id.imageViewBuy);
        txtDateTime = findViewById(R.id.txtDateTime);
        txtSignals = findViewById(R.id.txtSignals);
        recyclerViewMacdAlert = findViewById(R.id.recyclerViewMacdAlert);
        gifImageViewBottomImage = findViewById(R.id.gifImageViewBottomImage);
        txtBuyTitleDetail = findViewById(R.id.txtBuyTitleDetail);
        layoutTitles = findViewById(R.id.layoutTitles);
        layoutBlank = findViewById(R.id.layoutBlank);
        bottomlinearLayout = findViewById(R.id.bottomView);
        layoutBottomImage = findViewById(R.id.layoutBottomImage);
        imageViewPlayPause = findViewById(R.id.imageViewPlayPause);
        textViewImagePress = findViewById(R.id.textViewImagePress);

        getNotificationCount();
        Common.BottomTabColorChange(this,bottomlinearLayout);

        if (from.equals("Buy")) {

            if (buyStockArrayList.size() > 0) {

                txtBuyTitleDetail.setText("S&P500 and Nasdaq100\n" +
                        "All MACD convergence crossover signals\n" + buyStockArrayList.get(0).getCrossoverDate());

                txtDateTime.setText(DateTimeHelperElapsed.toString(buyStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "
                        + getResources().getString(R.string.TimeZone));
                if (!buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") &&
                        !buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("buy_stock") &&
                        buyStockArrayList.get(0).getStockSymbol() != null) {

                    layoutTitles.setVisibility(View.VISIBLE);
                    layoutBlank.setVisibility(View.GONE);
                    recyclerViewMacdAlert.setVisibility(View.VISIBLE);

                    // For Buy Card
                    if (buyStockArrayList.size() <= 1) {
                        txtSignals.setText(buyStockArrayList.size() + " Signal");
                    } else {
                        txtSignals.setText(buyStockArrayList.size() + " Signals");
                    }

                } else {

                    layoutTitles.setVisibility(View.INVISIBLE);
                    layoutBlank.setVisibility(View.VISIBLE);
                    recyclerViewMacdAlert.setVisibility(View.GONE);
                    txtSignals.setText("0 Signal");

                }

            } else {

                layoutTitles.setVisibility(View.INVISIBLE);
                layoutBlank.setVisibility(View.VISIBLE);
                recyclerViewMacdAlert.setVisibility(View.GONE);
                txtSignals.setText("0 Signal");
                //txtBuyTitleDetail.setText("Tracking all S&P 500 & Nasdaq 100 MACD Events List created from the crossovers of " + ".");
            }

            txtMACDBuy.setVisibility(View.VISIBLE);
            txtMACDSell.setVisibility(View.GONE);
            imageViewBuy.setImageResource(R.drawable.buy_icon);

        } else {

            if (sellStockArrayList.size() > 0) {

                txtBuyTitleDetail.setText("S&P500 and Nasdaq100\n" +
                        "All MACD convergence crossover signals\n" + sellStockArrayList.get(0).getCrossoverDate());
                txtDateTime.setText(DateTimeHelperElapsed.toString(sellStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "

                        + getResources().getString(R.string.TimeZone));
                if (!sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") &&
                        !sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("sell_stock") &&
                        sellStockArrayList.get(0).getStockSymbol() != null) {

                    layoutTitles.setVisibility(View.VISIBLE);
                    layoutBlank.setVisibility(View.GONE);
                    recyclerViewMacdAlert.setVisibility(View.VISIBLE);

                    // For Sell Card
                    if (sellStockArrayList.size() <= 1) {
                        txtSignals.setText(sellStockArrayList.size() + " Signal");
                    } else {
                        txtSignals.setText(sellStockArrayList.size() + " Signals");
                    }

                } else {

                    layoutTitles.setVisibility(View.INVISIBLE);
                    layoutBlank.setVisibility(View.VISIBLE);
                    recyclerViewMacdAlert.setVisibility(View.GONE);
                    txtSignals.setText("0 Signal");

                }

            } else {

                layoutTitles.setVisibility(View.INVISIBLE);
                layoutBlank.setVisibility(View.VISIBLE);
                recyclerViewMacdAlert.setVisibility(View.GONE);
                txtSignals.setText("0 Signal");
            }

            txtMACDBuy.setVisibility(View.GONE);
            txtMACDSell.setVisibility(View.VISIBLE);
            imageViewBuy.setImageResource(R.drawable.sell_icon);

        }

        if (gifFile.equalsIgnoreCase("") || gifFile == null) {
            layoutBottomImage.setVisibility(View.GONE);
        } else {
            layoutBottomImage.setVisibility(View.VISIBLE);
            //Glide.with(this).load(gifFile).into(gifImageViewBottomImage);

            // Set gif and start with animation.
            new RetrieveByteArray().execute(gifFile);
            gifImageViewBottomImage.startAnimation();
            textViewImagePress.setText("Press image to stop");

            gifImageViewBottomImage.setOnClickListener(v -> {
                if (gifImageViewBottomImage.isAnimating()) {
                    gifImageViewBottomImage.stopAnimation();
                    imageViewPlayPause.setVisibility(View.VISIBLE);
                    imageViewPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow));
                    textViewImagePress.setText("Press image to play");
                } else {
                    gifImageViewBottomImage.startAnimation();
                    imageViewPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    imageViewPlayPause.setVisibility(View.GONE);
                    textViewImagePress.setText("Press image to stop");
                }
            });
        }

        findViewById(R.id.backAction).setOnClickListener(v -> {
            super.onBackPressed();
        });

        findViewById(R.id.layoutWhatIsMACD).setOnClickListener(v -> {
            showPopup();
            //Common.showMessageWithCenterText(this, "MACD triggers technical signals when it crosses above (to buy) or below (to sell) its signal line.", "MACD");
        });

        MACDDetailAdapter macdBuyAdapter = new MACDDetailAdapter(this, buyStockArrayList, sellStockArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMacdAlert.setLayoutManager(layoutManager);
        recyclerViewMacdAlert.setAdapter(macdBuyAdapter);

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

    private void showPopup() {
        new WhatIsMacdDialog(this) {
            @Override
            public void gotItButtonClicked() {

            }

            @Override
            public void closeButtonClicked() {

            }
        };
    }

    @Override
    public void onBackPressed() {
        if (isFromNotificationClick) {
            Intent mIntent = new Intent(MacdAlertDetailsActivity.this, DashBoardActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
        } else {
            finishActivity();
        }
    }

    public void onBackPress(View v) {
        //handleBackPress();
        if (isFromNotificationClick) {
            Intent mIntent = new Intent(MacdAlertDetailsActivity.this, DashBoardActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
        } else
            onBackPressed();

    }

    public void finishActivity() {
        MacdAlertDetailsActivity.this.finish();
    }

    class RetrieveByteArray extends AsyncTask<String, Void, byte[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected byte[] doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[10240];
                    while ((nRead = in.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    return buffer.toByteArray();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            dismissProgressDialog();
            gifImageViewBottomImage.setBytes(bytes);

            // For stop gif animation after 10 second.
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gifImageViewBottomImage.stopAnimation();
                    imageViewPlayPause.setVisibility(View.VISIBLE);
                    imageViewPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow));
                    textViewImagePress.setText("Press image to play.");
                }
            }, 10000);

        }
    }

    public void showProgressDialog() {
        if (!isFinishing()) {
            try {
                progressDialog = new ProgressDialog(MacdAlertDetailsActivity.this, R.style.style_progress_dialog);
                progressDialog.setMessage("Loading....");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dismissProgressDialog() {
        try {
            if (!isFinishing() && progressDialog.isShowing()) {
                progressDialog.dismiss();
            } else {
                Log.e("MacdAlertDetailsError", "dialog not dismiss");
            }
        } catch (Exception e) {
            Log.e("MacdAlertDetailsError", "-> " + e.getMessage());
        }
    }

}