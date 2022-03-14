package com.info.tradewyse;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.info.adapter.CommentAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.DateTimeHelperElapsed;
import com.info.commons.EndlessRecyclerViewScrollListener;
import com.info.commons.FIleHelper;
import com.info.commons.FlagReasonOptionUtility;
import com.info.commons.LikeHandler;
import com.info.commons.PinHandler;
import com.info.commons.StringHelper;
import com.info.commons.TipHideHandler;
import com.info.commons.TradWyseSession;
import com.info.dao.DataRepository;
import com.info.interfaces.FlagOptionSelectListener;
import com.info.interfaces.HideTipResponseListener;
import com.info.interfaces.ImageDownloadResponse;
import com.info.interfaces.LikeResponseListener;
import com.info.interfaces.PinResponseListener;
import com.info.interfaces.CommentCountChangedInterface;
import com.info.logger.Logger;
import com.info.model.AdBannerModel;
import com.info.model.Comment;
import com.info.model.FlagResponse;
import com.info.model.FooterModel;
import com.info.model.NotificationCountModel;
import com.info.model.NotificationModel;
import com.info.model.Quote;
import com.info.model.StockPrice;
import com.info.model.TipClass;
import com.info.model.TipResponse;
import com.info.model.Tips;
import com.info.model.User;
import com.info.tradewyse.databinding.ActivityTipDetailBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TipDetailActivity extends BaseActivity implements View.OnClickListener, CommentCountChangedInterface {
    Tips tips = null;
    Context context;
    int like_count;
    int pin_count;
    int comment_count;
    boolean like_status;
    boolean pin_status;
    int tipPosition;
    Intent intent = new Intent();
    EndlessRecyclerViewScrollListener scrollListener;
    private ArrayList<Comment> commentList = new ArrayList<>();
    private CommentAdapter mAdapter;
    private RelativeLayout rootLayout1;
    private RelativeLayout rootLayout;
    private RelativeLayout profileLayout;
    private SimpleDraweeView imgProfile;
    private TextView txtName;
    private TextView txtTimeAgo;
    private LinearLayout rl1;
    private TextView txtStockName;
    private TextView txtStockCompany;
    private TextView txtStockPrice;
    private TextView txtBuySellText;
    private TextView txtComment;
    private LinearLayout rlPoint;
    private TextView labelEntry;
    private TextView txtEntryPoint, txtCreateTipPrice, txtCurrentPrice, txtChangeValue;
    private TextView labelExit;
    private TextView txtExitPoint;
    private TextView labelStop;
    private TextView txtStopPoint;
    FrameLayout imageFrame;
    private SimpleDraweeView imgGraph;
    private TextView txtNoOfComments;
    private RecyclerView recCommentsList;
    private EditText edtComment;
    private TextView txtSend, txtLikeCount, txtPin;
    private ProgressBar imageProgress;
    private View focusView;
    private int PAGE_LIMIT = 10;
    private int start = 0;
    private ImageView imgLike;
    private ImageView imgMore;
    private ImageView imgPin;
    private Uri imagePathUri = null;
    private ImageView imgComment;
    private ProgressBar progress, progressBar;
    private AppCompatImageButton flagIcon;
    private TextView otherAction;
    private String selectedScreen = "";
    ActivityTipDetailBinding tipDetailBinding;
    private RelativeLayout layoutRoot, layoutRootTop;
    private LinearLayout bottomLinearLayout;
    private LinearLayout layoutBottom;
    private boolean isFromNotification = false;
    private boolean isFromAfterMacd = false;
    private TipClass tipClass;
    boolean fromComment;
    boolean hide_status;

    public static void startActivity(Context context, TipResponse tips, boolean fromComment, String selectedScreen) {
        Intent starter = new Intent(context, TipDetailActivity.class);
        starter.putExtra("tips", tips.getTip());
        starter.putExtra("comment_count", tips.getCommentCount());
        starter.putExtra("like_count", tips.getLikeCount());
        starter.putExtra("pin_count", tips.getPinCount());
        starter.putExtra("hide_status", tips.isUserHideStatus());
        starter.putExtra("like_status", tips.isUserLikeStatus());
        starter.putExtra("pin_status", tips.isUserPinStatus());
        starter.putExtra("fromComment", fromComment);
        starter.putExtra("selectedScreen", selectedScreen);
        context.startActivity(starter);
    }

    public static void startActivityResult(Context context, TipResponse tips, boolean fromComment) {
        Intent starter = new Intent(context, TipDetailActivity.class);
        starter.putExtra("tips", tips.getTip());
        starter.putExtra("comment_count", tips.getCommentCount());
        starter.putExtra("like_count", tips.getLikeCount());
        starter.putExtra("pin_count", tips.getPinCount());
        starter.putExtra("hide_status", tips.isUserHideStatus());
        starter.putExtra("like_status", tips.isUserLikeStatus());
        starter.putExtra("pin_status", tips.isUserPinStatus());
        starter.putExtra("fromComment", fromComment);
        ((Activity) context).startActivityForResult(starter, Constants.TIP_DETAIL_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_detail);
        tips = getIntent().getExtras().getParcelable("tips");
        String tipId = getIntent().getStringExtra("tipId");
        String notificationId = getIntent().getStringExtra("notificationId");
        isFromNotification = getIntent().getBooleanExtra("fromNotificationClick", false);
        if (getIntent().getStringExtra("selectedScreen") != null)
            selectedScreen = getIntent().getStringExtra("selectedScreen");
        tipDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_tip_detail);
        bottomLinearLayout = findViewById(R.id.bottomView);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Common.BottomTabColorChange(TipDetailActivity.this,bottomLinearLayout);
            }
        }, 500);
                FooterModel footerModel = null;

        if (selectedScreen.equalsIgnoreCase(Constants.HOME)) {
            footerModel = new FooterModel(true, false, false, false, false);
        } else if (selectedScreen.equalsIgnoreCase(Constants.MORE_TAB)) {
            footerModel = new FooterModel(false, false, false, false, true);
        } else if (selectedScreen.equalsIgnoreCase(Constants.NOTIFICATION)) {
            footerModel = new FooterModel(false, false, false, true, false);
        } else if (selectedScreen.equalsIgnoreCase(Constants.SERVICES)) {
            footerModel = new FooterModel(false, false, true, false, false);
        } else {
            footerModel = new FooterModel(false, false, false, false, false);
        }

        tipDetailBinding.setFooterModel(footerModel);
        context = this;
        progress = findViewById(R.id.progress);
        progressBar = findViewById(R.id.progressBar);
        getIds();
        getNotificationCount();

        otherAction.setVisibility(View.GONE);

        comment_count = getIntent().getIntExtra("comment_count", 0);
        like_count = getIntent().getIntExtra("like_count", 0);
        pin_count = getIntent().getIntExtra("pin_count", 0);

        hide_status = getIntent().getBooleanExtra("hide_status", false);
        like_status = getIntent().getBooleanExtra("like_status", false);
        pin_status = getIntent().getBooleanExtra("pin_status", false);
        tipPosition = getIntent().getIntExtra("TipPosition", 0);
        isFromAfterMacd = getIntent().getBooleanExtra("isFromAfterMacd", false);
        fromComment = getIntent().getBooleanExtra("fromComment", false);


        if (tips != null) {
            tipsDataFoundMoveNext();
        } else if (tipId != null && !tipId.isEmpty()) {
            callGetTipDetailByTipId(tipId);
        } else {
            Logger.debug("TipDetailActivity", "tipId not found closing the activity");
            this.finish();
        }
        if (isFromNotification) {
            markNotificationRead(notificationId, isFromNotification);
        } else if (notificationId != null && !notificationId.isEmpty())
            markNotificationRead(notificationId, isFromNotification);

        setToolBarAddTip(getString(R.string.tipDetails));

        imgGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(context, String.valueOf(imagePathUri), Constants.ImageMessageType);

            }
        });


        // For detect keyboard is open or not.
        layoutRootTop.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        layoutRootTop.getWindowVisibleDisplayFrame(r);
                        int screenHeight = layoutRootTop.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        Log.d("TAG", "keypadHeight = " + keypadHeight);

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            layoutBottom.setVisibility(View.GONE);
                        } else {
                            // keyboard is closed
                            layoutBottom.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private void tipsDataFoundMoveNext() {
        initialize();
        setPageData();
        callGetStockDetail(tips.getStockName().trim(), false);
        if (!StringHelper.isEmpty(tips.getImageDetails())) {
            downloadImage();
        } else {
            imageFrame.setVisibility(View.GONE);
            imgGraph.setVisibility(View.GONE);
        }
    }

    public void initialize() {

        txtLikeCount.setText(String.valueOf(like_count));
        txtPin.setText(String.valueOf(pin_count));
        txtNoOfComments.setText(String.valueOf(comment_count));

        if (like_status) {
            imgLike.setImageResource(R.drawable.ic_like_fill);
        } else {
            imgLike.setImageResource(R.drawable.ic_like);
        }
        LikeHandler likeHandler = new LikeHandler(context, imgLike, tips.getId());
        LikeResponseListener likeResponseListener = likeResponse -> {
            if (likeResponse) {
                like_count++;
                txtLikeCount.setText(String.valueOf(like_count));
                imgLike.setImageResource(R.drawable.ic_like_fill);
            } else {
                if (like_count > 0) {
                    like_count--;
                    txtLikeCount.setText(String.valueOf(like_count));
                }
                imgLike.setImageResource(R.drawable.ic_like);
            }
            like_status = likeResponse;
            intent.putExtra("TipPosition", tipPosition);
            intent.putExtra("like_status", like_status);
            intent.putExtra("like_count", like_count);
            intent.putExtra("isFromAfterMacd", isFromAfterMacd);
            setResult(RESULT_OK, intent); // You can also send result without any data using setResult(int resultCode)

        };

        imgLike.setOnClickListener(view -> {
            if (like_status) {
                likeHandler.unLikeTip(likeResponseListener);
            } else {
                likeHandler.likeTip(likeResponseListener);
            }
        });

        if (pin_status) {
            imgPin.setImageResource(R.drawable.ic_pin_save);
        } else {
            imgPin.setImageResource(R.drawable.ic_tag);
        }
        // pin response handling.
        PinHandler pinHandler = new PinHandler(context, imgPin, tips.getId());
        PinResponseListener pinResponseListener = pinResponse -> {
            if (pinResponse) {
                pin_count++;
                txtPin.setText(String.valueOf(pin_count));
                imgPin.setImageResource(R.drawable.ic_pin_save);
            } else {
                if (pin_count > 0) {
                    pin_count--;
                    txtPin.setText(String.valueOf(pin_count));
                }
                imgPin.setImageResource(R.drawable.ic_tag);
            }
            pin_status = pinResponse;

            intent.putExtra("TipPosition", tipPosition);
            intent.putExtra("pin_status", pin_status);
            intent.putExtra("pin_count", pin_count);
            intent.putExtra("isFromAfterMacd", isFromAfterMacd);
            setResult(RESULT_OK, intent);
        };
        // Pin button click event.
        imgPin.setOnClickListener(view -> {
            //handle pin click response.
            if (pin_status) {
                pinHandler.unPinTip(pinResponseListener);
            } else {
                pinHandler.pinTip(pinResponseListener);
            }
        });

        imgMore.setOnClickListener(view -> {
            showMoreDialog(tips.getAppUser().getUserName(), tips, tips.getAppUser().getId(), rootLayout);
        });

        txtSend.setOnClickListener(this);

        if (fromComment) {
            openSoftKeyboard(300);
        }
        imgComment.setOnClickListener(view -> {
            openSoftKeyboard(200);
        });

        setFlagIconClick(flagIcon, tips);

        User user = tips.getAppUser();
        Common.setProfileImage(imgProfile, user.getId());
        if (TradWyseSession.getSharedInstance(context).getUserId().equalsIgnoreCase(tips.getUserId())) {
            flagIcon.setVisibility(View.GONE);
        } else {
            flagIcon.setVisibility(View.VISIBLE);
        }
        // gone for always for now.
        flagIcon.setVisibility(View.GONE);
        txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToProfile();
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToProfile();
            }
        });
    }

    private void getIds() {
        otherAction = findViewById(R.id.otherAction);
        imageProgress = findViewById(R.id.imageProgress);
        rootLayout1 = findViewById(R.id.rootLayout1);
        rootLayout = findViewById(R.id.rootLayout);
        profileLayout = findViewById(R.id.profileLayout);
        imgProfile = findViewById(R.id.imgProfile);
        txtName = findViewById(R.id.txtName);
        txtTimeAgo = findViewById(R.id.txtTimeAgo);
        rl1 = findViewById(R.id.rl1);
        txtStockName = findViewById(R.id.txtStockName);
        txtStockCompany = findViewById(R.id.txtStockCompany);
        // txtStockPrice = findViewById(R.id.txtStockPrice);
        txtBuySellText = findViewById(R.id.txtBuySellText);
        txtComment = findViewById(R.id.txtComment);
        rlPoint = findViewById(R.id.rlPoint);
        labelEntry = findViewById(R.id.labelEntry);
        txtEntryPoint = findViewById(R.id.txtEntryPoint);
        labelExit = findViewById(R.id.labelExit);
        txtCreateTipPrice = findViewById(R.id.txtCreateTipPrice);
        txtCurrentPrice = findViewById(R.id.txtCurrentPrice);
        txtChangeValue = findViewById(R.id.txtChangeValue);
        txtLikeCount = findViewById(R.id.txtLikeCount);
        txtPin = findViewById(R.id.txtPin);
        txtExitPoint = findViewById(R.id.txtExitPoint);
        labelStop = findViewById(R.id.labelStop);
        txtStopPoint = findViewById(R.id.txtStopPoint);
        imgGraph = findViewById(R.id.imgGraph);
        imageFrame = findViewById(R.id.imageFrame);
        txtNoOfComments = findViewById(R.id.txtNoOfComments);
        recCommentsList = findViewById(R.id.recCommentsList);
        imgLike = findViewById(R.id.imgLike);
        imgMore = findViewById(R.id.imgMore);
        imgPin = findViewById(R.id.imgPin);
        imgComment = findViewById(R.id.imgComment);
        txtSend = findViewById(R.id.txtSend);
        edtComment = findViewById(R.id.edtComment);
        flagIcon = findViewById(R.id.flagIcon);
        focusView = findViewById(R.id.view);
        layoutRoot = findViewById(R.id.layoutRoot);
        layoutRootTop = findViewById(R.id.layoutRootTop);
        layoutBottom = findViewById(R.id.bottomView);
    }

    void openSoftKeyboard(int delay) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                edtComment.requestFocus();
                edtComment.setFocusableInTouchMode(true);
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(edtComment, InputMethodManager.SHOW_FORCED);
            }
        }, delay);
    }

    public void setFlagIconClick(AppCompatImageButton flagIcon, Tips tips) {
        flagIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog sheet then set flag.
                FlagReasonOptionUtility frUtility = new FlagReasonOptionUtility.Builder((AppCompatActivity) context).build();
                frUtility.showFlagOptionList();
                frUtility.setFlagOptionSelectListener(new FlagOptionSelectListener() {
                    @Override
                    public void startActivityForResult(int selectedOption, int requestCode) {
                        //  Toast.makeText(context,"selectedOption:"+selectedOption,Toast.LENGTH_LONG).show();

                        if (selectedOption == 0) return; //when user click cancel.
                        submitFlagForTip(tips.getId(), TradWyseSession.getSharedInstance(context).getUserId(), true,
                                Common.getFlagOptionById(selectedOption, context), context);

                    }
                });
            }
        });
    }

    public void redirectToProfile() {
        ProfileTabbedActivity.starProfileTabbedtActivityForResult(context, false, tips.getAppUser().getUserName(), tips.getAppUser().getId(), 104, selectedScreen);
    }

    public void setPageData() {
        layoutRoot.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        rootLayout1.setVisibility(View.VISIBLE);
        txtName.setText(getName(tips.getAppUser()));
        txtStockName.setText(tips.getStockName());
        txtTimeAgo.setText(DateTimeHelperElapsed.toString(tips.getCreatedOn(), "MMM dd, hh:mm a") + "  "
                + context.getResources().getString(R.string.TimeZone));
        txtEntryPoint.setText(StringHelper.getValue(Common.formatDouble(tips.getEntryPoint()), "--"));
        txtExitPoint.setText(StringHelper.getValue(Common.formatDouble(tips.getExitpoint()), "--"));
        txtStopPoint.setText(StringHelper.getValue(Common.formatDouble(tips.getStopPoint()), "--"));
        txtCreateTipPrice.setText(StringHelper.getValue(Common.formatDouble(tips.getCreateTipPrice()), "--"));
        txtChangeValue.setText(StringHelper.currencyFormatter(tips.getStockChange()));
        txtCurrentPrice.setText(StringHelper.getAmount(tips.getStockPrice(), "--"));
        txtComment.setText(tips.getComment());

        txtBuySellText.setText(tips.getStockSuggestion());
        String stockSuggestion = "";
        if (tips.getEntryPoint() < tips.getExitpoint()) {
            stockSuggestion = "Buy";
            txtBuySellText.setBackgroundResource(R.drawable.text_backgroud_green);
            txtBuySellText.setTextColor(ContextCompat.getColor(context, R.color.color_text_buy));
        } else if (tips.getEntryPoint() > tips.getExitpoint()) {
            stockSuggestion = "Sell";
            txtBuySellText.setBackgroundResource(R.drawable.text_backgroud_red);
            txtBuySellText.setTextColor(ContextCompat.getColor(context, R.color.color_text_sell));
        } else {
            stockSuggestion = "Avoid";
            txtBuySellText.setBackgroundResource(R.drawable.text_backgroud_black);
            txtBuySellText.setTextColor(ContextCompat.getColor(context, R.color.color_text_avoid));
        }
        if (stockSuggestion.length() >= 4)
            txtBuySellText.setEms(4);
        else if (stockSuggestion.length() == 3)
            txtBuySellText.setEms(3);
        else
            txtBuySellText.setEms(2);

        txtBuySellText.setText(stockSuggestion);

        setStockChange();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                int nextPage = page * PAGE_LIMIT;
                Log.e("nextpage", nextPage + " totalItemsCount" + totalItemsCount);
                getAllTipComment(nextPage);
            }
        };


        getAllTipComment(start);

        recCommentsList.setLayoutManager(mLayoutManager);
        recCommentsList.addOnScrollListener(scrollListener);
        Common.setEntryExitColor(context, tips.getEntryPoint(), tips.getExitpoint(), tips.getStopPoint(), tips.getStockPrice(), tips.getCreateTipPrice(), tips.getStockSuggestion(),
                txtEntryPoint, txtExitPoint, txtStopPoint);

    }

    public void setStockChange() {
        double change = tips.getStockPrice() - tips.getCreateTipPrice();
        if (change > 0) {
            txtChangeValue.setTextColor(context.getResources().getColor(R.color.color_text_buy));
        } else {
            if (change == 0) {
                txtChangeValue.setTextColor(context.getResources().getColor(R.color.color_text_buy));
            } else {
                txtChangeValue.setTextColor(context.getResources().getColor(R.color.color_text_sell));
            }
        }

        // I am checking the current price to update the change value.
        if (tips.getStockPrice() == 0) {
            txtChangeValue.setText("--");
        } else {
            txtChangeValue.setText(StringHelper.currencyFormatter(change));
        }
        //   txtChangeValue.setText(StringHelper.getValue(Common.formatDouble(Math.abs(change)), "--"));
    }

    public String getName(User user) {
        if (user.getfName() == null || user.getfName().isEmpty()) {
            return user.getUserName();
        } else {
            return user.getfName() + " " + user.getlName();
        }
    }

    private void callGetTipDetailByTipId(String tipId) {
        if (!Common.isNetworkAvailable(context)) {
            Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                    context.getResources().getString(R.string.JustLetYouKnow));
            return;
        }
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<TipClass> call = apiInterface.getTipByTipId(tipId);
        progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<TipClass>() {
            @Override
            public void onResponse(Call<TipClass> call, Response<TipClass> response) {
                progress.setVisibility(View.GONE);
                if (response != null) {
                    if (response.body() != null) {
                        tipClass = response.body();
                        tips = tipClass.getTips();
                        comment_count = tipClass.getCommentCount();
                        like_count = tipClass.getLikeCount();
                        pin_count = tipClass.getPinCount();

                        like_status = tipClass.isUserLikeStatus();
                        pin_status = tipClass.isUserPinStatus();
                        hide_status = tipClass.isUserHideStatus();

                        callGetStockDetail(tips.getStockName(), true);
                    } else {
                        Common.showMessageWithFinishActivity(TipDetailActivity.this,
                                getResources().getString(R.string.this_post_has_been_deleted), getResources().getString(R.string.messageAlert));
                    }

                }
            }

            @Override
            public void onFailure(Call<TipClass> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Logger.debug(Constants.ON_FAILURE_TAG, "TipDetailActivity getAllTipComment: onFailure");
            }
        });
    }

    public void getAllTipComment(int nextPage) {
        // check network is available or not.
        if (!Common.isNetworkAvailable(context)) {
            Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                    context.getResources().getString(R.string.JustLetYouKnow));
            return;
        }
        if (start == nextPage) {
            mAdapter = null;
        }
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<List<Comment>> call = apiInterface.getTipsComment(tips.getId(), nextPage, PAGE_LIMIT);
        //showProgressDialog();
        progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                // dismissProgressDialog();
                progress.setVisibility(View.GONE);
                if (response.body() != null) {
                    List<Comment> commentList = response.body();
                    Collections.reverse(commentList);
                    scrollListener.hasMore(commentList.size(), PAGE_LIMIT);
                    if (mAdapter == null || mAdapter.getItemCount() == 0) {
                        mAdapter = new CommentAdapter(commentList, tips, context, TipDetailActivity.this, selectedScreen);
                        recCommentsList.setItemAnimator(new DefaultItemAnimator());
                        recCommentsList.setAdapter(mAdapter);

                    } else {
                        mAdapter.addMoreComment(commentList);
                    }

                    int count = mAdapter.getItemCount();
                    if (count == 1) {
                        txtNoOfComments.setText(count + "");
                    } else {
                        txtNoOfComments.setText(count + "");
                    }
                    for (int i = 0; i < commentList.size(); i++) {
                        User user = commentList.get(i).getAppUser();
                        Common.downloadImage(user.getUserName(), user.getImage(), TipDetailActivity.this, new ImageDownloadResponse() {
                            @Override
                            public void onImageDownload(@NotNull Uri uri) {
                                mAdapter.setUserImage(uri, user.getUserName());
                            }
                        });
                    }
                    mAdapter.sortComment();
                    intent.putExtra("TipPosition", tipPosition);
                    intent.putExtra("comment_count", count);
                    intent.putExtra("isFromAfterMacd", isFromAfterMacd);
                    setResult(RESULT_OK, intent);
                }
                scrollListener.setFirstTime(false);
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                //dismissProgressDialog();
                progress.setVisibility(View.GONE);
                scrollListener.setFirstTime(false);
                Log.d(Constants.ON_FAILURE_TAG, "TipDetailActivity getAllTipComment: onFailure");
            }
        });
    }

    public void addTipComment(String commentDetail, String status, String imageDetail, String userId) {
        // check network is available or not.
        if (!Common.isNetworkAvailable(context)) {
            Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                    context.getResources().getString(R.string.JustLetYouKnow));
            return;
        }
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<Comment> call = apiInterface.addCommentOnTip(tips.getId(), commentDetail, status, imageDetail, userId);
        showProgressDialog();
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                dismissProgressDialog();
                if (response.body() != null) {
                    edtComment.setText("");
                    edtComment.clearFocus();
                    getAllTipComment(start);
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                dismissProgressDialog();
                Log.d(Constants.ON_FAILURE_TAG, "TipDetailActivity addTipComment: onFailure");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtSend:
                String commentDetail = edtComment.getText().toString();
                if (!StringHelper.isEmpty(commentDetail)) {
                    focusView.requestFocus();
                    hideKeyboard();
                    addTipComment(commentDetail, "Pending", "", tips.getUserId());
                } else {
                    Toast.makeText(context, "Please enter comment", Toast.LENGTH_SHORT).show();
                    edtComment.requestFocus();
                }

                break;
        }
    }


    public void callGetStockDetail(final String stockName, boolean shouldMoveToNext) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_URL_TRADEWYSE, this);
        Call<Quote> call = apiInterface.getStockDetailData(stockName, "pk_ce90f5bb6d66478991993248d4c1f355");
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.body() != null) {
                    Quote quote = response.body();
                    double newStockPrice = Common.formatDouble2(quote.getLatestPrice());
                    if (shouldMoveToNext) {
                        tips.setStockPrice(newStockPrice);
                        tips.setStockChange(quote.getChange());
                        tips.setCompanyName(quote.getCompanyName());
                        tipsDataFoundMoveNext();
                    } else {
                        txtStockCompany.setText(quote.getCompanyName());
                        txtCurrentPrice.setText("$ " + Common.formatDouble(quote.getLatestPrice()));
                        tips.setStockPrice(quote.getLatestPrice());
                        setStockChange();
                    }
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                Log.d("Detail", "onFailure: ");
                Log.d(Constants.ON_FAILURE_TAG, "TipDetailActivity callGetStockDetail: onFailure");
            }
        });
    }

    public void downloadImage() {
        imageProgress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<ResponseBody> call = apiInterface.getTipsImage(tips.getImageDetails());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        new AsyncTask<Void, Void, Uri>() {
                            @Override
                            protected Uri doInBackground(Void... voids) {
                                File file = FIleHelper.writeResponseBodyToDisk(response.body(), context, tips.getImageDetails(), "gif");
                                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                                imagePathUri = uri;
                                return uri;
                            }

                            @Override
                            protected void onPostExecute(Uri uri) {
                                super.onPostExecute(uri);
                                if (uri != null) {
                                    imageFrame.setVisibility(View.VISIBLE);
                                    imageProgress.setVisibility(View.GONE);
                                    imgGraph.setVisibility(View.VISIBLE);
                                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                                            .setUri(uri)
                                            .setAutoPlayAnimations(true)
                                            .setControllerListener(new BaseControllerListener<ImageInfo>() {
                                                @Override
                                                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                                                    // imgGraph.setAspectRatio(imageInfo.getHeight()/imageInfo.getWidth());
                                                }
                                            })
                                            .build();
                                    imgGraph.setController(controller);

                                }
                            }
                        }.execute();
                    }
                } else {
                    imageProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                imageProgress.setVisibility(View.GONE);
                Log.d(Constants.ON_FAILURE_TAG, "TipDetailActivity downloadImage: onFailure");
            }
        });
    }

    public void submitFlagForTip(String tipId, String userId, boolean isFlag, String flagReason, Context context) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.style_progress_dialog);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Call<FlagResponse> call = apiInterface.addFlagForTip(tipId, userId, isFlag, flagReason);
        call.enqueue(new Callback<FlagResponse>() {
            @Override
            public void onResponse(Call<FlagResponse> call, Response<FlagResponse> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(String.valueOf(true))) {
                        Common.showMessage(context, context.getResources().getString(R.string.flag_success), "Success");
                    } else {
                        Common.showMessage(context, context.getResources().getString(R.string.flag_already_submitted), "Error");
                    }
                } else {
                    Toast.makeText(context, "Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FlagResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed,Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                Log.d(Constants.ON_FAILURE_TAG, "TipDetailActivity submitFlagForTip: onFailure");
            }

        });
    }

    private void showMoreDialog(String username, Tips tips, String userId, View viewForSS) {
        try {
            BottomSheetDialog moreOptionsDialog = new BottomSheetDialog(context);
            View sheetView = ((Activity) context).getLayoutInflater().inflate(R.layout.tip_more_options, null);
            TextView txtVisitProfile = sheetView.findViewById(R.id.txtVisitProfile);
            TextView txtTweetTip = sheetView.findViewById(R.id.txtTweetTip);
            TextView txtRemoveTip = sheetView.findViewById(R.id.txtRemoveTip);
            TextView txtCancel = sheetView.findViewById(R.id.optionCancel);
            moreOptionsDialog.setContentView(sheetView);
            Resources res = context.getResources();
            String text = String.format(res.getString(R.string.VisitProfile), username);
            txtVisitProfile.setText(text);
            txtVisitProfile.setOnClickListener(view -> {
                ProfileTabbedActivity.starProfileTabbedtActivity(context, false, username, userId, selectedScreen);
                moreOptionsDialog.cancel();
            });
            txtTweetTip.setOnClickListener((view) -> {
                moreOptionsDialog.cancel();
                //requestStoragePermission(username, tips, context);
                Bitmap image = Common.screenShot(viewForSS);
                Common.prepareShareDeepLinkData(username, tips, context, image);
            });
            txtRemoveTip.setOnClickListener(view -> {
                showConfirmDialog(context, tipPosition, tips);
                moreOptionsDialog.cancel();
            });
            txtCancel.setOnClickListener(view -> moreOptionsDialog.cancel());
            moreOptionsDialog.show();

        } catch (Exception e) {
        }
    }

    public void deleteTipConfirmed(int position, Tips tipResponse) {
        // TipHideHandler handler = new TipHideHandler();
        TipHideHandler tipHideHandler = new TipHideHandler(context, tipResponse.getId());
        HideTipResponseListener hideTipResponseListener = htResponse -> {
            if (htResponse) {
                //if delete tip response true then set result to intent and finish this activity.
                //and update the list after going to dashboard.

                /*intent.putExtra("TipDeleted", "true");
                intent.putExtra("TipPosition", tipPosition);
                intent.putExtra("isFromAfterMacd", isFromAfterMacd);
                setResult(RESULT_OK, intent); // You can also send result without any data using setResult(int resultCode)
                finish();*/

                startActivity(new Intent(TipDetailActivity.this, DashBoardActivity.class));
            } else {
                Log.d("Item Deleted", "Item Deleted: ");
            }
        };

        tipHideHandler.hideTip(hideTipResponseListener);
    }

    public void showConfirmDialog(Context context, int position, Tips response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.remove_tip_confirm_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        TextView cancelBtn = view.findViewById(R.id.cancel_btn);
        TextView okBtn = view.findViewById(R.id.ok_btn);
        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    deleteTipConfirmed(position, response);
                    alertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPress(View v) {
        super.onBackPress(v);
        //Toast.makeText(context,"onback press",Toast.LENGTH_LONG).show();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TipDetailActivity.this, R.style.style_progress_dialog);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public void homeTabClicked(View v) {
        DashBoardActivity.CallDashboardActivity(this, false);
        finish();
    }

    public void chatTabClicked(View v) {
        ChatActivity.starChatActivity(this, false);
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

    private void markNotificationRead(String notificationModel, boolean isFromNotification) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        Call<List<NotificationModel>> call = null;
        if (isFromNotification) {
            call = apiInterface.createNotificationAsReadByNotificationId2(notificationModel, "true");
        } else {
            call = apiInterface.createNotificationAsReadByNotificationId(notificationModel);
        }
        call.enqueue(new Callback<List<NotificationModel>>() {
            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        response.body().get(0).setRead(true);
                        getNotificationCount();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "TipDetailActivity", t.fillInStackTrace());
            }
        });
    }

    @Override
    public void commentCountChange(int commentCount) {
        txtNoOfComments.setText(String.valueOf(commentCount));
        intent.putExtra("TipPosition", tipPosition);
        intent.putExtra("comment_count", commentCount);
        intent.putExtra("isFromAfterMacd", isFromAfterMacd);
        setResult(RESULT_OK, intent);
    }


}

