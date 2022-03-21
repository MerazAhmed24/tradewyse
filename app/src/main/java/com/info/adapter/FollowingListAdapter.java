package com.info.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.model.FollowUserModel;
import com.info.tradewyse.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amit.
 */

public class FollowingListAdapter extends RecyclerView.Adapter<FollowingListAdapter.FollowListItemViewHolder> {
    List<FollowUserModel> followList;
    Context context;
    private OnFollowItemClick mOnFollowItemClick;
    private boolean fromLoggedInProfile = true;

    /**
     * Interface to call method of fragment from adapter.
     */
    public interface OnFollowItemClick {
        void onFollowItemClick(String username, String userId);
        void onFollowClick(int position);
        void onUnFollowClick(int position);
    }

    public FollowingListAdapter(Context context, List<FollowUserModel> followList, OnFollowItemClick listener, boolean fromLoggedInProfile) {
        this.context = context;
        this.followList = followList;
        mOnFollowItemClick = listener;
        this.fromLoggedInProfile = fromLoggedInProfile;
        Common.setFollowersData(followList);
    }

    @Override
    public FollowingListAdapter.FollowListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(context).inflate(R.layout.follow_list_item, parent, false);
        return new FollowListItemViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(FollowListItemViewHolder holder, int position) {
        FollowUserModel model = followList.get(position);
        String username = model.getFollowUserName();
        holder.txtName.setText(username);

        if (fromLoggedInProfile) {
            //(view.findViewById(R.id.frag_timeline_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
            holder.rootLayout.setBackgroundResource(R.drawable.mentor_list_item_bg_rounded);
        } else {
            //(view.findViewById(R.id.frag_timeline_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
            holder.rootLayout.setBackgroundResource(R.drawable.mentor_list_item_bg_rounded);
        }

        setUserImage(holder.imgProfile, model);
        if (TradWyseSession.getSharedInstance(context).getUserName().equalsIgnoreCase(username)) {
            holder.txtFollowBtn.setVisibility(View.INVISIBLE);
        } else {
            holder.txtFollowBtn.setVisibility(View.VISIBLE);
        }

        if (model.getLoginUserFollow() != null)
            Common.setFollowText(context, holder.txtFollowBtn, model.getLoginUserFollow());

        Common.observeFollowers(context, holder.txtFollowBtn, username);

        holder.txtFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.txtFollowBtn.getText().toString().equalsIgnoreCase(context.getResources().getString(R.string.Following))) {
                    showUnFollowDialog((Activity) context, username, position, holder.txtFollowBtn);
                } else {
                    Common.updatedFollowItemList.put(username, username);
                    followUser(position, username, holder.txtFollowBtn);
                  //  Toast.makeText(context,context.getResources().getString(R.string.Messagefollow)+" "+username,Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnFollowItemClick.onFollowItemClick(username, model.getFollowUserId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    public class FollowListItemViewHolder extends ViewHolder {
        TextView txtFollowBtn;
        TextView txtName;
        SimpleDraweeView imgProfile;

        LinearLayout rootLayout;

        public FollowListItemViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName = itemView.findViewById(R.id.txtName);
            txtFollowBtn = itemView.findViewById(R.id.txtFollowBtn);
        }
    }


    private void showUnFollowDialog(final Activity activity, final String Username, int postion, TextView followTxtView) {
        try {
            if (activity != null && !activity.isFinishing()) {
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
                View sheetView = activity.getLayoutInflater().inflate(R.layout.unfollow_option, null);
                TextView tvUnfollow = sheetView.findViewById(R.id.optUnFollow);
                tvUnfollow.setText(context.getResources().getString(R.string.Unfollow) + " " + Username);
                mBottomSheetDialog.setContentView(sheetView);
                sheetView.findViewById(R.id.optUnFollow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unfollowUser(postion, Username, followTxtView);
                        Common.updatedFollowItemList.put(Username, Username);
                        mBottomSheetDialog.dismiss();
                       // Toast.makeText(context,context.getResources().getString(R.string.MessageUnfollow)+" "+Username,Toast.LENGTH_SHORT).show();
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
    public void unfollowUser(int position, String userName, TextView txtFollowView) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        Call<FollowUserModel> call = apiInterface.unfollowUser(userName, "", "");
        call.enqueue(new Callback<FollowUserModel>() {
            @Override
            public void onResponse(Call<FollowUserModel> call, Response<FollowUserModel> response) {
                if (response != null && response.body() != null) {
                    if (!response.body().getId().isEmpty()) {
                        FollowUserModel followUserModel = response.body();
                        followList.set(position, followUserModel);
                        txtFollowView.setText(context.getResources().getString(R.string.Follow));
                        txtFollowView.setBackgroundResource(R.drawable.follow_btn_bg_transplant);
                        Common.setFollowersData(userName, false);
                        mOnFollowItemClick.onUnFollowClick(position);
                    }
                }
            }

            @Override
            public void onFailure(Call<FollowUserModel> call, Throwable t) {
                Log.v("error: ", "something went wrong");
                Log.d(Constants.ON_FAILURE_TAG, "FollowingAdapter unfollowUser: onFailure");
            }
        });
    }

    /*
     * follow user
     */
    public void followUser(int position, String userName, TextView txtFollowView) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        Call<FollowUserModel> call = apiInterface.followUser(userName, "", "");
        call.enqueue(new Callback<FollowUserModel>() {
            @Override
            public void onResponse(Call<FollowUserModel> call, Response<FollowUserModel> response) {
                if (response != null && response.body() != null) {
                    if (!response.body().getId().isEmpty()) {
                        FollowUserModel followUserModel = response.body();
                        followList.set(position, followUserModel);
                        Common.setFollowersData(userName, true);
                        txtFollowView.setText(context.getResources().getString(R.string.Following));
                        mOnFollowItemClick.onFollowClick(position);
                        if (fromLoggedInProfile) {
                            txtFollowView.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg_india));
                        } else {
                            txtFollowView.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg_india));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FollowUserModel> call, Throwable t) {
                Log.v("error: ", "something went wrong");
                Log.d(Constants.ON_FAILURE_TAG, "FollowingAdapter followUser: onFailure");
            }
        });
    }

    public void addMoreFollowItems(List<FollowUserModel> followList) {
        this.followList.addAll(followList);
        Common.setFollowersData(this.followList);
        notifyDataSetChanged();
    }

    public void EmptyList() {
        this.followList.clear();
        notifyDataSetChanged();
    }

    public void removeItemsFromList(List<FollowUserModel> followList) {
        this.followList.removeAll(followList);
        notifyDataSetChanged();
    }

    public void clearAdapter() {
        this.followList.clear();
        notifyDataSetChanged();
    }

    public void setUserImage(Uri uri, String userName) {
        for (int i = 0; i < followList.size(); i++) {
            FollowUserModel model = followList.get(i);
            if (model.getFollowUserName().equalsIgnoreCase(userName)) {
                model.setUri(uri);
                notifyItemChanged(i);
            }
        }
    }

    private void setUserImage(SimpleDraweeView simpleDraweeView, FollowUserModel user) {
        Common.setProfileImage(simpleDraweeView, user.getFollowUserId());
    }
}
