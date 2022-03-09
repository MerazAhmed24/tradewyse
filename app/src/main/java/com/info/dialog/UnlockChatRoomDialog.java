package com.info.dialog;

import static com.info.dialog.NewAdBannerDialog.getDeviceMetrics;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.info.tradewyse.R;


public abstract class UnlockChatRoomDialog extends Dialog implements View.OnClickListener {

    private Context context;

    public UnlockChatRoomDialog(@NonNull Context context, String msg) {
        super(context, R.style.AddBannerDialog);
        this.context = context;

        View view = LayoutInflater.from(context).inflate(R.layout.unlock_chat_room_dialog, null);
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
        ImageView imageViewClose = view.findViewById(R.id.imageViewClose);
        Button buttonAddMe = view.findViewById(R.id.buttonAddMe);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);

        textViewTitle.setText(msg);
        buttonAddMe.setOnClickListener(this);
        imageViewClose.setOnClickListener(this);
    }

    public abstract void addMeButtonClicked();

    public abstract void closeButtonClicked();

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewClose:
                closeButtonClicked();
                dismiss();
                break;

            case R.id.buttonAddMe:
                addMeButtonClicked();
                dismiss();
                break;
        }
    }

}
