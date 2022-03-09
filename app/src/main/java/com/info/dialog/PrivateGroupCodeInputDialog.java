package com.info.dialog;

import static com.info.dialog.NewAdBannerDialog.getDeviceMetrics;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.info.tradewyse.R;

public abstract class PrivateGroupCodeInputDialog extends Dialog implements View.OnClickListener{
    Button StartRoomButton;
    EditText EnterCodeView;
    ImageView closeDialog;
    private Context context;

    public PrivateGroupCodeInputDialog(@NonNull Context context) {
        super(context, R.style.AddBannerDialog);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.chat_room_code_input_dialog, null);
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
        closeDialog = (ImageView) view.findViewById(R.id.closeDialogOne);
        EnterCodeView = (EditText) view.findViewById(R.id.etEnterCode);
        StartRoomButton = (Button) view.findViewById(R.id.StartRoomButton);
        closeDialog.setOnClickListener(this);
        StartRoomButton.setOnClickListener(this);

    }
    public abstract void closeDialog();

    public abstract void startRoom(String roomCode);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeDialogOne:
                closeDialog();
                dismiss();
                break;
            case R.id.StartRoomButton:

                if (EnterCodeView
                        .getText().toString().trim().length() > 0) {
                    startRoom(EnterCodeView
                            .getText().toString().trim());
                } else {
                    EnterCodeView.setError(context.getResources().getString(R.string.please_enter_room_code));
                }

                break;
        }

    }
}
