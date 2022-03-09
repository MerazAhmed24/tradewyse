
package com.info.tradewyse;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.info.ComplexPreference.ComplexPreferences;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Analytics;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.eventBus.MessageEvent;
import com.info.model.BasilPrivateChatModel;
import com.info.model.FirestoreAuthentication;
import com.info.model.PublicChatModel;
import com.info.model.SocialChatModel;
import com.sendbird.android.SendBird;
import com.stripe.android.PaymentConfiguration;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TradwyseApplication extends Application {
    private static final String TAG = "TradwyseApplication";
    private static TradwyseApplication appInstance;
    private static FirebaseFirestore db;
    private static FirebaseStorage firebaseStorage;
    public static PublicChatModel publicChatModel = null;
    public static SocialChatModel socialChatModel = null;
    public static BasilPrivateChatModel basilPrivateChatModel = null;
    public static List<String> browserPackageName;
    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        Object context;
        SendBird.init(Constants.SENDBIRD_APP_ID, this);

        Fresco.initialize(this);
        PaymentConfiguration.init(
                this,
                Constants.STRIPE_TOKEN
        );
        getFirestoreAuthToken();
        getListOfBrowser(getApplicationContext());
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleListener());
    }


    public static FirebaseFirestore getFirestoreDb() {
        return db != null ? db : (db = FirebaseFirestore.getInstance());
    }

    public static FirebaseStorage getFirebaseStorageObj() {
        return firebaseStorage != null ? firebaseStorage : (firebaseStorage = FirebaseStorage.getInstance("gs://tradetips-9baa3.appspot.com"));
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
                    signInWithCustomToken(firestoreAuthentication.getAuthToken());
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

    private void signInWithCustomToken(String FCMID) {
        FirebaseAuth.getInstance().signInWithCustomToken(FCMID).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkIfBasilPrivateChatGroupIsAvailable();
                    checkIfPublicSocialChatGroupIsAvailable();
                    checkIfPublicChatGroupIsAvailable();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to signInWithCustomToken", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkIfPublicChatGroupIsAvailable() {
        TradwyseApplication.getFirestoreDb().collection(Constants.OPEN_GROUP)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Strategy Chat
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_GROUP_ID)) {
                            publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            EventBus.getDefault().post(true);
                            break;
                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_GROUP_ID)) {
                            publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            EventBus.getDefault().post(true);
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void checkIfBasilPrivateChatGroupIsAvailable() {
        TradwyseApplication.getFirestoreDb().collection(Constants.BASIL_PRIVATE_GROUP)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Basil Private Chat
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_BASIL_PRIVATE_GROUP_ID)) {
                            basilPrivateChatModel = new BasilPrivateChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION), document.getString(Constants.GROUP_CODE));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            EventBus.getDefault().post(true);
                            break;
                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_BASIL_PRIVATE_GROUP_ID)) {
                            basilPrivateChatModel = new BasilPrivateChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION), document.getString(Constants.GROUP_CODE));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            EventBus.getDefault().post(true);
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void checkIfPublicSocialChatGroupIsAvailable() {
        TradwyseApplication.getFirestoreDb().collection(Constants.SOCIAL_CHAT)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Social Chat
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_SOCIAL_GROUP_ID)) {
                            socialChatModel = new SocialChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            EventBus.getDefault().post(true);
                            break;
                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_SOCIAL_GROUP_ID)) {
                            socialChatModel = new SocialChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            EventBus.getDefault().post(true);
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }


    public void storeStockNameWhosePredectionIsReady(String stockName) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, Constants.PREDECTION_READY_STOCK_NAME_PREF, MODE_PRIVATE);
        complexPreferences.putObject(Constants.PREDECTION_READY_STOCK_NAME_PREF_OBJ, stockName);
        complexPreferences.commit();
    }

    public static List<String> getListOfBrowser(Context context) {
        browserPackageName = new ArrayList<String>();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://www.google.com"));
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> browserList = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL);
            for (ResolveInfo info : browserList) {
                browserPackageName.add(info.activityInfo.packageName);
            }
            //list_browsers = browserPackageName.toString();
            Log.e("list", browserPackageName.toString());
            Log.e("size", browserList.size() + "");
            if (browserList.size() == 0)
                Log.e("browser installed", "No");
            else
                Log.e("browser installed", "Yes " + "Total Browsers = " + browserList.size());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("BrowserList Info ", e.getMessage());
        }
        return browserPackageName;
    }

    class AppLifecycleListener implements DefaultLifecycleObserver {

        public void onStart(LifecycleOwner owner) { // app moved to foreground
            TradWyseSession tradWyseSession = TradWyseSession.getSharedInstance(getApplicationContext());
            String token = tradWyseSession.getLoginAccessToken();

            if (token != null && !token.equalsIgnoreCase("")) {
                Analytics.AnalyticsDataSetOpenApp(getApplicationContext());
            }
        }

        public void onStop(LifecycleOwner owner) { // app moved to background

        }
    }
}