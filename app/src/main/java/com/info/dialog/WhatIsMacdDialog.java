package com.info.dialog;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.info.api.APIClient;
import com.info.model.AdBannerModel;
import com.info.tradewyse.R;
import com.squareup.picasso.Picasso;


public abstract class WhatIsMacdDialog extends Dialog implements View.OnClickListener {

    private Context context;

    public WhatIsMacdDialog(@NonNull Context context) {
        super(context, R.style.AddBannerDialog);
        this.context = context;

        View view = LayoutInflater.from(context).inflate(R.layout.common_new_dialog, null);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        show();
        initView(view);
    }

    public void initView(View view) {
        ImageView imageViewClose = view.findViewById(R.id.imageViewClose);
        Button buttonGotIt = view.findViewById(R.id.buttonGotIt);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);

        buttonGotIt.setOnClickListener(this);
        imageViewClose.setOnClickListener(this);
    }

    public abstract void gotItButtonClicked();

    public abstract void closeButtonClicked();

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewClose:
                closeButtonClicked();
                dismiss();
                break;

            case R.id.buttonGotIt:
                gotItButtonClicked();
                dismiss();
                break;
        }
    }

}
