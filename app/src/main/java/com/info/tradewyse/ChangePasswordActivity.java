package com.info.tradewyse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.model.ChangePassModel;
import com.info.model.FooterModel;
import com.info.model.NotificationCountModel;
import com.info.tradewyse.databinding.ActivityChangePasswordBinding;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.info.adapter.TipsAdapter.context;
import static com.info.tradewyse.SignUpActivity.PASSWORD_VALIDATION;

public class ChangePasswordActivity extends BaseActivity {
    private ImageView backaction;
    private Button submitrequest;
    private TextView tvusername;
    LinearLayout bottomlinearLayout;
    private EditText etOldPass, etNewPass, etCnfNewPass;
    TradWyseSession tradWyseSession;
    String username = "";
    String password = "";
    ActivityChangePasswordBinding changePasswordBinding;

    private boolean isValidPassword(String password) {
        return PASSWORD_VALIDATION.matcher(password).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        changePasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        FooterModel footerModel = new FooterModel(false, false, false, false, true);
        changePasswordBinding.setFooterModel(footerModel);

        getNotificationCount();
        tradWyseSession = TradWyseSession.getSharedInstance(this);
        Initialize();
        clickactionback();
        usernamepasssharedpref();
        submitaction();
        Common.BottomTabColorChange(this,bottomlinearLayout);

    }

    public void usernamepasssharedpref() {
        username = tradWyseSession.getUserName();
        tvusername.setText(username);
        password = tradWyseSession.getPassword();
    }

    public void Initialize() {
        backaction = findViewById(R.id.backAction);
        etNewPass = findViewById(R.id.etnewpass);
        etCnfNewPass = findViewById(R.id.etnewconfpass);
        submitrequest = findViewById(R.id.btchangepass);
        tvusername = findViewById(R.id.tvusernamechangepass);
        etOldPass = findViewById(R.id.etCurrentpass);
        bottomlinearLayout = findViewById(R.id.bottomView);

    }

    public void clickactionback() {
        backaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });
    }

    public void submitaction() {
        submitrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateinput()) {
                    String username = tvusername.getText().toString();
                    String oldpassword = etOldPass.getText().toString();
                    String newpassword = etNewPass.getText().toString();
                    changepass(username, oldpassword, newpassword);
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


                }
            }
        });

    }

    public boolean validateinput() {
        String oldpass = etOldPass.getText().toString().trim();
        String newpass = etNewPass.getText().toString().trim();
        String confirmpass = etCnfNewPass.getText().toString().trim();
        if (oldpass.isEmpty()) {
            Common.showMessage(ChangePasswordActivity.this, getResources().getString(R.string.oldpassempty), getResources().getString(R.string.InputRequired));
            return false;
        } else if (!oldpass.equals(password)) {
            Common.showMessage(ChangePasswordActivity.this, getResources().getString(R.string.incorrect_pass), getResources().getString(R.string.messageAlert));
            return false;
        } else if (newpass.isEmpty()) {
            Common.showMessage(ChangePasswordActivity.this, getResources().getString(R.string.newpassempty), getResources().getString(R.string.InputRequired));
            return false;
        } else if (!isValidPassword(newpass)) {
            Common.showMessage(ChangePasswordActivity.this, getResources().getString(R.string.password_must_contain), getResources().getString(R.string.required));
            etNewPass.requestFocus();
            return false;
        } else if (confirmpass.isEmpty()) {
            Common.showMessage(ChangePasswordActivity.this, getResources().getString(R.string.cnfpassempty), getResources().getString(R.string.required));
            return false;
        } else if (!newpass.equals(confirmpass)) {
            Common.showMessage(ChangePasswordActivity.this, getResources().getString(R.string.new_cng_pwd_same), getResources().getString(R.string.messageAlert));
            return false;
        } else {
            return true;
        }

    }

    public void changepass(String username, String oldpass, String newpass) {
        showProgressDialog();
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        Call<ChangePassModel> call = apiInterface.changepassword(username, oldpass, newpass);
        call.enqueue(new Callback<ChangePassModel>() {
            @Override
            public void onResponse(Call<ChangePassModel> call, Response<ChangePassModel> response) {
                dismissProgressDialog();
                if (response.isSuccessful() && response != null && response.body() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.MyAlertDialogStyle);
                    builder.setMessage(getResources().getString(R.string.password_updated));
                    builder.setTitle(getResources().getString(R.string.Success_title));
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TradWyseSession.getSharedInstance(ChangePasswordActivity.this).setPassword(newpass);
                            etOldPass.setText("");
                            etNewPass.setText("");
                            etCnfNewPass.setText("");
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<ChangePassModel> call, Throwable t) {
                dismissProgressDialog();
                Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                        context.getResources().getString(R.string.JustLetYouKnow));

            }
        });

    }

    public void homeTabClicked(View v) {
        DashBoardActivity.CallDashboardActivity(this, false);
        finish();
    }

    public void chatTabClicked(View v) {
        ChatActivity.starChatActivity(this,false);
        //TabbedChatActivity.CallTabbedChatActivity(this, true);
        finish();
    }

    public void servicesTabClicked(View v) {
        ServicesActivity.CallServicesActivity(this);
        finish();
    }

    public void notificationTabClicked(View v) {
        NotificationActivity.starNotificationActivity(this);
        finish();
    }

    public void moreTabClicked(View v) {
        MoreTabActivity.startMoreTabActivity(this);
        finish();
    }

    private void getNotificationCount() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<NotificationCountModel> call = apiInterface.getCountOfAllUnreadNotificationActivityDetails();
        call.enqueue(new Callback<NotificationCountModel>() {
            @Override
            public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                if (response != null && response.body() != null) {
                    NotificationCountModel notificationCountModel = response.body();
                    if (notificationCountModel.getUnReadCount() > 0) {
                        (findViewById(R.id.nav_unread_badge)).setVisibility(View.VISIBLE);
                    } else {
                        (findViewById(R.id.nav_unread_badge)).setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationCountModel> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getUserProfile: onFailure", t.fillInStackTrace());
            }
        });
    }
}
