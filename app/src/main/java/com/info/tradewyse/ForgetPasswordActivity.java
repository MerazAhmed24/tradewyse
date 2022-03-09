package com.info.tradewyse;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.model.FlagResponse;
import com.info.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends BaseActivity {

    TextView txtHeading;
    EditText eEmail;
    Button btnForgetPassword;
    Context context;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        context = ForgetPasswordActivity.this;
        txtHeading = (TextView) findViewById(R.id.txtHeading);
        eEmail = (EditText) findViewById(R.id.eEmail);
        btnForgetPassword = (Button) findViewById(R.id.btnForgetPassword);
        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = eEmail.getText().toString().trim();
                if (username.isEmpty() || !Common.isValidEmail(username)) {
                    //edtUserName.setError(getResources().getString(R.string.EnterUsername));
                    Common.showMessage(context, getResources().getString(R.string.EnterValidEmail), getResources().getString(R.string.required));
                    eEmail.requestFocus();

                } else {
                    submitForgetPasswordRequest(username, true);
                }
            }
        });
    }


    public void submitForgetPasswordRequest(String username, boolean fromEMail) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.style_progress_dialog);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Call<FlagResponse> call = apiInterface.forgotPassword(username);
        call.enqueue(new Callback<FlagResponse>() {
            @Override
            public void onResponse(Call<FlagResponse> call, Response<FlagResponse> response) {
                progressDialog.dismiss();
                if (response.body() != null) {

                    if (response != null && response.body() != null && response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("true")) {
                            Common.showMessage(context, response.body().getMessage(), response.body().getTitle());
                        } else {
                            Common.showMessage(context, response.body().getMessage(), response.body().getTitle());
                        }
                    }
                } else {
                    Toast.makeText(context, "Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FlagResponse> call, Throwable t) {
                progressDialog.dismiss();
                Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                        context.getResources().getString(R.string.JustLetYouKnow));

            }
        });
    }

}
