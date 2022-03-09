package com.info.tradewyse;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.info.ComplexPreference.ComplexPreferences;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.FirebaseManager;
import com.info.commons.TradWyseSession;
import com.info.logger.Logger;
import com.info.model.DeviceLimitExceedModel;
import com.info.model.FirestoreAuthentication;
import com.info.model.LoginCredentials;
import com.info.model.LoginResponse;
import com.info.model.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    Button btnRegister, btnForgetPassword, btnTour, btnLogin;
    Context mContext;
    TextView txtHeading;
    EditText eEmail, edPassword;
    Context context;
    boolean checkForPendingIndent;
    RelativeLayout layoutLogInbg;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static void CallLoginActivity(Context mContext, boolean checkForPendingIndent) {
        Intent mIntent = new Intent(mContext, LoginActivity.class);
        mIntent.putExtra("checkForPendingIndent", checkForPendingIndent);
        mContext.startActivity(mIntent);
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        layoutLogInbg = findViewById(R.id.layoutLogInbg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if (Constants.IS_PRODUCTION == false)
        {
            layoutLogInbg.setBackground(ContextCompat.getDrawable(this, R.drawable.testloginbg));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForPendingIndent = getIntent().getBooleanExtra("checkForPendingIndent", false);
        registerWithFireBaseToken();
        context = LoginActivity.this;
        initialize();
        mContext = this;
    }

    public void initialize() {
        eEmail = (EditText) findViewById(R.id.eEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);
        btnRegister = (Button) findViewById(R.id.btnCreateAcc);
        btnForgetPassword = (Button) findViewById(R.id.btnFgtPwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnTour = (Button) findViewById(R.id.btnTour);
        txtHeading = (TextView) findViewById(R.id.txtHeading);
        btnTour.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgetPassword.setOnClickListener(this);
        ((TextView) ((TextView) findViewById(R.id.txtHeading))).setTypeface(Common.getTypeface(context, 1));

    }


    public void registerWithFireBaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull @NotNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("DashBoardActivity", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                String newToken = task.getResult();
                Log.e("newToken", newToken);
                TradWyseSession.getSharedInstance(LoginActivity.this).setFcmToken(newToken);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (validateInputs()) {
                    String username = eEmail.getText().toString().trim();
                    String pwd = edPassword.getText().toString().trim();
                    LoginCredentials credentials = new LoginCredentials();
                    credentials.setUserName(username);
                    credentials.setPassword(pwd);
                    //The below line of code is used to store password locally
                    TradWyseSession.getSharedInstance(LoginActivity.this).setPassword(pwd);
                    credentials.setDeviceLoginId(Common.getDeviceId(LoginActivity.this));
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("appVersion", Common.getVersionName(LoginActivity.this));
                        jsonObject.put("deviceModel", Build.MODEL);
                        jsonObject.put("platform", Constants.PLATFORM);
                        jsonObject.put("deviceName", Build.MANUFACTURER);
                    } catch (Exception exception) {
                        Log.e("Exception", exception.getMessage());
                    }
                    Log.d("deviceInfo==", jsonObject.toString());
                    Log.d("deviceloginId==", credentials.getDeviceLoginId());

                    credentials.setDeviceInfo(jsonObject.toString());
                    processUserLogin(credentials);
                }
                break;
            case R.id.btnCreateAcc:
                Intent ca = new Intent(mContext, SignUpActivity.class);
                startActivity(ca);
                break;
            case R.id.btnFgtPwd:
                Intent fgt = new Intent(mContext, ForgetPasswordActivity.class);
                startActivity(fgt);
                break;
            case R.id.btnTour:
                Intent tour = new Intent(mContext, TourActivity.class);
                tour.putExtra("fromWhere", "BeforeLogin");
                startActivity(tour);
                break;
        }
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public boolean validateInputs() {
        if (eEmail.getText().toString().trim().isEmpty()) {
            // eEmail.setError(getResources().getString(R.string.UserNameCanNotEmpty));
            Common.showMessage(context, getResources().getString(R.string.UserNameCanNotEmpty), getResources().getString(R.string.messageAlert));
            eEmail.requestFocus();
            return false;
        }
        if (edPassword.getText().toString().trim().isEmpty()) {
            //edPassword.setError(getResources().getString(R.string.UserNameCanNotEmpty));
            Common.showMessage(context, getResources().getString(R.string.UserNameCanNotEmpty), getResources().getString(R.string.messageAlert));
            edPassword.requestFocus();
            return false;
        }
        return true;
    }

    public void processUserLogin(final LoginCredentials user) {
        Logger.error("FCMToken", TradWyseSession.getSharedInstance(LoginActivity.this).getFcmToken());
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, LoginActivity.this);
        compositeDisposable.add(new FirebaseManager().getFirebaseUserID(this).subscribe(result -> {
            Call<LoginResponse> call = apiInterface.loginUser(user.getUserName(), user.getPassword(),
                    result, user.getDeviceLoginId(), user.getDeviceInfo());
            showProgressDialog();
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                    dismissProgressDialog();
                    if (response.body() != null) {
                        if (response.body().getAccessToken() != null && response.body().getAccessToken() != "" &&
                                !response.body().getAccessToken().isEmpty()) {
                            LoginResponse loginResponse = response.body();

                            TradWyseSession.getSharedInstance(LoginActivity.this).setLoginInfoId(loginResponse.getLoginInfo().getId());
                            TradWyseSession.getSharedInstance(LoginActivity.this).setEmailId(loginResponse.getEmail());

                            getUserDetail(loginResponse.getUserName(), loginResponse.getAccessToken(), loginResponse.getAccessTokenStreamIOWithID());

                        } else if (!response.body().getStatus() && response.body().getDeviceLimitExceeded() != null && response.body().getDeviceLimitExceeded()) {
                            //Common.showMessage(context, response.body().getMessage(), response.body().getTitle());
                            DeviceLimitExceedModel model = new DeviceLimitExceedModel();
                            model.setTitle(response.body().getTitle());
                            model.setMessage(response.body().getMessage());
                            model.setDeviceDetails(response.body().getDeviceDetails());
                            startActivity(new Intent(LoginActivity.this, ActiveSessionsActivity.class)
                                    .putExtra("deviceLimitExceedModel", model));
                        } else {
                            // if body null.
                            dismissProgressDialog();
                            if (response.body() != null && response.body().getMessage() != null)
                                Common.showMessage(LoginActivity.this, response.body().getMessage(), response.body().getTitle());
                            else
                                Common.showMessage(LoginActivity.this, getResources().getString(R.string.invalidUsernamePassword), getResources().getString(R.string.retry));
                        }
                    } else {
                        // if body null.
                        dismissProgressDialog();
                        if (response.body() != null && response.body().getMessage() != null)
                            Common.showMessage(LoginActivity.this, response.body().getMessage(), getResources().getString(R.string.retry));
                        else
                            Common.showMessage(LoginActivity.this, getResources().getString(R.string.invalidUsernamePassword), getResources().getString(R.string.retry));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                    // Toast.makeText(LoginActivity.this,"Something went wrong!please try again",Toast.LENGTH_LONG).show();
                    dismissProgressDialog();
                    Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                            context.getResources().getString(R.string.JustLetYouKnow));
                    Log.d(Constants.ON_FAILURE_TAG, "LoginActivity processUserLogin: onFailure");
                }
            });
        }, error -> {
            Common.showMessage(this, error.getMessage(), getResources().getString(R.string.messageAlert));
        }));

    }


    public void getUserDetail(String userName, final String token, String accessTokenStreamIO) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, LoginActivity.this);
        Call<User> call = apiInterface.getAppUserByUserName(userName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                dismissProgressDialog();
                if (response.body() != null) {
                    if (!response.body().getId().isEmpty()) {
                        User userResponse = response.body();
                        TradWyseSession.getSharedInstance(LoginActivity.this).setUserIdString(userResponse.getId());
                        TradWyseSession.getSharedInstance(LoginActivity.this).setLoginAccessToken(token);
                        TradWyseSession.getSharedInstance(LoginActivity.this).setAccessTokenStreamIO(accessTokenStreamIO);
                        TradWyseSession.getSharedInstance(LoginActivity.this).setUserName(userResponse.getUserName());
                        TradWyseSession.getSharedInstance(LoginActivity.this).setUserImage(userResponse.getImage());
                        TradWyseSession.getSharedInstance(LoginActivity.this).setIsMentor(userResponse.getIsMentor());
                        TradWyseSession.getSharedInstance(LoginActivity.this).setFirstName(userResponse.getfName());
                        TradWyseSession.getSharedInstance(LoginActivity.this).setLastName(userResponse.getlName());
                        TradWyseSession.getSharedInstance(LoginActivity.this).setInternalSubscribedMember(userResponse.getInternalSubscribedUser());
                        TradWyseSession.getSharedInstance(LoginActivity.this).setIsLoggedIn(true);

                        Log.d("isMentor", TradWyseSession.getSharedInstance(LoginActivity.this).getIsMentor());

                        getFirestoreAuthToken();

                        if (checkForPendingIndent) {
                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LoginActivity.this, Constants.PENDING_DYNAMIC_LIST_PREF, MODE_PRIVATE);
                            PendingDynamicLinkData pendingDynamicLinkData = complexPreferences.getObject(Constants.PENDING_DYNAMIC_LIST_PREF_OBJ, PendingDynamicLinkData.class);
                            if (pendingDynamicLinkData == null) {
                                redirectToDashBoard();
                            } else {
                                DashBoardActivity.CallDashboardActivity(LoginActivity.this, true);
                                LoginActivity.this.finish();
                            }
                        } else {
                            redirectToDashBoard();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Something went wrong!please try again", Toast.LENGTH_LONG).show();
                        dismissProgressDialog();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong!please try again", Toast.LENGTH_LONG).show();
                    dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Something went wrong!please try again", Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                Log.d(Constants.ON_FAILURE_TAG, "LoginActivity getUserDetail: onFailure");
            }
        });

    }

    private void redirectToDashBoard() {
        boolean isGoIntroScreen = TradWyseSession.getSharedInstance(LoginActivity.this).isGoIntroScreen();
        if (isGoIntroScreen) {
            TradWyseSession.getSharedInstance(LoginActivity.this).setIsGoIntroScreen(false);
            startActivity(new Intent(LoginActivity.this, TourActivity.class)
                    .putExtra("fromWhere", "AfterLogin"));
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            Intent login = new Intent(mContext, DashBoardActivity.class);
            startActivity(login);

        }

        LoginActivity.this.finish();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
    public void getFirestoreAuthToken() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getApplicationContext());
        Call<FirestoreAuthentication> call = apiInterface.getFirestoreAuth();
        call.enqueue(new Callback<FirestoreAuthentication>() {
            @Override
            public void onResponse(Call<FirestoreAuthentication> call, Response<FirestoreAuthentication> response) {
                if (response != null && response.body() != null) {
                    FirestoreAuthentication firestoreAuthentication = response.body();
                    ComplexPreferences mComplexPreferences = ComplexPreferences.getComplexPreferences(getApplicationContext(), Constants.FIRESTORE_AUTH_PREF, MODE_PRIVATE);
                    mComplexPreferences.putObject(Constants.FIRESTORE_AUTH_PREF_OBJ, firestoreAuthentication);
                    mComplexPreferences.commit();
                   //signInWithCustomToken(firestoreAuthentication.getAuthToken());
                } else {
                    Log.d(Constants.ON_FAILURE_TAG, "Dashboard getFirestoreAuthToken: No response ");
                }
            }

            @Override
            public void onFailure(Call<FirestoreAuthentication> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getFirestoreAuthToken: onFailure ", t.fillInStackTrace());
            }
        });
    }
}
