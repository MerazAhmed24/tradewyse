package com.info.tradewyse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.info.commons.Common;

public class NewWebViewActivity extends AppCompatActivity {

    Context context;
    WebView webView;
    ProgressBar newsloaderprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_web_view);

        webView = (WebView) findViewById(R.id.webview);
        context = this;
        setToolBar(getResources().getString(R.string.newsDetails));
        newsloaderprogress = findViewById(R.id.newsprogressbar);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        webView.setWebViewClient(webViewClient);

        String url = getIntent().getStringExtra("SectorNewsURL");
        webView.loadUrl(url);

    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
    private void setAppBarHeight() {
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight() + dpToPx( 56)));
    }
    public void setToolBar(String title){
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbar_title=findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);
        toolbar_title.setTypeface(Common.getTypeface(getApplicationContext(),1));

        AppBarLayout.LayoutParams toolBarLayoutParams=(AppBarLayout.LayoutParams)toolbar.getLayoutParams();
       // toolBarLayoutParams.topMargin=getStatusBarHeight();
        toolbar.setLayoutParams(toolBarLayoutParams);

        setSupportActionBar(toolbar);
        setAppBarHeight();
        getSupportActionBar().setElevation(0f);
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
            newsloaderprogress.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onBackPress(View v){
        finish();
    }
}
