package com.info.tradewyse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class TermsOfUseActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    Button btnAccept,btnCancel;
    TextView txtTitle,txtSubtitle,txtDescriptionTerm;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_terms_condition);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
       context = TermsOfUseActivity.this;
        btnAccept = findViewById(R.id.btnAcceptTerm);
        btnCancel = findViewById(R.id.btnCancelTerm);
        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        context = TermsOfUseActivity.this;

        webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/terms_conditions.html");
        getSoftButtonsBarSizePort(this);

    }
    public static int getSoftButtonsBarSizePort(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }
    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.btnAcceptTerm){
            Intent intent=new Intent();
            intent.putExtra("MESSAGE",true);
            setResult(2,intent);
            finish();//finishing activity
        }else if(v.getId()== R.id.btnCancelTerm){

            showDialogOnCancelClick(context,context.getResources().getString(R.string.termsAndCondition),
                    context.getResources().getString(R.string.PleaseClickAccept),context.getResources().getString(R.string.Accept),
                    context.getResources().getString(R.string.Cancel));
        }
    }

    public  void showDialogOnCancelClick(Context context, String title, String message,String positveBtnText, String negativeBtnText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(positveBtnText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent=new Intent();
                intent.putExtra("MESSAGE",true);
                setResult(2,intent);
                finish();//finishing activity
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent();
                intent.putExtra("MESSAGE",false);
                setResult(2,intent);
                finish();//finishing activity
            }
        });

        AlertDialog dialog = builder.create();
        //2. now setup to change color of the button
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.text_color_red));
            }
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent();
        intent.putExtra("MESSAGE",false);
        setResult(2,intent);
    }
}
