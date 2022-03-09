package com.info.tradewyse;

import static com.info.commons.Constants.DocMessageType;
import static com.info.commons.Constants.ImageMessageType;
import static com.info.commons.Constants.MessageProfileImageUrl;
import static com.info.commons.Constants.VideoMessageType;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.info.ComplexPreference.ComplexPreferences;
import com.info.CustomToast.Toasty;
import com.info.adapter.ChatRoomsFilterAdapter;
import com.info.adapter.SocialChatStockAdapter;
import com.info.adapter.SocialMessageAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.AutoScrollRecyclerView;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.EndlessRecyclerViewScrollListener;
import com.info.commons.FileUtils;
import com.info.commons.OnSwipeTouchListener;
import com.info.commons.ProfileImageAbstractActivity;
import com.info.commons.StringHelper;
import com.info.commons.TradWyseSession;
import com.info.dialog.DialogThankyou;
import com.info.dialog.ImageCaptionDialog;
import com.info.dialog.PrivateGroupCodeInputDialog;
import com.info.dialog.SocialMsgConfirmationDialog;
import com.info.dialog.UnlockChatRoomDialog;
import com.info.fragment.ChatsFragment;
import com.info.logger.Logger;
import com.info.model.Attachment;
import com.info.model.BasilPrivateChatModel;
import com.info.model.ChatModel;
import com.info.model.ChatRooms;
import com.info.model.FirestoreAuthentication;
import com.info.model.FooterModel;
import com.info.model.NotificationCountModel;
import com.info.model.PublicChatModel;
import com.info.model.SocialChatLike;
import com.info.model.SocialChatModel;
import com.info.model.SocialChatStocksDetails;
import com.info.model.SocialMessageDetails;
import com.info.model.SocialComment;
import com.info.sendbird.utils.DateUtils;
import com.info.tradewyse.databinding.ActivityTabbedChatBinding;
import com.sendbird.android.FileMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabbedChatActivity extends ProfileImageAbstractActivity {
    ChatsFragment chatsFragment;

    ActivityTabbedChatBinding activityTabbedChatBinding;
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    public boolean fromLoggedInProfile = false;
    ArrayList<ChatRooms> chatRoomsArrayList = new ArrayList<>();
    String groupId, groupTitle, groupDesc, groupImage;
    String basilPrivateGroupId, basilPrivateGroupTitle, basilPrivateGroupDesc, basilPrivateGroupImage, basilPrivateGroupCode;
    String socialGroupId;
    String prevSystemDate = "";
    RelativeLayout layoutSocialChatStock;
    private static final int PAGE_LIMIT = 10;
    public static final int START_FROM = 0;
    ProgressBar  progressBarMessage, progressForChatRooms;
    EndlessRecyclerViewScrollListener scrollListenerLoadMessage;
    TextView tvNodataMessage;
    ArrayList<SocialChatStocksDetails> socialChatStocksDetailsArrayList;
    AutoScrollRecyclerView recycleTopSocialChat;
    RecyclerView recyclerViewLiveStockChat;
    RelativeLayout layoutStock, layoutRefresh;
    SocialChatStockAdapter adapter;
    SocialMessageAdapter socialMessageAdapter;
    LinearLayoutManager stockLayoutManager, messageLayoutManager;
    private EditText editTextMsg;
    private int clickCount = 0;
    TextView refreshStockView;
    private TextView textViewSend, tvLoadMoreMessage;
    private TradWyseSession tradWyseSession;
    private RelativeLayout rootLayout, layoutStockView;
    private ImageView imageViewScrollDown, viewOpenStock, viewHideStock, imgRefereshicon;
    DocumentSnapshot lastVisibleValue;
    boolean moreDataAvailable = false;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<SocialMessageDetails> mMessageList = new ArrayList<>();
    UploadTask firebaseStorageUploadTask;
    boolean isWaitingForResponse = false;
    private PrivateGroupCodeInputDialog privateGroupCodeInputDialog = null;

    private static final int INTENT_REQUEST_CHOOSE_IMAGE = 300;
    private static final int INTENT_REQUEST_CHOOSE_VIDEO = 400;
    private static final int INTENT_REQUEST_CHOOSE_DOC = 500;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    public static final int PERMISSION_STORAGE = 100;
    private static final String IMAGE = "IMAGE";
    private static final String VIDEO = "VIDEO";
    private static final String DOC = "DOC";
    int swipecount = 1;
    public static boolean isUserExistInBasinPrivateChatRoom = false;
    private long mLastClickTime = 0;

    public static void CallTabbedChatActivity(Context mContext, boolean fromProfile) {
        Intent mIntent = new Intent(mContext, TabbedChatActivity.class);
        mIntent.putExtra("fromProfile", fromProfile);
        mContext.startActivity(mIntent);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTabbedChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_tabbed_chat);
        FooterModel footerModel = new FooterModel(false, true, false, false, false);
        activityTabbedChatBinding.setFooterModel(footerModel);
        fromLoggedInProfile = getIntent().getBooleanExtra("fromProfile", false);
        tradWyseSession = TradWyseSession.getSharedInstance(TabbedChatActivity.this);
        TradwyseApplication.getFirestoreDb().getFirestoreSettings().isPersistenceEnabled();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        findViewById(R.id.backAction).setOnClickListener(v -> {
            super.onBackPressed();
        });

        findViewById(R.id.layoutRoomsMenu).setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            showFiltersDialog();
        });

        recycleTopSocialChat = findViewById(R.id.recycleTopSocialChat);
        recyclerViewLiveStockChat = (RecyclerView) findViewById(R.id.recyclerViewLiveStockChat);
        layoutSocialChatStock = findViewById(R.id.layoutSocialChatStock);
        editTextMsg = findViewById(R.id.editTextMsg);
        textViewSend = findViewById(R.id.textViewSend);
        refreshStockView = findViewById(R.id.refreshStock);
        layoutStock = findViewById(R.id.layoutMain);
        layoutRefresh = findViewById(R.id.layoutRefresh);
        rootLayout = findViewById(R.id.rootLayout);
        tvLoadMoreMessage = findViewById(R.id.loading_tv);
        tvNodataMessage = findViewById(R.id.tvNodataMessage);
        progressBarMessage = findViewById(R.id.progressBarMessage);
        progressForChatRooms = findViewById(R.id.progressForChatRooms);
        imageViewScrollDown = findViewById(R.id.imageViewScrollDown);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        layoutStockView = findViewById(R.id.layoutRecycleview);
        viewOpenStock = findViewById(R.id.openViewIcon);
        viewHideStock = findViewById(R.id.hideViewIcon);
        imgRefereshicon = findViewById(R.id.imgRefereshicon);
        progressBarMessage.setVisibility(View.VISIBLE);
        //method to get id for all chat room
        checkIfPublicChatGroupIsAvailable();

        //use to rotate refresh button for stock
        Animation animation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(5000);

        messageLayoutManager = new LinearLayoutManager(TabbedChatActivity.this);
        setUpRecyclerView();
        chevronclick();
        getAllSocialChatStockDetailsList();
       // use to Reload to refresh social chat data
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBarMessage.setVisibility(View.GONE);
                mMessageList.clear();
                checkIfPublicChatGroupIsAvailable();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        // code to click refresh stock button
        layoutRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgRefereshicon.setAnimation(animation);
                imgRefereshicon.startAnimation(animation);
                if (!Common.isNetworkAvailable(TabbedChatActivity.this)) {
                    layoutRefresh.setVisibility(View.VISIBLE);
                    imgRefereshicon.clearAnimation();
                    Common.showOfflineMemeDialog(TabbedChatActivity.this, getResources().getString(R.string.memeMsg),
                            getResources().getString(R.string.JustLetYouKnow));
                }

                else {
                    clickCount++;
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            getAllSocialChatStockDetailsList();
                        }
                    }, 3000);
                }

            }
        });


        findViewById(R.id.attachmentIv).setOnClickListener(v -> {
            showFirstFiltersDialog();
        });

        imageViewScrollDown.setOnClickListener(v -> {
            recyclerViewLiveStockChat.scrollToPosition(0);
            imageViewScrollDown.setVisibility(View.GONE);
        });

        textViewSend.setOnClickListener(v -> {
            if (editTextMsg.getText().toString().trim().length() > 0) {
                progressBarMessage.setVisibility(View.VISIBLE);
                // Send data to firestore.
                if (!isWaitingForResponse) {
                    String message = editTextMsg.getText().toString().trim();
                    if (message != null && !message.isEmpty() && groupId != null) {
                        sendMessageToFireStore(message, Constants.TextMessageType, "");
                        isWaitingForResponse = true;
                        progressBarMessage.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Please Try Again Later.", Toast.LENGTH_SHORT).show();
                        editTextMsg.setText("");

                    }

                }
                editTextMsg.setText("");
                progressBarMessage.setVisibility(View.GONE);
            }

        });

        editTextMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && groupId != null)
                    textViewSend.setTextColor(getResources().getColor(R.color.blue_color));
                else
                    textViewSend.setTextColor(getResources().getColor(R.color.tw__medium_gray));
            }
        });

    }


    private void showPopupGroupCode() {
        privateGroupCodeInputDialog = new PrivateGroupCodeInputDialog(this) {
            @Override
            public void closeDialog() {

            }

            @Override
            public void startRoom(String roomCode) {
                if (basilPrivateGroupCode.equalsIgnoreCase(roomCode)) {
                    if ((basilPrivateGroupId != null && !basilPrivateGroupId.isEmpty()) && (basilPrivateGroupTitle != null && !basilPrivateGroupTitle.isEmpty())) {
                        sendUserInfoToBasilPrivateChatRoom();
                        ChatActivity.starChatActivity(TabbedChatActivity.this, basilPrivateGroupId, basilPrivateGroupTitle, basilPrivateGroupDesc, basilPrivateGroupImage,true);
                    } else
                        Toast.makeText(TabbedChatActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                    privateGroupCodeInputDialog.dismiss();
                } else {
                    Toast.makeText(TabbedChatActivity.this, "Please enter correct room code.", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void showPopup(ChatRooms chatRooms) {
        new UnlockChatRoomDialog(this, "Unlock The " + chatRooms.getChatRoomName()) {
            @Override
            public void addMeButtonClicked() {
                addUserChatGroupDetail(chatRooms.getId(), chatRooms.getChatRoomName());
            }

            @Override
            public void closeButtonClicked() {
            }
        };
    }

    private void showFiltersDialog() {
        try {
            BottomSheetDialog filterOption = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.service_filter_option, null);
            RecyclerView recyclerViewFilter = sheetView.findViewById(R.id.recyclerViewFilter);
            TextView optionCancel = sheetView.findViewById(R.id.optionCancel);
            TextView filterResetTips = sheetView.findViewById(R.id.filterResetTips);
            TextView textViewTitle = sheetView.findViewById(R.id.textViewTitle);
            textViewTitle.setText("Chat Rooms");
            filterResetTips.setVisibility(View.GONE);
            filterOption.setContentView(sheetView);

            chatRoomsArrayList.clear();
            chatRoomsArrayList.add(new ChatRooms("1", "Social Chat Room", false));
            chatRoomsArrayList.add(new ChatRooms("2", "Strategy Chat Room", false));
            if (isUserExistInBasinPrivateChatRoom)
                chatRoomsArrayList.add(new ChatRooms("3", "@basilnsage Room", false));
            else
                chatRoomsArrayList.add(new ChatRooms("3", "@basilnsage Room", true));
            chatRoomsArrayList.add(new ChatRooms("4", "Options Chat Room", true));
            chatRoomsArrayList.add(new ChatRooms("5", "ETFs Chat Room", true));
            chatRoomsArrayList.add(new ChatRooms("6", "Energy Trade Room", true));
            chatRoomsArrayList.add(new ChatRooms("7", "Fundamentals Room", true));

            // Set data to adapter
            ChatRoomsFilterAdapter adapter = new ChatRoomsFilterAdapter(this, chatRoomsArrayList, Constants.SOCIAL_CHAT_ROOM);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerViewFilter.setLayoutManager(layoutManager);
            recyclerViewFilter.setAdapter(adapter);
            adapter.setOnChatRoomsListener(new ChatRoomsFilterAdapter.ChatRoomsListener() {
                @Override
                public void onFilterClick(int position) {
                    if (chatRoomsArrayList.get(position).getId().equalsIgnoreCase("1")) {

                        filterOption.cancel();

                    } else if (chatRoomsArrayList.get(position).getId().equalsIgnoreCase("2")) {

                        filterOption.cancel();

                        if ((groupId != null && !groupId.isEmpty()) && (groupTitle != null && !groupTitle.isEmpty()))
                            ChatActivity.starChatActivity(TabbedChatActivity.this, groupId, groupTitle, groupDesc, groupImage, false);
                        else
                            Toast.makeText(TabbedChatActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();

                    } else if (chatRoomsArrayList.get(position).getId().equalsIgnoreCase("3")) {

                        if (isUserExistInBasinPrivateChatRoom) {
                            if ((basilPrivateGroupId != null && !basilPrivateGroupId.isEmpty()) && (basilPrivateGroupTitle != null && !basilPrivateGroupTitle.isEmpty())) {
                                ChatActivity.starChatActivity(TabbedChatActivity.this, basilPrivateGroupId, basilPrivateGroupTitle, basilPrivateGroupDesc, basilPrivateGroupImage, true);
                            } else
                                Toast.makeText(TabbedChatActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                        } else {
                            showPopupGroupCode();
                        }
                        filterOption.cancel();
                    } else {
                        filterOption.cancel();
                        showPopup(chatRoomsArrayList.get(position));
                    }
                }
            });

            optionCancel.setOnClickListener(view -> filterOption.cancel());
            filterOption.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(boolean connectionEstablishWithFirebase) {
        if (connectionEstablishWithFirebase)
            checkIfPublicChatGroupIsAvailable();
    }

    /**
     * Check strategy chat room is ready to go
     **/

    private void checkIfPublicChatGroupIsAvailable() {
        if (TradwyseApplication.socialChatModel != null && TradwyseApplication.publicChatModel != null && TradwyseApplication.basilPrivateChatModel != null) {
            //chatRoomsArrayList.add(new ChatRooms("2", "Strategy Chat Room", false));

            // For Strategy Chat Room
            groupId = TradwyseApplication.publicChatModel.getGroupId();
            groupImage = TradwyseApplication.publicChatModel.getGroupImage();
            groupTitle = TradwyseApplication.publicChatModel.getGroupTitle();
            groupDesc = TradwyseApplication.publicChatModel.getGroupDesc();

            // For Social Chat Room
            socialGroupId = TradwyseApplication.socialChatModel.getGroupId();

            // For Basil Private Chat Room
            // if (TradwyseApplication.basilPrivateChatModel != null) {
            basilPrivateGroupId = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupId();
            basilPrivateGroupImage = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupImage();
            basilPrivateGroupTitle = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupTitle();
            basilPrivateGroupDesc = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupDesc();
            basilPrivateGroupCode = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupCode();
            // }

            addMessageUpdateChangeListener();
            Log.d("TabbedChatActivity", "socialGroupId:-" + socialGroupId + " => " + "\ngroupId:-" + groupId + " => " + "\ngroupImage:-" + groupImage + "\ngroupTitle:-" + groupTitle + "\ngroupDesc:-" + groupDesc);
        } else {
            getFirestoreAuthToken();
        }
    }

    public void getFirestoreAuthToken() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<FirestoreAuthentication> call = apiInterface.getFirestoreAuth();
        call.enqueue(new Callback<FirestoreAuthentication>() {
            @Override
            public void onResponse(Call<FirestoreAuthentication> call, Response<FirestoreAuthentication> response) {
                if (response != null && response.body() != null) {
                    FirestoreAuthentication firestoreAuthentication = response.body();
                    ComplexPreferences mComplexPreferences = ComplexPreferences.getComplexPreferences(TabbedChatActivity.this, Constants.FIRESTORE_AUTH_PREF, MODE_PRIVATE);
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
                    checkIfPublicSocialChatGroupIsAvailable();
                    checkIfPublicChatGroupIsAvailableHere();
                    checkIfBasilPrivateChatGroupIsAvailableHere();
                } else {
                    Toast.makeText(TabbedChatActivity.this, "Unable to signInWithCustomToken", Toast.LENGTH_SHORT).show();
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
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_SOCIAL_GROUP_ID)) {
                            TradwyseApplication.socialChatModel = new SocialChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));

                            break;
                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_SOCIAL_GROUP_ID)) {
                            TradwyseApplication.socialChatModel = new SocialChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            break;
                        }
                        checkIfPublicChatGroupIsAvailableHere();
                    }
                } else {
                    //Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void checkIfPublicChatGroupIsAvailableHere() {
        TradwyseApplication.getFirestoreDb().collection(Constants.OPEN_GROUP)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_GROUP_ID)) {
                            TradwyseApplication.publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            checkIfPublicChatGroupIsAvailable();
                            break;
                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_GROUP_ID)) {
                            TradwyseApplication.publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            checkIfPublicChatGroupIsAvailable();
                            break;
                        }
                    }
                } else {
                    //Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void checkIfBasilPrivateChatGroupIsAvailableHere() {
        TradwyseApplication.getFirestoreDb().collection(Constants.BASIL_PRIVATE_GROUP)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_BASIL_PRIVATE_GROUP_ID)) {
                            TradwyseApplication.basilPrivateChatModel = new BasilPrivateChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION), document.getString(Constants.GROUP_CODE));
                            break;

                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_BASIL_PRIVATE_GROUP_ID)) {
                            TradwyseApplication.basilPrivateChatModel = new BasilPrivateChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION), document.getString(Constants.GROUP_CODE));
                            break;
                        }
                    }
                } else {
                    //Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    /**
     * End Check of strategy chat room is ready to go or not
     **/


    public void homeTabClicked(View v) {
        DashBoardActivity.CallDashboardActivity(this, false);
        finish();
    }

    public void chatTabClicked(View v) {

    }

    public void servicesTabClicked(View v) {
        ServicesActivity.CallServicesActivity(TabbedChatActivity.this);
    }

    public void notificationTabClicked(View v) {
        NotificationActivity.starNotificationActivity(this);
        finish();
    }

    public void moreTabClicked(View v) {
        MoreTabActivity.startMoreTabActivity(this);
        finish();
    }

    public void getAllSocialChatStockDetailsList() {
        // check network is available or not.
        if (!Common.isNetworkAvailable(this)) {
            layoutRefresh.setVisibility(View.VISIBLE);
            tvNodataMessage.setVisibility(View.VISIBLE);
            progressForChatRooms.setVisibility(View.GONE);
            progressBarMessage.setVisibility(View.GONE);
            Common.showOfflineMemeDialog(this, getResources().getString(R.string.memeMsg),
                    getResources().getString(R.string.JustLetYouKnow));
            return;
        }

        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<List<SocialChatStocksDetails>> call = apiInterface.getAllSocialChatStockDetailsList();
        call.enqueue(new Callback<List<SocialChatStocksDetails>>() {
            @Override
            public void onResponse(Call<List<SocialChatStocksDetails>> call, Response<List<SocialChatStocksDetails>> response) {
                layoutRefresh.setVisibility(View.GONE);
                imgRefereshicon.clearAnimation();

                if (response.body() != null && response.body().size() > 0) {
                    ArrayList<SocialChatStocksDetails> socialChatStocksDetailsArrayList = (ArrayList<SocialChatStocksDetails>) response.body();

                    Collections.reverse(socialChatStocksDetailsArrayList);
                    //  socialChatStocksDetailsArrayList.clear();
                    adapter = new SocialChatStockAdapter(socialChatStocksDetailsArrayList, TabbedChatActivity.this);
                    stockLayoutManager = new LinearLayoutManager(TabbedChatActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recycleTopSocialChat.setLayoutManager(stockLayoutManager);

                    // For auto scrolling
                    recycleTopSocialChat.startAutoScroll();
                    recycleTopSocialChat.setLoopEnabled(true);
                    recycleTopSocialChat.setCanTouch(true);
                    recycleTopSocialChat.setAdapter(adapter);
                    recycleTopSocialChat.setReverse(false);
                    if (Common.shouldStockLayoutVisible(TabbedChatActivity.this)) {
                        layoutStockView.setVisibility(View.VISIBLE);
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                layoutStockView.setVisibility(View.GONE);
                            }
                        }, 15000);

                    } else {
                        layoutStockView.setVisibility(View.GONE);
                    }

                    if (socialChatStocksDetailsArrayList.size() <= 0) {
                        layoutRefresh.setVisibility(View.VISIBLE);
                        if(clickCount >= 2)
                        {
                            Common.showMessageWithCenterText(TabbedChatActivity.this,getString(R.string.try_again_no_list_available),getString(R.string.coffee_break));
                            imgRefereshicon.clearAnimation();
                        }
                    }
                } else {
                    layoutRefresh.setVisibility(View.VISIBLE);
                    if (clickCount >= 2) {
                        Common.showMessageWithCenterText(TabbedChatActivity.this,getString(R.string.try_again_no_list_available),getString(R.string.coffee_break));
                        imgRefereshicon.clearAnimation();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SocialChatStocksDetails>> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "News onFailure");
                layoutRefresh.setVisibility(View.VISIBLE);
                if (clickCount >= 2) {
                    Common.showMessageWithCenterText(TabbedChatActivity.this,getString(R.string.try_again_no_list_available),getString(R.string.coffee_break));
                    imgRefereshicon.clearAnimation();
                }

            }
        });
    }

    public void addUserChatGroupDetail(String groupId, String groupName) {

        progressForChatRooms.setVisibility(View.VISIBLE);
        // check network is available or not.
        if (!Common.isNetworkAvailable(this)) {
            progressForChatRooms.setVisibility(View.GONE);
            tvNodataMessage.setVisibility(View.VISIBLE);
            Common.showOfflineMemeDialog(this, getResources().getString(R.string.memeMsg),
                    getResources().getString(R.string.JustLetYouKnow));
            return;
        }

        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<ResponseBody> call = apiInterface.addUserChatGroupDetail(
                TradWyseSession.getSharedInstance(this).getUserName(), groupId, groupName
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressForChatRooms.setVisibility(View.GONE);

                if (response.body() != null) {
                    new DialogThankyou(TabbedChatActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "News onFailure");
                progressForChatRooms.setVisibility(View.GONE);
            }
        });
    }

    private void showFirstFiltersDialog() {
        TabbedChatActivity activity = this;
        try {
            BottomSheetDialog filterOption = new BottomSheetDialog(activity);
            View sheetView = activity.getLayoutInflater().inflate(R.layout.filter_chat_first, null);
            TextView textViewPhoto = sheetView.findViewById(R.id.textViewPhoto);
            TextView textViewVideo = sheetView.findViewById(R.id.textViewVideo);
            TextView optionCancel = sheetView.findViewById(R.id.optionCancel);
            TextView textViewDocuments = sheetView.findViewById(R.id.textViewDocuments);
            filterOption.setContentView(sheetView);

            if (!StringHelper.isEmpty(tradWyseSession.getIsMentor()) && tradWyseSession.getIsMentor().equalsIgnoreCase("true")) {
                textViewDocuments.setVisibility(View.VISIBLE);
            } else {
                textViewDocuments.setVisibility(View.GONE);
            }

            textViewPhoto.setOnClickListener(view -> {

                showSecondFiltersDialog(1);
                filterOption.cancel();
            });

            textViewVideo.setOnClickListener((view) -> {

                showSecondFiltersDialog(2);
                filterOption.cancel();
            });

            textViewDocuments.setOnClickListener(view -> {

                requestDocuments();
                filterOption.cancel();
            });

            optionCancel.setOnClickListener(view -> filterOption.cancel());
            filterOption.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    private void showSecondFiltersDialog(int type) {
        TabbedChatActivity activity = this;
        try {
            BottomSheetDialog filterOption = new BottomSheetDialog(activity);
            View sheetView = activity.getLayoutInflater().inflate(R.layout.filter_chat_two, null);
            TextView textViewCamera = sheetView.findViewById(R.id.textViewCamera);
            TextView textViewGallery = sheetView.findViewById(R.id.textViewGallery);
            TextView optionCancel = sheetView.findViewById(R.id.optionCancel);
            TextView textViewTitle1 = sheetView.findViewById(R.id.textViewTitle1);
            TextView textViewTitle2 = sheetView.findViewById(R.id.textViewTitle2);
            filterOption.setContentView(sheetView);

            if (type == 1) {
                textViewTitle1.setText("Attach Photo");
                textViewTitle2.setText("Where would you like to attach a photo from?");
            } else {
                textViewTitle1.setText("Attach Video");
                textViewTitle2.setText("Where would you like to attach a video from?");
            }

            textViewCamera.setOnClickListener(view -> {

                if (type == 1) {
                    checkPermission();
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
                        startActivityForResult(intent, INTENT_REQUEST_CHOOSE_VIDEO);

                    } else {
                        checkForPermission();
                    }
                }

                filterOption.cancel();
            });

            textViewGallery.setOnClickListener((view) -> {

                if (type == 1) {
                    requestImage();
                } else {
                    requestVideo();
                }
                filterOption.cancel();
            });

            optionCancel.setOnClickListener(view -> filterOption.cancel());
            filterOption.show();

        } catch (Exception e) {
        }

    }

    private void checkForPermission() {
        requestAppPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_STORAGE, new BaseActivity.setPermissionListener() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        //selectVideoDialog();
                        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
                        startActivityForResult(intent, INTENT_REQUEST_CHOOSE_VIDEO);
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        checkForPermission();
                    }

                    @Override
                    public void onPermissionNeverAsk(int requestCode) {
                        //showPermissionSettingDialog(getString(R.string.permission_gallery_camera));
                    }
                });
    }

    @Override
    protected void previewCapturedImage() {
        if (getImagePath() != null) {
            List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
            thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
            thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(getImagePath())));
                showFullScreenDialog(bitmap, Uri.fromFile(new File(getImagePath())), thumbnailSizes, IMAGE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(rootLayout, "Storage access permissions are required to upload/download files.",
                    Snackbar.LENGTH_LONG)
                    .setAction("Okay", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_WRITE_EXTERNAL_STORAGE);
                            }
                        }
                    })
                    .show();
        } else {
            // Permission has not been granted yet. Request it directly.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void requestImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_IMAGE);
        }
    }

    private void requestVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_VIDEO);
        }
    }

    private void requestDocuments() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("application/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select File"), INTENT_REQUEST_CHOOSE_DOC);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Log.d(TAG, "data is null!");
                return;
            }
            List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
            thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
            thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                showFullScreenDialog(bitmap, data.getData(), thumbnailSizes, IMAGE);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == INTENT_REQUEST_CHOOSE_VIDEO && resultCode == Activity.RESULT_OK) {

            if (data != null) {
                showFullScreenDialog(null, data.getData(), null, VIDEO);
            }

        } else if (requestCode == INTENT_REQUEST_CHOOSE_DOC && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uploadDocFirebaseStorage(data.getData(), null);
            }

        }
        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Log.d(TAG, "data is null!");
                return;
            }
            List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
            thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
            thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                //showFullScreenDialog(bitmap, data.getData(), thumbnailSizes, IMAGE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void showFullScreenDialog(Bitmap bitmap, Uri uri, List<FileMessage.ThumbnailSize> thumbnailSizes, String type) {
        new ImageCaptionDialog(TabbedChatActivity.this, bitmap, uri, editTextMsg.getText().toString().trim()) {
            @Override
            public void onClickSend(String captionText) {
                if (type.equalsIgnoreCase(IMAGE)) {
                    UploadImageFirebaseStorage(uri, thumbnailSizes, captionText);
                } else {
                    try {
                        uploadThumbnail(uri.toString());
                        //uploadVideoFirebaseStorage(uri, captionText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    // Send msg to firestore.
    private void sendMessageToStrategyChatRoomOnFireStore(String message, String type, String caption, String refId) {

        Map<String, Object> messageData = new HashMap<>();
        messageData.put(Constants.MessageCreatedDate, System.currentTimeMillis());
        messageData.put(Constants.MessageText, message);
        messageData.put(Constants.MessageId, tradWyseSession.getUserId() + "_" + System.currentTimeMillis());
        messageData.put(Constants.MessageType, type);

        if (ImageMessageType.equals(type)) {
            messageData.put(Constants.MessageCaption, caption);
        }

        messageData.put(Constants.MessageProfileImageUrl, Common.getProfileImageUrl(tradWyseSession.getUserId()));
        messageData.put(Constants.MessageUserId, tradWyseSession.getUserId());
        messageData.put(Constants.MessageSource, Constants.deviceType);
        messageData.put(Constants.MessageUserName, tradWyseSession.getUserName());
        messageData.put(Constants.MessageSocialReferenceId, refId);

        TradwyseApplication.getFirestoreDb().collection(Constants.OPEN_GROUP)
                .document(groupId).collection("messages")
                .add(messageData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        editTextMsg.setText("");
                        isWaitingForResponse = false;
                        messageData.clear();
                        Log.d("SOCIAL_CHAT", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isWaitingForResponse = false;
                        Log.w("SOCIAL_CHAT", "Error adding document", e);
                    }
                });

    }

    private void sendMessageToFireStore(String message, String type, String caption) {
        if (!Common.isNetworkAvailable(this)) {
            Common.showOfflineMemeDialog(this, getResources().getString(R.string.memeMsg),
                    getResources().getString(R.string.JustLetYouKnow));
            return;
        }

        Map<String, Object> messageData = new HashMap<>();
        messageData.put(Constants.CREATED_ON, System.currentTimeMillis());
        messageData.put(Constants.PUBLISH_DATE_TIME, System.currentTimeMillis());
        messageData.put(Constants.COMMENT_BODY, message);
        messageData.put(Constants.MessageId, tradWyseSession.getUserId() + "_" + System.currentTimeMillis());
        messageData.put(Constants.ID, tradWyseSession.getUserId() + "_" + System.currentTimeMillis());
        messageData.put(Constants.MessageType, type);

        if (ImageMessageType.equals(type)) {
            messageData.put(Constants.MessageCaption, caption);
        }

        messageData.put(Constants.MessageProfileImageUrl, Common.getProfileImageUrl(tradWyseSession.getUserId()));
        messageData.put(Constants.MessageUserId, tradWyseSession.getUserId());
        messageData.put(Constants.MessageSource, Constants.deviceType);
        messageData.put(Constants.AUTHOR_NAME, tradWyseSession.getUserName());
        messageData.put(Constants.SOCIAL_SITE_NAME, "TradeTips");

        TradwyseApplication.getFirestoreDb().collection(Constants.SOCIAL_CHAT)
                .document(socialGroupId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        editTextMsg.setText("");
                        isWaitingForResponse = false;
                        //socialMessageAdapter.notifyDataSetChanged();
                        //messageLayoutManager.scrollToPosition(0);
                        Log.d("SOCIAL_CHAT", "DocumentSnapshot written with ID: " + documentReference.getId());
                        sendMessageToStrategyChatRoomOnFireStore(message, type, caption, documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isWaitingForResponse = false;
                        Log.w("SOCIAL_CHAT", "Error adding document", e);
                    }
                });

    }

    private void sendUserInfoToBasilPrivateChatRoom() {
        if (!Common.isNetworkAvailable(this)) {
            Common.showOfflineMemeDialog(this, getResources().getString(R.string.memeMsg),
                    getResources().getString(R.string.JustLetYouKnow));
            return;
        }

        Map<String, Object> messageData = new HashMap<>();
        messageData.put(Constants.MessageCreatedDate, System.currentTimeMillis());
        messageData.put(Constants.ID, tradWyseSession.getUserId() + "_" + System.currentTimeMillis());
        messageData.put(Constants.MessageProfileImageUrl, Common.getProfileImageUrl(tradWyseSession.getUserId()));
        messageData.put(Constants.MessageUserId, tradWyseSession.getUserId());
        messageData.put(Constants.MessageSource, Constants.deviceType);
        messageData.put(Constants.MessageUserName, tradWyseSession.getUserName());

        TradwyseApplication.getFirestoreDb().collection(Constants.BASIL_PRIVATE_GROUP)
                .document(basilPrivateGroupId)
                .collection("groupMembersInfo")
                .add(messageData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isWaitingForResponse = false;
                        Log.w("SOCIAL_CHAT", "Error adding document", e);
                    }
                });

    }

    private void UploadImageFirebaseStorage(Uri uri, List<FileMessage.ThumbnailSize> thumbnailSizes, String captionText) {
        showProgressDialog("Uploading Image...");
        Hashtable<String, Object> info = FileUtils.getFileInfo(this, uri);
        if (info == null || info.isEmpty()) {
            Toast.makeText(this, "Extracting file information failed.", Toast.LENGTH_LONG).show();
            dismissProgressDialog();
            return;
        }

        final String name;
        if (info.containsKey("name")) {
            name = ((String) info.get("name")) + System.currentTimeMillis();
        } else {
            name = "TeadeTipsChatImage_" + System.currentTimeMillis();
        }
        final String path = (String) info.get("path");
        final File file = new File(path);
        final String mime = (String) info.get("mime");
        final int size = (Integer) info.get("size");

        if (path == null || path.equals("")) {
            Toast.makeText(this, "File must be located in local storage.", Toast.LENGTH_LONG).show();
            dismissProgressDialog();
        } else {
            Logger.debug("SOCIAL_CHAT", "Uploading image name will be " + name);
            StorageReference riversRef = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_images/" + name);
            firebaseStorageUploadTask = riversRef.putFile(uri);

            // Register observers to listen for when the download is done or if it fails
            firebaseStorageUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                    dismissProgressDialog();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    getDownloadUrl(name, uri, captionText, IMAGE);

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    //updateProgressDialogMessage("Image uploaded " + progress + "%");
                    Logger.debug("SOCIAL_CHAT", "Uploading Image progress " + progress);
                }
            });

        }
    }

    private void getDownloadUrl(String name, Uri uri, String captionText, String type) {
        StorageReference ref = null;
        if (type.equalsIgnoreCase(IMAGE)) {
            ref = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_images/" + name);
        } else if (type.equalsIgnoreCase(VIDEO)) {
            ref = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_videos/" + name);
        } else if (type.equalsIgnoreCase(DOC)) {
            ref = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_doc/" + name);
        }
        firebaseStorageUploadTask = ref.putFile(uri);

        StorageReference finalRef = ref;
        Task<Uri> urlTask = firebaseStorageUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return finalRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    dismissProgressDialog();

                    //  sendMessageToFireStore(downloadUri.toString(), Constants.ImageMessageType,"");
                    if (type.equalsIgnoreCase(IMAGE)) {

                        sendMessageToFireStore(downloadUri.toString(), ImageMessageType, captionText);
                        Log.d("captionText", "" + captionText);

                        Toasty.success(TabbedChatActivity.this, "Image uploaded successfully").show();
                        Logger.debug("SOCIAL_CHAT", "FirebaseStorage Storage Image Uploaded successfully " + downloadUri);

                    } else if (type.equalsIgnoreCase(VIDEO)) {

                        sendMessageToFireStore(downloadUri.toString(), VideoMessageType, captionText);
                        Log.d("captionText", "" + captionText);

                        Toasty.success(TabbedChatActivity.this, "Video uploaded successfully").show();
                        Logger.debug("SOCIAL_CHAT", "FirebaseStorage Storage Video Uploaded successfully " + downloadUri);
                    } else if (type.equalsIgnoreCase(DOC)) {

                        sendMessageToFireStore(downloadUri.toString(), DocMessageType, captionText);
                        Log.d("captionText", "" + captionText);

                        Toasty.success(TabbedChatActivity.this, "Doc uploaded successfully").show();
                        Logger.debug("SOCIAL_CHAT", "FirebaseStorage Storage Doc Uploaded successfully " + downloadUri);
                    }
                } else {
                    Toasty.error(TabbedChatActivity.this, "Can't upload image at this time please try again later.").show();
                }
            }
        });
        //dismissProgressDialog();
    }

    private void uploadVideoFirebaseStorage(Uri videoUri, String captionText, Attachment attachment) {
        if (videoUri != null) {
            showProgressDialog("Uploading Video...");

            Hashtable<String, Object> info = FileUtils.getFileInfo(this, videoUri);
            if (info == null || info.isEmpty()) {
                Toast.makeText(this, "Extracting file information failed.", Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                return;
            }

            final String name;
            if (info.containsKey("name")) {
                name = ((String) info.get("name")) + System.currentTimeMillis() + ".mov";
            } else {
                name = "video_message_" + System.currentTimeMillis() + ".mov";
            }

            StorageReference riversRef = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_videos/" + name);
            firebaseStorageUploadTask = riversRef.putFile(videoUri);

            firebaseStorageUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        dismissProgressDialog();
                        //Toast.makeText(TabbedChatActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                        getDownloadUrl(name, videoUri, captionText, VIDEO);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                    dismissProgressDialog();
                }
            });
        } else {
            Toast.makeText(TabbedChatActivity.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDocFirebaseStorage(Uri docUri, String captionText) {
        if (docUri != null) {
            showProgressDialog("Uploading Document...");
            Hashtable<String, Object> info = FileUtils.getFileInfo(this, docUri);
            if (info == null || info.isEmpty()) {
                Toast.makeText(this, "Extracting file information failed.", Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                return;
            }

            final String name;
            if (info.containsKey("name")) {
                name = ((String) info.get("name"));//+ System.currentTimeMillis();
            } else {
                name = "doc_message_" + System.currentTimeMillis();
            }

            StorageReference riversRef = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_doc/" + name);
            firebaseStorageUploadTask = riversRef.putFile(docUri);

            firebaseStorageUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        dismissProgressDialog();
                        //Toast.makeText(TabbedChatActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                        getDownloadUrl(name, docUri, captionText, DOC);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                    dismissProgressDialog();
                }
            });
        } else {
            Toast.makeText(TabbedChatActivity.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadThumbnail(final String filePath) {
        Toast.makeText(this, "just a moment..", Toast.LENGTH_LONG).show();
        File file = new File(filePath);
        Uri fileUri = Uri.parse(filePath);

        StorageReference storageReference = TradwyseApplication.getFirebaseStorageObj().getReference().child("video_thumbnail").child(file.getName() + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            //If thumbnail exists
            Attachment attachment = new Attachment();
            attachment.setData(uri.toString());
            uploadVideoFirebaseStorage(fileUri, "", attachment);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, byte[]> thumbnailTask = new AsyncTask<String, Void, byte[]>() {
                    @Override
                    protected byte[] doInBackground(String... params) {
                        //Create thumbnail
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(params[0], MediaStore.Video.Thumbnails.MINI_KIND);
                        if (bitmap != null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            return baos.toByteArray();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(byte[] data) {
                        super.onPostExecute(data);
                        if (data != null) {
                            UploadTask uploadTask = storageReference.putBytes(data);
                            uploadTask.continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                // Continue with the task to get the download URL
                                return storageReference.getDownloadUrl();
                            }).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    Attachment attachment = new Attachment();
                                    attachment.setData(downloadUri.toString());
                                    uploadVideoFirebaseStorage(fileUri, "", attachment);
                                } else {
                                    uploadVideoFirebaseStorage(fileUri, "", null);
                                }
                            }).addOnFailureListener(e1 -> uploadVideoFirebaseStorage(fileUri, "", null));
                        } else
                            uploadVideoFirebaseStorage(fileUri, "", null);
                    }
                };
                thumbnailTask.execute(filePath);
            }
        });
    }


    // Fetch the data and show to list.
    private void setUpRecyclerView() {
        socialMessageAdapter = new SocialMessageAdapter(TabbedChatActivity.this, tradWyseSession.getUserName());
        messageLayoutManager = new LinearLayoutManager(this);
        messageLayoutManager.setReverseLayout(true);
        messageLayoutManager.setStackFromEnd(false);
        recyclerViewLiveStockChat.setLayoutManager(messageLayoutManager);
        recyclerViewLiveStockChat.setAdapter(socialMessageAdapter);
        socialMessageAdapter.SetOnSocialChatListener(new SocialMessageAdapter.OnSocialChatListener() {
            @Override
            public void onClickReplies(int position, SocialMessageDetails socialMessageDetails) {
                startActivity(new Intent(TabbedChatActivity.this, SocialChatReplyActivity.class)
                        .putExtra("chatModel", socialMessageDetails));
            }

            @Override
            public void onClickLike(int position, boolean isLiked, SocialMessageDetails socialMessageDetails) {
                addLikeInMessage(position, isLiked, socialMessageDetails);
            }
        });


        recyclerViewLiveStockChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (messageLayoutManager.findLastVisibleItemPosition() == socialMessageAdapter.getItemCount() - 1) {

                    swipeRefreshLayout.setRefreshing(false);

                    if (moreDataAvailable && tvLoadMoreMessage.getVisibility() == View.GONE) {
                        loadMoreMessage();
                    } else {
                        //Toast.makeText(ChatActivity.this, "End of list.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (messageLayoutManager.findFirstCompletelyVisibleItemPosition() <= 1) {
                    imageViewScrollDown.setVisibility(View.GONE);
                } else {
                    imageViewScrollDown.setVisibility(View.VISIBLE);
                }
                Log.v("SOCIAL_CHAT", "onScrollStateChanged");
            }
        });

        recyclerViewLiveStockChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerViewLiveStockChat.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = messageLayoutManager.findFirstVisibleItemPosition();
                            if (pos <= 1)
                                recyclerViewLiveStockChat.smoothScrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });
    }

    private void addMessageUpdateChangeListener() {

        // progressBarMessage.setVisibility(View.VISIBLE);

        if (!Common.isNetworkAvailable(this)) {
            tvNodataMessage.setVisibility(View.VISIBLE);
            progressBarMessage.setVisibility(View.GONE);
            Common.showOfflineMemeDialog(this, getResources().getString(R.string.memeMsg),
                    getResources().getString(R.string.JustLetYouKnow));
            return;
        }

        // For Social Chat Room
        TradwyseApplication.getFirestoreDb()
                .collection(Constants.SOCIAL_CHAT)
                .document(socialGroupId)
                .collection("messages")
                //.orderBy(Constants.SOCIAL_SITE_NAME, Query.Direction.ASCENDING)
                .orderBy(Constants.PUBLISH_DATE_TIME, Query.Direction.DESCENDING)
                .limit(30)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        progressBarMessage.setVisibility(View.GONE);
                        List<DocumentChange> documentChangeList = value.getDocumentChanges();
                        if (!documentChangeList.isEmpty()) {
                            tvNodataMessage.setVisibility(View.GONE);
                            lastVisibleValue = value.getDocuments().get(value.size() - 1);
                            moreDataAvailable = true;
                        } else {
                            moreDataAvailable = false;
                        }

                        if (documentChangeList.size() > 0) {
                            tvNodataMessage.setVisibility(View.GONE);
                        } else {
                            tvNodataMessage.setVisibility(View.VISIBLE);
                        }

                        Logger.debug("SOCIAL_CHAT", "Changed document size is:- " + documentChangeList.size());
                        processChatDataAndDisplay(value, documentChangeList);
                    }
                });


        // For Basil Private Chat Room
        TradwyseApplication.getFirestoreDb().collection(Constants.BASIL_PRIVATE_GROUP)
                .document(basilPrivateGroupId)
                .collection("groupMembersInfo")
                .orderBy(Constants.MessageCreatedDate, Query.Direction.DESCENDING)
                //.limit(30)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentChange> documentChangeList = value.getDocumentChanges();

                        Logger.debug("BASIL_PRIVATE_CHAT_ROOM", "Changed document size is:- " + documentChangeList.size());
                        isUserExistInBasilPrivateChatRoom(value, documentChangeList);
                    }
                });
    }

    private void isUserExistInBasilPrivateChatRoom(@NotNull QuerySnapshot value, List<DocumentChange> documentChangeList) {

        for (DocumentChange documentChange : documentChangeList) {
            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                QueryDocumentSnapshot document = documentChange.getDocument();
                String documentReferenceId = document.getReference().getId();
                Long createdDate = document.getLong(Constants.MessageCreatedDate);
                String id = document.getString(Constants.ID);
                String messageSource = document.getString(Constants.MessageSource);
                String profileImageUrl = document.getString(MessageProfileImageUrl);
                String userId = document.getString(Constants.MessageUserId);
                String userName = document.getString(Constants.MessageUserName);

                if (tradWyseSession.getUserName().equals(userName)) {
                    isUserExistInBasinPrivateChatRoom = true;
                }
                Log.e("BASIL_CHAT_MEMBER : ", userName);
            }
        }
    }

    private void processChatDataAndDisplay(@NotNull QuerySnapshot value, List<DocumentChange> documentChangeList) {

        for (DocumentChange documentChange : documentChangeList) {

            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                QueryDocumentSnapshot document = documentChange.getDocument();
                String documentReferenceId = document.getReference().getId();
                Long createdOn = document.getLong(Constants.CREATED_ON);
                String authorName = document.getString(Constants.AUTHOR_NAME);

                List<SocialComment> comment = null;
                if (document.get(Constants.COMMENT) != null) {
                    comment = (List<SocialComment>) document.get(Constants.COMMENT);
                    //List<Map<String, SocialComment>> commentMapList = (List<Map<String, SocialComment>>) document.get(Constants.COMMENT);
                }

                String commentBody = document.getString(Constants.COMMENT_BODY);
                String description = document.getString(Constants.DESCRIPTION);
                String id = document.getString(Constants.ID);
                String link = document.getString(Constants.LINK);
                Long modifiedOn = document.getLong(Constants.MODIFIED_ON);
                Long publishDateTime = document.getLong(Constants.PUBLISH_DATE_TIME);
                String socialSiteName = document.getString(Constants.SOCIAL_SITE_NAME);
                String stockName = document.getString(Constants.STOCK_NAME);
                String radditUrlName = document.getString(Constants.RADDIT_URL_NAME);
                String url = document.getString(Constants.URL);
                String stockSymbol = document.getString(Constants.STOCK_SYMBOL);
                String profileImage = document.getString(Constants.PROFILE_IMAGE_URL);

                String messageType = "";
                if (document.contains(Constants.MessageType)) {
                    messageType = document.getString(Constants.MessageType);
                }

                String caption = "";
                if (document.contains(Constants.MessageCaption)) {
                    caption = document.getString(Constants.MessageCaption);
                }

                int likeCount = 0;
                if (document.contains(Constants.LIKE_COUNT)) {
                    likeCount = Integer.parseInt(String.valueOf(document.get(Constants.LIKE_COUNT)));
                }

                List<Map<String, Object>> likedByUserName = null;
                if (document.contains(Constants.LIKEED_BY_USER_NAME)) {
                    try {
                        likedByUserName = (List<Map<String, Object>>) document.get(Constants.LIKEED_BY_USER_NAME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                SocialMessageDetails chatModel = new SocialMessageDetails(documentReferenceId, createdOn, authorName, commentBody, description,
                        id, link, modifiedOn, publishDateTime, socialSiteName, stockName, stockSymbol, profileImage, url, radditUrlName, comment,
                        messageType, caption, likeCount, likedByUserName);

                mMessageList.add(chatModel);

                Collections.sort(mMessageList, new Comparator<SocialMessageDetails>() {
                    DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm:ss a zzz");

                    @Override
                    public int compare(SocialMessageDetails o1, SocialMessageDetails o2) {
                        try {
                            return dateFormat.parse(DateUtils.getChatMessageFormatDate(o2.getPublishDateTime())).compareTo(dateFormat.parse(DateUtils.getChatMessageFormatDate(o1.getPublishDateTime())));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
                boolean isAlreadyAListAvailable = socialMessageAdapter.getItemCount() > 0;
                socialMessageAdapter.setMessageList(mMessageList);
                if (!isAlreadyAListAvailable)
                    recycleTopSocialChat.scrollToPosition(0);
                Log.w("SOCIAL_CHAT", "Update Event Called:- " + value.getDocumentChanges().toString());

            } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                QueryDocumentSnapshot document = documentChange.getDocument();
                String documentReferenceId = document.getReference().getId();
                for (int i = 0; i < mMessageList.size(); i++) {
                    if (mMessageList.get(i).getDocumentReferenceId().equals(documentReferenceId)) {
                        mMessageList.remove(i);
                        socialMessageAdapter.setMessageList(mMessageList);
                    }
                }
            } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {

                QueryDocumentSnapshot document = documentChange.getDocument();
                String documentReferenceId = document.getReference().getId();
                Long createdOn = document.getLong(Constants.CREATED_ON);
                String authorName = document.getString(Constants.AUTHOR_NAME);

                List<SocialComment> comment = null;
                if (document.get(Constants.COMMENT) != null) {
                    comment = (List<SocialComment>) document.get(Constants.COMMENT);
                }

                String commentBody = document.getString(Constants.COMMENT_BODY);
                String description = document.getString(Constants.DESCRIPTION);
                String id = document.getString(Constants.ID);
                String link = document.getString(Constants.LINK);
                Long modifiedOn = document.getLong(Constants.MODIFIED_ON);
                Long publishDateTime = document.getLong(Constants.PUBLISH_DATE_TIME);
                String socialSiteName = document.getString(Constants.SOCIAL_SITE_NAME);
                String stockName = document.getString(Constants.STOCK_NAME);
                String radditUrlName = document.getString(Constants.RADDIT_URL_NAME);
                String url = document.getString(Constants.URL);
                String stockSymbol = document.getString(Constants.STOCK_SYMBOL);
                String profileImage = document.getString(Constants.PROFILE_IMAGE_URL);

                String messageType = "";
                if (document.contains(Constants.MessageType)) {
                    messageType = document.getString(Constants.MessageType);
                }

                String caption = "";
                if (document.contains(Constants.MessageCaption)) {
                    caption = document.getString(Constants.MessageCaption);
                }

                int likeCount = 0;
                if (document.contains(Constants.LIKE_COUNT)) {
                    likeCount = Integer.parseInt(String.valueOf(document.get(Constants.LIKE_COUNT)));
                }

                List<Map<String, Object>> likedByUserName = null;
                if (document.contains(Constants.LIKEED_BY_USER_NAME)) {
                    try {
                        likedByUserName = (List<Map<String, Object>>) document.get(Constants.LIKEED_BY_USER_NAME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                SocialMessageDetails chatModel = new SocialMessageDetails(documentReferenceId, createdOn, authorName, commentBody, description,
                        id, link, modifiedOn, publishDateTime, socialSiteName, stockName, stockSymbol, profileImage, url, radditUrlName, comment,
                        messageType, caption, likeCount, likedByUserName);

                for (int i = 0; i < mMessageList.size(); i++) {
                    String e = null;

                    try {
                        if (mMessageList.get(i).getId().equalsIgnoreCase(chatModel.getId())) {
                            mMessageList.set(i, chatModel);
                        }
                    } catch (Exception e1) {
                        // Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }


                }

                Collections.sort(mMessageList, new Comparator<SocialMessageDetails>() {
                    DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm:ss a zzz");

                    @Override
                    public int compare(SocialMessageDetails o1, SocialMessageDetails o2) {
                        try {
                            return dateFormat.parse(DateUtils.getChatMessageFormatDate(o2.getPublishDateTime())).compareTo(dateFormat.parse(DateUtils.getChatMessageFormatDate(o1.getPublishDateTime())));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
                boolean isAlreadyAListAvailable = socialMessageAdapter.getItemCount() > 0;
                socialMessageAdapter.setMessageList(mMessageList);
                if (!isAlreadyAListAvailable)
                    recycleTopSocialChat.scrollToPosition(0);
                Log.w("SOCIAL_CHAT", "Update Event Called:- " + value.getDocumentChanges().toString());

            }
        }
    }

    private void loadMoreMessage() {
        tvLoadMoreMessage.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);
        Query next = TradwyseApplication.getFirestoreDb()
                .collection(Constants.SOCIAL_CHAT)
                .document(socialGroupId)
                .collection("messages")
                .orderBy(Constants.PUBLISH_DATE_TIME, Query.Direction.DESCENDING)
                .startAfter(lastVisibleValue)
                .limit(90);
        next.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                        if (!documentChangeList.isEmpty()) {
                            lastVisibleValue = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                            moreDataAvailable = true;
                            processChatDataAndDisplay(queryDocumentSnapshots, documentChangeList);
                        } else {
                            moreDataAvailable = false;
                        }
                        Logger.debug("SOCIAL_CHAT", "Changed document size is:- " + documentChangeList.size());
                        tvLoadMoreMessage.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        tvLoadMoreMessage.setVisibility(View.GONE);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        tvLoadMoreMessage.setVisibility(View.GONE);
                    }
                });
    }

    private void addLikeInMessage(int position, boolean isLiked, SocialMessageDetails socialMessageDetails) {

        Map<String, Object> messageData = new HashMap<>();

        List<Map<String, Object>> likedByUserName = new ArrayList<>();
        if (socialMessageDetails.getLikedByUserName() != null) {
            for (int i = 0; i < socialMessageDetails.getLikedByUserName().size(); i++) {
                /*if (!socialMessageDetails.getLikedByUserName().get(i).get(Constants.UserName).equals(tradWyseSession.getUserName())) {

                }*/
                Map<String, Object> likeData = new HashMap<>();
                likeData.put(Constants.UserName, socialMessageDetails.getLikedByUserName().get(i).get(Constants.UserName));
                likedByUserName.add(likeData);
                Log.e("USERNAME", "" + likedByUserName.get(i).get(Constants.UserName));
            }
        }

        if (isLiked) {
            socialMessageDetails.setLikeCount(socialMessageDetails.getLikeCount() + 1);
            Map<String, Object> likeData = new HashMap<>();
            likeData.put(Constants.UserName, tradWyseSession.getUserName());
            likedByUserName.add(likeData);
            Log.e("USERNAME1", "" + likedByUserName.get(likedByUserName.size() - 1).get(Constants.UserName));
        } else {
            socialMessageDetails.setLikeCount(socialMessageDetails.getLikeCount() - 1);
            for (int i = 0; i < likedByUserName.size(); i++) {
                if (likedByUserName.get(i).get(Constants.UserName).equals(tradWyseSession.getUserName())) {
                    Log.e("USERNAME2", "" + likedByUserName.get(i).get(Constants.UserName));
                    likedByUserName.remove(i);
                }
                //Log.e("USERNAME3", "" + likedByUserName.get(i).get(Constants.UserName));
            }
        }

        messageData.put(Constants.LIKE_COUNT, socialMessageDetails.getLikeCount());
        messageData.put(Constants.LIKEED_BY_USER_NAME, likedByUserName);

        TradwyseApplication.getFirestoreDb()
                .collection(Constants.SOCIAL_CHAT)
                .document(socialGroupId)
                .collection("messages")
                .document(socialMessageDetails.getDocumentReferenceId())
                .update(messageData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TabbedChatActivity.this, "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Use to handle hide and show stock view clicklistener chevron button
    public void chevronclick() {
        viewOpenStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutStockView.getVisibility() == View.GONE) {
                    layoutStockView.setVisibility(View.VISIBLE);
                    viewOpenStock.setVisibility(View.GONE);
                    viewHideStock.setVisibility(View.VISIBLE);
                    recycleTopSocialChat.pauseAutoScroll(true);
                } else {

                    layoutStockView.setVisibility(View.GONE);
                    viewOpenStock.setVisibility(View.VISIBLE);
                    viewHideStock.setVisibility(View.GONE);
                    recycleTopSocialChat.pauseAutoScroll(true);

                }
            }
        });
        viewHideStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutStockView.getVisibility() == View.VISIBLE) {
                    layoutStockView.setVisibility(View.GONE);
                    viewHideStock.setVisibility(View.GONE);
                    viewOpenStock.setVisibility(View.VISIBLE);
                    recycleTopSocialChat.pauseAutoScroll(true);
                } else {
                    layoutStockView.setVisibility(View.VISIBLE);
                    viewHideStock.setVisibility(View.VISIBLE);
                    viewOpenStock.setVisibility(View.GONE);
                    recycleTopSocialChat.pauseAutoScroll(true);
                }
            }
        });
    }


}