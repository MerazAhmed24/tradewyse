package com.info.tradewyse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.info.adapter.FollowingListAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.EndlessRecyclerViewScrollListener;
import com.info.commons.TradWyseSession;
import com.info.interfaces.ImageDownloadResponse;
import com.info.model.FollowUserModel;
import com.info.model.FollowingFollowersUserCount;
import com.info.model.FooterModel;
import com.info.model.NotificationCountModel;
import com.info.tradewyse.databinding.ActivityFollowersFollowingBinding;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowersFollowingActivity extends AppCompatActivity implements View.OnClickListener, FollowingListAdapter.OnFollowItemClick {

    private TextView tvFollowers;
    private TextView tvFollowing;
    private ProgressBar progressBar;
    private RelativeLayout rlNoDataFound;
    private MaterialTextView txtToolbarTitle;
    private RecyclerView recyclerView;
    LinearLayout bottomlinearLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager lm;

    private HashMap<String, Boolean> loggedInUserFollowMap = new HashMap<String, Boolean>();
    private List<FollowUserModel> loggedInUserFollowList = new ArrayList<>();
    private boolean isFromFollowing = false;
    private String userName;
    String from;
    private static int start = 0;
    private FollowingListAdapter mAdapter;
    private final int PAGE_LIMIT = 150;
    private TradWyseSession tradWyseSession;
    private boolean fromLoggedInProfile = true;
    private String selectedScreen = "";
    ActivityFollowersFollowingBinding followersFollowingBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_following);
        followersFollowingBinding = DataBindingUtil.setContentView(this,R.layout.activity_followers_following);

        if (getIntent().getStringExtra("selectedScreen") != null) {
            selectedScreen = getIntent().getStringExtra("selectedScreen");
        }

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

        followersFollowingBinding.setFooterModel(footerModel);
        getNotificationCount();
        tradWyseSession = TradWyseSession.getSharedInstance(this);
        fromLoggedInProfile = getIntent().getBooleanExtra("fromProfile", false);
        initView();
        initListeners();
        ChangeTestBottomColor();
    }

    @SuppressLint("SetTextI18n")
    public void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvFollowers = findViewById(R.id.tv_followers);
        tvFollowing = findViewById(R.id.tv_following);
        progressBar = findViewById(R.id.progress);
        rlNoDataFound = findViewById(R.id.noDataLayout);
        recyclerView = findViewById(R.id.recycler_view);
        txtToolbarTitle = findViewById(R.id.txt_toolbar_title);
        bottomlinearLayout = findViewById(R.id.bottomView);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);


        lm = new LinearLayoutManager(FollowersFollowingActivity.this);
        mAdapter = new FollowingListAdapter(FollowersFollowingActivity.this, new ArrayList<FollowUserModel>(), this,fromLoggedInProfile);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        lm.scrollToPosition(0);


        scrollListener = new EndlessRecyclerViewScrollListener(lm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                int nextPage = page * PAGE_LIMIT;
                Log.e("nextpage", nextPage + " totalItemsCount" + totalItemsCount);
                getAllFollowUserListByUserName(userName, nextPage, true);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);


        if (getIntent() != null) {
            Intent intent = getIntent();
            userName = intent.getStringExtra(Constants.USERNAME);
            from = intent.getStringExtra(Constants.FROM);

        }

        getFollowerAndFollowingUserCount(userName);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        assert userName != null;
        if (!userName.isEmpty()) {
            if (Objects.equals(from, Constants.FOLLOWERS)) {
                tvFollowerClicked();
                txtToolbarTitle.setText(getFollowersText());
            } else if (Objects.requireNonNull(from).equals(Constants.FOLLOWING)) {
                tvFollowingClicked();
                txtToolbarTitle.setText(getFollowingText());
            }

        }

        if (fromLoggedInProfile) {
            //(view.findViewById(R.id.frag_timeline_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
            (findViewById(R.id.parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
        } else {
            //(view.findViewById(R.id.frag_timeline_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
            (findViewById(R.id.parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
        }
    }

    private String getFollowersText() {
        if (Objects.equals(userName, tradWyseSession.getUserName()))
            return "People Who Follow You";
        else
            //  return "People Who Follow " + userName.substring(0, 1).toUpperCase() + userName.substring(1);
//            return userName.substring(0, 1).toUpperCase() + userName.substring(1)+"'s Followers";
            return userName + "'s  Followers";
    }

    private String getFollowingText() {
        if (Objects.equals(userName, tradWyseSession.getUserName()))
            return "People You Are Following";
        else
            // return "People " + userName.substring(0, 1).toUpperCase() + userName.substring(1) + " is Following";
            //return userName.substring(0, 1).toUpperCase() + userName.substring(1) + " is Following";
            return userName + " is Following";
    }

    public void initListeners() {
        tvFollowers.setOnClickListener(this);
        tvFollowing.setOnClickListener(this);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_followers:
//                txtToolbarTitle.setText("(" + userName.substring(0, 1).toUpperCase() + userName.substring(1) + ") Followers");
                txtToolbarTitle.setText(getFollowersText());
                tvFollowerClicked();
                break;
            case R.id.tv_following:
//                txtToolbarTitle.setText("(" + userName.substring(0, 1).toUpperCase() + userName.substring(1) + ") is Following");
                txtToolbarTitle.setText(getFollowingText());
                tvFollowingClicked();
                break;
        }
    }

    private void tvFollowingClicked() {
        tvFollowing.setBackground(getResources().getDrawable(R.drawable.bg_fill_white_round_border));
        tvFollowing.setTextColor(getResources().getColor(R.color.color_app_dark_bg));

        tvFollowers.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFollowers.setTextColor(getResources().getColor(R.color.text_color_white));
        isFromFollowing = true;
        //mAdapter.EmptyList();
        getAllFollowUserListByUserName(userName, 0, true);
    }

    private void tvFollowerClicked() {
        tvFollowers.setBackground(getResources().getDrawable(R.drawable.bg_fill_white_round_border));
        tvFollowers.setTextColor(getResources().getColor(R.color.color_app_dark_bg));

        tvFollowing.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFollowing.setTextColor(getResources().getColor(R.color.text_color_white));
        isFromFollowing = false;
        //mAdapter.EmptyList();
        getAllFollowUserListByUserName(userName, 0, true);
    }


    public void getAllFollowUserListByUserName(String userName, int nextPage, boolean showLoading) {
        if (!Common.isNetworkAvailable(FollowersFollowingActivity.this)) {
            Common.showOfflineMemeDialog(FollowersFollowingActivity.this, getResources().getString(R.string.memeMsg),
                    getResources().getString(R.string.JustLetYouKnow));
            return;
        }

        if (start == nextPage) {
            scrollListener.resetState();
            scrollListener.setFirstTime(false);
            //mAdapter = null;
        }
        if (showLoading) {
            progressBar.setVisibility(View.VISIBLE);
            if (mAdapter != null) {
                mAdapter.clearAdapter();
                rlNoDataFound.setVisibility(View.GONE);
            }
        }
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, FollowersFollowingActivity.this);
        Call<List<FollowUserModel>> call = apiInterface.getAllFollowingUserListByUserName(userName, nextPage, PAGE_LIMIT);
        if (!isFromFollowing)
            call = apiInterface.getAllFollowerUserListByUserName(userName, nextPage, PAGE_LIMIT);
        call.enqueue(new Callback<List<FollowUserModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<FollowUserModel>> call, @NotNull Response<List<FollowUserModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (!response.body().isEmpty()) {
                        List<FollowUserModel> followUserModelList = response.body();
                        shortInDescindingOrder(followUserModelList);
                        loggedInUserFollowList = followUserModelList;
                        // show message if we need to show anything.
                        if (TradWyseSession.getSharedInstance(FollowersFollowingActivity.this).getUserName().equalsIgnoreCase(userName)) {
                            storeFollowListForLoggedInUsers((followUserModelList));
                        }
                        scrollListener.hasMore(followUserModelList.size(), PAGE_LIMIT);
                        if (followUserModelList.size() > 0) {
                            rlNoDataFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            if (mAdapter == null || mAdapter.getItemCount() == 0) {
                                mAdapter = new FollowingListAdapter(FollowersFollowingActivity.this, followUserModelList, FollowersFollowingActivity.this, fromLoggedInProfile);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                lm.scrollToPosition(0);
                                recyclerView.setAdapter(mAdapter);
                            } else {
                                mAdapter.addMoreFollowItems(followUserModelList);
                            }

                            for (int i = 0; i < followUserModelList.size(); i++) {
                                FollowUserModel user = followUserModelList.get(i);
                                Common.downloadImage(user.getFollowUserName(), null, FollowersFollowingActivity.this, new ImageDownloadResponse() {
                                    @Override
                                    public void onImageDownload(@NotNull Uri uri) {
                                        try {
                                            mAdapter.setUserImage(uri, user.getFollowUserName());
                                        } catch (Exception e) {
                                            Log.e("Exception", "" + e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {
                            //tabBar1.setVisibility(View.GONE);
                            if (start == nextPage) {
                                rlNoDataFound.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        if (start == nextPage) {
                            rlNoDataFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<FollowUserModel>> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d(Constants.ON_FAILURE_TAG, "FollowingFragment getAllFollowUserListByUserName: onFailure");
            }
        });
    }

    private void shortInDescindingOrder(List<FollowUserModel> followUserModelList) {
        if (followUserModelList.size() > 0) {
            Collections.sort(followUserModelList, new Comparator<FollowUserModel>() {
                @SuppressLint("SimpleDateFormat")
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

                @Override
                public int compare(FollowUserModel lhs, FollowUserModel rhs) {
                    return rhs.getModifiedOn().compareTo(lhs.getModifiedOn());
                }
            });
            for (int i = 0; i < followUserModelList.size(); i++) {
                if (followUserModelList.get(i).getFollowUserName().equalsIgnoreCase(userName))
                    followUserModelList.remove(i);
            }
        }
        //  return followUserModelList;
    }


    public void storeFollowListForLoggedInUsers(List<FollowUserModel> list) {
        loggedInUserFollowMap = new HashMap<String, Boolean>();
        for (FollowUserModel followModel : list) {
            loggedInUserFollowMap.put(followModel.getFollowUserName(), followModel.getLoginUserFollow());
        }
    }

    @Override
    public void onFollowItemClick(String username, String userId) {
        Log.d("FollowCLick", "onFollowItemClick: ");
        redirectToProfile(username, userId);

    }

    @Override
    public void onFollowClick(int position) {
        getFollowerAndFollowingUserCount(userName);
    }

    @Override
    public void onUnFollowClick(int position) {
        getFollowerAndFollowingUserCount(userName);
    }

    public void redirectToProfile(String username, String userId) {
        // if(isAdded()) {
        if (!TradWyseSession.getSharedInstance(FollowersFollowingActivity.this).getUserName().equalsIgnoreCase(username)) {
            Intent intent = new Intent(FollowersFollowingActivity.this, ProfileTabbedActivity.class);
            intent.putExtra("fromProfile", false);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("current_page", 2);
            startActivityForResult(intent, Constants.REQUEST_CODE_FOLLOW_SCREEN);
        }
        // }

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

    public void getFollowerAndFollowingUserCount(String userName) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, FollowersFollowingActivity.this);
        Call<FollowingFollowersUserCount> call = apiInterface.getFollowerAndFollowingUserCount(userName);
        call.enqueue(new Callback<FollowingFollowersUserCount>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<FollowingFollowersUserCount> call, @NotNull Response<FollowingFollowersUserCount> response) {
                //dismissProgressDialog();
                if (response.body() != null) {

                    if (response.body().getFollowerCount() <= 1) {
                        tvFollowers.setText("Follower");
                    } else if (response.body().getFollowingCount() <= 1) {
                        tvFollowing.setText("Followers");
                    }
                    tvFollowers.setText(getResources().getString(R.string.followers) +"  "+ response.body().getFollowerCount());
                    tvFollowing.setText(getResources().getString(R.string.Following) +"  "+ response.body().getFollowingCount());

                    tradWyseSession.setUserFollowersCount(response.body().getFollowerCount());
                    tradWyseSession.setUserFollowingCount(response.body().getFollowingCount());
                }

            }

            @Override
            public void onFailure(@NotNull Call<FollowingFollowersUserCount> call, @NotNull Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity getUserDetail: onFailure");
            }
        });

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
    public void ChangeTestBottomColor()
    {
        if (Constants.IS_PRODUCTION == false)
        {
            bottomlinearLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        }
    }

}
