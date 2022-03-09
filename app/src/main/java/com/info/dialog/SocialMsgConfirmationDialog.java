package com.info.dialog;

import static com.info.dialog.NewAdBannerDialog.getDeviceMetrics;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.facebook.drawee.view.SimpleDraweeView;
import com.info.commons.Common;
import com.info.commons.DateTimeHelperElapsed;
import com.info.commons.TradWyseSession;
import com.info.tradewyse.R;


public abstract class SocialMsgConfirmationDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TradWyseSession tradWyseSession;
    private CheckBox view_check_box;
    private String userName = "";
    private String userId ="";


    public SocialMsgConfirmationDialog(@NonNull Context context, String msg) {
        super(context, R.style.AddBannerDialog);
        this.context = context;
        this.userName = userName;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirmation_social, null);


        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        if (window != null) {
            int width = window.getAttributes().width = (int) (getDeviceMetrics(context).widthPixels * 0.9);
            window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        show();
        initView(view, msg);
    }

    public void initView(View view, String msg) {

        ImageView imageViewClose = view.findViewById(R.id.closeDialog);
        SimpleDraweeView imageUser= view.findViewById(R.id.userImageSocial);
        Button buttonAddMe = view.findViewById(R.id.buttonPost);
        TextView tvDate = view.findViewById(R.id.tvDateTimeMessage);
        TextView textViewTitle = view.findViewById(R.id.tvSocialMessageConfirmation);
        view_check_box = view.findViewById(R.id.view_check_box);
        TextView tvUsername = view.findViewById(R.id.tvUsernameCngDialog);
        textViewTitle.setText(msg);
        tradWyseSession = TradWyseSession.getSharedInstance(context);
        userName = tradWyseSession.getUserName();
        userId = tradWyseSession.getUserId();

        Common.setProfileImage(imageUser, userId);
        tvUsername.setText(userName);

        tvDate.setText(DateTimeHelperElapsed.toString(System.currentTimeMillis(), "MMM dd, hh:mm a") + "  "
                + "EST");
        buttonAddMe.setOnClickListener(this);
        imageViewClose.setOnClickListener(this);
    }

    public abstract void postButtonClicked(boolean noNeedToSeeAgain);


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeDialog:
                dismiss();
                break;

            case R.id.buttonPost:
                if (view_check_box.isChecked()) {
                    postButtonClicked(true);
                } else {
                    postButtonClicked(false);
                }
                dismiss();
                break;
        }
    }

}
