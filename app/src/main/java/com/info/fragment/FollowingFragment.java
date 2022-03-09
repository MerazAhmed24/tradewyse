package com.info.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.info.tradewyse.FollowersFollowingActivity;
import com.info.tradewyse.ProfileTabbedActivity;
import com.info.tradewyse.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingFragment extends Fragment implements FollowingListAdapter.OnFollowItemClick {

    public static int start = 0;
    Context context;
    TextView tvText1, tvText2, tvText3, tvText4, tvFollowing, tvFollowingText, tvFollowers, tvFollowerText;
    String username = "";
    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager lm;
    LinearLayout tabBar1, tabBar2;
    HashMap<String, Boolean> loggedInUserFollowMap = new HashMap<String, Boolean>();
    List<FollowUserModel> loggedInUserFollowList = new ArrayList<>();
    boolean isFromFollowing = false;
    private RecyclerView recMentorList;
    private FollowingListAdapter mAdapter;
    private ProgressBar progress;
    private RelativeLayout noDataLayout;
    private int PAGE_LIMIT = 15;
    private TradWyseSession tradWyseSession;


    //  private HashMap<String,Boolean> loggedInUserFollowList=new HashMap<>();
    public FollowingFragment() {
        // Required empty public constructor
    }

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        Log.d("Fragment", "follow fragment");
        tradWyseSession = TradWyseSession.getSharedInstance(context);

        return inflater.inflate(R.layout.fragment_mentors, container, false);
    }

    private void setEmptyDataText() {
        String s1 = getActivity().getResources().getString(R.string._1_click_on_any_user_s_profile_icon_or_name_then_click_the_follow_button_that_looks_like);
        String s2 = getActivity().getResources().getString(R.string._2_grow_your_network_and_learn_from_others);
        String s3 = getActivity().getResources().getString(R.string._3_get_invited_to_exclusive_chat_rooms_and_special_events);
        String s4 = getActivity().getResources().getString(R.string.hint_follow_others_to_find_winning_stocks_and_strategies);
        SpannableString ss1 = getSpannableString(s1, 0, 8);
        SpannableString ss2 = getSpannableString(s2, 0, 7);
        SpannableString ss3 = getSpannableString(s3, 0, 6);
        SpannableString ss4 = getSpannableString(s4, 0, 5);
        tvText1.setText(ss1);
        tvText2.setText(ss2);
        tvText3.setText(ss3);
        tvText4.setText(ss4);
    }

    @NotNull
    private SpannableString getSpannableString(String s, int start, int end) {
        SpannableString ss1 = new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(1.5f), start, end, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, 0);// set color
        return ss1;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // getTimeLineData();
        tvText1 = view.findViewById(R.id.text1);
        tvText2 = view.findViewById(R.id.text2);
        tvText3 = view.findViewById(R.id.text3);
        tvText4 = view.findViewById(R.id.text4);
        tvFollowers = view.findViewById(R.id.tv_followers);
        tvFollowing = view.findViewById(R.id.tv_following);
        tvFollowingText = view.findViewById(R.id.tv_people_you_follow);
        tvFollowerText = view.findViewById(R.id.tv_people_following_you);
        tabBar1 = view.findViewById(R.id.tab_bar);
        tabBar2 = view.findViewById(R.id.tab_bar2);
        tabBar2.setVisibility(View.GONE);
        recMentorList = view.findViewById(R.id.recMentorList);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        noDataLayout.setVisibility(View.GONE);
        progress = view.findViewById(R.id.progress);
        lm = new LinearLayoutManager(context);
        mAdapter = new FollowingListAdapter(getActivity(), new ArrayList<FollowUserModel>(), this,true);
        recMentorList.setLayoutManager(lm);
        recMentorList.setAdapter(mAdapter);
        setEmptyDataText();
        lm.scrollToPosition(0);
      //  username = ((ProfileTabbedActivity) getActivity()).userName; by Vijendra

        if (getArguments()!=null)
        {
            Bundle bundle=getArguments();
            username=bundle.getString(Constants.USERNAME);
            if (bundle.getString(Constants.FOR).equals(Constants.FOLLOWING))
            {
                tvFollowingClicked();
            }
            else if (bundle.getString(Constants.FOR).equals(Constants.FOLLOWERS))
            {
               tvFollowerClicked();
            }
            else
            {
                Toast.makeText(context, "Data not found!", Toast.LENGTH_SHORT).show();
            }
        }

        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvFollowingClicked();
            }
        });
        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvFollowerClicked();
            }
        });

        recMentorList.setItemAnimator(new DefaultItemAnimator());

        scrollListener = new EndlessRecyclerViewScrollListener(lm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                int nextPage = page * PAGE_LIMIT;
                Log.e("nextpage", nextPage + " totalItemsCount" + totalItemsCount);
                getAllFollowUserListByUserName(username, nextPage, true);
            }
        };
        recMentorList.addOnScrollListener(scrollListener);
          }
    private void tvFollowingClicked() {
            isFromFollowing = true;
             getAllFollowUserListByUserName(username, 0, true);
    }

    private void tvFollowerClicked() {
        tvFollowers.setBackground(getResources().getDrawable(R.drawable.bg_fill_white_round_border));
        tvFollowers.setTextColor(getResources().getColor(R.color.color_app_dark_bg));

        tvFollowing.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFollowing.setTextColor(getResources().getColor(R.color.text_color_white));
        isFromFollowing = false;
        tvFollowingText.setVisibility(View.GONE);
        tvFollowerText.setVisibility(View.VISIBLE);
        if (!TradWyseSession.getSharedInstance(context).getUserName().equalsIgnoreCase(username))
            tvFollowerText.setText("People who follow " + username);
        getAllFollowUserListByUserName(username, 0, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Onactivityresult", "fragment onactivity username:" + username);

        if (TradWyseSession.getSharedInstance(context).getUserName().equalsIgnoreCase(username)) {
            removeUnfollowUserforLoggedInUser();
            checkAndAddNewFollowItems();
        }
    }

    private void removeUnfollowUserforLoggedInUser() {
        ArrayList<FollowUserModel> itemsNeedToHide = new ArrayList<>();
        HashMap<String, Boolean> liveDataItems = Common.getFollowersData().getValue();
        for (Map.Entry m : liveDataItems.entrySet()) {
            for (int i = 0; i < loggedInUserFollowList.size(); i++) {
                if (loggedInUserFollowList.get(i).getFollowUserName().equalsIgnoreCase(m.getKey().toString())
                        && !(boolean) m.getValue()) {
                    itemsNeedToHide.add(loggedInUserFollowList.get(i));
                }
            }
        }
        //remove items from loggedInUserFollowList
        if (mAdapter != null) {
            mAdapter.removeItemsFromList(itemsNeedToHide);
        }
    }

    private void checkAndAddNewFollowItems() {
        // extra item in live data list.
        HashMap<String, Boolean> liveDataMap = Common.getFollowersData().getValue();
        //HashMap<String,Boolean> temDataMap =new HashMap<>();
        HashSet<String> extraItemsInLiveDataList = new HashSet<String>(loggedInUserFollowMap.keySet());
        extraItemsInLiveDataList.addAll(liveDataMap.keySet());
        extraItemsInLiveDataList.removeAll(loggedInUserFollowMap.keySet());
        if (extraItemsInLiveDataList.size() > 0) {
            // now check with the extra item of live data if any one with true status
            boolean isNewFollowItemPresent = false;
            //Iterator<String> i = extraItemsInLiveDataList.iterator();
            Iterator value = extraItemsInLiveDataList.iterator(); // Iterating over hash set items
            while (value.hasNext()) {
                String keyValue = (String) value.next();
                if (liveDataMap.containsKey(keyValue) && liveDataMap.get(keyValue) == true) {
                    String key = keyValue;
                    isNewFollowItemPresent = true;
                    boolean status = (boolean) liveDataMap.get(key);
                    //   temDataMap.put(key,status);
                    break;
                }
            }

                     if (isNewFollowItemPresent) {
                //mean these are the items whose status we don't konw may be these are new followed items.
                getAllFollowUserListByUserName(username,
                        FollowingFragment.start, true);
            }
        }

    }

    /*
     * get All follow user list
     */
    public void getAllFollowUserListByUserName(String userName, int nextPage, boolean showLoading) {
        // check network is available or not.
        if (!Common.isNetworkAvailable(getActivity())) {
            Common.showOfflineMemeDialog(getActivity(), getActivity().getResources().getString(R.string.memeMsg),
                    getActivity().getResources().getString(R.string.JustLetYouKnow));
            return;
        }

        if (start == nextPage) {
              mAdapter = null;
        }
        if (showLoading) progress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getActivity());
        Call<List<FollowUserModel>> call = apiInterface.getAllFollowingUserListByUserName(userName, nextPage, PAGE_LIMIT);
        if (!isFromFollowing)
            call = apiInterface.getAllFollowerUserListByUserName(userName, nextPage, PAGE_LIMIT);
        call.enqueue(new Callback<List<FollowUserModel>>() {
            @Override
            public void onResponse(Call<List<FollowUserModel>> call, Response<List<FollowUserModel>> response) {
                progress.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    if (!response.body().isEmpty()) {
                         List<FollowUserModel> followUserModelList = response.body();
                        shortInDescindingOrder(followUserModelList);
                        loggedInUserFollowList = followUserModelList;
                          if (TradWyseSession.getSharedInstance(context).getUserName().equalsIgnoreCase(username)) {
                            storeFollowListForLoggedInUsers((followUserModelList));
                        }
                        scrollListener.hasMore(followUserModelList.size(), PAGE_LIMIT);
                        if (followUserModelList.size() > 0) {
                            noDataLayout.setVisibility(View.GONE);
                             recMentorList.setVisibility(View.VISIBLE);
                            if (mAdapter == null || mAdapter.getItemCount() == 0) {
                                if (getActivity() != null) {
                                    mAdapter = new FollowingListAdapter(context, followUserModelList, FollowingFragment.this,true);
                                    recMentorList.setItemAnimator(new DefaultItemAnimator());
                                    lm.scrollToPosition(0);
                                    recMentorList.setAdapter(mAdapter);
                                }
                            } else {
                                mAdapter.addMoreFollowItems(followUserModelList);
                            }

                            for (int i = 0; i < followUserModelList.size(); i++) {
                                FollowUserModel user = followUserModelList.get(i);
                                Common.downloadImage(user.getFollowUserName(), null, getActivity(), new ImageDownloadResponse() {
                                    @Override
                                    public void onImageDownload(@NotNull Uri uri) {
                                        try {
                                            mAdapter.setUserImage(uri, user.getFollowUserName());
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                            }
                        } else {
                            //tabBar1.setVisibility(View.GONE);
                            if (start == nextPage) {
                                tabBar2.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                                recMentorList.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        // tabBar1.setVisibility(View.GONE);
                        if (start == nextPage) {
                            tabBar2.setVisibility(View.GONE);
                            noDataLayout.setVisibility(View.VISIBLE);
                            recMentorList.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FollowUserModel>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Log.d(Constants.ON_FAILURE_TAG, "FollowingFragment getAllFollowUserListByUserName: onFailure");
            }
        });
    }

    private List<FollowUserModel> shortInDescindingOrder(List<FollowUserModel> followUserModelList) {
        if (followUserModelList.size() > 0) {
            Collections.sort(followUserModelList, new Comparator<FollowUserModel>() {
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

                @Override
                public int compare(FollowUserModel lhs, FollowUserModel rhs) {
                    return rhs.getModifiedOn().compareTo(lhs.getModifiedOn());
                }
            });
            for (int i = 0; i < followUserModelList.size(); i++) {
                if (followUserModelList.get(i).getFollowUserName().equalsIgnoreCase(username))
                    followUserModelList.remove(i);
            }
        }
        return followUserModelList;
    }

    @Override
    public void onFollowItemClick(String username,String userId) {
        Log.d("FollowCLick", "onFollowItemClick: ");
        redirectToProfile(username,userId);

    }

    @Override
    public void onFollowClick(int position) {
        getFollowerAndFollowingUserCount(username);
    }

    @Override
    public void onUnFollowClick(int position) {
        getFollowerAndFollowingUserCount(username);
    }

    public void storeFollowListForLoggedInUsers(List<FollowUserModel> list) {
        loggedInUserFollowMap = new HashMap<String, Boolean>();
        for (FollowUserModel followModel : list) {
            loggedInUserFollowMap.put(followModel.getFollowUserName(), followModel.getLoginUserFollow());
        }
    }

    public void redirectToProfile(String username,String userId) {
        // if(isAdded()) {
        if (!TradWyseSession.getSharedInstance(context).getUserName().equalsIgnoreCase(username)) {
            Intent intent = new Intent(context, ProfileTabbedActivity.class);
            intent.putExtra("fromProfile", false);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("current_page", 2);
            startActivityForResult(intent, Constants.REQUEST_CODE_FOLLOW_SCREEN);
        }
        // }

    }

    public void getFollowerAndFollowingUserCount(String userName) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
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

}
