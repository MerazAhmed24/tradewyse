package com.info.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.info.tradewyse.R;

import java.util.Objects;


public abstract class ImageCaptionDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AppCompatImageView imgPicked;
    private AppCompatEditText editCaption;
    private Bitmap bitmap;
    private VideoView videoView;
    String captionText;
    Uri videoPath;

    public ImageCaptionDialog(@NonNull Context context, Bitmap bitmap, Uri videoPath, String captionText) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.bitmap = bitmap;
        this.videoPath = videoPath;
        this.captionText = captionText;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image_caption, null);

        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }
        show();
        initView(view);
    }

    public void initView(View view) {
        AppCompatImageView imgCross = view.findViewById(R.id.img_cross);
        imgPicked = view.findViewById(R.id.img_picked);
        AppCompatImageView imgSend = view.findViewById(R.id.img_send);
        editCaption = view.findViewById(R.id.edit_caption);
        videoView = view.findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(context);
        videoView.setMediaController(mediaController);

        editCaption.setText(captionText);
        if (bitmap != null) {
            imgPicked.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            imgPicked.setImageBitmap(bitmap);
        } else {
            imgPicked.setVisibility(View.GONE);
            editCaption.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(videoPath);
            videoView.start();
        }

        imgSend.setOnClickListener(this);
        imgCross.setOnClickListener(this);
    }


    public abstract void onClickSend(String captionText);


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cross:
                dismiss();
                break;

            case R.id.img_send:
                onClickSend(Objects.requireNonNull(editCaption.getText()).toString());
                dismiss();
                break;
        }
    }
}
