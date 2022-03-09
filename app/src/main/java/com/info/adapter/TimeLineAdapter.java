package com.info.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.DateTimeHelperElapsed;
import com.info.commons.StringHelper;
import com.info.commons.TradWyseSession;
import com.info.fragment.TimelineFragment;
import com.info.logger.Logger;
import com.info.model.FollowUserModel;
import com.info.model.IsFollowResponse;
import com.info.model.timelineModels.TimeLineDataModel;
import com.info.tradewyse.ProfileTabbedActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.TipDetailActivity;
import com.shuhart.stickyheader.StickyAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amit on 3/07/2020.
 */

public class TimeLineAdapter extends StickyAdapter<RecyclerView.ViewHolder, RecyclerView.ViewHolder> {
    private static int TYPE_FOLLOW_USER = 1;
    private static int TYPE_TIP_TWEET_DETAIL = 2;
    private static int TYPE_TIP_PIN = 3;
    private static int TYPE_COMMENT = 4;
    private static int TYPE_DATE = 5;

    ArrayList<TimeLineDataModel> timeLineModelArrayList;
    static Context context;
    TimelineFragment timelineFragment;
    ProfileTabbedActivity profileTabbedActivityContext;
    TradWyseSession tradWyseSession;
    boolean fromLoggedInProfile;
    private String selectedScreen = "";
    String otherUserName;

    public TimeLineAdapter(ArrayList<TimeLineDataModel> timeLineModelArrayList, Context context, TimelineFragment timelineFragment, ProfileTabbedActivity profileTabbedActivityContext, TradWyseSession tradWyseSession, boolean fromLoggedInProfile, String otherUserName, String selectedScreen) {
        this.timeLineModelArrayList = timeLineModelArrayList;
        this.context = context;
        this.timelineFragment = timelineFragment;
        this.profileTabbedActivityContext = profileTabbedActivityContext;
        this.tradWyseSession = tradWyseSession;
        this.fromLoggedInProfile = fromLoggedInProfile;
        this.otherUserName = otherUserName;
        this.selectedScreen = selectedScreen;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOLLOW_USER) {
            itemView = LayoutInflater.from(context).inflate(R.layout.timeline_follow_row_layout, parent,
                    false);
            return new FollowUserViewHolder(itemView);

        } else if (viewType == TYPE_TIP_TWEET_DETAIL) {
            itemView = LayoutInflater.from(context).inflate(R.layout.timeline_tip_tweet_detail_row_layout, parent,
                    false);
            return new TweetDetailRowViewHolder(itemView);

        } else if (viewType == TYPE_TIP_PIN) {
            itemView = LayoutInflater.from(context).inflate(R.layout.timeline_tip_pin_row_layout, parent,
                    false);
            return new TipPinRowViewHolder(itemView);

        } else if (viewType == TYPE_COMMENT) {
            itemView = LayoutInflater.from(context).inflate(R.layout.timeline_comment_row_layout, parent,
                    false);
            return new CommentRowViewHolder(itemView);

        } else if (viewType == TYPE_DATE) {
            itemView = LayoutInflater.from(context).inflate(R.layout.timeline_date_row_layout, parent,
                    false);
            return new DateTypeViewHolder(itemView);

        } else {
            itemView = LayoutInflater.from(context).inflate(R.layout.timeline_unknown_case_row_layout, parent,
                    false);
            return new UnknownTypeViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TimeLineDataModel timeLineDataModel = timeLineModelArrayList.get(position);
        if (timeLineDataModel.getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_FOLLOW_USER)) {
            FollowUserViewHolder followUserViewHolder = (FollowUserViewHolder) holder;
            SpannableString ss1 = new SpannableString(timeLineDataModel.getFollowUserName() + " followed You.");
            if (fromLoggedInProfile) {
                followUserViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg));

            } else {
                followUserViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_other_profile_bg));

            }
            if (fromLoggedInProfile || tradWyseSession.getUserName().equalsIgnoreCase(otherUserName)) {
                ss1 = new SpannableString(timeLineDataModel.getFollowUserName() + " followed You");
            } else {
                if (tradWyseSession.getUserName().equalsIgnoreCase(timeLineDataModel.getFollowUserName())) {
                    ss1 = new SpannableString("You" + " followed " + otherUserName);
                } else {
                    ss1 = new SpannableString(timeLineDataModel.getFollowUserName() + " followed " + otherUserName);
                }
            }
            ss1.setSpan(new RelativeSizeSpan(1.1f), 0, timeLineDataModel.getFollowUserName().length(), 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, timeLineDataModel.getFollowUserName().length(), 0);// set color
            ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, timeLineDataModel.getFollowUserName().length(), 0);// set bold

            followUserViewHolder.txtName.setText(ss1);
            Long timeInMiliseconds = DateTimeHelperElapsed.getTimeInMiliSeconds(timeLineDataModel.getModifiedTime());
            followUserViewHolder.txtTime.setText(DateTimeHelperElapsed.toStringNYTtime(timeInMiliseconds, "hh:mm a") + "  "
                    + context.getResources().getString(R.string.TimeZone));
            /*if (timeLineDataModel.getFollowUserImageBitmap() == null && !timeLineDataModel.isAlreadyCalledGetImageApi()) {
                timeLineDataModel.setAlreadyCalledGetImageApi(true);
                timelineFragment.loadProfileImageByUserName(timeLineDataModel.getFollowUserName(), position);
            } else {
                Common.loadBitmapByPicasso(context, timeLineDataModel.getFollowUserImageBitmap(), followUserViewHolder.imgUser);
            }*/
            Common.setProfileImage(followUserViewHolder.imgUser, timeLineDataModel.getFollowUserId());
            if (tradWyseSession.getUserName().equalsIgnoreCase(timeLineDataModel.getFollowUserName())) {
                followUserViewHolder.tvFollowBtn.setVisibility(View.GONE);
                followUserViewHolder.tvYouAre.setVisibility(View.GONE);
            } else {
                followUserViewHolder.tvYouAre.setText(timeLineDataModel.getFollowUserName());
                isLoginUserFollowGivenUser(timeLineDataModel.getFollowUserName(), followUserViewHolder.tvFollowBtn);
                followUserViewHolder.tvFollowBtn.setVisibility(View.VISIBLE);
                followUserViewHolder.tvYouAre.setVisibility(View.VISIBLE);
            }
            followUserViewHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timelineFragment.positionOfItemClicked(position);
                    timelineFragment.redirectToProfile(timeLineDataModel.getFollowUserName(), timeLineDataModel.getFollowUserId());
                }
            });

            followUserViewHolder.tvFollowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (followUserViewHolder.tvFollowBtn.getText().toString().equalsIgnoreCase(context.getResources().getString(R.string.Following))) {
                        showUnFollowDialog(timeLineDataModel.getFollowUserName(), followUserViewHolder.tvFollowBtn);
                    } else {
                        followUser(timeLineDataModel.getFollowUserName(), followUserViewHolder.tvFollowBtn);
                        // Toast.makeText(context,context.getResources().getString(R.string.Messagefollow)+" "+timeLineDataModel.getFollowUserName(),Toast.LENGTH_SHORT).show();


                    }
                }
            });

        } else if (timeLineDataModel.getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_TIP_TWEET_DETAIL)) {
            TweetDetailRowViewHolder tweetDetailRowViewHolder = (TweetDetailRowViewHolder) holder;
            if (fromLoggedInProfile) {
                tweetDetailRowViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg));

            } else {
                tweetDetailRowViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_other_profile_bg));

            }
            tweetDetailRowViewHolder.tvTweet.setText(timeLineDataModel.getTweetText());
            Long timeInMiliSeconds = DateTimeHelperElapsed.getTimeInMiliSeconds(timeLineDataModel.getModifiedTime());
            tweetDetailRowViewHolder.txtTime.setText(DateTimeHelperElapsed.toStringNYTtime(timeInMiliSeconds, "hh:mm a") + "  "
                    + context.getResources().getString(R.string.TimeZone));
            tweetDetailRowViewHolder.llTweetDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timelineFragment.positionOfItemClicked(position);
                    TipDetailActivity.startActivity(context, timeLineDataModel.getTipResponse(), false, selectedScreen);
                }
            });
        }
        else if (timeLineDataModel.getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_TIP_PIN)) {
            TipPinRowViewHolder tipPinRowViewHolder = (TipPinRowViewHolder) holder;
            if (fromLoggedInProfile) {
                tipPinRowViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg));

            } else {
                tipPinRowViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_other_profile_bg));

            }
            if (timeLineDataModel.getTipResponse() != null) {
                tipPinRowViewHolder.tvUserName.setText(timeLineDataModel.getTipResponse().getTip().getAppUser().getUserName());
                tipPinRowViewHolder.tvStockName.setText(timeLineDataModel.getTipResponse().getTip().getStockName());
                tipPinRowViewHolder.tvEntryPointPrice.setText(StringHelper.getValue(Common.formatDouble(timeLineDataModel.getTipResponse().getTip().getEntryPoint()), "--"));
                tipPinRowViewHolder.tvCreateTipPrice.setText(StringHelper.getValue(Common.formatDouble(timeLineDataModel.getTipResponse().getTip().getCreateTipPrice()), "--"));
                tipPinRowViewHolder.tvExitPointPrice.setText(StringHelper.getValue(Common.formatDouble(timeLineDataModel.getTipResponse().getTip().getExitpoint()), "--"));
                tipPinRowViewHolder.tvStopPointPrice.setText(StringHelper.getValue(Common.formatDouble(timeLineDataModel.getTipResponse().getTip().getStopPoint()), "--"));

                Common.setEntryExitColor(context, timeLineDataModel.getTipResponse().getTip().getEntryPoint(), timeLineDataModel.getTipResponse().getTip().getExitpoint(), timeLineDataModel.getTipResponse().getTip().getStopPoint(), timeLineDataModel.getTipResponse().getTip().getStockPrice(), timeLineDataModel.getTipResponse().getTip().getCreateTipPrice(), timeLineDataModel.getTipResponse().getTip().getStockSuggestion(), tipPinRowViewHolder.tvEntryPointPrice, tipPinRowViewHolder.tvExitPointPrice, tipPinRowViewHolder.tvStopPointPrice);

                Log.d("stockprice===", timeLineDataModel.getTipResponse().getTip().getStockPrice() + "===");

                tipPinRowViewHolder.tvStockSuggestion.setText(timeLineDataModel.getTipResponse().getTip().getStockSuggestion());
                if (timeLineDataModel.getTipResponse().getTip().getStockSuggestion().toUpperCase().equalsIgnoreCase("avoid")) {
                    tipPinRowViewHolder.tvStockSuggestion.setTextColor(Color.parseColor("#707890"));
                    tipPinRowViewHolder.tvStockSuggestion.setBackgroundResource(R.drawable.text_backgroud_black);
                } else if (timeLineDataModel.getTipResponse().getTip().getStockSuggestion().toUpperCase().equalsIgnoreCase("sell")) {
                    tipPinRowViewHolder.tvStockSuggestion.setBackgroundResource(R.drawable.text_backgroud_red);
                    tipPinRowViewHolder.tvStockSuggestion.setTextColor(Color.parseColor("#a94018"));
                } else if (timeLineDataModel.getTipResponse().getTip().getStockSuggestion().toUpperCase().equalsIgnoreCase("buy")) {
                    tipPinRowViewHolder.tvStockSuggestion.setBackgroundResource(R.drawable.text_backgroud_green);
                    tipPinRowViewHolder.tvStockSuggestion.setTextColor(Color.parseColor("#1e841c"));
                }

                Common.changeLikeBtnBg(context, tipPinRowViewHolder.ivLike, timeLineDataModel.getTipResponse().isUserLikeStatus());
                Common.setProfileImage(tipPinRowViewHolder.ivUserImg, timeLineDataModel.getTipResponse().getTip().getAppUser().getId());

                tipPinRowViewHolder.tvlike.setText("" + timeLineDataModel.getTipResponse().getLikeCount());
                tipPinRowViewHolder.tv_comment_count.setText("" + timeLineDataModel.getTipResponse().getCommentCount());
                Long timeInMiliSeconds = DateTimeHelperElapsed.getTimeInMiliSeconds(timeLineDataModel.getModifiedTime());
                tipPinRowViewHolder.txtTime.setText(DateTimeHelperElapsed.toStringNYTtime(timeInMiliSeconds, "hh:mm a") + "  "
                        + context.getResources().getString(R.string.TimeZone));
                tipPinRowViewHolder.llUserNameImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timelineFragment.positionOfItemClicked(position);
                        timelineFragment.redirectToProfile(timeLineDataModel.getTipResponse().getTip().getAppUser().getUserName(), timeLineDataModel.getTipResponse().getTip().getAppUser().getId());
                    }
                });
                tipPinRowViewHolder.rlTipDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timelineFragment.positionOfItemClicked(position);
                        TipDetailActivity.startActivity(context, timeLineDataModel.getTipResponse(), false, selectedScreen);
                    }
                });
            }
        }
        else if (timeLineDataModel.getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_COMMENT)) {
            CommentRowViewHolder commentRowViewHolder = (CommentRowViewHolder) holder;
            if (fromLoggedInProfile) {
                commentRowViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg));

            } else {
                commentRowViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_other_profile_bg));

            }

            if (timeLineDataModel.getTipResponse() != null) {
                SpannableString ss1 = new SpannableString("You commented on " + timeLineDataModel.getTipResponse().getTip().getStockName());
                if (fromLoggedInProfile || tradWyseSession.getUserName().equalsIgnoreCase(otherUserName)) {
                    ss1 = new SpannableString("You commented on " + timeLineDataModel.getTipResponse().getTip().getStockName());
                } else {
                    ss1 = new SpannableString(otherUserName + " commented on " + timeLineDataModel.getTipResponse().getTip().getStockName());
                }
                ss1.setSpan(new RelativeSizeSpan(1.1f), (ss1.length() - timeLineDataModel.getTipResponse().getTip().getStockName().length()), ss1.length(), 0); // set size
                ss1.setSpan(new ForegroundColorSpan(Color.WHITE), (ss1.length() - timeLineDataModel.getTipResponse().getTip().getStockName().length()), ss1.length(), 0);// set color
                ss1.setSpan(new StyleSpan(Typeface.BOLD), (ss1.length() - timeLineDataModel.getTipResponse().getTip().getStockName().length()), ss1.length(), 0);// set bold

                commentRowViewHolder.tvComment.setText(ss1);
                commentRowViewHolder.tvCommentCount.setText("" + timeLineDataModel.getTipResponse().getCommentCount());
                commentRowViewHolder.tvLikeCount.setText("" + timeLineDataModel.getTipResponse().getLikeCount());
                Long timeInMiliSeconds = DateTimeHelperElapsed.getTimeInMiliSeconds(timeLineDataModel.getModifiedTime());
                commentRowViewHolder.txtTime.setText(DateTimeHelperElapsed.toStringNYTtime(timeInMiliSeconds, "hh:mm a") + "  "
                        + context.getResources().getString(R.string.TimeZone));

                Common.changeLikeBtnBg(context, commentRowViewHolder.ivLike, timeLineDataModel.getTipResponse().isUserLikeStatus());
                commentRowViewHolder.llCommentRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timelineFragment.positionOfItemClicked(position);
                        TipDetailActivity.startActivity(context, timeLineDataModel.getTipResponse(), false, selectedScreen);
                    }
                });
            }
        } else if (timeLineDataModel.getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_DATE_ROW)) {
            DateTypeViewHolder dateTypeViewHolder = (DateTypeViewHolder) holder;
            if (fromLoggedInProfile) {
                dateTypeViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg));

            } else {
                dateTypeViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_other_profile_bg));

            }
            Long currentNYTInMiliseconds = DateTimeHelperElapsed.getNewYorkCurrentTimeInMiliSec();
            Long timeInMiliseconds = DateTimeHelperElapsed.getTimeInMiliSeconds(timeLineDataModel.getModifiedTime());
            Long yesterdayTimeInMiliSeconds = currentNYTInMiliseconds - (1000 * 60 * 60 * 24);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Constants.NY_ZONE));

            Date yesterdayDate = new Date(DateTimeHelperElapsed.toStringNYTtime(yesterdayTimeInMiliSeconds, "MMM dd, yyyy"));
            Date currentDate = new Date(DateTimeHelperElapsed.toStringNYTtime(currentNYTInMiliseconds, "MMM dd, yyyy"));
            Date serverDate = new Date(DateTimeHelperElapsed.toStringNYTtime(timeInMiliseconds, "MMM dd, yyyy"));

            Logger.debug("timeCompare", "" + currentDate.compareTo(serverDate));
            if (currentDate.compareTo(serverDate) == 0) {
                dateTypeViewHolder.txtTime.setText("Today");
            } else if (yesterdayDate.compareTo(serverDate) == 0) {
                dateTypeViewHolder.txtTime.setText("Yesterday");
            } else {
                dateTypeViewHolder.txtTime.setText(DateTimeHelperElapsed.toStringNYTtime(timeInMiliseconds, "MMM dd, yyyy"));
            }
        } else {
            UnknownTypeViewHolder unknownTypeViewHolder = (UnknownTypeViewHolder) holder;
            unknownTypeViewHolder.tvType.setText(timeLineDataModel.getActivityType());
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (timeLineModelArrayList.get(position).getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_FOLLOW_USER)) {
            return TYPE_FOLLOW_USER;
        } else if (timeLineModelArrayList.get(position).getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_TIP_TWEET_DETAIL)) {
            return TYPE_TIP_TWEET_DETAIL;
        } else if (timeLineModelArrayList.get(position).getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_TIP_PIN)) {
            return TYPE_TIP_PIN;
        } else if (timeLineModelArrayList.get(position).getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_COMMENT)) {
            return TYPE_COMMENT;
        } else if (timeLineModelArrayList.get(position).getActivityType().equalsIgnoreCase(Constants.ACTIVITY_TYPE_DATE_ROW)) {
            return TYPE_DATE;
        } else {
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return timeLineModelArrayList.size();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        if (itemPosition >= timeLineModelArrayList.size()) {
            return -1;
        }
        return timeLineModelArrayList.get(itemPosition).getSection();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int headerPosition) {
        if (headerPosition != -1) {
            DateTypeViewHolder dateTypeViewHolder = (DateTypeViewHolder) holder;
            if (fromLoggedInProfile) {
                dateTypeViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg));

            } else {
                dateTypeViewHolder.parentView.setBackgroundColor(context.getResources().getColor(R.color.color_other_profile_bg));

            }
            Long currentNYTInMiliseconds = DateTimeHelperElapsed.getNewYorkCurrentTimeInMiliSec();
            Long timeInMiliseconds = DateTimeHelperElapsed.getTimeInMiliSeconds(timeLineModelArrayList.get(headerPosition).getModifiedTime());
            Long yesterdayTimeInMiliSeconds = currentNYTInMiliseconds - (1000 * 60 * 60 * 24);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Constants.NY_ZONE));

            Date yesterdayDate = new Date(DateTimeHelperElapsed.toStringNYTtime(yesterdayTimeInMiliSeconds, "MMM dd, yyyy"));
            Date currentDate = new Date(DateTimeHelperElapsed.toStringNYTtime(currentNYTInMiliseconds, "MMM dd, yyyy"));
            Date serverDate = new Date(DateTimeHelperElapsed.toStringNYTtime(timeInMiliseconds, "MMM dd, yyyy"));

            Logger.debug("timeCompare", "" + currentDate.compareTo(serverDate));
            if (currentDate.compareTo(serverDate) == 0) {
                dateTypeViewHolder.txtTime.setText("Today");
            } else if (yesterdayDate.compareTo(serverDate) == 0) {
                dateTypeViewHolder.txtTime.setText("Yesterday");
            } else {
                dateTypeViewHolder.txtTime.setText(DateTimeHelperElapsed.toStringNYTtime(timeInMiliseconds, "MMM dd, yyyy"));
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return createViewHolder(parent, TYPE_DATE);
    }

    public static class FollowUserViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private TextView txtTime;
        private SimpleDraweeView imgUser;
        RelativeLayout profileLayout;
        private TextView tvYouAre, tvFollowBtn;
        private LinearLayout parentView;

        public FollowUserViewHolder(@NonNull View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            imgUser = view.findViewById(R.id.imgProfile);
            profileLayout = view.findViewById(R.id.profileLayout);
            tvYouAre = view.findViewById(R.id.tv_you_are);
            tvFollowBtn = view.findViewById(R.id.tv_follow_btn);
            parentView = view.findViewById(R.id.timeline_adapter_parent_ll);

        }
    }

    public static class TweetDetailRowViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout parentView;
        private TextView tvTweet;
        private TextView txtTime;
        LinearLayout llTweetDetail;

        public TweetDetailRowViewHolder(@NonNull View view) {
            super(view);
            tvTweet = (TextView) view.findViewById(R.id.tv_tweet_text);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            llTweetDetail = view.findViewById(R.id.tipTweetDetailLL);
            parentView = view.findViewById(R.id.timeline_adapter_parent_ll);
        }
    }

    public static class TipPinRowViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView ivUserImg;
        private TextView tvUserName;
        private TextView txtTime;
        private TextView tvStockName;
        private TextView tvEntryPointPrice;
        private TextView tvCreateTipPrice;
        private TextView tvExitPointPrice;
        private TextView tvStockSuggestion;
        private TextView tvStopPointPrice;
        private TextView tvlike;
        private TextView tv_comment_count;
        LinearLayout llUserNameImage;
        RelativeLayout rlTipDetail;
        SimpleDraweeView ivLike;
        private LinearLayout parentView;

        public TipPinRowViewHolder(@NonNull View view) {
            super(view);
            ivUserImg = view.findViewById(R.id.user_img);
            tvUserName = view.findViewById(R.id.user_name);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            tvStockName = (TextView) view.findViewById(R.id.tvStockName);
            tvEntryPointPrice = (TextView) view.findViewById(R.id.tvEntryPointPrice);
            tvCreateTipPrice = (TextView) view.findViewById(R.id.tvCreateTipPrice);
            tvExitPointPrice = (TextView) view.findViewById(R.id.tvExitPointPrice);
            tvStockSuggestion = (TextView) view.findViewById(R.id.tvStockSuggestion);
            tvStopPointPrice = (TextView) view.findViewById(R.id.tvStopPointPrice);
            tvlike = (TextView) view.findViewById(R.id.tvlike);
            tv_comment_count = (TextView) view.findViewById(R.id.tv_comment_count);
            llUserNameImage = view.findViewById(R.id.llUserName);
            rlTipDetail = view.findViewById(R.id.rl_content);
            ivLike = view.findViewById(R.id.iv_like);
            parentView = view.findViewById(R.id.timeline_adapter_parent_ll);
        }
    }

    public static class CommentRowViewHolder extends RecyclerView.ViewHolder {

        private TextView tvComment;
        private TextView tvCommentCount;
        private TextView tvLikeCount;
        private TextView txtTime;
        LinearLayout llCommentRow;
        SimpleDraweeView ivLike;
        private LinearLayout parentView;

        public CommentRowViewHolder(@NonNull View view) {
            super(view);
            tvComment = (TextView) view.findViewById(R.id.txtComment);
            tvCommentCount = (TextView) view.findViewById(R.id.tv_comment_count);
            tvLikeCount = (TextView) view.findViewById(R.id.tvlike);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            llCommentRow = view.findViewById(R.id.comment_row_ll);
            ivLike = view.findViewById(R.id.ivLike);
            parentView = view.findViewById(R.id.timeline_adapter_parent_ll);
        }
    }

    public static class UnknownTypeViewHolder extends RecyclerView.ViewHolder {

        private TextView tvType;

        public UnknownTypeViewHolder(@NonNull View view) {
            super(view);
            tvType = (TextView) view.findViewById(R.id.tv_type);

        }
    }

    public static class DateTypeViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout parentView;
        private TextView txtTime;

        public DateTypeViewHolder(@NonNull View view) {
            super(view);
            txtTime = (TextView) view.findViewById(R.id.tv_date);
            parentView = view.findViewById(R.id.timeline_adapter_parent_ll);
        }
    }

    public ArrayList<TimeLineDataModel> getAdapterArrayList() {
        return timeLineModelArrayList;
    }

    public void changeValueInItemOfAdapterArrayList(TimeLineDataModel timeLineDataModel, int position) {
        if (timeLineModelArrayList.size() >= position) {
            timeLineModelArrayList.get(position).setFollowUserImageBitmap(timeLineDataModel.getFollowUserImageBitmap());
            notifyItemChanged(position);
        }
    }

     public void showUnFollowDialog(final String Username, TextView tvFollowBtn) {
        try {
            if (profileTabbedActivityContext != null && !profileTabbedActivityContext.isFinishing()) {
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(profileTabbedActivityContext);
                View sheetView = profileTabbedActivityContext.getLayoutInflater().inflate(R.layout.unfollow_option, null);
                mBottomSheetDialog.setContentView(sheetView);
                TextView tvUnfollow = sheetView.findViewById(R.id.optUnFollow);
                tvUnfollow.setText(profileTabbedActivityContext.getResources().getString(R.string.Unfollow) + " " + Username);
                sheetView.findViewById(R.id.optUnFollow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unfollowUser(Username, tvFollowBtn);
                        mBottomSheetDialog.dismiss();
                        //  Toast.makeText(context,context.getResources().getString(R.string.MessageUnfollow)+" "+Username,Toast.LENGTH_SHORT).show();
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

    /*
     * Unfollow user
     */
    public void unfollowUser(String userName, TextView tvFollowBtn) {
        profileTabbedActivityContext.showProgressDialog("Please wait....");
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, profileTabbedActivityContext);
        Call<FollowUserModel> call = apiInterface.unfollowUser(userName, "", "");
        call.enqueue(new Callback<FollowUserModel>() {
            @Override
            public void onResponse(Call<FollowUserModel> call, Response<FollowUserModel> response) {
                profileTabbedActivityContext.dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (!response.body().getId().isEmpty()) {
                        FollowUserModel followUserModel = response.body();
                        tvFollowBtn.setText(context.getResources().getString(R.string.Follow));
                        tvFollowBtn.setTextColor(context.getResources().getColor(R.color.text_color_white));
                        tvFollowBtn.setBackgroundResource(R.drawable.follow_btn_bg_transplant);
                    }
                }
            }

            @Override
            public void onFailure(Call<FollowUserModel> call, Throwable t) {
                profileTabbedActivityContext.dismissProgressDialog();
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity unfollowUser: onFailure");
            }
        });
    }

    /*
     * follow user
     */
    public void followUser(String userName, TextView tvFollowBtn) {
        profileTabbedActivityContext.showProgressDialog("Please wait....");
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, profileTabbedActivityContext);
        Call<FollowUserModel> call = apiInterface.followUser(userName, "", "");
        call.enqueue(new Callback<FollowUserModel>() {
            @Override
            public void onResponse(Call<FollowUserModel> call, Response<FollowUserModel> response) {
                profileTabbedActivityContext.dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (!response.body().getId().isEmpty()) {
                        FollowUserModel followUserModel = response.body();
                        tvFollowBtn.setText(context.getResources().getString(R.string.Following));
                        tvFollowBtn.setTextColor(context.getResources().getColor(R.color.edit_text_bg_login));
                        tvFollowBtn.setBackgroundResource(R.drawable.dark_follow_btn_bg);
                    }
                }
            }

            @Override
            public void onFailure(Call<FollowUserModel> call, Throwable t) {
                profileTabbedActivityContext.dismissProgressDialog();
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity followUser: onFailure");
            }
        });
    }

    /*
     * isLoginUserFollowGivenUser user
     */
    public void isLoginUserFollowGivenUser(String userName, TextView tvFollowBtn) {
        // check network is available or not.
        if (!Common.isNetworkAvailable(profileTabbedActivityContext)) {
            Common.showOfflineMemeDialog(profileTabbedActivityContext, profileTabbedActivityContext.getResources().getString(R.string.memeMsg),
                    profileTabbedActivityContext.getResources().getString(R.string.JustLetYouKnow));
            return;
        }
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, profileTabbedActivityContext);
        Call<IsFollowResponse> call = apiInterface.isLoginUserFollowGivenUser(userName);
        call.enqueue(new Callback<IsFollowResponse>() {
            @Override
            public void onResponse(Call<IsFollowResponse> call, Response<IsFollowResponse> response) {
                //dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body() != null) {
                        IsFollowResponse isFollowResponse = response.body();
                        if (isFollowResponse.isFollow()) {
                            tvFollowBtn.setText(context.getResources().getString(R.string.Following));
                            tvFollowBtn.setTextColor(context.getResources().getColor(R.color.edit_text_bg_login));
                            tvFollowBtn.setBackgroundResource(R.drawable.dark_follow_btn_bg);
                        } else {
                            tvFollowBtn.setText(context.getResources().getString(R.string.Follow));
                            tvFollowBtn.setTextColor(context.getResources().getColor(R.color.text_color_white));
                            tvFollowBtn.setBackgroundResource(R.drawable.follow_btn_bg_transplant);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<IsFollowResponse> call, Throwable t) {
                Log.v("error: ", "something went wrong");
                Log.d(Constants.ON_FAILURE_TAG, "ProfileTabbedActivity isLoginUserFollowGivenUser: onFailure");
                Common.showOfflineMemeDialog(profileTabbedActivityContext, profileTabbedActivityContext.getResources().getString(R.string.memeMsg),
                        profileTabbedActivityContext.getResources().getString(R.string.JustLetYouKnow));
                return;
            }
        });
    }


}
