package com.info.tradewyse;

import static com.info.commons.Constants.DocMessageType;
import static com.info.commons.Constants.IS_PRODUCTION;
import static com.info.commons.Constants.ImageMessageType;
import static com.info.commons.Constants.MessageProfileImageUrl;
import static com.info.commons.Constants.VideoMessageType;
import static com.info.tradewyse.ChatActivity.DOC;
import static com.info.tradewyse.ChatActivity.IMAGE;
import static com.info.tradewyse.ChatActivity.VIDEO;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.info.CustomToast.Toasty;
import com.info.adapter.ChatActivityAdapter;
import com.info.adapter.ChatReplyAdapter;
import com.info.adapter.SocialChatStockAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.FileUtils;
import com.info.commons.ProfileImageAbstractActivity;
import com.info.commons.StringHelper;
import com.info.dialog.ImageCaptionDialog;
import com.info.logger.Logger;
import com.info.model.Attachment;
import com.info.model.ChatModel;
import com.info.model.SocialChatStocksDetails;
import com.info.sendbird.utils.DateUtils;
import com.sendbird.android.FileMessage;

import okhttp3.ResponseBody;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatReplyActivity extends ProfileImageAbstractActivity {

    private static final String TAG = "ChatRepltActivity";
    private static final int INTENT_REQUEST_CHOOSE_IMAGE = 300;
    private static final int INTENT_REQUEST_CHOOSE_VIDEO = 400;
    private static final int INTENT_REQUEST_CHOOSE_DOC = 500;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    public static final int PERMISSION_STORAGE = 100;
    private RecyclerView recyclerViewReply;
    private ImageView attachmentIv;
    private EditText editTextMsg;
    private TextView sendTv, loadingTv, tvCountReplies;
    private ChatModel chatModel;
    private String groupId = "";
    private RelativeLayout rootLayout;
    boolean isWaitingForResponse = false;
    private ProgressBar progressBar;
    private List<ChatModel> mMessageList = new ArrayList<>();
    private ChatReplyAdapter mChatReplyAdapter;
    DocumentSnapshot lastVisibleValue;
    boolean moreDataAvailable = false;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout layoutEmpty;
    UploadTask firebaseStorageUploadTask;
    private boolean fromNotificationClick, fromPushNotificationClick;
    private String messageId = "";
    private String messageReferenceId = "";
    private RelativeLayout layoutReplyCount;
    private FirebaseStorage mFirebaseStorage;
    private boolean isBasilPrivateRoom = false;
    private String currentMessgaeId = "";
    private String postedByUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_reply);

        // Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Replies");
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0f);
        findViewById(R.id.backAction).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.menuTab).setVisibility(View.GONE);
        findViewById(R.id.tvRoom).setVisibility(View.GONE);
        // getIntents
        if (getIntent() != null) {
            chatModel = getIntent().getParcelableExtra("chatModel");
            groupId = getIntent().getStringExtra(Constants.GROUP_ID);
            fromNotificationClick = getIntent().getBooleanExtra(Constants.FROM_NOTIFICATION_CLICK, false);
            fromPushNotificationClick = getIntent().getBooleanExtra(Constants.FROM_PUSH_NOTIFICATION_CLICK, false);

            messageId = getIntent().getStringExtra(Constants.MessageId);
            messageReferenceId = getIntent().getStringExtra(Constants.MESSAGE_REFERENCE_ID);
            postedByUserName = getIntent().getStringExtra(Constants.POSTED_BY_USERNAME);
            isBasilPrivateRoom = getIntent().getBooleanExtra("isBasilPrivateRoom", false);
            if (postedByUserName == null) {
                postedByUserName = "";
            }
            if (messageReferenceId == null) {
                messageReferenceId = "";
            }
        }

        // Find Id's
        recyclerViewReply = findViewById(R.id.recyclerViewReply);
        progressBar = findViewById(R.id.progress);
        attachmentIv = findViewById(R.id.attachmentIv);
        editTextMsg = findViewById(R.id.editTextMsg);
        sendTv = findViewById(R.id.textViewSend);
        loadingTv = findViewById(R.id.loading_tv);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvCountReplies = findViewById(R.id.tvCountReplies);
        layoutReplyCount = findViewById(R.id.layoutReplyCount);


        // Click Listener's
        sendTv.setOnClickListener(v -> {
            if (!isWaitingForResponse) {
                String message = editTextMsg.getText().toString().trim();
                if (message != null && !message.isEmpty()) {
                    sendMessageToFireStore(message, Constants.TextMessageType, "");
                    isWaitingForResponse = true;
                }
            }
        });

        attachmentIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFirstFiltersDialog();
            }
        });

        editTextMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    sendTv.setTextColor(getResources().getColor(R.color.blue_color));
                else
                    sendTv.setTextColor(getResources().getColor(R.color.tw__medium_gray));
            }
        });

        setUpRecyclerView();

        if (groupId != null && !groupId.equalsIgnoreCase("") &&
                (chatModel != null || (messageReferenceId != null && !messageReferenceId.equalsIgnoreCase(""))))
            addMessageUpdateChangeListener();

    }

    private void showFirstFiltersDialog() {
        ChatReplyActivity activity = this;
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
        ChatReplyActivity activity = this;
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

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            takePicture();
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

    private void addMessageUpdateChangeListener() {

        progressBar.setVisibility(View.VISIBLE);

        if (fromPushNotificationClick) {

            TradwyseApplication.getFirestoreDb()
                    .collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                    .document(groupId)
                    .collection("messages")
                    .document(!messageReferenceId.equalsIgnoreCase("") ? messageReferenceId : chatModel.getDocumentReferenceId())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().get(Constants.MessageId) != null) {

                        getDataFromFireStore();
                    } else {

                        Common.showMessageWithRedirectToDashBoard(ChatReplyActivity.this, getString(R.string.this_post_has_been_deleted), getString(R.string.messageAlert));
                    }
                }
            });
        } else {
            getDataFromFireStore();
        }

    }

    private void getDataFromFireStore() {
        TradwyseApplication.getFirestoreDb()
                .collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                .document(groupId)
                .collection("messages")
                .document(!messageReferenceId.equalsIgnoreCase("") ? messageReferenceId : chatModel.getDocumentReferenceId())
                .collection("replies")
                .orderBy(Constants.MessageCreatedDate, Query.Direction.DESCENDING)
                .limit(30)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        progressBar.setVisibility(View.GONE);
                        List<DocumentChange> documentChangeList = value.getDocumentChanges();
                        //if (!documentChangeList.isEmpty()) {

                        if (!documentChangeList.isEmpty()) {

                            layoutEmpty.setVisibility(View.GONE);
                            layoutReplyCount.setVisibility(View.VISIBLE);

                            if (fromNotificationClick) {
                                fromNotificationClick = false;
                                boolean isMsgExist = false;
                                for (int i = 0; i < documentChangeList.size(); i++) {
                                    if (!documentChangeList.get(i).getDocument().get(Constants.MessageId).equals(messageId)) {
                                        isMsgExist = false;
                                    } else {
                                        isMsgExist = true;
                                        break;
                                    }
                                }
                                if (!isMsgExist)
                                    Common.showMessage(ChatReplyActivity.this, getResources().getString(R.string.this_message_has_been_deleted), getResources().getString(R.string.messageAlert));
                            }

                        } else {
                            moreDataAvailable = false;
                            layoutEmpty.setVisibility(View.VISIBLE);
                            layoutReplyCount.setVisibility(View.GONE);
                            if (fromNotificationClick) {
                                fromNotificationClick = false;
                                Common.showMessage(ChatReplyActivity.this, getResources().getString(R.string.this_message_has_been_deleted), getResources().getString(R.string.messageAlert));
                                //Common.showMessageWithFinishActivity(ChatReplyActivity.this, getResources().getString(R.string.this_post_has_been_deleted), getResources().getString(R.string.messageAlert));
                            }

                        }

                        try {
                            lastVisibleValue = value.getDocuments().get(value.size() - 1);
                        } catch (Exception e) {
                            layoutEmpty.setVisibility(View.VISIBLE);
                            layoutReplyCount.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                        moreDataAvailable = true;

                       /* } else {
                            moreDataAvailable = false;
                            layoutEmpty.setVisibility(View.VISIBLE);
                            layoutReplyCount.setVisibility(View.GONE);
                        }*/

                        Logger.debug("CHAT_REPLY", "Changed document size is:- " + documentChangeList.size());
                        processChatDataAndDisplay(value, documentChangeList);
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
                List<Map<String, Object>> flagMapList = (List<Map<String, Object>>) document.get(Constants.Flag);

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
                if (/*userName == null || userName.isEmpty() || */userId == null || userId.isEmpty() || messageType == null || messageType.isEmpty()) {

                } else {
                    //if(messageType.equalsIgnoreCase("text") || messageType.equalsIgnoreCase("photo"))
                    {
                        ChatModel chatModel = new ChatModel(documentReferenceId, createdDate, message, messageId, messageType, profileImageUrl, userId, userName, caption, messageSource, isFlag, messageFlagedUserName, messageSocialReferenceId, flagMapList);
                        Log.d("CHAT_REPLY", document.getId() + " => \n"
                                + "createdDate:- " + createdDate + "\n"
                                + "message:- " + message + "\n"
                                + "messageId:- " + messageId + "\n"
                                + "messageType:- " + messageType + "\n"
                                + "profileImageUrl:- " + profileImageUrl + "\n"
                                + "userId:- " + userId + "\n"
                                + "userName:- " + userName + "\n"
                                + "caption:- " + caption + "\n");
                        mMessageList.add(chatModel);
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
                boolean isAlreadyAListAvailable = mChatReplyAdapter.getItemCount() > 0;
                mChatReplyAdapter.setMessageList(mMessageList);
                if (!isAlreadyAListAvailable)
                    recyclerViewReply.scrollToPosition(0);
                Log.w("CHAT_REPLY", "Update Event Called:- " + value.getDocumentChanges().toString());

            } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                QueryDocumentSnapshot document = documentChange.getDocument();
                String documentReferenceId = document.getReference().getId();
                for (int i = 0; i < mMessageList.size(); i++) {
                    if (mMessageList.get(i).getDocumentReferenceId().equals(documentReferenceId)) {
                        mMessageList.remove(i);
                        mChatReplyAdapter.setMessageList(mMessageList);
                    }
                }
            }

        }

        // Set total reply count.
        if (mMessageList.size() == 1) {
            tvCountReplies.setText("Replies " + mMessageList.size());
        } else {
            tvCountReplies.setText("Replies " + mMessageList.size());
        }

    }

    private void setUpRecyclerView() {
        mChatReplyAdapter = new ChatReplyAdapter(ChatReplyActivity.this);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        recyclerViewReply.setLayoutManager(mLayoutManager);
        recyclerViewReply.setAdapter(mChatReplyAdapter);
        mChatReplyAdapter.SetOnListClickListener(new ChatReplyAdapter.OnListClickListener() {
            @Override
            public void onMessageLongClick(ChatModel messageModel, int position) {

                if (messageModel.getUserName().equalsIgnoreCase(tradWyseSession.getUserName()))
                    showMessageOptionsDialog(messageModel, position);
            }
        });

        // Load more messages when user reaches the top of the current message list.
        recyclerViewReply.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (mLayoutManager.findLastVisibleItemPosition() == mChatReplyAdapter.getItemCount() - 1) {
                    if (moreDataAvailable && loadingTv.getVisibility() == View.GONE) {
                        if (mMessageList.size() == 0)
                        {
                            recyclerView.stopScroll();
                        }
                        else{
                            loadMoreMessage();
                        }

                    } else {
                        //Toast.makeText(ChatActivity.this, "End of list.", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.v("CHAT_REPLY", "onScrollStateChanged");
            }
        });

        recyclerViewReply.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerViewReply.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                            if (pos <= 1)
                                recyclerViewReply.smoothScrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });
    }

    private void showMessageOptionsDialog(final ChatModel message, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatReplyActivity.this, R.style.CustomAlertDialog);
        builder.setMessage(getResources().getString(R.string.deleteAlertMessage));
        builder.setTitle(getResources().getString(R.string.deleteAlert));
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
                .document(groupId)
                .collection("messages")
                .document(!messageReferenceId.equalsIgnoreCase("") ? messageReferenceId : chatModel.getDocumentReferenceId())
                .collection("replies")
                .document(message.getDocumentReferenceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(ChatReplyActivity.this, "Message deleted successfully").show();
                    }
                });
    }

    private void loadMoreMessage() {
        loadingTv.setVisibility(View.VISIBLE);
        //Toast.makeText(this, "Loading more messages....", Toast.LENGTH_LONG).show();
        Query next = TradwyseApplication.getFirestoreDb()
                .collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                .document(groupId)
                .collection("messages")
                .document(!messageReferenceId.equalsIgnoreCase("") ? messageReferenceId : chatModel.getDocumentReferenceId())
                .collection("replies")
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
                        Logger.debug("CHAT_REPLY", "Changed document size is:- " + documentChangeList.size());
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

    private void sendMessageToFireStore(String message, String type, String caption) {

        currentMessgaeId = tradWyseSession.getUserId() + "_" + System.currentTimeMillis();
        Map<String, Object> messageData = new HashMap<>();
        messageData.put(Constants.MessageCreatedDate, System.currentTimeMillis());
        messageData.put(Constants.MessageText, message);
        messageData.put(Constants.MessageId, currentMessgaeId);
        messageData.put(Constants.MessageType, type);

        if (ImageMessageType.equals(type)) {
            messageData.put(Constants.MessageCaption, caption);
        }

        messageData.put(Constants.MessageProfileImageUrl, Common.getProfileImageUrl(tradWyseSession.getUserId()));
        messageData.put(Constants.MessageUserId, tradWyseSession.getUserId());
        messageData.put(Constants.MessageSource, Constants.deviceType);
        messageData.put(Constants.MessageUserName, tradWyseSession.getUserName());

        TradwyseApplication.getFirestoreDb().collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP).document(groupId)
                .collection("messages")
                .document(!messageReferenceId.equalsIgnoreCase("") ? messageReferenceId : chatModel.getDocumentReferenceId())
                .collection("replies")
                .add(messageData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        editTextMsg.setText("");
                        isWaitingForResponse = false;
                        Log.d("CHAT_REPLY", "DocumentSnapshot written with ID: " + documentReference.getId());

                        if (!tradWyseSession.getUserName().equalsIgnoreCase(!postedByUserName.equalsIgnoreCase("") ? postedByUserName : chatModel.getUserName())) {
                            sendFCMNotificationForSocialChatWithUserId(currentMessgaeId, Constants.deviceType, tradWyseSession.getUserName(), groupId, !messageReferenceId.equalsIgnoreCase("") ? messageReferenceId : chatModel.getDocumentReferenceId(),
                                    isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP, !postedByUserName.equalsIgnoreCase("") ? postedByUserName : chatModel.getUserName());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isWaitingForResponse = false;
                        Log.w("CHAT_REPLY", "Error adding document", e);
                    }
                });

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
                showFullScreenDialog(bitmap, data.getData(), thumbnailSizes, IMAGE);

            } catch (IOException e) {
                e.printStackTrace();
            }
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

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showFullScreenDialog(Bitmap bitmap, Uri uri, List<FileMessage.ThumbnailSize> thumbnailSizes, String type) {
        new ImageCaptionDialog(ChatReplyActivity.this, bitmap, uri, editTextMsg.getText().toString().trim()) {
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

    private void UploadImageFirebaseStorage(Uri uri, List<FileMessage.ThumbnailSize> thumbnailSizes, String captionText) {
        showProgressDialog("Uploading Image...");
       // Toasty.info(ChatReplyActivity.this, "Uploading Image...", Toast.LENGTH_LONG).show();
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
            Logger.debug("CHAT_REPLY", "Uploading image name will be " + name);
            if (IS_PRODUCTION == true) {
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
                    Logger.debug("CHAT_REPLY", "Uploading Image progress " + progress);
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

                        Toasty.success(ChatReplyActivity.this, "Image uploaded successfully").show();
                        Logger.debug("CHAT_REPLY", "FirebaseStorage Storage Image Uploaded successfully " + downloadUri);

                    } else if (type.equalsIgnoreCase(VIDEO)) {

                        sendMessageToFireStore(downloadUri.toString(), VideoMessageType, captionText);
                        Log.d("captionText", "" + captionText);

                        Toasty.success(ChatReplyActivity.this, "Video uploaded successfully").show();
                        Logger.debug("CHAT_REPLY", "FirebaseStorage Storage Video Uploaded successfully " + downloadUri);
                    } else if (type.equalsIgnoreCase(DOC)) {

                        sendMessageToFireStore(downloadUri.toString(), DocMessageType, captionText);
                        Log.d("captionText", "" + captionText);

                        Toasty.success(ChatReplyActivity.this, "Doc uploaded successfully").show();
                        Logger.debug("CHAT_REPLY", "FirebaseStorage Storage Doc Uploaded successfully " + downloadUri);
                    }
                } else {
                    Toasty.error(ChatReplyActivity.this, "Can't upload image at this time please try again later.").show();
                }
            }
        });
        //dismissProgressDialog();
    }

    private void uploadVideoFirebaseStorage(Uri videoUri, String captionText, Attachment attachment) {
        if (videoUri != null) {
            //   Toasty.info(ChatReplyActivity.this, "Uploading Video...", Toast.LENGTH_LONG).show();
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
            if (IS_PRODUCTION == true)
            {
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
                        dismissProgressDialog();
                        //Toast.makeText(ChatReplyActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ChatReplyActivity.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDocFirebaseStorage(Uri docUri, String captionText) {
        if (docUri != null) {
           showProgressDialog("Uploading Document...");
           // Toasty.info(ChatReplyActivity.this, "Uploading Document...", Toast.LENGTH_LONG).show();
            Hashtable<String, Object> info = FileUtils.getFileInfo(this, docUri);
            if (info == null || info.isEmpty()) {
                Toast.makeText(this, "Extracting file information failed.", Toast.LENGTH_LONG).show();
              //  dismissProgressDialog();
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
                        dismissProgressDialog();
                        //Toast.makeText(ChatReplyActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ChatReplyActivity.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadThumbnail(final String filePath) {
      //  Toast.makeText(this, "just a moment..", Toast.LENGTH_LONG).show();
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

    public void sendFCMNotificationForSocialChatWithUserId(String msgId, String deviceType, String userName, String groupId, String documentReferenceId, String groupName, String postedByUsername) {

        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<ResponseBody> call = apiInterface.sendFCMNotificationForSocialChatWithUserId(msgId, deviceType, userName, groupId, documentReferenceId, groupName, postedByUsername);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    //Toast.makeText(ChatReplyActivity.this, "Notification Sent Successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "News onFailure");
            }
        });
    }

    public void onBackPressed() {
        if (fromPushNotificationClick) {
            startActivity(new Intent(this, DashBoardActivity.class));
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }
}