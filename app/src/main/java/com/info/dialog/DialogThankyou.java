package com.info.dialog;

import static com.info.dialog.NewAdBannerDialog.getDeviceMetrics;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.info.tradewyse.R;

public class DialogThankyou extends Dialog implements View.OnClickListener {
    private Context context;
    ImageView closeDialogOne;
    Button btdialogclose;

    public DialogThankyou(@NonNull Context context) {
        super(context, R.style.AddBannerDialog);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_thank_you_social, null);
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
    }

    public void initView(View view) {
        closeDialogOne = (ImageView) view.findViewById(R.id.closeDialogOne);
        btdialogclose = (Button) view.findViewById(R.id.closeDialogTwo);
        closeDialogOne.setOnClickListener(this);
        btdialogclose.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeDialogOne:
                dismiss();
                break;
            case R.id.closeDialogTwo:
                dismiss();
                break;
        }
    }

}