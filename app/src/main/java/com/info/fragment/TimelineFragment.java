package com.info.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info.adapter.TimeLineAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.DateTimeHelperElapsed;
import com.info.commons.TradWyseSession;
import com.info.logger.Logger;
import com.info.model.timelineModels.TimeLineDataModel;
import com.info.tradewyse.ProfileTabbedActivity;
import com.info.tradewyse.R;
import com.shuhart.stickyheader.StickyHeaderItemDecorator;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.info.commons.Constants.ACTIVITY_TYPE_COMMENT;
import static com.info.commons.Constants.ACTIVITY_TYPE_TIP_PIN;

public class TimelineFragment extends BaseFragment implements com.info.interfaces.TimeLineOnclickListner {

    int lastItemSelectedPosition = 0;
    private static String ARG_PARAM_USERNAME = "userName";
    private static String ARG_FROM_LOGGED_IN_PROFILE = "fromLoggedInProfile";

    private String mParamUserName;
    private boolean mFromLoggedInProfile;
    Context currentContext;
    private RecyclerView recList;
    private TimeLineAdapter mAdapter;
    TradWyseSession tradWyseSession;
    TextView tvFragmentAll, tvFragmentSavedTips, tvFragmentComment, tvFragmentFollowers;
    TextView tvText1, tvText2, tvText3, tvText4;
    ProgressBar progress;
    boolean isFirstTime = true;
    String ACTIVITY_TYPE = "All";
    int offsetAll = 0;
    int offsetOther = 0;
    int limit = 30;
    long apiCalledTime = 0;
    LinearLayout mTabBarLinearLayout;
    RelativeLayout defaultViewForFirstTimeUser;
    private String selectedScreen = "";
    //ArrayList<TimeLineDataModel> timeLineModelArrayList;

    public TimelineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userName            Parameter 1.
     * @param fromLoggedInProfile Parameter 2.
     * @param selectedScreen      Parameter 3.
     * @return A new instance of fragment TimelineFragment.
     */
    public static TimelineFragment newInstance(String userName, boolean fromLoggedInProfile, String selectedScreen) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_USERNAME, userName);
        args.putBoolean(ARG_FROM_LOGGED_IN_PROFILE, fromLoggedInProfile);
        args.putString("selectedScreen", selectedScreen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamUserName = getArguments().getString(ARG_PARAM_USERNAME);
            mFromLoggedInProfile = getArguments().getBoolean(ARG_FROM_LOGGED_IN_PROFILE);
            selectedScreen = getArguments().getString("selectedScreen");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Fragment", "timeline fragment");
        currentContext = getActivity();
        tradWyseSession = TradWyseSession.getSharedInstance(getActivity());
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvText1 = view.findViewById(R.id.text1);
        tvText2 = view.findViewById(R.id.text2);
        tvText3 = view.findViewById(R.id.text3);
        tvText4 = view.findViewById(R.id.text4);
        tvFragmentAll = view.findViewById(R.id.tv_all);
        mTabBarLinearLayout = view.findViewById(R.id.tab_bar);
        tvFragmentSavedTips = view.findViewById(R.id.tv_saved_tips);
        tvFragmentComment = view.findViewById(R.id.tv_comment);
        tvFragmentFollowers = view.findViewById(R.id.tv_followers);

        progress = view.findViewById(R.id.progress);
        defaultViewForFirstTimeUser = view.findViewById(R.id.default_view_for_first_time_user);
        tvFragmentAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFragAllClicked();
            }
        });
        tvFragmentSavedTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFragSavedTipsClicked();
            }
        });
        tvFragmentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFragCommentsClicked();
            }
        });
        tvFragmentFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFragFollwersClicked();
            }
        });
        recList = view.findViewById(R.id.recTimeLineList);

        setEmptyDataText();
        if (((ProfileTabbedActivity) getActivity()).fromLoggedInProfile) {
            (view.findViewById(R.id.frag_timeline_parent_rl)).setBackgroundColor(getResources().getColor(R.color.transparent));
            recList.setBackgroundColor(getResources().getColor(R.color.transparent));
        } else {
            (view.findViewById(R.id.frag_timeline_parent_rl)).setBackgroundColor(getResources().getColor(R.color.transparent));
            recList.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
        initScrollListener();
    }

    private void setEmptyDataText() {
        String s1 = getActivity().getResources().getString(R.string._1_track_the_comments_you_ve_made_on_mentor_tips);
        String s2 = getActivity().getResources().getString(R.string._2_save_tips_and_see_their_progress_here);
        String s3 = getActivity().getResources().getString(R.string._3_share_tips_and_chat_with_your_mentors_followers);
        String s4 = getActivity().getResources().getString(R.string.hint_visit_other_people_s_profiles_to_connect_and_learn_from_them);

        tvText1.setText(s1);
        tvText2.setText(s2);
        tvText3.setText(s3);
        tvText4.setText(s4);

    }

    @NotNull
    private SpannableString getSpannableString(String s, int start, int end) {
        SpannableString ss1 = new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(1.5f), start, end, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, 0);// set color
        return ss1;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ACTIVITY_TYPE.equalsIgnoreCase("All")) {
            if (apiCalledTime == 0 || (System.currentTimeMillis() - apiCalledTime) > 2000) {
                Logger.error("TImelineFragment 201", "Calling getTimeLine api with user name " + mParamUserName);
                apiCalledTime = System.currentTimeMillis();
                getTimeLine(mParamUserName);
            }
        } else {
            Logger.error("TImelineFragment 204", "Calling getTimeLineByActivityType api withActivity " + ACTIVITY_TYPE + " user name " + mParamUserName);
            getTimeLineByActivityType(ACTIVITY_TYPE, mParamUserName);
        }
    }

    private void tvFragAllClicked() {
        tvFragmentAll.setBackground(getResources().getDrawable(R.drawable.background_dark_button));
        tvFragmentAll.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentComment.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentComment.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentFollowers.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentFollowers.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentSavedTips.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentSavedTips.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        ACTIVITY_TYPE = "All";
        isFirstTime = true;
        offsetAll = 0;
        offsetOther = 0;
        Logger.error("TImelineFragment 226", "Calling getTimeLine api with user name " + mParamUserName);
        getTimeLine(mParamUserName);
    }

    private void tvFragSavedTipsClicked() {
        tvFragmentSavedTips.setBackground(getResources().getDrawable(R.drawable.background_dark_button));
        tvFragmentSavedTips.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentAll.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentAll.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentComment.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentComment.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentFollowers.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentFollowers.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        ACTIVITY_TYPE = ACTIVITY_TYPE_TIP_PIN;
        isFirstTime = true;
        offsetAll = 0;
        offsetOther = 0;
        Logger.error("TImelineFragment 247", "Calling getTimeLineByActivityType api withActivity " + ACTIVITY_TYPE + " user name " + mParamUserName);
        getTimeLineByActivityType(ACTIVITY_TYPE_TIP_PIN, mParamUserName);

    }

    private void tvFragCommentsClicked() {
        tvFragmentSavedTips.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentSavedTips.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentAll.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentAll.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentComment.setBackground(getResources().getDrawable(R.drawable.background_dark_button));
        tvFragmentComment.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentFollowers.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentFollowers.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        ACTIVITY_TYPE = ACTIVITY_TYPE_COMMENT;
        isFirstTime = true;
        offsetAll = 0;
        offsetOther = 0;
        Logger.error("TImelineFragment 269", "Calling getTimeLineByActivityType api withActivity " + ACTIVITY_TYPE + " user name " + mParamUserName);
        getTimeLineByActivityType(ACTIVITY_TYPE_COMMENT, mParamUserName);

    }

    private void tvFragFollwersClicked() {
        tvFragmentSavedTips.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentSavedTips.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentAll.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentAll.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentComment.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        tvFragmentComment.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        tvFragmentFollowers.setBackground(getResources().getDrawable(R.drawable.background_dark_button));
        tvFragmentFollowers.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        ACTIVITY_TYPE = Constants.ACTIVITY_TYPE_FOLLOW_USER;
        isFirstTime = true;
        offsetAll = 0;
        offsetOther = 0;
        Logger.error("TImelineFragment 291", "Calling getTimeLineByActivityType api withActivity " + ACTIVITY_TYPE + " user name " + mParamUserName);
        getTimeLineByActivityType(Constants.ACTIVITY_TYPE_FOLLOW_USER, mParamUserName);
    }

    public void getTimeLine(String userName) {
        progress.setVisibility(View.VISIBLE);
        if (isFirstTime) {
            Common.disableInteraction((AppCompatActivity) getActivity());
            isFirstTime = false;
        }
        //Toast.makeText(currentContext, "Calling getTimeLine with userName:- " + userName + " offset:- " + offsetAll + " Limit:- " + limit, Toast.LENGTH_SHORT).show();
        if (mAdapter != null && (mAdapter.getAdapterArrayList() == null || mAdapter.getAdapterArrayList().isEmpty())) {
            offsetAll = 0;
        }
        Logger.error("TImelineFragment 308", "Calling getTimeLine api with user name " + userName + " offsetAll:- " + offsetAll + " limit " + limit);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getActivity());
        Call<ArrayList<TimeLineDataModel>> call = apiInterface.getTimeLine(userName, offsetAll, limit);
        call.enqueue(new Callback<ArrayList<TimeLineDataModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TimeLineDataModel>> call, Response<ArrayList<TimeLineDataModel>> response) {
              //  Logger.error("TImelineFragment 314", "Response of  getTimeLine api with user name " + userName + " Response:- " + response.body().toString()+" list size "+response.body().size());
                if (response.body() != null && response.body().size() > 0) {
                    int scrollToPosition = 0;
                    ArrayList<TimeLineDataModel> timeLineModelArrayList = new ArrayList<>();
                    if (mAdapter != null)
                        timeLineModelArrayList = mAdapter.getAdapterArrayList();
                    else
                        timeLineModelArrayList = new ArrayList<>();
                    ArrayList<TimeLineDataModel> updatedTimeLineModelArrayList = response.body();
                    Logger.debug("TimeLineFragment", updatedTimeLineModelArrayList.size() + "");
                    if (offsetAll == 0 || timeLineModelArrayList == null) {
                        timeLineModelArrayList = new ArrayList<>(updatedTimeLineModelArrayList);
                    } else {
                        scrollToPosition = timeLineModelArrayList.size();
                        timeLineModelArrayList.addAll(updatedTimeLineModelArrayList);
                    }
                    timeLineModelArrayList = shortArrayDatewiseAndAddDate(timeLineModelArrayList);
                    if (timeLineModelArrayList.size() >= scrollToPosition)
                        lastItemSelectedPosition = scrollToPosition;
                    setDataInRV(timeLineModelArrayList, true);
                } else {
                    if (offsetAll == 0 || mAdapter == null || mAdapter.getAdapterArrayList() == null || mAdapter.getAdapterArrayList().isEmpty()) {
                        //Logger.error("TimelineFragment", "NO Data is available from api getTimeLine");
                        setDataInRV(new ArrayList<>(), true);
                    } else {
                        //setDataInRV(mAdapter.getAdapterArrayList(), true);
                        offsetAll = offsetAll - limit >= 0 ? offsetAll - limit : 0;
                    }
                }
                progress.setVisibility(View.GONE);
                Common.enableInteraction((AppCompatActivity) getActivity());
            }

            @Override
            public void onFailure(Call<ArrayList<TimeLineDataModel>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                if (offsetAll == 0 || mAdapter == null || mAdapter.getAdapterArrayList() == null || mAdapter.getAdapterArrayList().isEmpty()) {
                          } else
                    offsetAll = offsetAll - limit >= 0 ? offsetAll - limit : 0;
                Common.enableInteraction((AppCompatActivity) getActivity());
                t.printStackTrace();
                Log.e(Constants.ON_FAILURE_TAG, "TimeLineFragment getTimeLine: onFailure");
            }
        });
    }

    public void getTimeLineByActivityType(String activityType, String userName) {
        progress.setVisibility(View.VISIBLE);
        if (isFirstTime) {
            Common.disableInteraction((AppCompatActivity) getActivity());
            isFirstTime = false;
        }
        //Toast.makeText(currentContext, "Calling getTimeLineByActivityType with ActiityType:- " + activityType + " userName:- " + userName + " offsetOther:- " + offsetOther + " Limit:- " + limit, Toast.LENGTH_SHORT).show();
        if (mAdapter != null && (mAdapter.getAdapterArrayList() == null || mAdapter.getAdapterArrayList().isEmpty())) {
            offsetOther = 0;
        }
        Logger.error("TImelineFragment 372", "Calling getTimeLineByActivityType api with user name " + userName + " offsetother:- " + offsetOther + " limit " + limit);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getActivity());
        Call<ArrayList<TimeLineDataModel>> call = apiInterface.getTimeLineActivityWise(activityType, userName, offsetOther, limit);
        call.enqueue(new Callback<ArrayList<TimeLineDataModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TimeLineDataModel>> call, Response<ArrayList<TimeLineDataModel>> response) {
                Logger.error("TImelineFragment 378", "Response of  getTimeLineByActivityType api with user name " + userName + " Response:- " + response.body().toString()+" list size "+response.body().size());
                if (response.body() != null && response.body().size() > 0) {
                    int scrollToPosition = 0;
                    ArrayList<TimeLineDataModel> timeLineModelArrayList = new ArrayList<>();
                    if (mAdapter != null)
                        timeLineModelArrayList = mAdapter.getAdapterArrayList();
                    else
                        timeLineModelArrayList = new ArrayList<>();
                    ArrayList<TimeLineDataModel> updatedTimeLineModelArrayList = response.body();
                    updatedTimeLineModelArrayList = removeNullDataFromArray(updatedTimeLineModelArrayList);
                    Logger.debug("TimeLineFragment", updatedTimeLineModelArrayList.size() + "");

                    if (offsetOther == 0 || timeLineModelArrayList == null) {
                        timeLineModelArrayList = new ArrayList<>(updatedTimeLineModelArrayList);
                    } else {
                        scrollToPosition = timeLineModelArrayList.size();
                        timeLineModelArrayList.addAll(updatedTimeLineModelArrayList);
                    }
                    timeLineModelArrayList = shortArrayDatewiseAndAddDate(timeLineModelArrayList);
                    if (timeLineModelArrayList.size() >= scrollToPosition)
                        lastItemSelectedPosition = scrollToPosition;
                    if (timeLineModelArrayList.isEmpty())
                        Logger.error("TimelineFragment", "NO Data is available from api getTimeLineByActivityType");
                    setDataInRV(timeLineModelArrayList, false);
                } else {
                    Logger.error("TimelineFragment", "NO Data is available from api getTimeLineByActivityType");
                    if (offsetOther == 0 || mAdapter == null || mAdapter.getAdapterArrayList() == null || mAdapter.getAdapterArrayList().isEmpty()) {
                        setDataInRV(new ArrayList<>(), false);
                    } else {
                        offsetOther = offsetOther - limit >= 0 ? offsetOther - limit : 0;
                        //setDataInRV(mAdapter.getAdapterArrayList(), false);
                    }
                }
                progress.setVisibility(View.GONE);
                Common.enableInteraction((AppCompatActivity) getActivity());
            }

            @Override
            public void onFailure(Call<ArrayList<TimeLineDataModel>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                if (offsetOther == 0 || mAdapter == null || mAdapter.getAdapterArrayList() == null || mAdapter.getAdapterArrayList().isEmpty()) {

                    //Logger.error("TimelineFragment", "NO Data is available from api getTimeLineWithPaginationForUser");
                    //setDataInRV(new ArrayList<>(), false);
                } else
                    offsetOther = offsetOther - limit >= 0 ? offsetOther - limit : 0;
                Common.enableInteraction((AppCompatActivity) getActivity());
                t.printStackTrace();
                Log.e(Constants.ON_FAILURE_TAG, "TimeLineFragment getTimeLine: onFailure");
            }
        });
    }


    private void initScrollListener() {
        recList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == recList.getAdapter().getItemCount() - 1) {
                    if (ACTIVITY_TYPE.equalsIgnoreCase("All")) {
//                        Toast.makeText(currentContext, "Loading more data for all", Toast.LENGTH_SHORT).show();
                        if (recList.getAdapter().getItemCount() > 5) {
                            offsetAll += limit;
                            Logger.error("TImelineFragment 425", "Calling getTimeLine api with user name " + mParamUserName);
                            getTimeLine(mParamUserName);
                        }
                    } else {
//                        Toast.makeText(currentContext, "Loading more data for " + ACTIVITY_TYPE, Toast.LENGTH_SHORT).show();
                        if (recList.getAdapter().getItemCount() > 5) {
                            offsetOther += limit;
                            Logger.error("TImelineFragment 436", "Calling getTimeLineByActivityType api withActivity " + ACTIVITY_TYPE + " user name " + mParamUserName);
                            getTimeLineByActivityType(ACTIVITY_TYPE, mParamUserName);
                        }
                    }
                }
            }
        });

    }

    private ArrayList<TimeLineDataModel> removeNullDataFromArray(ArrayList<TimeLineDataModel> timeLineModelArrayList) {
        for (int i = 0; i < timeLineModelArrayList.size(); i++) {
            if (timeLineModelArrayList.get(i).getActivityType().equalsIgnoreCase(ACTIVITY_TYPE_TIP_PIN)) {
                if (timeLineModelArrayList.get(i).getTipResponse() == null)
                    timeLineModelArrayList.remove(i);
            } else if (timeLineModelArrayList.get(i).getActivityType().equalsIgnoreCase(ACTIVITY_TYPE_COMMENT)) {
                if (timeLineModelArrayList.get(i).getTipResponse() == null)
                    timeLineModelArrayList.remove(i);
            }
        }

        return timeLineModelArrayList;
    }

    private ArrayList<TimeLineDataModel> shortArrayDatewiseAndAddDate(ArrayList<TimeLineDataModel> timeLineModelArrayList) {
        for (int i = 0; i < timeLineModelArrayList.size(); i++) {
            if (timeLineModelArrayList.get(i).getActivityType().equals(Constants.ACTIVITY_TYPE_DATE_ROW)) {
                timeLineModelArrayList.remove(i);
            }
        }
        if (timeLineModelArrayList.size() > 0) {
            Collections.sort(timeLineModelArrayList, new Comparator<TimeLineDataModel>() {
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

                @Override
                public int compare(TimeLineDataModel lhs, TimeLineDataModel rhs) {
                    try {
                        return f.parse(rhs.getModifiedTime()).compareTo(f.parse(lhs.getModifiedTime()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });

            Long timeInMiliSeconds = DateTimeHelperElapsed.getTimeInMiliSeconds(timeLineModelArrayList.get(0).getModifiedTime());
            String tempDate = DateTimeHelperElapsed.toString(timeInMiliSeconds, "yyyy-MM-dd");
            int section = 0;
            timeLineModelArrayList.add(0, new TimeLineDataModel(Constants.ACTIVITY_TYPE_DATE_ROW, timeLineModelArrayList.get(0).getModifiedTime(), section));
            for (int i = 0; i < timeLineModelArrayList.size(); i++) {
                timeInMiliSeconds = DateTimeHelperElapsed.getTimeInMiliSeconds(timeLineModelArrayList.get(i).getModifiedTime());
                if (!tempDate.equalsIgnoreCase(DateTimeHelperElapsed.toString(timeInMiliSeconds, "yyyy-MM-dd"))) {
                    section = i;
                    timeLineModelArrayList.add(i, new TimeLineDataModel(Constants.ACTIVITY_TYPE_DATE_ROW, timeLineModelArrayList.get(i).getModifiedTime(), section));
                    tempDate = DateTimeHelperElapsed.toString(timeInMiliSeconds, "yyyy-MM-dd");
                } else {
                    timeLineModelArrayList.get(i).setSection(section);
                }
            }
        }
        return timeLineModelArrayList;
    }

    private void setDataInRV(ArrayList<TimeLineDataModel> timeLineModelArrayList, boolean isGetTimeLineForAll) {
        progress.setVisibility(View.VISIBLE);
        if (timeLineModelArrayList.isEmpty()) {
            defaultViewForFirstTimeUser.setVisibility(View.VISIBLE);
            recList.setVisibility(View.GONE);
            //Logger.error("TImilineFragment","Hiding recList");
            if (isGetTimeLineForAll) {
                //Logger.error("TImilineFragment","Hiding mTabBarLinearLayout");
                mTabBarLinearLayout.setVisibility(View.GONE);
            }
        } else {
            recList.setVisibility(View.VISIBLE);
            mTabBarLinearLayout.setVisibility(View.VISIBLE);
            defaultViewForFirstTimeUser.setVisibility(View.GONE);
            mAdapter = new TimeLineAdapter(timeLineModelArrayList, currentContext, this, ((ProfileTabbedActivity) getActivity()), tradWyseSession, mFromLoggedInProfile, mParamUserName, selectedScreen);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recList.setLayoutManager(mLayoutManager);
            recList.setItemAnimator(new DefaultItemAnimator());
            recList.setAdapter(mAdapter);
            StickyHeaderItemDecorator decorator = new StickyHeaderItemDecorator(mAdapter);
            decorator.attachToRecyclerView(recList);
            mAdapter.notifyDataSetChanged();
            if (!isFirstTime && lastItemSelectedPosition > 0 && timeLineModelArrayList.size() > lastItemSelectedPosition) {
                recList.scrollToPosition(lastItemSelectedPosition - 2);

            }
        }
        progress.setVisibility(View.GONE);
    }

    @Override
    public void redirectToProfile(String userName, String userId) {
        if (mParamUserName != null && !mParamUserName.equalsIgnoreCase(userName))
            ProfileTabbedActivity.starProfileTabbedtActivity(getActivity(), false, userName,userId, selectedScreen);
    }

    @Override
    public void loadProfileImageByUserName(String userName, int position) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getActivity());
        Call<ResponseBody> call = apiInterface.getUserImageByUserName(userName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                    ArrayList<TimeLineDataModel> timeLineDataModelArrayList = mAdapter.getAdapterArrayList();
                    if (timeLineDataModelArrayList.size() >= position && timeLineDataModelArrayList.get(position).getFollowUserName().equalsIgnoreCase(userName)) {
                        timeLineDataModelArrayList.get(position).setFollowUserImageBitmap(bmp);
                        mAdapter.changeValueInItemOfAdapterArrayList(timeLineDataModelArrayList.get(position), position);
                    }
                }
                progress.setVisibility(View.GONE);
                Common.enableInteraction((AppCompatActivity) getActivity());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Common.enableInteraction((AppCompatActivity) getActivity());
                t.printStackTrace();
                Log.e(Constants.ON_FAILURE_TAG, "TimeLineFragment getTimeLine: onFailure");
            }
        });
    }

    @Override
    public void positionOfItemClicked(int position) {
        lastItemSelectedPosition = position;
    }
}
