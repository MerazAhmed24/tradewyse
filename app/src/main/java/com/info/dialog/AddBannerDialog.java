package com.info.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
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

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;


public abstract class AddBannerDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AdBannerModel adBannerModel;
    TextView shortDescTv;
    TextView seeMoreTv;

    public AddBannerDialog(@NonNull Context context, AdBannerModel adBannerModel) {
        super(context, R.style.AddBannerDialog);
        this.context = context;
        this.adBannerModel = adBannerModel;

        View view = LayoutInflater.from(context).inflate(R.layout.ad_banner_popup, null);
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
        ImageView imgCross = view.findViewById(R.id.ad_banner_cose_btn);
        ImageView bannerImage = view.findViewById(R.id.center_iv);
        Button gotItBtn = view.findViewById(R.id.got_it_btn);
        TextView titleTv = view.findViewById(R.id.title_tv);
        shortDescTv = view.findViewById(R.id.short_desc_tv);
        seeMoreTv = view.findViewById(R.id.seemore);
        titleTv.setText(adBannerModel.getTitle());
        setText(shortDescTv, adBannerModel.getShortDescription());
        String imageUrl = APIClient.BASE_SERVER_URL_IMAGE + adBannerModel.getImage();
        Picasso.get().load(imageUrl).into(bannerImage);

        seeMoreTv.setOnClickListener(this);
        gotItBtn.setOnClickListener(this);
        imgCross.setOnClickListener(this);
    }

    private void seeMoreClicked() {
        if (seeMoreTv.getText().toString().equalsIgnoreCase(context.getString(R.string.see_more))) {
            seeMoreTv.setText("See less");
            setText(shortDescTv, adBannerModel.getLongDescription());
        } else {
            seeMoreTv.setText(context.getString(R.string.see_more));
            setText(shortDescTv, adBannerModel.getShortDescription());
        }
    }

    public abstract void gotItButtonClicked(AdBannerModel adBannerModel);

    public abstract void closeButtonClicked(AdBannerModel adBannerModel);

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ad_banner_cose_btn:
                closeButtonClicked(adBannerModel);
                dismiss();
                break;
            case R.id.got_it_btn:
                gotItButtonClicked(adBannerModel);
                dismiss();
                break;
            case R.id.seemore:
                seeMoreClicked();
                break;
        }
    }

    private void setText(TextView textView, String ss) {

        if (ss != null && ss.length() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(ss, Html.FROM_HTML_MODE_LEGACY));
                //textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                textView.setText(Html.fromHtml(ss));
            }
            Linkify.addLinks(textView, Linkify.ALL);
        }

    }

    private void setSpannableTextForSeeMore(String name, TextView textView) {
        name += "\n\n" + context.getString(R.string.see_more);
        SpannableString ss = new SpannableString(name);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                setSpannableTextForSeeless(adBannerModel.getLongDescription(), shortDescTv);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, (ss.length() - 8), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary));
        ss.setSpan(fcs, (ss.length() - 8), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setText(textView, ss);
    }

    private void setText(TextView textView, SpannableString ss) {
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>"));
        }*/
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
    }

    private void setSpannableTextForSeeless(String name, TextView textView) {
        name += "\n\nSee less";
        SpannableString ss = new SpannableString(name);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                setSpannableTextForSeeMore(adBannerModel.getShortDescription(), shortDescTv);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, (ss.length() - 8), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary));
        ss.setSpan(fcs, (ss.length() - 8), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(textView, ss);
    }
}
