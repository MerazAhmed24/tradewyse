package com.info.tradewyse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.BCrypt;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.model.SignUpResponse;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    TextView txtHeading, txtLogin;
    EditText edtEmail, edtUserName, edtPassword, edtcnfPassword;
    Button btnCreateAccount;
    Context context;
    String email, password, username;
    RelativeLayout layoutSignUpbg;
    public final Pattern usernameRegex = Pattern.compile("^([a-zA-Z0-9]{1,16})$");
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );
    public static final Pattern PASSWORD_VALIDATION = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=.])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        context = SignUpActivity.this;
        initialize();
        if (Constants.IS_PRODUCTION == false)
        {
            layoutSignUpbg.setBackground(ContextCompat.getDrawable(this, R.drawable.testloginbg));
        }

    }

    public void initialize() {
        txtHeading = (TextView) findViewById(R.id.txtHeading);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtcnfPassword = (EditText) findViewById(R.id.edtcnfPassword);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
        layoutSignUpbg = findViewById(R.id.layoutSignUpbg);
        btnCreateAccount.setOnClickListener(this);
        txtLogin.setOnClickListener(this);

    }


    public boolean isValidUsername(String target) {
        return usernameRegex.matcher(target).matches();
    }


    private boolean isValidEmail11(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return PASSWORD_VALIDATION.matcher(password).matches();
    }

    public boolean validateInputs() {
        String password = edtPassword.getText().toString().trim();
        String confirmpass = edtcnfPassword.getText().toString().trim();

        if (edtUserName.length() == 0) {
            Common.showMessage(context, getResources().getString(R.string.username_not_empty), getResources().getString(R.string.InputRequired));
            edtUserName.requestFocus();
            return false;
        } else if (!isValidUsername(edtUserName.getText().toString().trim())) {
            Common.showMessage(context, getResources().getString(R.string.ValidUsername), getResources().getString(R.string.JustLetYouKnow));
            edtUserName.requestFocus();
            return false;
        }
        if (edtEmail.length() == 0) {
            Common.showMessage(context, getResources().getString(R.string.email_not_empty), getResources().getString(R.string.InputRequired));
            edtEmail.requestFocus();
            return false;
        } else if (!Common.isValidEmail(edtEmail.getText().toString().trim())) {
            Common.showMessage(context, getResources().getString(R.string.EnterValidEmail), getResources().getString(R.string.messageAlert));
            edtEmail.requestFocus();
            return false;
        }
        if (edtPassword.getText().toString().trim().isEmpty()) {
            Common.showMessage(context, getResources().getString(R.string.password_not_empty), getResources().getString(R.string.messageAlert));
            edtEmail.requestFocus();
            return false;
        } else if (!isValidPassword(edtPassword.getText().toString().trim())) {
            Common.showMessage(context, getResources().getString(R.string.password_must_contain), getResources().getString(R.string.InputRequired));
            edtPassword.requestFocus();
            return false;
        }

        if (edtcnfPassword.getText().toString().trim().isEmpty()) {
            Common.showMessage(context, getResources().getString(R.string.cnf_passnot_empty), getResources().getString(R.string.messageAlert));
            edtcnfPassword.requestFocus();
            return false;
        } else if (!isValidPassword(edtcnfPassword.getText().toString().trim())) {
            Common.showMessage(context, getResources().getString(R.string.password_must_contain), getResources().getString(R.string.InputRequired));
            edtcnfPassword.requestFocus();
            return false;
        } else if (!password.equals(confirmpass)) {
            Common.showMessage(context, getResources().getString(R.string.Password_not_match), getResources().getString(R.string.required));
            edtcnfPassword.requestFocus();
            return false;
        }
        return true;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateAccount:
                if (validateInputs()) {
                    email = edtEmail.getText().toString().trim();
                    password = edtPassword.getText().toString().trim();
                    username = edtUserName.getText().toString().trim();
                    String generatedSecuredPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
                    Intent intent1 = new Intent(SignUpActivity.this, TermsOfUseActivity.class);
                    startActivityForResult(intent1, 2);
                }
                break;

            case R.id.txtLogin:
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivityForResult(intent, 2);
                SignUpActivity.this.finish();
                break;
        }
    }


    public void processSignupUser(String email, String username, String password) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        Call<SignUpResponse> call = apiInterface.signupUser(email, username, password);
        showProgressDialog();
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                dismissProgressDialog();
                if (response != null && !response.toString().isEmpty()) {

                    if (response != null && response.body() != null && response.isSuccessful()) {
                        if (response.body().getStatus()) {

                            TradWyseSession.getSharedInstance(SignUpActivity.this).setIsGoIntroScreen(true);
                            showMessageWithFinishActivity(context, getResources().getString(R.string.alertMsgSignupSuccess),
                                    getResources().getString(R.string.alertTitleSignupSuccess));
                        } else {
                            Common.showMessage(context, response.body().getMessage(), response.body().getTitle());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                //Toast.makeText(SignUpActivity.this,"Something went wrong!please try again",Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                        context.getResources().getString(R.string.JustLetYouKnow));
                Log.d(Constants.ON_FAILURE_TAG, "SignupActivity processSignupUser: onFailure");
            }
        });

    }

    public static void showMessageWithFinishActivity(Context context, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent login = new Intent(context, LoginActivity.class);
                context.startActivity(login);
                ((AppCompatActivity) context).finish();
                // ((AppCompatActivity)context).overridePendingTransition(R.anim.nothing,R.anim.nothing);

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            if (data != null) {
                String generatedSecuredPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
                boolean message = data.getBooleanExtra("MESSAGE", false);
                // Toast.makeText(SignUpActivity.this,"message:"+message,Toast.LENGTH_LONG).show();
                if (message == true) {
                    //call api
                    processSignupUser(email, username, generatedSecuredPasswordHash);
                } else {
                    //halt on signup activity.
                }
            }

        }
    }
}
