package com.info.tradewyse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.info.commons.TradWyseSession;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    ProgressDialog progressDialog;
    public TradWyseSession tradWyseSession;
    private setPermissionListener permissionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tradWyseSession = TradWyseSession.getSharedInstance(this);
    }


    public void showProgressDialog() {
        if
        (!isFinishing()) {
            try {
                progressDialog = new ProgressDialog(BaseActivity.this, R.style.style_progress_dialog);
                progressDialog.setMessage("Loading....");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }

    public void showProgressDialog(String message) {
        if (!isFinishing()) {
            try {
                progressDialog = new ProgressDialog(BaseActivity.this, R.style.style_progress_dialog);
                progressDialog.setMessage(message);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }

    public void updateProgressDialogMessage(String message) {
        if (!isFinishing()) {
            try {
                if(progressDialog.isShowing())
                progressDialog.setMessage(message);
            } catch (Exception e) {
            }
        }
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void dismissProgressDialog() {
        try {
            if (!isFinishing() && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            else
            {
                Log.e("Error=====","dialog not dismiss");
            }
        } catch (Exception e) {
            Log.e("Error=====","=="+e.getMessage());
        }

    }

    public void setToolBar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);
        //toolbar_title.setTypeface(Common.getTypeface(getApplicationContext(),1));

        AppBarLayout.LayoutParams toolBarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        // toolBarLayoutParams.topMargin=getStatusBarHeight();
        toolbar.setLayoutParams(toolBarLayoutParams);

        setSupportActionBar(toolbar);
        setAppBarHeight();
        getSupportActionBar().setElevation(0f);
    }

    public void setToolBar1(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);
        //toolbar_title.setTypeface(Common.getTypeface(getApplicationContext(),1));

        AppBarLayout.LayoutParams toolBarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        //toolBarLayoutParams.topMargin=getStatusBarHeight();
        toolbar.setLayoutParams(toolBarLayoutParams);
        setSupportActionBar(toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight() + dpToPx(56)));
        getSupportActionBar().setElevation(0f);
    }

    public void setToolBarAddTip(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);
        //toolbar_title.setTypeface(Common.getTypeface(getApplicationContext(),1));
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0f);
    }


    private void setAppBarHeight() {
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight() + dpToPx(56)));
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

    public void onBackPress(View v) {
        finish();
        hideKeyboard();
    }

    public void requestAppPermissions(final String[] requestedPermissions,
                                      final int requestCode, setPermissionListener listener) {
        this.permissionListener = listener;
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String permission : requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, requestedPermissions, requestCode);
        } else {
            if (permissionListener != null) permissionListener.onPermissionGranted(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                if (permissionListener != null) permissionListener.onPermissionGranted(requestCode);
                break;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                if (permissionListener != null) permissionListener.onPermissionDenied(requestCode);
                break;
            } else {
                if (permissionListener != null)
                    permissionListener.onPermissionNeverAsk(requestCode);
                break;
            }
        }
    }


    public interface setPermissionListener {
        public void onPermissionGranted(int requestCode);

        public void onPermissionDenied(int requestCode);

        public void onPermissionNeverAsk(int requestCode);
    }

}
