package com.info.tradewyse;

import static com.info.adapter.ChatActivityAdapter.MESSAGE_TYPE_TEXT;
import static com.info.commons.Constants.DocMessageType;
import static com.info.commons.Constants.IS_PRODUCTION;
import static com.info.commons.Constants.ImageMessageType;
import static com.info.commons.Constants.MessageProfileImageUrl;
import static com.info.commons.Constants.VideoMessageType;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.info.ComplexPreference.ComplexPreferences;
import com.info.CustomToast.Toasty;
import com.info.adapter.ChatActivityAdapter;
import com.info.adapter.ChatRoomsFilterAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.FileUtils;
import com.info.commons.FlagReasonOptionUtility;
import com.info.commons.ProfileImageAbstractActivity;
import com.info.commons.StringHelper;
import com.info.commons.TradWyseSession;
import com.info.dialog.DialogThankyou;
import com.info.dialog.ImageCaptionDialog;
import com.info.dialog.PrivateGroupCodeInputDialog;
import com.info.dialog.UnlockChatRoomDialog;
import com.info.interfaces.FlagOptionSelectListener;
import com.info.logger.Logger;
import com.info.model.Attachment;
import com.info.model.BasilPrivateChatModel;
import com.info.model.ChatModel;

import com.info.model.ChatRooms;
import com.info.model.FirestoreAuthentication;
import com.info.model.GroupMemberInfo;
import com.info.model.PublicChatModel;
import com.info.model.SocialChatModel;
import com.info.model.SocialComment;
import com.info.sendbird.utils.DateUtils;
import com.info.sendbird.utils.ImageUtils;
import com.kbeanie.multipicker.api.VideoPicker;
import com.sendbird.android.FileMessage;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amit Gupta on 14,November,2020
 */
public class ChatActivity extends ProfileImageAbstractActivity {

    private static final String TAG = "ChatActivity";
    private static final int INTENT_REQUEST_CHOOSE_IMAGE = 300;
    private static final int INTENT_REQUEST_CHOOSE_VIDEO = 400;
    private static final int INTENT_REQUEST_CHOOSE_DOC = 500;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 15;
    public static final int PERMISSION_STORAGE = 100;
    public static final String IMAGE = "IMAGE";
    public static final String VIDEO = "VIDEO";
    public static final String DOC = "DOC";
    private static final int PIC_CROP = 1001;
    private PrivateGroupCodeInputDialog privateGroupCodeInputDialog = null;
    UploadTask firebaseStorageUploadTask;
    private View mRootLayout;
    ArrayList<ChatRooms> chatRoomsArrayList = new ArrayList<>();
    ArrayList<ChatModel> mMessageListUnFlagged = new ArrayList<>();
    String groupId, groupTitle, groupDesc, groupImage;
    private List<ChatModel> mMessageList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    TextView sendBtnTv, loadingTv, toolBarTitle;
    ImageView attachmentIv, imageViewScrollDown;
    EditText messageEdt;
    private ChatActivityAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    TradWyseSession tradWyseSession;
    boolean isWaitingForResponse = false;
    private ProgressBar progressBar;
    DocumentSnapshot lastVisibleValue;
    boolean moreDataAvailable = false;
    private VideoPicker videoPicker;
    protected String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private LinearLayout layoutReplyRoot, linearOnlineClikcView;
    private ImageView imgViewReplyClose, imgTypeReplyImage, imgTypeReplyVideo, imgTypeReplyDoc;
    private TextView tvReplyUsername, tvReplyUserTextAndType;
    private RelativeLayout layoutReplyAttachment, layoutOnlineView;
    private FirebaseStorage mFirebaseStorage;
    String basilPrivateGroupId, basilPrivateGroupTitle, basilPrivateGroupDesc, basilPrivateGroupImage, basilPrivateGroupCode;
    private String replyMsgId = "";
    private String replyUserId = "";
    private String replyUserName = "";
    private boolean isBasilPrivateRoom = false;
    private List<GroupMemberInfo> groupMemberInfoList = new ArrayList<>();
    private long mLastClickTime = 0;
    private boolean isUserExistInBasinPrivateChatRoom = false;


    public static void starChatActivity(Context context, String groupId, String groupTitle, String groupDesc, String groupImage, boolean isBasilPrivateRoom) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.GROUP_ID, groupId);
        intent.putExtra(Constants.GROUP_TITLE, groupTitle);
        intent.putExtra(Constants.GROUP_DESCRIPTION, groupDesc);
        intent.putExtra(Constants.GROUP_IMAGE, groupImage);
        intent.putExtra("isBasilPrivateRoom", isBasilPrivateRoom);
        ((Activity) context).startActivity(intent);
    }

    public static void starChatActivity(Context context, boolean isBasilPrivateRoom) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("isBasilPrivateRoom", isBasilPrivateRoom);
        ((Activity) context).startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        loadingTv = findViewById(R.id.loading_tv);
        mRootLayout = findViewById(R.id.rootLayout);
        mRecyclerView = findViewById(R.id.chat_rv);
        sendBtnTv = findViewById(R.id.sendTv);
        attachmentIv = findViewById(R.id.attachmentIv);
        messageEdt = findViewById(R.id.message_edt);
        progressBar = findViewById(R.id.progress);
        layoutOnlineView = findViewById(R.id.rlOnlineView);
        layoutReplyRoot = findViewById(R.id.layoutReplyRoot);
        layoutReplyRoot.setVisibility(View.GONE);
        linearOnlineClikcView = findViewById(R.id.linearViewOnline);
        //layoutReplyDeleteMenus.setVisibility(View.GONE);
        imgViewReplyClose = findViewById(R.id.imgViewReplyClose);
        tvReplyUsername = findViewById(R.id.tvReplyUsername);
        tvReplyUserTextAndType = findViewById(R.id.tvReplyUserTextAndType);
        layoutReplyAttachment = findViewById(R.id.layoutReplyAttachment);
        imgTypeReplyImage = findViewById(R.id.imgTypeReplyImage);
        imgTypeReplyVideo = findViewById(R.id.imgTypeReplyVideo);
        imgTypeReplyDoc = findViewById(R.id.imgTypeReplyDoc);
        imageViewScrollDown = findViewById(R.id.imageViewScrollDown);
        toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText("");

        /*socialGroupId = getIntent().getStringExtra(Constants.SOCIAL_GROUP_ID);
        groupId = getIntent().getStringExtra(Constants.GROUP_ID);
        groupTitle = getIntent().getStringExtra(Constants.GROUP_TITLE);
        groupDesc = getIntent().getStringExtra(Constants.GROUP_DESCRIPTION);
        groupImage = getIntent().getStringExtra(Constants.GROUP_IMAGE);*/
        isBasilPrivateRoom = getIntent().getBooleanExtra("isBasilPrivateRoom", false);
        tradWyseSession = TradWyseSession.getSharedInstance(ChatActivity.this);
        TradwyseApplication.getFirestoreDb().getFirestoreSettings().isPersistenceEnabled();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        progressBar.setVisibility(View.VISIBLE);
        checkIfPublicChatGroupIsAvailable();

        if (isBasilPrivateRoom) {
            setToolBarAddChanelName(basilPrivateGroupTitle, groupImage);
            layoutOnlineView.setVisibility(View.VISIBLE);

            linearOnlineClikcView.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, ChatRoomMemberActivity.class);
                intent.putParcelableArrayListExtra("memberList", (ArrayList<? extends Parcelable>) groupMemberInfoList);
                startActivity(intent);
            });

        } else {
            layoutOnlineView.setVisibility(View.GONE);
        }

        sendBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWaitingForResponse) {
                    String message = messageEdt.getText().toString().trim();
                    if (message != null && !message.isEmpty()) {
                        layoutReplyRoot.setVisibility(View.GONE);
                        sendMessageToFireStore(message, Constants.TextMessageType, "");
                        isWaitingForResponse = true;
                    }
                }
            }
        });

        findViewById(R.id.layoutRoomsMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showBottomDialog();
            }
        });

        attachmentIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFirstFiltersDialog();
                //requestImage();
            }
        });

        messageEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    sendBtnTv.setTextColor(getResources().getColor(R.color.blue_color));
                else
                    sendBtnTv.setTextColor(getResources().getColor(R.color.tw__medium_gray));
            }
        });

        imageViewScrollDown.setOnClickListener(v -> {
            mRecyclerView.scrollToPosition(0);
            imageViewScrollDown.setVisibility(View.GONE);
        });

    }

    /**
     * Check strategy chat room is ready to go
     **/
    private void checkIfPublicChatGroupIsAvailable() {
        if (TradwyseApplication.publicChatModel != null && TradwyseApplication.basilPrivateChatModel != null) {
            // For Strategy Chat Room
            groupId = TradwyseApplication.publicChatModel.getGroupId();
            groupImage = TradwyseApplication.publicChatModel.getGroupImage();
            groupTitle = TradwyseApplication.publicChatModel.getGroupTitle();
            groupDesc = TradwyseApplication.publicChatModel.getGroupDesc();

            // For Basil Private Chat Room
            basilPrivateGroupId = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupId();
            basilPrivateGroupImage = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupImage();
            basilPrivateGroupTitle = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupTitle();
            basilPrivateGroupDesc = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupDesc();
            basilPrivateGroupCode = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupCode();

            //setUpChatAdapter();
            setUpRecyclerView();
            addMessageUpdateChangeListner();
            setToolBarAddChanelName(groupTitle, groupImage);

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
                    ComplexPreferences mComplexPreferences = ComplexPreferences.getComplexPreferences(ChatActivity.this, Constants.FIRESTORE_AUTH_PREF, MODE_PRIVATE);
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
                    checkIfPublicChatGroupIsAvailableHere();
                    checkIfBasilPrivateChatGroupIsAvailableHere();
                } else {
                    Toast.makeText(ChatActivity.this, "Unable to signInWithCustomToken", Toast.LENGTH_SHORT).show();
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

    //Basil private chat room data check
    private void checkBasilPrivateChatModelData(boolean needStratageData) {
        if (needStratageData) {
            if (TradwyseApplication.publicChatModel != null) {
                groupId = TradwyseApplication.publicChatModel.getGroupId();
                groupImage = TradwyseApplication.publicChatModel.getGroupImage();
                groupTitle = TradwyseApplication.publicChatModel.getGroupTitle();
                groupDesc = TradwyseApplication.publicChatModel.getGroupDesc();
            }
        } else {
            if (TradwyseApplication.basilPrivateChatModel != null) {
                /*groupId = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupId();
                groupImage = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupImage();
                groupTitle = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupTitle();
                groupDesc = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupDesc();
                basilPrivateGroupCode = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupCode();*/
                basilPrivateGroupId = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupId();
                basilPrivateGroupImage = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupImage();
                basilPrivateGroupTitle = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupTitle();
                basilPrivateGroupDesc = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupDesc();
                basilPrivateGroupCode = TradwyseApplication.basilPrivateChatModel.getBasilPrivateGroupCode();
            }
        }
    }

    //Bottom dialog code for menu tab chat list
    private void showBottomDialog() {
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
            //chatRoomsArrayList.add(new ChatRooms("1", "Social Chat Room", false));
            chatRoomsArrayList.add(new ChatRooms("2", "Strategy Chat Room", false));
            if (isUserExistInBasinPrivateChatRoom)
                chatRoomsArrayList.add(new ChatRooms("3", "@basilnsage Room", false));
            else
                chatRoomsArrayList.add(new ChatRooms("3", "@basilnsage Room", true));
            chatRoomsArrayList.add(new ChatRooms("4", "Options Chat Room", true));
            chatRoomsArrayList.add(new ChatRooms("5", "ETFs Chat Room", true));
            chatRoomsArrayList.add(new ChatRooms("6", "Energy Trade Room", true));
            chatRoomsArrayList.add(new ChatRooms("7", "Fundamentals Room", true));

            optionCancel.setOnClickListener(view -> filterOption.cancel());
            filterOption.show();

            ChatRoomsFilterAdapter adapter = new ChatRoomsFilterAdapter(this, chatRoomsArrayList,
                    isBasilPrivateRoom ? Constants.BASIL_PRIVATE_CHAT_ROOM : Constants.STRATEGY_CHAT_ROOM);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerViewFilter.setLayoutManager(layoutManager);
            recyclerViewFilter.setAdapter(adapter);
            adapter.setOnChatRoomsListener(new ChatRoomsFilterAdapter.ChatRoomsListener() {
                @Override
                public void onFilterClick(int position) {
                    if (chatRoomsArrayList.get(position).getId().equalsIgnoreCase("1")) {
                        finish();

                    } else if (chatRoomsArrayList.get(position).getId().equalsIgnoreCase("2")) {

                        if (isBasilPrivateRoom) {
                            checkBasilPrivateChatModelData(true);
                            if ((groupId != null && !groupId.isEmpty()) && (groupTitle != null && !groupTitle.isEmpty())) {
                                isBasilPrivateRoom = false;
                                ChatActivity.starChatActivity(ChatActivity.this, groupId, groupTitle, groupDesc, groupImage, false);
                                finish();
                            } else {
                                Toast.makeText(ChatActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            filterOption.cancel();
                        }

                    } else if (chatRoomsArrayList.get(position).getId().equalsIgnoreCase("3")) {

                        if (isBasilPrivateRoom) {
                            filterOption.cancel();
                        } else {
                            checkBasilPrivateChatModelData(false);
                            if ((basilPrivateGroupId != null && !basilPrivateGroupId.isEmpty()) && (basilPrivateGroupTitle != null && !basilPrivateGroupTitle.isEmpty())) {
                                if (isUserExistInBasinPrivateChatRoom) {
                                    isBasilPrivateRoom = true;
                                    ChatActivity.starChatActivity(ChatActivity.this, basilPrivateGroupId, basilPrivateGroupTitle, basilPrivateGroupDesc, basilPrivateGroupImage, true);
                                    finish();
                                } else {
                                    filterOption.cancel();
                                    showPopupGroupCode();
                                }
                            } else
                                Toast.makeText(ChatActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        filterOption.cancel();
                        showPopup(chatRoomsArrayList.get(position));
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void addUserChatGroupDetail(String groupId, String groupName) {

        progressBar.setVisibility(View.VISIBLE);
        // check network is available or not.
        if (!Common.isNetworkAvailable(this)) {
            Common.showOfflineMemeDialog(this, getResources().getString(R.string.memeMsg),
                    getResources().getString(R.string.JustLetYouKnow));
            return;
        }

        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<ResponseBody> call = apiInterface.addUserChatGroupDetail(
                TradWyseSession.getSharedInstance(this).getUserName(), isBasilPrivateRoom ? basilPrivateGroupId : groupId, groupName
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);

                if (response.body() != null) {
                    new DialogThankyou(ChatActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "News onFailure");
                progressBar.setVisibility(View.GONE);
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
                    isBasilPrivateRoom = true;
                    if ((basilPrivateGroupId != null && !basilPrivateGroupId.isEmpty()) && (basilPrivateGroupTitle != null && !basilPrivateGroupTitle.isEmpty())) {
                        sendUserInfoToBasilPrivateChatRoom();
                        ChatActivity.starChatActivity(ChatActivity.this, basilPrivateGroupId, basilPrivateGroupTitle, basilPrivateGroupDesc, basilPrivateGroupImage, true);
                    } else
                        Toast.makeText(ChatActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                    privateGroupCodeInputDialog.dismiss();
                } else {
                    Toast.makeText(ChatActivity.this, "please enter correct code.", Toast.LENGTH_SHORT).show();
                }
            }
        };
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

    private void sendMessageToFireStore(String message, String type, String caption) {
        layoutReplyRoot.setVisibility(View.GONE);

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


        TradwyseApplication.getFirestoreDb().collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                .document(isBasilPrivateRoom ? basilPrivateGroupId : groupId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        messageEdt.setText("");
                        isWaitingForResponse = false;
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isWaitingForResponse = false;
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        layoutReplyRoot.setVisibility(View.GONE);

    }

    private void DeleteFirestoreMessage(ChatModel message, int position) {

        if (message.getMessageType().equalsIgnoreCase(ImageMessageType) ||
                message.getMessageType().equalsIgnoreCase(VideoMessageType) ||
                message.getMessageType().equalsIgnoreCase(DocMessageType)) {

            mFirebaseStorage = FirebaseStorage.getInstance();
            StorageReference fileUrl = mFirebaseStorage.getReferenceFromUrl(message.getMessage());

            fileUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Log.d(TAG, "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.d(TAG, "onFailure: did not delete file");
                }
            });
        }


        TradwyseApplication.getFirestoreDb()
                .collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                .document(isBasilPrivateRoom ? basilPrivateGroupId : groupId)
                .collection("messages")
                .document(message.getDocumentReferenceId())
                .collection("replies")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        progressBar.setVisibility(View.GONE);
                        List<DocumentChange> documentChangeList = value.getDocumentChanges();
                        if (!documentChangeList.isEmpty()) {

                            for (int i = 0; i < documentChangeList.size(); i++) {
                                DocumentChange documentChange = documentChangeList.get(i);
                                documentChange.getDocument().getReference().delete();
                            }
                        }
                    }
                });


        TradwyseApplication.getFirestoreDb()
                .collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                .document(isBasilPrivateRoom ? basilPrivateGroupId : groupId)
                .collection("messages")
                .document(message.getDocumentReferenceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // if (message.getMessageSocialReferenceId() != null && !message.getMessageSocialReferenceId().equalsIgnoreCase(""))
                        //DeleteSocialChatFirestoreMessage(message);

                        Toasty.success(ChatActivity.this, "Message deleted successfully").show();

                    }
                });
    }

    private void DeleteSocialChatFirestoreMessage(ChatModel message) {

        /*TradwyseApplication.getFirestoreDb()
                .collection(Constants.SOCIAL_CHAT)
                .document(socialGroupId)
                .collection("messages")
                .document(message.getMessageSocialReferenceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toasty.success(ChatActivity.this, "Message deleted successfully").show();
                    }
                });*/
    }

    private void addMessageUpdateChangeListner() {

        // For Strategy Chat Room
        TradwyseApplication.getFirestoreDb()
                .collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                .document(isBasilPrivateRoom ? basilPrivateGroupId : groupId)
                .collection("messages")
                .orderBy(Constants.MessageCreatedDate, Query.Direction.DESCENDING)
                .limit(30)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        progressBar.setVisibility(View.GONE);
                        List<DocumentChange> documentChangeList = value.getDocumentChanges();
                        if (!documentChangeList.isEmpty()) {
                            lastVisibleValue = value.getDocuments().get(value.size() - 1);
                            moreDataAvailable = true;
                        } else {
                            moreDataAvailable = false;
                        }
                        Logger.debug(TAG, "Changed document size is:- " + documentChangeList.size());

                        processChatDataAndDisplay(value, documentChangeList);
                    }
                });

        // For Basil Private Chat Room Member List
        if (isBasilPrivateRoom) {
            TradwyseApplication.getFirestoreDb().collection(Constants.BASIL_PRIVATE_GROUP)
                    .document(isBasilPrivateRoom ? basilPrivateGroupId : groupId)
                    .collection("groupMembersInfo")
                    .orderBy(Constants.MessageCreatedDate, Query.Direction.DESCENDING)
                    //.limit(30)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            List<DocumentChange> documentChangeList = value.getDocumentChanges();

                            Logger.debug("BASIL_PRIVATE_CHAT_ROOM", "Changed document size is:- " + documentChangeList.size());
                            getMemberListOfBasilPrivateChatRoom(value, documentChangeList);
                        }
                    });
        }

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

    private void getMemberListOfBasilPrivateChatRoom(@NotNull QuerySnapshot value, List<DocumentChange> documentChangeList) {

        for (DocumentChange documentChange : documentChangeList) {
            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                QueryDocumentSnapshot document = documentChange.getDocument();
                String documentReferenceId = document.getReference().getId();
                Long createdOn = document.getLong(Constants.MessageCreatedDate);
                String id = document.getString(Constants.ID);
                String messageSource = document.getString(Constants.MessageSource);
                String profileImageUrl = document.getString(MessageProfileImageUrl);
                String userId = document.getString(Constants.MessageUserId);
                String userName = document.getString(Constants.MessageUserName);

                GroupMemberInfo groupMemberInfo = new GroupMemberInfo(id, documentReferenceId, createdOn, messageSource, profileImageUrl,
                        userId, userName);
                groupMemberInfoList.add(groupMemberInfo);
            }
        }
    }

    private void isUserExistInBasilPrivateChatRoom(@NotNull QuerySnapshot value, List<DocumentChange> documentChangeList) {

        for (DocumentChange documentChange : documentChangeList) {
            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                QueryDocumentSnapshot document = documentChange.getDocument();
                String userName = document.getString(Constants.MessageUserName);

                if (tradWyseSession.getUserName().equals(userName)) {
                    isUserExistInBasinPrivateChatRoom = true;
                }
                Log.e("BASIL_CHAT_MEMBER : ", userName);
            }
        }
    }

    private void setUpRecyclerView() {
        mChatAdapter = new ChatActivityAdapter(this, isBasilPrivateRoom ? basilPrivateGroupId : groupId, isBasilPrivateRoom);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);
        mChatAdapter.SetOnClickListener(new ChatActivityAdapter.OnClickListener() {
            @Override
            public void onReplyMsgClick(ChatModel chatModel, int position, boolean isReplyAvailable) {
                if (isReplyAvailable)
                    startActivity(new Intent(ChatActivity.this, ChatReplyActivity.class)
                            .putExtra("chatModel", chatModel)
                            .putExtra("isBasilPrivateRoom", isBasilPrivateRoom)
                            .putExtra(Constants.GROUP_ID, isBasilPrivateRoom ? basilPrivateGroupId : groupId));
            }

            @Override
            public void onMessageLongClick(ChatModel chatModel, int position) {
                if (tradWyseSession.getUserId().equals(chatModel.getUserId())) {
                    showOptionsDialog(true, chatModel, position);
                } else {
                    showOptionsDialog(false, chatModel, position);
                }
            }
        });

        // Load more messages when user reaches the top of the current message list.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                    if (moreDataAvailable && loadingTv.getVisibility() == View.GONE) {
                        loadMoreMessage();
                    } else {
                        //Toast.makeText(ChatActivity.this, "End of list.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (mLayoutManager.findFirstCompletelyVisibleItemPosition() <= 1) {
                    imageViewScrollDown.setVisibility(View.GONE);
                } else {
                    imageViewScrollDown.setVisibility(View.VISIBLE);
                }

                Log.v(TAG, "onScrollStateChanged");
            }
        });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = mLayoutManager.findFirstVisibleItemPosition();
                            if (pos <= 2) {
                                mRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }, 100);
                }
            }
        });
    }

    private void loadMoreMessage() {
        loadingTv.setVisibility(View.VISIBLE);
        Query next = TradwyseApplication.getFirestoreDb()
                .collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                .document(isBasilPrivateRoom ? basilPrivateGroupId : groupId)
                .collection("messages")
                .orderBy(Constants.MessageCreatedDate, Query.Direction.DESCENDING)
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
                        Logger.debug(TAG, "Changed document size is:- " + documentChangeList.size());
                        loadingTv.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        loadingTv.setVisibility(View.GONE);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        loadingTv.setVisibility(View.GONE);
                    }
                });
    }

    private void processChatDataAndDisplay(@NotNull QuerySnapshot value, List<DocumentChange> documentChangeList) {
        for (DocumentChange documentChange : documentChangeList) {
            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                QueryDocumentSnapshot document = documentChange.getDocument();
                String documentReferenceId = document.getReference().getId();
                long createdDate = document.getLong(Constants.MessageCreatedDate);
                String message = document.getString(Constants.MessageText);
                String messageId = document.getString(Constants.MessageId);
                String messageType = document.getString(Constants.MessageType);
                String profileImageUrl = document.getString(MessageProfileImageUrl);
                String userId = document.getString(Constants.MessageUserId);
                String userName = document.getString(Constants.MessageUserName);
                /*String replyMsgId = document.getString(Constants.MessageReplyId);
                String replyUserName = document.getString(Constants.MessageReplyUserName);
                String replyUserId = document.getString(Constants.MessageReplyUserId);*/
                String messageSource = document.getString(Constants.MessageSource);

                String messageSocialReferenceId = "";
                if (document.contains(Constants.MessageSocialReferenceId)) {
                    messageSocialReferenceId = document.getString(Constants.MessageSocialReferenceId);
                }

                boolean isFlag = false;
                String messageFlagedUserName = "";
                List<Map<String, Object>> flagMapList = null;
                if (document.contains(Constants.Flag)) {
                    flagMapList = (List<Map<String, Object>>) document.get(Constants.Flag);
                }

                if (flagMapList != null) {

                    for (Map<String, Object> i : flagMapList) {
                        if (Boolean.parseBoolean(String.valueOf(i.get(Constants.MessageFlag))) &&
                                i.get(Constants.MessageFlagedUserName).equals(tradWyseSession.getUserName())) {

                            isFlag = Boolean.parseBoolean(String.valueOf(i.get(Constants.MessageFlag)));
                            messageFlagedUserName = String.valueOf(i.get(Constants.MessageFlagedUserName));

                        }
                    }
                }

                String caption = null;
                if (document.contains(Constants.MessageCaption)) {
                    caption = document.getString(Constants.MessageCaption);
                }
                if (userId == null || userId.isEmpty() || messageType == null || messageType.isEmpty()) {

                } else {
                    ChatModel chatModel = new ChatModel(documentReferenceId, createdDate, message, messageId, messageType, profileImageUrl, userId, userName, caption, messageSource, isFlag, messageFlagedUserName, messageSocialReferenceId, flagMapList);
                    Log.d(TAG, document.getId() + " => \n"
                            + "createdDate:- " + createdDate + "\n"
                            + "message:- " + message + "\n"
                            + "messageId:- " + messageId + "\n"
                            + "messageType:- " + messageType + "\n"
                            + "profileImageUrl:- " + profileImageUrl + "\n"
                            + "userId:- " + userId + "\n"
                            + "userName:- " + userName + "\n"
                            + "caption:- " + caption + "\n"
                            + "replyMsgId:- " + replyMsgId + "\n"
                            + "replyUserName:- " + replyUserName + "\n"
                            + "replyUserId:- " + replyUserId + "\n");
                    mMessageList.add(chatModel);
                }
                Collections.sort(mMessageList, new Comparator<ChatModel>() {
                    DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm:ss a zzz");

                    @Override
                    public int compare(ChatModel o1, ChatModel o2) {
                        try {
                            return dateFormat.parse(DateUtils.getChatMessageFormatDate(o2.getCreatedDate())).compareTo(dateFormat.parse(DateUtils.getChatMessageFormatDate(o1.getCreatedDate())));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
                boolean isAlreadyAListAvailable = mChatAdapter.getItemCount() > 0;
                mMessageListUnFlagged.clear();
                for (int i = 0; i < mMessageList.size(); i++) {
                    if (!mMessageList.get(i).isFlag()) {
                        mMessageListUnFlagged.add(mMessageList.get(i));
                    }
                }
                mChatAdapter.setMessageList(mMessageListUnFlagged);
                if (!isAlreadyAListAvailable)
                    mRecyclerView.scrollToPosition(0);

                Log.w(TAG, "Update Event Called:- " + value.getDocumentChanges().toString());

            } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                QueryDocumentSnapshot document = documentChange.getDocument();
                String documentReferenceId = document.getReference().getId();
                if (isBasilPrivateRoom) {
                    for (int i = 0; i < mMessageListUnFlagged.size(); i++) {
                        if (mMessageListUnFlagged.get(i).getDocumentReferenceId().equals(documentReferenceId)) {
                            mMessageList.remove(i);
                            mMessageListUnFlagged.remove(i);
                            mChatAdapter.setMessageList(mMessageListUnFlagged);
                            mChatAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    for (int i = 0; i < mMessageList.size(); i++) {
                        if (mMessageList.get(i).getDocumentReferenceId().equals(documentReferenceId)) {
                            mMessageList.remove(i);
                            mChatAdapter.setMessageList(mMessageList);
                            mChatAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                QueryDocumentSnapshot document = documentChange.getDocument();
                String documentReferenceId = document.getReference().getId();
                long createdDate = document.getLong(Constants.MessageCreatedDate);
                String message = document.getString(Constants.MessageText);
                String messageId = document.getString(Constants.MessageId);
                String messageType = document.getString(Constants.MessageType);
                String profileImageUrl = document.getString(MessageProfileImageUrl);
                String userId = document.getString(Constants.MessageUserId);
                String userName = document.getString(Constants.MessageUserName);
                String messageSource = document.getString(Constants.MessageSource);

                String messageSocialReferenceId = "";
                if (document.contains(Constants.MessageSocialReferenceId)) {
                    messageSocialReferenceId = document.getString(Constants.MessageSocialReferenceId);
                }

                boolean isFlag = false;
                String messageFlagedUserName = "";
                List<Map<String, Object>> flagMapList = null;
                if (document.contains(Constants.Flag)) {
                    flagMapList = (List<Map<String, Object>>) document.get(Constants.Flag);
                }

                if (flagMapList != null) {

                    for (Map<String, Object> i : flagMapList) {
                        if (Boolean.parseBoolean(String.valueOf(i.get(Constants.MessageFlag))) &&
                                i.get(Constants.MessageFlagedUserName).equals(tradWyseSession.getUserName())) {

                            isFlag = Boolean.parseBoolean(String.valueOf(i.get(Constants.MessageFlag)));
                            messageFlagedUserName = String.valueOf(i.get(Constants.MessageFlagedUserName));

                        }
                    }
                }

                String caption = null;
                if (document.contains(Constants.MessageCaption)) {
                    caption = document.getString(Constants.MessageCaption);
                }
                if (userId == null || userId.isEmpty() || messageType == null || messageType.isEmpty()) {

                } else {
                    ChatModel chatModel = new ChatModel(documentReferenceId, createdDate, message, messageId, messageType, profileImageUrl, userId, userName, caption, messageSource, isFlag, messageFlagedUserName, messageSocialReferenceId, flagMapList);
                    for (int i = 0; i < mMessageList.size(); i++) {
                        if (mMessageList.get(i).getMessageId().equalsIgnoreCase(chatModel.getMessageId())) {
                            mMessageList.set(i, chatModel);
                        }
                    }
                }
                Collections.sort(mMessageList, new Comparator<ChatModel>() {
                    DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm:ss a zzz");

                    @Override
                    public int compare(ChatModel o1, ChatModel o2) {
                        try {
                            return dateFormat.parse(DateUtils.getChatMessageFormatDate(o2.getCreatedDate())).compareTo(dateFormat.parse(DateUtils.getChatMessageFormatDate(o1.getCreatedDate())));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
                boolean isAlreadyAListAvailable = mChatAdapter.getItemCount() > 0;
                mMessageListUnFlagged.clear();
                for (int i = 0; i < mMessageList.size(); i++) {
                    if (!mMessageList.get(i).isFlag()) {
                        mMessageListUnFlagged.add(mMessageList.get(i));
                    }
                }
                mChatAdapter.setMessageList(mMessageListUnFlagged);
                if (!isAlreadyAListAvailable)
                    mRecyclerView.scrollToPosition(0);

            }

        }
    }

    private void showMessageOptionsDialog(final ChatModel message, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this, R.style.CustomAlertDialog);
        builder.setMessage("Are you sure you want to delete this message?");
        builder.setTitle("Delete");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DeleteFirestoreMessage(message, position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setToolBarAddChanelName(String title, String groupImage) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView groupImageIv = findViewById(R.id.group_image);
        toolBarTitle.setText(title);
        //toolBarTitle.setTypeface(Common.getTypeface(getApplicationContext(),1));
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0f);

        // Get profile image and display it
        ImageUtils.displayRoundImageFromUrl(this, groupImage, groupImageIv);
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
                Log.d(TAG, "data is null!");
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

            //   UploadImageFirebaseStorage(data.getData(), thumbnailSizes);
        } else if (requestCode == INTENT_REQUEST_CHOOSE_VIDEO && resultCode == Activity.RESULT_OK) {

            if (data != null) {
                showFullScreenDialog(null, data.getData(), null, VIDEO);
                //showFullScreenDialog(null, data.getData(), null);
            }

        } else if (requestCode == INTENT_REQUEST_CHOOSE_DOC && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uploadDocFirebaseStorage(data.getData(), null);
            }

        }
        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Log.d(TAG, "data is null!");
                return;
            }
            //Removing upload confirmation dialog
            //showUploadConfirmDialog(data.getData());
            List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
            thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
            thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));


            //UploadImageFirebaseStorage(data.getData(), thumbnailSizes);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                showFullScreenDialog(bitmap, data.getData(), thumbnailSizes, IMAGE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void UploadImageFirebaseStorage(Uri uri, List<FileMessage.ThumbnailSize> thumbnailSizes, String captionText) {
        // showProgressDialog("Uploading Image...");
        Toasty.info(ChatActivity.this, "Uploading Image...", Toast.LENGTH_LONG).show();
        Hashtable<String, Object> info = FileUtils.getFileInfo(this, uri);
        if (info == null || info.isEmpty()) {
            Toast.makeText(this, "Extracting file information failed.", Toast.LENGTH_LONG).show();
            // dismissProgressDialog();
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
            Logger.debug(TAG, "Uploading image name will be " + name);
            if (IS_PRODUCTION == true)
            {
                StorageReference riversRef = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_images/" + name);
                firebaseStorageUploadTask = riversRef.putFile(uri);
            }
            else
                {
                    StorageReference riversRef = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_storage_test_env/" + name);
                    firebaseStorageUploadTask = riversRef.putFile(uri);
                }
            // Register observers to listen for when the download is done or if it fails
            firebaseStorageUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                    Toast.makeText(ChatActivity.this, "Failed To Upload Image.", Toast.LENGTH_SHORT).show();

                    //dismissProgressDialog();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //  Toast.makeText(ChatActivity.this, "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                    getDownloadUrl(name, uri, captionText, IMAGE);

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    //updateProgressDialogMessage("Image uploaded " + progress + "%");
                    Logger.debug(TAG, "Uploading Image progress " + progress);
                }
            });

        }
    }

    private void getDownloadUrl(String name, Uri uri, String captionText, String type) {
        StorageReference ref = null;
        if (type.equalsIgnoreCase(IMAGE) && IS_PRODUCTION == true) {
            ref = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_images/" + name);
        } else if (type.equalsIgnoreCase(VIDEO) && IS_PRODUCTION == true) {
            ref = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_videos/" + name);
        } else if (type.equalsIgnoreCase(DOC) && IS_PRODUCTION == true) {
            ref = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_doc/" + name);
        }
        else
        {
            ref = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_storage_test_env/" + name);
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

                        Toasty.success(ChatActivity.this, "Image uploaded successfully").show();
                        Logger.debug(TAG, "FirebaseStorage Storage Image Uploaded successfully " + downloadUri);

                    } else if (type.equalsIgnoreCase(VIDEO)) {

                        sendMessageToFireStore(downloadUri.toString(), VideoMessageType, captionText);
                        Log.d("captionText", "" + captionText);

                        Toasty.success(ChatActivity.this, "Video uploaded successfully").show();
                        Logger.debug(TAG, "FirebaseStorage Storage Video Uploaded successfully " + downloadUri);
                    } else if (type.equalsIgnoreCase(DOC)) {

                        sendMessageToFireStore(downloadUri.toString(), DocMessageType, captionText);
                        Log.d("captionText", "" + captionText);

                        Toasty.success(ChatActivity.this, "Doc uploaded successfully").show();
                        Logger.debug(TAG, "FirebaseStorage Storage Doc Uploaded successfully " + downloadUri);
                    }
                } else {
                    Toasty.error(ChatActivity.this, "Can't upload image at this time please try again later.").show();
                }
            }
        });
        //dismissProgressDialog();
    }

    private void uploadVideoFirebaseStorage(Uri videoUri, String captionText, Attachment attachment) {
        if (videoUri != null) {
            Toasty.info(ChatActivity.this, "Uploading Video...", Toast.LENGTH_LONG).show();
            // showProgressDialog("Uploading Video...");

            Hashtable<String, Object> info = FileUtils.getFileInfo(this, videoUri);
            if (info == null || info.isEmpty()) {
                Toast.makeText(this, "Extracting file information failed.", Toast.LENGTH_LONG).show();
                // dismissProgressDialog();
                return;
            }

            final String name;
            if (info.containsKey("name")) {
                name = ((String) info.get("name")) + System.currentTimeMillis() + ".mov";
            } else {
                name = "video_message_" + System.currentTimeMillis() + ".mov";
            }
            if (IS_PRODUCTION == true) {
                StorageReference riversRef = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_videos/" + name);
                firebaseStorageUploadTask = riversRef.putFile(videoUri);
            }
            else
                {
                    StorageReference riversRef = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_storage_test_env/" + name);
                    firebaseStorageUploadTask = riversRef.putFile(videoUri);
                }

            firebaseStorageUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        // dismissProgressDialog();
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
            Toast.makeText(ChatActivity.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDocFirebaseStorage(Uri docUri, String captionText) {
        if (docUri != null) {
            Toasty.info(ChatActivity.this, "Uploading Document...", Toast.LENGTH_LONG).show();
            // showProgressDialog("Uploading Document...");
            Hashtable<String, Object> info = FileUtils.getFileInfo(this, docUri);
            if (info == null || info.isEmpty()) {
                Toast.makeText(this, "Extracting file information failed.", Toast.LENGTH_LONG).show();
                //dismissProgressDialog();
                return;
            }

            final String name;
            if (info.containsKey("name")) {
                name = ((String) info.get("name"));//+ System.currentTimeMillis();
            } else {
                name = "doc_message_" + System.currentTimeMillis();
            }
            if (IS_PRODUCTION == true) {
                StorageReference riversRef = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_doc/" + name);
                firebaseStorageUploadTask = riversRef.putFile(docUri);
            }
            else
            {
                StorageReference riversRef = TradwyseApplication.getFirebaseStorageObj().getReference().child("message_storage_test_env/" + name);
                firebaseStorageUploadTask = riversRef.putFile(docUri);
            }

            firebaseStorageUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        // dismissProgressDialog();
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
            Toast.makeText(ChatActivity.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
        }
    }

    public void showFullScreenDialog(Bitmap bitmap, Uri uri, List<FileMessage.ThumbnailSize> thumbnailSizes, String type) {
        new ImageCaptionDialog(ChatActivity.this, bitmap, uri, messageEdt.getText().toString().trim()) {
            @Override
            public void onClickSend(String captionText) {
                if (type.equalsIgnoreCase(IMAGE)) {
                    UploadImageFirebaseStorage(uri, thumbnailSizes, captionText);
                } else {
                    try {
                        //String filePath = SiliCompressor.with(ChatActivity.this).compressVideo(uri.toString(),  Environment.getExternalStorageState());

                        /*String filePath = null;
                        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/videos");
                        if (f.mkdirs() || f.isDirectory())
                            filePath = String.valueOf(new VideoCompressAsyncTask(ChatActivity.this).execute(uri.toString(), f.getPath()));
*/
                        uploadThumbnail(uri.toString());
                        //uploadVideoFirebaseStorage(uri, captionText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(mRootLayout, "Storage access permissions are required to upload/download files.",
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted.
                    Snackbar.make(mRootLayout, "Storage permissions granted. You can now upload or download files.",
                            Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    // Permission denied.
                    Snackbar.make(mRootLayout, "Permissions denied.",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void showFirstFiltersDialog() {
        ChatActivity activity = this;
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
        }

    }

    private void showSecondFiltersDialog(int type) {
        ChatActivity activity = this;
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

    @Override
    protected void previewCapturedImage() {
        if (getImagePath() != null) {
            List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
            thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
            thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(getImagePath())));
                showFullScreenDialog(bitmap, Uri.fromFile(new File(getImagePath())), thumbnailSizes, IMAGE);
                // performCrop(data.getData());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadThumbnail(final String filePath) {
        //Toast.makeText(this, "just a moment..", Toast.LENGTH_LONG).show();
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

    public void setFlagIconClick(ChatModel chatModel, int position) {
        FlagReasonOptionUtility frUtility = new FlagReasonOptionUtility.Builder(ChatActivity.this).build();
        frUtility.showFlagOptionList();
        frUtility.setFlagOptionSelectListener(new FlagOptionSelectListener() {
            @Override
            public void startActivityForResult(int selectedOption, int requestCode) {

                if (selectedOption == 0) return; //when user click cancel.

                addFlagInMessage(chatModel, position, Common.getFlagOptionById(selectedOption, ChatActivity.this));

            }
        });
    }

    private void showOptionsDialog(boolean isCurrentUser, ChatModel chatModel, int position) {

        ChatActivity activity = this;
        try {
            BottomSheetDialog filterOption = new BottomSheetDialog(activity);
            View sheetView = activity.getLayoutInflater().inflate(R.layout.filter_options_for_chat, null);
            TextView textViewDelete = sheetView.findViewById(R.id.textViewDelete);
            TextView textViewFlag = sheetView.findViewById(R.id.textViewFlag);
            TextView viewFlag = sheetView.findViewById(R.id.viewFlag);
            TextView textViewCopy = sheetView.findViewById(R.id.textViewCopy);
            TextView viewCopy = sheetView.findViewById(R.id.viewCopy);
            filterOption.setContentView(sheetView);

            if (isCurrentUser) {
                textViewDelete.setVisibility(View.VISIBLE);
                textViewFlag.setVisibility(View.GONE);
                viewFlag.setVisibility(View.GONE);
            } else {
                textViewDelete.setVisibility(View.GONE);
                textViewFlag.setVisibility(View.VISIBLE);
                viewFlag.setVisibility(View.VISIBLE);
            }

            if (chatModel.getMessageType().equalsIgnoreCase(MESSAGE_TYPE_TEXT)) {
                textViewCopy.setVisibility(View.VISIBLE);
                viewCopy.setVisibility(View.VISIBLE);
            } else {
                textViewCopy.setVisibility(View.GONE);
                viewCopy.setVisibility(View.GONE);
            }

            textViewCopy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied text", chatModel.getMessage());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ChatActivity.this, "Copied text", Toast.LENGTH_SHORT).show();
                filterOption.cancel();
            });

            sheetView.findViewById(R.id.textViewReply).setOnClickListener(view -> {

                startActivity(new Intent(ChatActivity.this, ChatReplyActivity.class)
                        .putExtra("chatModel", chatModel)
                        .putExtra("isBasilPrivateRoom", isBasilPrivateRoom)
                        .putExtra(Constants.GROUP_ID, isBasilPrivateRoom ? basilPrivateGroupId : groupId));
                //setDataForReplyView(chatModel);
                filterOption.cancel();
            });

            textViewFlag.setOnClickListener((view) -> {

                setFlagIconClick(chatModel, position);
                filterOption.cancel();
            });

            textViewDelete.setOnClickListener(view -> {

                showMessageOptionsDialog(chatModel, position);
                filterOption.cancel();
            });

            sheetView.findViewById(R.id.optionCancel).setOnClickListener(view -> filterOption.cancel());
            filterOption.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addFlagInMessage(ChatModel chatModel, int position, String flagOptionById) {

        Map<String, Object> messageData = new HashMap<>();

        List<Map<String, Object>> flagArrayList = new ArrayList<>();
        if (chatModel.getFlagMapList() != null) {
            if (chatModel.getFlagMapList().size() > 0) {
                flagArrayList.addAll(chatModel.getFlagMapList());
            }
        }

        Map<String, Object> flagData = new HashMap<>();
        flagData.put(Constants.MessageFlag, true);
        flagData.put(Constants.MessageFlagMsg, flagOptionById);
        flagData.put(Constants.MessageFlagedUserName, tradWyseSession.getUserName());
        flagData.put(Constants.MessageFlagedUserId, tradWyseSession.getUserId());
        flagArrayList.add(flagData);

        messageData.put(Constants.Flag, flagArrayList);

        TradwyseApplication.getFirestoreDb()
                .collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                .document(isBasilPrivateRoom ? basilPrivateGroupId : groupId)
                .collection("messages")
                .document(chatModel.getDocumentReferenceId())
                .update(messageData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mMessageList.remove(position);
                        mChatAdapter.notifyDataSetChanged();
                        Common.showMessage(ChatActivity.this, getResources().getString(R.string.flag_success_chat), "Flagged");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChatAdapter != null)
            mChatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.include_header_menus).getVisibility() == View.VISIBLE) {
            findViewById(R.id.include_header_menus).setVisibility(View.GONE);
            findViewById(R.id.layoutToolbarRoot).setVisibility(View.VISIBLE);

            for (int i = 0; i < mMessageList.size(); i++) {
                mMessageList.get(i).setSelected(false);
            }
            if (mChatAdapter != null)
                mChatAdapter.notifyDataSetChanged();

        } else {
            super.onBackPressed();
        }
    }

}
