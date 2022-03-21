package com.info.tradewyse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.FIleHelper;
import com.info.commons.PhotoUtility;
import com.info.commons.StringHelper;
import com.info.commons.TradWyseSession;
import com.info.fragment.ChatsFragment;
import com.info.fragment.FollowingFragment;
import com.info.fragment.ServiceFragment;
import com.info.fragment.TimelineFragment;
import com.info.interfaces.MentorServiceCount;
import com.info.interfaces.PhotoOptionSelectListener;
import com.info.model.FollowUserModel;
import com.info.model.FollowingFollowersUserCount;
import com.info.model.FooterModel;
import com.info.model.IsFollowResponse;
import com.info.model.NotificationCountModel;
import com.info.model.User;
import com.info.model.UserSubscriptionDetail;
import com.info.tradewyse.databinding.ActivityProfileTabbedBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.annotations.NonNull;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static okhttp3.MultipartBody.FORM;
import static okhttp3.MultipartBody.Part.createFormData;

public class ProfileTabbedActivity extends BaseActivity implements View.OnClickListener, MentorServiceCount {

    public static void starProfileTabbedtActivityForResult(Context context, boolean fromProfile, int requestCode, String selectedScreen) {
        Intent intent = new Intent(context, ProfileTabbedActivity.class);
        intent.putExtra("fromProfile", fromProfile);
        intent.putExtra("selectedScreen", selectedScreen);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void starProfileTabbedtActivityForResult(Context context, boolean fromProfile, boolean goToChat, int requestCode, String selectedScreen) {
        Intent intent = new Intent(context, ProfileTabbedActivity.class);
        intent.putExtra("goToChat", goToChat);
        intent.putExtra("fromProfile", fromProfile);
        intent.putExtra("selectedScreen", selectedScreen);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void starProfileTabbedtActivityForResult(Context context, boolean fromProfile, String userName, String userId, int requestCode, String selectedScreen) {
        Intent intent = new Intent(context, ProfileTabbedActivity.class);
        intent.putExtra("fromProfile", fromProfile);
        intent.putExtra("username", userName);
        intent.putExtra("userId", userId);
        intent.putExtra("selectedScreen", selectedScreen);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void starProfileTabbedtActivity(Context context, boolean fromProfile, String userName, String userId, String selectedScreen) {
        Intent intent = new Intent(context, ProfileTabbedActivity.class);
        intent.putExtra("fromProfile", fromProfile);
        intent.putExtra("username", userName);
        intent.putExtra("userId", userId);
        intent.putExtra("selectedScreen", selectedScreen);
        ((Activity) context).startActivity(intent);
    }

    private static final String TAG = "ProfileTabbedActivity";
    public static int Selected_Tab_Position = 0;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabItem tab1, /*tab2,*/
            tab3;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    TabLayout tabLayout;
    TextView toolbar_title;
    int mentorListCount = -1;
    ImageView otherAction, homeIcon;
    PhotoUtility photoUtility;
    SimpleDraweeView imgProfile;
    TextView txtEditPhoto;
    TradWyseSession tradWyseSession;
    TextView txtFollow;
    LinearLayout txtFollowBtnLL, followerFollowingParentLL;
    TextView txtPay;
    public String userName = "";
    TimelineFragment timelineFragment;
    ChatsFragment chatsFragment;
    FollowingFragment followingFragment;
    private ProgressBar progress;
    public boolean fromLoggedInProfile = false;
    public boolean goToChat = false;
    private LinearLayout llFollowers;
    private LinearLayout llFollowing,bottomLinearLayout;
    private MaterialTextView txtFollowersCount;
    private MaterialTextView txt_followers;
    private MaterialTextView txtFollowingsCount;
    private AppCompatImageView imgEditPhoto;
    private String isMentor = null;
    private String userId = null;
    private boolean isSubscribed = false;
    public static int REQUEST_CODE_FOR_SERVICE_DETAILS = 1001;
    private boolean FROM_SERVICE_DETAILS = false;
    private String selectedScreen = Constants.HOME;
    ActivityProfileTabbedBinding activityProfileTabbedBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile_tabbed);
        activityProfileTabbedBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile_tabbed);
        if (getIntent().getStringExtra("selectedScreen") != null)
            selectedScreen = getIntent().getStringExtra("selectedScreen");
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


        activityProfileTabbedBinding.setFooterModel(footerModel);
        tradWyseSession = TradWyseSession.getSharedInstance(this);
        Log.d("selectedTab", "selectedTab oncreate(): " + Selected_Tab_Position);
        txtFollow = findViewById(R.id.txtFollowBtn);
        txtFollowBtnLL = findViewById(R.id.txtFollowBtnLL);
        followerFollowingParentLL = findViewById(R.id.followerFollowingParentLL);
        txtPay = findViewById(R.id.txtPay);
        progress = findViewById(R.id.progress);
        llFollowers = findViewById(R.id.ll_followers);
        llFollowing = findViewById(R.id.ll_following);
        txtFollowersCount = findViewById(R.id.txt_followers_count);
        txt_followers = findViewById(R.id.txt_followers);
        txtFollowingsCount = findViewById(R.id.txt_following_count);
        imgEditPhoto = findViewById(R.id.img_edit_photo);
        bottomLinearLayout = findViewById(R.id.bottomView);
        fromLoggedInProfile = getIntent().getBooleanExtra("fromProfile", false);
        goToChat = getIntent().getBooleanExtra("goToChat", false);
        if (fromLoggedInProfile) {
            userName = tradWyseSession.getUserName();
            userId = tradWyseSession.getUserId();
        } else {
            userName = getIntent().getStringExtra("username");
            userId = getIntent().getStringExtra("userId");
        }

        if (userName != null && userName.equalsIgnoreCase(tradWyseSession.getUserName())) {
            // we need to hide the follow btn in this case.
            txtFollowBtnLL.setVisibility(View.GONE);
            followerFollowingParentLL.setWeightSum(2);
            txtPay.setVisibility(View.GONE);
        } else {
            Common.observeFollowers(ProfileTabbedActivity.this, txtFollow, userName);
            isLoginUserFollowGivenUser(userName);
            txtPay.setVisibility(View.GONE);
        }
        final String followUser = userName;
        setToolBar(userName);
        getSupportActionBar().setElevation(0f);
        getNotificationCount();
        Common.BottomTabColorChange(this,bottomLinearLayout);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);

        imgProfile = findViewById(R.id.imgProfile);
        txtEditPhoto = findViewById(R.id.txtEditPhoto);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tab1 = (TabItem) findViewById(R.id.tabItem);
        //tab2 = (TabItem) findViewById(R.id.tabItem2);
        tab3 = (TabItem) findViewById(R.id.tabItem3);
        toolbar_title = findViewById(R.id.toolbar_title);
        otherAction = findViewById(R.id.otherAction);
        homeIcon = findViewById(R.id.home_btn);

        txtFollow.setOnClickListener(view -> {
            // call follow user
            if (txtFollow.getText().toString().equalsIgnoreCase(getResources().getString(R.string.Following))) {
                showUnFollowDialog(ProfileTabbedActivity.this, followUser);
            } else {
                Common.updatedFollowItemList.put(followUser, followUser);
                followUser(followUser);
            }
        });


        setTabsFont();
        Selected_Tab_Position = tabLayout.getSelectedTabPosition();
        Log.d("selectedTab", "selectedTab: " + Selected_Tab_Position);
        otherAction.setOnClickListener(v -> {

            Intent starter = new Intent(ProfileTabbedActivity.this, SettingActivity.class);
            ProfileTabbedActivity.this.startActivity(starter);
        });

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileTabbedActivity.this, DashBoardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK));
            ProfileTabbedActivity.this.finish();
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(@NotNull TabLayout.Tab tab) {
                super.onTabSelected(tab);
                Selected_Tab_Position = tab.getPosition();
                Log.d("selectedTab", "on tab change: " + Selected_Tab_Position);

                if (Selected_Tab_Position == 1 && mentorListCount != -1) {
                    mentorServiceCountRecieved(mentorListCount);
                    // Common.mentorServiceDialog(ProfileTabbedActivity.this, "Hey, " + userName, getString(R.string.mentor_service_description));

                }

            }
        });
        imgEditPhoto.setOnClickListener(view -> takePhoto());

        if (!userName.equalsIgnoreCase(tradWyseSession.getUserName())) {
            homeIcon.setVisibility(View.VISIBLE);
            imgEditPhoto.setVisibility(View.GONE);
            otherAction.setVisibility(View.GONE);
        } else {
            homeIcon.setVisibility(View.GONE);
            //TODO: Make the setting button visible when enabling the setting screen
            otherAction.setVisibility(View.GONE);
            imgEditPhoto.setVisibility(View.VISIBLE);
        }

        llFollowing.setOnClickListener(this);
        llFollowers.setOnClickListener(this);
        getUserDetail(userName);

        //Common.mentorServiceDialog(ProfileTabbedActivity.this, "Hey, " + userName, getString(R.string.mentor_service_description));
        callSectionPagerAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("selectedTab", "selectedTab onresume(): " + Selected_Tab_Position);
        getFollowerAndFollowingUserCount(userName);
        isSubscribedUser(userId);

        if (fromLoggedInProfile) {
            //(view.findViewById(R.id.frag_timeline_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
            //tabLayout.setBackgroundColor(getResources().getColor(R.color.color_border_stroke));

            tabLayout.setBackgroundResource(R.drawable.mentor_list_item_bg_rounded);
        } else {
            //(view.findViewById(R.id.frag_timeline_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
            tabLayout.setBackgroundResource(R.drawable.mentor_list_item_bg_rounded);
        }
    }

    @Override
    public void mentorServiceCountRecieved(int mentorListCount) {
        this.mentorListCount = mentorListCount;
        if (!(mentorListCount > 0)) {
            if (Selected_Tab_Position == 1) {
                if ((isMentor != null && isMentor.equalsIgnoreCase("true")) && tradWyseSession.getUserName().equals(userName)) {
                    int count = tradWyseSession.getOpenMentorDialogCount();
                    count = count + 1;
                    if (count <= 3) {
                        tradWyseSession.setOpenMentorDialogCount(count);
                        Common.mentorServiceDialog(ProfileTabbedActivity.this, "Hey, " + userName, getString(R.string.mentor_service_description));
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void getProfileImage(String userId) {
        Common.setProfileImage(imgProfile, userId);
    }


    public void callSectionPagerAdapter() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        timelineFragment = TimelineFragment.newInstance(userName, fromLoggedInProfile, selectedScreen);
        chatsFragment = ChatsFragment.newInstance(fromLoggedInProfile);
        followingFragment = new FollowingFragment();

        Fragment serviceFragment = new ServiceFragment(this);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("userId", userId);
        bundle.putString("isMentor", isMentor);
        bundle.putBoolean("isSubscribed", tradWyseSession.isSubscribedMember());
        bundle.putString("selectedScreen", selectedScreen);
        serviceFragment.setArguments(bundle);


        mSectionsPagerAdapter.addFragment(timelineFragment, "TimeLine");
        //mSectionsPagerAdapter.addFragment(chatsFragment, "Chat Rooms");
        mSectionsPagerAdapter.addFragment(serviceFragment, "Services");

        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (goToChat) {
            mViewPager.setCurrentItem(1);
        } else if (FROM_SERVICE_DETAILS) {
            mViewPager.setCurrentItem(1);
        } else {
            mViewPager.setCurrentItem(0);
        }


    }

    public void getUserDetail(String userName) {
        //showProgressDialog();
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, ProfileTabbedActivity.this);
        Call<User> call = apiInterface.getAppUserByUserName(userName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                dismissProgressDialog();
                if (response.body() != null) {
                    if (!response.body().getId().isEmpty()) {
                        User userResponse = response.body();
                        userId = userResponse.getId();
                        isMentor = userResponse.getIsMentor();
                        isSubscribedUser(userId);
                        // callSectionPagerAdapter();

                        if (userResponse.getUserName().equalsIgnoreCase(tradWyseSession.getUserName())) {
                            tradWyseSession.setUserImage(userResponse.getImage());
                            tradWyseSession.setInternalSubscribedMember(userResponse.getInternalSubscribedUser());
                        }
                        getProfileImage(userResponse.getId());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                //dismissProgressDialog();
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity getUserDetail: onFailure");
            }
        });

    }

    public void getFollowerAndFollowingUserCount(String userName) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, ProfileTabbedActivity.this);
        Call<FollowingFollowersUserCount> call = apiInterface.getFollowerAndFollowingUserCount(userName);
        call.enqueue(new Callback<FollowingFollowersUserCount>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<FollowingFollowersUserCount> call, @NotNull Response<FollowingFollowersUserCount> response) {
                //dismissProgressDialog();
                if (response.body() != null) {

                    if (response.body().getFollowerCount() <= 1) {
                        txt_followers.setText("Follower");
                    } else {
                        txt_followers.setText("Followers");
                    }
                    txtFollowersCount.setText("" + response.body().getFollowerCount());
                    txtFollowingsCount.setText("" + response.body().getFollowingCount());

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


    /*
     * follow user
     */
    public void followUser(String userName) {
        progress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, ProfileTabbedActivity.this);
        Call<FollowUserModel> call = apiInterface.followUser(userName, "", "");
        call.enqueue(new Callback<FollowUserModel>() {
            @Override
            public void onResponse(@NotNull Call<FollowUserModel> call, @NotNull Response<FollowUserModel> response) {
                progress.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().getId() != null) {
                        FollowUserModel followUserModel = response.body();
                        // show message if we need to show anything.
                        txtFollow.setText(getResources().getString(R.string.Following));
                        txtFollow.setBackgroundResource(R.drawable.dark_follow_btn_bg);
                        Common.setFollowersData(userName, true);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<FollowUserModel> call, @NotNull Throwable t) {
                progress.setVisibility(View.GONE);
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity followUser: onFailure");
            }
        });
    }

    /*
     * Unfollow user
     */
    public void unfollowUser(String userName) {
        progress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, ProfileTabbedActivity.this);
        Call<FollowUserModel> call = apiInterface.unfollowUser(userName, "", "");
        call.enqueue(new Callback<FollowUserModel>() {
            @Override
            public void onResponse(@NotNull Call<FollowUserModel> call, @NotNull Response<FollowUserModel> response) {
                progress.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (!response.body().getId().isEmpty()) {
                        FollowUserModel followUserModel = response.body();
                        // show message if we need to show anything.
                        txtFollow.setText(getResources().getString(R.string.Follow));
                        txtFollow.setBackgroundResource(R.drawable.follow_btn_bg_transplant);
                        Common.setFollowersData(userName, false);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<FollowUserModel> call, @NotNull Throwable t) {
                progress.setVisibility(View.GONE);
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity unfollowUser: onFailure");
            }
        });
    }


    /*
     * isLoginUserFollowGivenUser user
     */
    public void isLoginUserFollowGivenUser(String userName) {
        // check network is available or not.
        if (!Common.isNetworkAvailable(ProfileTabbedActivity.this)) {
            Common.showOfflineMemeDialog(ProfileTabbedActivity.this, ProfileTabbedActivity.this.getResources().getString(R.string.memeMsg),
                    ProfileTabbedActivity.this.getResources().getString(R.string.JustLetYouKnow));
            return;
        }
        progress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, ProfileTabbedActivity.this);
        Call<IsFollowResponse> call = apiInterface.isLoginUserFollowGivenUser(userName);
        call.enqueue(new Callback<IsFollowResponse>() {
            @Override
            public void onResponse(@NotNull Call<IsFollowResponse> call, @NotNull Response<IsFollowResponse> response) {
                progress.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    if (response.body() != null) {
                        IsFollowResponse isFollowResponse = response.body();
                        // show message if we need to show anything.
                        if (!userName.equalsIgnoreCase(tradWyseSession.getUserName())) {
                            txtFollowBtnLL.setVisibility(View.VISIBLE);
                            followerFollowingParentLL.setWeightSum(3);
                        }
                        if (isFollowResponse.isFollow()) {
                            txtFollow.setText(getResources().getString(R.string.Following));
                            txtFollow.setBackgroundResource(R.drawable.dark_follow_btn_bg);
                        } else {
                            txtFollow.setText(getResources().getString(R.string.Follow));
                            txtFollow.setBackgroundResource(R.drawable.follow_btn_bg_transplant);
                        }

                        Common.setFollowersData(userName, isFollowResponse.isFollow());
                    }

                }
            }

            @Override
            public void onFailure(Call<IsFollowResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Log.v("error: ", "something went wrong");
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity isLoginUserFollowGivenUser: onFailure");
                Common.showOfflineMemeDialog(ProfileTabbedActivity.this, ProfileTabbedActivity.this.getResources().getString(R.string.memeMsg),
                        ProfileTabbedActivity.this.getResources().getString(R.string.JustLetYouKnow));
                return;
            }
        });

    }

    public String upperCaseStringFirstLetter(String name) {
        String str = "java";
        if (!name.isEmpty()) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        } else {
            return name;
        }
    }

    public void setTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Common.getTypeface(ProfileTabbedActivity.this, 1));
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_followers:
                startActivity(new Intent(ProfileTabbedActivity.this, FollowersFollowingActivity.class)
                        .putExtra(Constants.USERNAME, userName)
                        .putExtra("fromProfile", fromLoggedInProfile)
                        .putExtra("selectedScreen", selectedScreen)
                        .putExtra(Constants.FROM, Constants.FOLLOWERS));
                break;

            case R.id.ll_following:
                startActivity(new Intent(ProfileTabbedActivity.this, FollowersFollowingActivity.class)
                        .putExtra(Constants.USERNAME, userName)
                        .putExtra("fromProfile", fromLoggedInProfile)
                        .putExtra("selectedScreen", selectedScreen)
                        .putExtra(Constants.FROM, Constants.FOLLOWING));
                break;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile_tabbed2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("selectedTab", "selectedTab onstart(): " + Selected_Tab_Position);
    }



    public void onBackPress(View v) {
        ProfileTabbedActivity.this.finish();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public Fragment getFragmentList(int position) {
            return mFragmentList.get(position);
        }
    }

    private void takePhoto() {
        try {
            photoUtility = new PhotoUtility.Builder(this)
                    .setImageView(imgProfile)
                    .setOutPutFile(FIleHelper.createNewFile(this, FIleHelper.createFileName("profile")))
                    .build();
            photoUtility.setPhotoOptionSelectListener(new PhotoOptionSelectListener() {
                @Override
                public void requestPermissions(@NonNull String[] permissions, int requestCode) {
                    ActivityCompat.requestPermissions(ProfileTabbedActivity.this, permissions, requestCode);
                }

                @Override
                public void startActivityForResult(Intent intent, int requestCode) {
                    ActivityCompat.startActivityForResult(ProfileTabbedActivity.this, intent, requestCode, null);
                }
            });

            photoUtility.requestPermissionsCameraAndStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void uploadProfilePhoto() {
        Builder builder = new Builder();
        builder.setType(FORM);
        String selectedFilePath = photoUtility.getSelectedFilePath();
        if (StringHelper.isEmpty(selectedFilePath)) return;
        File f = new File(selectedFilePath);
        if (f.exists()) {
            try {
                f = new Compressor(this).compressToFile(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(selectedFilePath), f);
            builder.addPart(createFormData("file", f.getName(), requestFile));
        } else {
            return;
        }
        String userName = TradWyseSession.getSharedInstance(this).getUserName();
        String userId = TradWyseSession.getSharedInstance(this).getUserId();
        builder.addFormDataPart("appUserName", userName);
        MultipartBody finalRequestBody = builder.build();
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        showProgressDialog("Uploading...");
        Call<ResponseBody> call = apiInterface.addProfilePhoto(finalRequestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileTabbedActivity.this, "Profile photo uploaded successfully", Toast.LENGTH_LONG).show();
                    Common.evictImage(userId);
                    setResult(RESULT_OK);
                } else {
                    Toast.makeText(ProfileTabbedActivity.this, "Unable to upload image at the moment.Please try again later", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                dismissProgressDialog();
                Toast.makeText(ProfileTabbedActivity.this, "Unable to upload image at the moment.Please try again later", Toast.LENGTH_LONG).show();
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity uploadProfilePhoto: onFailure");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("Onactivityresult", "onactivity result profile activity");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 110 || requestCode == 111) {
            photoUtility.setResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                uploadProfilePhoto();
            }
        } else if (requestCode == Constants.REQUEST_CODE_FOLLOW_SCREEN) {
            Log.d("Onactivityresult", "onactivity result profile req code follow screen");
        } else if (requestCode == REQUEST_CODE_FOR_SERVICE_DETAILS) {
            FROM_SERVICE_DETAILS = true;
            goToChat = false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        photoUtility.setPermissionResult(requestCode, grantResults);
    }


    @SuppressLint("SetTextI18n")
    public void showUnFollowDialog(final Activity activity, final String Username) {
        try {
            if (activity != null && !activity.isFinishing()) {
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
                View sheetView = activity.getLayoutInflater().inflate(R.layout.unfollow_option, null);
                mBottomSheetDialog.setContentView(sheetView);
                TextView tvUnfollow = sheetView.findViewById(R.id.optUnFollow);
                tvUnfollow.setText(activity.getResources().getString(R.string.Unfollow) + " " + Username);
                sheetView.findViewById(R.id.optUnFollow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unfollowUser(Username);
                        mBottomSheetDialog.dismiss();
                        Common.updatedFollowItemList.put(Username, Username);
                    }
                });
                sheetView.findViewById(R.id.optionCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBottomSheetDialog.dismiss();
                    }
                });
                mBottomSheetDialog.show();
            }
        } catch (Exception e) {
            Log.d("BottomSheet", "showBottomDialog: exception");
        }
    }


    public void isSubscribedUser(String userId) {
        // check network is available or not.

        TradWyseSession tradWyseSession = TradWyseSession.getSharedInstance(ProfileTabbedActivity.this);
        String token = tradWyseSession.getLoginAccessToken();
        Log.e("Token==", token);
        Log.e("userId==", tradWyseSession.getUserId());

        if (!Common.isNetworkAvailable(ProfileTabbedActivity.this)) {
            // if no network available then we will not call api.
            return;
        }
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<List<UserSubscriptionDetail>> call = apiInterface.getProductSubscriptionsByUserId(userId);
        //  showProgressDialog();
        call.enqueue(new Callback<List<UserSubscriptionDetail>>() {
            @Override
            public void onResponse(Call<List<UserSubscriptionDetail>> call, Response<List<UserSubscriptionDetail>> response) {
                // dismissProgressDialog();
                Log.d("Response==", "" + response.body());
                Log.d("URL==", "" + call.request().url());
                if (response.body() != null) {
                    // we can parse the response and show the list of subscription.
                    List<UserSubscriptionDetail> userSubscriptionDetails = response.body();
                    for (int i = 0; i < userSubscriptionDetails.size(); i++) {
                        if (userSubscriptionDetails.get(i).getSubscriptionPlan().getSubscriptionStatus().equals("active")) {
                            //tradWyseSession.setSubscribedMember(true);
                            isSubscribed = true;
                            break;
                        }
                    }
//                    callSectionPagerAdapter();
                    Log.d("IsSusbscribed", "" + tradWyseSession.isSubscribedMember());
                }
            }

            @Override
            public void onFailure(Call<List<UserSubscriptionDetail>> call, Throwable t) {
                //  dismissProgressDialog();
                Log.d(Constants.ON_FAILURE_TAG, "TipDetailActivity addTipComment: onFailure");
            }
        });
    }

    public void homeTabClicked(View v) {
        DashBoardActivity.CallDashboardActivity(this, false);
        finish();
    }

    public void chatTabClicked(View v) {
        ChatActivity.starChatActivity(this,false);
        //TabbedChatActivity.CallTabbedChatActivity(ProfileTabbedActivity.this, true);
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



