package com.info.adapter.viewHolders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.info.tradewyse.BecomeMentorActivity;
import com.info.tradewyse.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

/**
 * Created by Amit Gupta on 15,July,2020
 */
public class FaqAnswerViewHolder extends ChildViewHolder {
    private TextView childTextView;

    public FaqAnswerViewHolder(View itemView) {
        super(itemView);
        childTextView = (TextView) itemView.findViewById(R.id.list_item_artist_name);
    }

    public void setArtistName(String name, Context context) {
        if (name.contains(": Click Here")) {
            setSpannableTextForBecomeMentorLink(name, context);
        }else if(name.contains("sayhello@tradetipsapp.com")){
            setSpannableEmailLink(name, context);
        }else if(name.contains("Contact us here")){
            setSpannableTextForContactUsHere(name, context);
        } else {
            childTextView.setText(name);
        }
    }

    private void setSpannableTextForBecomeMentorLink(String name, Context context) {
        SpannableString ss = new SpannableString(name);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //context.startActivity(new Intent(context, BecomeMentorActivity.class));
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cutt.ly/pdBEtpU"));
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cutt.ly/3dBRvPc"));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tradetipsapp.com/#signup"));
                context.startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, (ss.length()-11), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary));
        ss.setSpan(fcs, (ss.length()-11), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        childTextView.setText(ss);
        childTextView.setMovementMethod(LinkMovementMethod.getInstance());
        childTextView.setHighlightColor(Color.TRANSPARENT);
    }
    private void setSpannableEmailLink(String name, Context context) {
        SpannableString ss = new SpannableString(name);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"+name.substring(name.length()-26,name.length()-1)));
                context.startActivity(Intent.createChooser(emailIntent, "Send feedback"));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, (ss.length()-26), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary));
        ss.setSpan(fcs, (ss.length()-26), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        childTextView.setText(ss);
        childTextView.setMovementMethod(LinkMovementMethod.getInstance());
        childTextView.setHighlightColor(Color.TRANSPARENT);
    }
    private void setSpannableTextForContactUsHere(String name, Context context) {
        SpannableString ss = new SpannableString(name);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://TradeTipsApp.com/contact"));
                context.startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, (ss.length()-16), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary));
        ss.setSpan(fcs, (ss.length()-16), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        childTextView.setText(ss);
        childTextView.setMovementMethod(LinkMovementMethod.getInstance());
        childTextView.setHighlightColor(Color.TRANSPARENT);
    }
}
