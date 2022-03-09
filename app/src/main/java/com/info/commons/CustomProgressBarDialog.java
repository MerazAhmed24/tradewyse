package com.info.commons;

import static com.info.dialog.NewAdBannerDialog.getDeviceMetrics;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.info.model.AdBannerModel;
import com.info.tradewyse.R;


public abstract class CustomProgressBarDialog extends Dialog {

    private Context context;
    private TextView txtProgress;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();

    public CustomProgressBarDialog(@NonNull Context context) {
        super(context, R.style.AddBannerDialog);
        this.context = context;

        View view = LayoutInflater.from(context).inflate(R.layout.custom_progressbar, null);
        setContentView(view);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        if (window != null) {
            int width = window.getAttributes().width = (int) (getDeviceMetrics(context).widthPixels * 0.9);
            window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        show();
        initView(view);
    }

    public abstract void onBackPressedClicked();

    public void initView(View view) {
        txtProgress = (TextView) view.findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(pStatus);
                            txtProgress.setText(pStatus + " %");
                            if (pStatus == 100 && context != null) {
                                Common.showMessageWithFinishActivity(context, context.getResources().getString(R.string.prediction_error_message), context.getResources().getString(R.string.we_are_making_prediction));
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus++;
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressedClicked();
    }
}
