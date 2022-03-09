package com.info.tradewyse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

public class BecomeMentorActivity extends BaseActivity {
    WebView mWebView;
    Context context;
    ProgressBar progressBarMentor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_mentor);
        setToolBarTitle("Become A Mentor");
        mWebView = findViewById(R.id.webView);
        context = this;
        progressBarMentor = findViewById(R.id.progressbarMentor);
        WebSettings webSettings = mWebView.getSettings();
        String url ="https://tradetipsapp.com/#signup";
        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        mWebView.setWebViewClient(webViewClient);
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);


        (findViewById(R.id.btnSubmit)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String firstName = ((EditText) findViewById(R.id.edtFirstName)).getText().toString().trim();
                String lastName = ((EditText) findViewById(R.id.edtLastName)).getText().toString().trim();
                String reason = ((EditText) findViewById(R.id.edtReason)).getText().toString().trim();

                if (firstName == null || firstName.isEmpty()) {
                    ((EditText) findViewById(R.id.edtFirstName)).setError("Enter first name");
                } else if (lastName == null || lastName.isEmpty()) {
                    ((EditText) findViewById(R.id.edtFirstName)).setError("Enter last name");
                } else if (reason == null || reason.isEmpty()) {
                    ((EditText) findViewById(R.id.edtFirstName)).setError("Enter reason");
                } else {
                    String to = "marketing@tradetipsapp.com";
                    String subject = "Want to become a Mentor";
                    String body = "Hey my name is " + firstName + " " + lastName + "\n\nI am very much interested to become a Mentor because " + reason;
                    String mailTo = "mailto:" + to +
                            "?&subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(body);
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                    emailIntent.setData(Uri.parse(mailTo));
                    startActivity(emailIntent);
                }
            }
        });


    }
    public class WebViewClientImpl extends WebViewClient
    {

        private Activity activity = null;

        public WebViewClientImpl(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return super.shouldOverrideUrlLoading(view, url);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBarMentor.setVisibility(View.GONE);
        }

    }

    public void setToolBarTitle(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);
        //toolbar_title.setTypeface(Common.getTypeface(getApplicationContext(),1));
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0f);
    }
}
