package com.info.adapter;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

import android.annotation.SuppressLint;
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
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.info.api.APIClient;
import com.info.model.AdBannerModel;
import com.info.tradewyse.R;
import com.squareup.picasso.Picasso;

public class NewAdBannerAdapter extends PagerAdapter {
    //private ArrayList<Circular> circularArrayList;
    private LayoutInflater inflater;
    private Context context;
    private AdBannerModel adBannerModel;
    private AdBannerListener adBannerListener;
    private TextView seeMoreTv;
    private ImageView imageViewCenter;
    private TextView textViewTitle, textViewShortDes;
    private TextView textViewDes;

    public NewAdBannerAdapter(Context context, AdBannerModel adBannerM) {
        this.context = context;
        adBannerModel = adBannerM;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (adBannerModel.getTitle3() != null && !adBannerModel.getTitle3().equalsIgnoreCase("")) {
            return 3;
        } else if (adBannerModel.getTitle2() != null && !adBannerModel.getTitle2().equalsIgnoreCase("")) {
            return 2;
        } else if (adBannerModel.getTitle1() != null && !adBannerModel.getTitle1().equalsIgnoreCase("")) {
            return 1;
        }
        return 1;
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(R.layout.row_new_ad_banner, view, false);

        imageViewCenter = (ImageView) myImageLayout.findViewById(R.id.imageViewCenter);
        textViewTitle = myImageLayout.findViewById(R.id.textViewTitle);
        textViewShortDes = myImageLayout.findViewById(R.id.textViewShortDes);
        textViewDes = myImageLayout.findViewById(R.id.textViewDes);
        seeMoreTv = myImageLayout.findViewById(R.id.seemore);

        /*seeMoreTv.setOnClickListener(v -> {
            seeMoreClicked(position);
        });*/


        if (position == 0) {
            textViewTitle.setText(adBannerModel.getTitle1());
            /*textViewShortDes.setText(adBannerModel.getShortDescription1());
            textViewDes.setText(adBannerModel.getLongDescription1());*/
            setText(textViewShortDes, adBannerModel.getShortDescription1());
            setText(textViewDes, adBannerModel.getLongDescription1());
            String imageUrl = APIClient.BASE_SERVER_URL_IMAGE + adBannerModel.getImage1();
            Picasso.get().load(imageUrl).placeholder(context.getResources().getDrawable(R.drawable.placeholder))
                    .into(imageViewCenter);
        }

        if (position == 1) {
            textViewTitle.setText(adBannerModel.getTitle2());
            setText(textViewShortDes, adBannerModel.getShortDescription2());
            setText(textViewDes, adBannerModel.getLongDescription2());
            String imageUrl = APIClient.BASE_SERVER_URL_IMAGE + adBannerModel.getImage2();
            Picasso.get().load(imageUrl).into(imageViewCenter);
        }

        if (position == 2) {
            textViewTitle.setText(adBannerModel.getTitle3());
            setText(textViewShortDes, adBannerModel.getShortDescription3());
            setText(textViewDes, adBannerModel.getLongDescription3());
            String imageUrl = APIClient.BASE_SERVER_URL_IMAGE + adBannerModel.getImage3();
            Picasso.get().load(imageUrl).into(imageViewCenter);
        }

        view.addView(myImageLayout, 0);

        return myImageLayout;
    }

    private void seeMoreClicked(int position) {
        if (position == 0) {

            if (seeMoreTv.getText().toString().equalsIgnoreCase(context.getString(R.string.see_more))) {
                seeMoreTv.setText("See less");
                setText(textViewDes, adBannerModel.getLongDescription1());
                textViewDes.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
            } else {
                textViewDes.setGravity(Gravity.CENTER);
                seeMoreTv.setText(context.getString(R.string.see_more));
                setText(textViewDes, adBannerModel.getShortDescription1());
            }

        } else if (position == 1) {

            if (seeMoreTv.getText().toString().equalsIgnoreCase(context.getString(R.string.see_more))) {
                seeMoreTv.setText("See less");
                setText(textViewDes, adBannerModel.getLongDescription2());
                textViewDes.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
            } else {
                textViewDes.setGravity(Gravity.CENTER);
                seeMoreTv.setText(context.getString(R.string.see_more));
                setText(textViewDes, adBannerModel.getShortDescription2());
            }

        } else if (position == 2) {

            if (seeMoreTv.getText().toString().equalsIgnoreCase(context.getString(R.string.see_more))) {
                seeMoreTv.setText("See less");
                setText(textViewDes, adBannerModel.getLongDescription3());
                textViewDes.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
            } else {
                textViewDes.setGravity(Gravity.CENTER);
                seeMoreTv.setText(context.getString(R.string.see_more));
                setText(textViewDes, adBannerModel.getShortDescription3());
            }
        }
    }

    private void setSpannableTextForSeeMore(String name, TextView textView) {
        name += "\n\n" + context.getString(R.string.see_more);
        SpannableString ss = new SpannableString(name);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                setSpannableTextForSeeless(adBannerModel.getLongDescription(), textViewDes);
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

    private void setText(TextView textView, SpannableString ss) {
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
                setSpannableTextForSeeMore(adBannerModel.getShortDescription(), textViewDes);
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


    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view.equals(object);
    }

    public interface AdBannerListener {
        void onCloseClick(int position);
    }

    public void setOnAdBannerListener(AdBannerListener adBannerListener) {
        this.adBannerListener = adBannerListener;
    }
}
