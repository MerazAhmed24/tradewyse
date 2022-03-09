package com.info.dialog;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.info.adapter.NewAdBannerAdapter;
import com.info.api.APIClient;
import com.info.model.AdBannerModel;
import com.info.tradewyse.R;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;


public abstract class NewAdBannerDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AdBannerModel adBannerModel;
    private int currentPage = 0;
    private ViewPager pager;
    private CircleIndicator indicator;

    public NewAdBannerDialog(@NonNull Context context, AdBannerModel adBannerModel) {
        super(context, R.style.AddBannerDialog);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.new_ad_banner_popup, null);
        this.adBannerModel = adBannerModel;

        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        if (window != null) {
            int width = window.getAttributes().width = (int) (getDeviceMetrics(context).widthPixels * 0.9);
            window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        show();
        initView(view);
        setDataForSlider();
    }

    public static DisplayMetrics getDeviceMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

    private void setDataForSlider() {
        if (adBannerModel.getTitle2() != null || adBannerModel.getTitle3() != null) {
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.GONE);
        }
        NewAdBannerAdapter newAdBannerAdapter = new NewAdBannerAdapter(context, adBannerModel);
        pager.setAdapter(newAdBannerAdapter);
        indicator.setViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                if (position == 0) {
                    currentPage = 0;
                } else if (position == 1) {
                    currentPage = 1;
                } else if (position == 2) {
                    currentPage = 2;
                }
            }
        });
        
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 3) {
                    currentPage = 0;
                }
                pager.setCurrentItem(currentPage++, true);
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, 4000);

        newAdBannerAdapter.setOnAdBannerListener(new NewAdBannerAdapter.AdBannerListener() {
            @Override
            public void onCloseClick(int position) {
                dismiss();
            }
        });
    }


    public void initView(View view) {
        //ImageView bannerImage = view.findViewById(R.id.center_iv);
        Button gotItBtn = view.findViewById(R.id.got_it_btn);
        ImageView ad_banner_close_btn = view.findViewById(R.id.ad_banner_close_btn);

        pager = view.findViewById(R.id.pager);
        indicator = view.findViewById(R.id.indicator);


        gotItBtn.setOnClickListener(this);
        ad_banner_close_btn.setOnClickListener(this);
    }


    public abstract void gotItButtonClicked(AdBannerModel adBannerModel);

    public abstract void closeButtonClicked(AdBannerModel adBannerModel);

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.got_it_btn:
                gotItButtonClicked(adBannerModel);
                dismiss();
                break;
            case R.id.ad_banner_close_btn:
                gotItButtonClicked(adBannerModel);
                dismiss();
                break;
        }
    }

}
