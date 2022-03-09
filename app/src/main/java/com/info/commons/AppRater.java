package com.info.commons;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info.tradewyse.R;

public class AppRater {
    private final static String APP_TITLE = "TradeTips";// App Name
    private final static String APP_PNAME = "com.info.tradetips";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 0;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 0;//Min number of launches


    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle2);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_app_review_dilaog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TradWyseSession tradWyseSession = TradWyseSession.getSharedInstance(mContext);


        TextView tvRemindLater = dialog.findViewById(R.id.tvRemindLater);
        TextView tvNothanks = dialog.findViewById(R.id.tvNoThanks);
        TextView TvRateNow = dialog.findViewById(R.id.tvRateNow);
        TextView tvTitle = dialog.findViewById(R.id.tvtitleApp);


        tvTitle.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
        TvRateNow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tradWyseSession.setRateNow(true);
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();

            }
        });

        tvRemindLater.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                tradWyseSession.setReviewCount(0);
            }
        });
        tvNothanks.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}