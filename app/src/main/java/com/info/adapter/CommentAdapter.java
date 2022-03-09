package com.info.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.DateTimeHelperElapsed;
import com.info.commons.FlagReasonOptionUtility;
import com.info.commons.TradWyseSession;
import com.info.interfaces.CommentCountChangedInterface;
import com.info.interfaces.FlagOptionSelectListener;
import com.info.model.Comment;
import com.info.model.FlagResponse;
import com.info.model.Tips;
import com.info.tradewyse.ProfileTabbedActivity;
import com.info.tradewyse.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    List<Comment> commentsList;
    static Context context;
    boolean isVertical;
    Tips tips;
    private String selectedScreen = "";
    CommentCountChangedInterface commentCountChangedInterface;

    public CommentAdapter(List<Comment> comList, Tips tips, Context context, CommentCountChangedInterface commentCountChangedInterface, String selectedScreen) {
        this.commentsList = comList;
        this.tips = tips;
        this.context = context;
        this.isVertical = isVertical;
        this.commentCountChangedInterface = commentCountChangedInterface;
        this.selectedScreen = selectedScreen;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, parent,
                false);
        return new CommentAdapter.CommentViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = commentsList.get(position);
        holder.txtComment.setText(comment.getCommentDetails().trim());
        holder.txtName.setText(comment.getAppUser().getUserName());
        holder.txtTimeAgo.setText(DateTimeHelperElapsed.getElapsedTime(comment.getCreatedOn()));
        setUserImage(holder.imgProfile, comment);
        if (comment.getAppUser().getId().equalsIgnoreCase(TradWyseSession.getSharedInstance(context).getUserId())) {
            holder.flagIcon.setVisibility(View.GONE);
        } else {
            holder.flagIcon.setVisibility(View.VISIBLE);
        }
        holder.flagIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlagReasonOptionUtility frUtility = new FlagReasonOptionUtility.Builder((AppCompatActivity) context).build();
                frUtility.showFlagOptionList();
                frUtility.setFlagOptionSelectListener(new FlagOptionSelectListener() {
                    @Override
                    public void startActivityForResult(int selectedOption, int requestCode) {
                        //  Toast.makeText(context,"selectedOption:"+selectedOption,Toast.LENGTH_LONG).show();

                        if (selectedOption == 0) return; //when user click cancel.


                        submitFlagForComment(position, comment.getId(), TradWyseSession.getSharedInstance(context).getUserId(), true,
                                Common.getFlagOptionById(selectedOption, context), context);

                    }
                });
            }
        });

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToProfile(comment);
            }
        });
        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToProfile(comment);
            }
        });
    }

    public void redirectToProfile(Comment comment){
        ProfileTabbedActivity.starProfileTabbedtActivityForResult(context, false, comment.getAppUser().getUserName(), comment.getAppUser().getId(), 104, selectedScreen);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {


        private TextView txtName;
        private TextView txtTimeAgo;
        private TextView txtComment;
        private SimpleDraweeView imgProfile;
        private AppCompatImageView flagIcon;

        public CommentViewHolder(@NonNull View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtTimeAgo = (TextView) view.findViewById(R.id.txtTimeAgo);
            txtComment = (TextView) view.findViewById(R.id.txtComment);
            imgProfile = view.findViewById(R.id.imgProfile);
            flagIcon = (AppCompatImageView) view.findViewById(R.id.flagIcon);
        }

    }

    public void submitFlagForComment(int position, String tipId, String userId, boolean isFlag, String flagReason, Context context) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.style_progress_dialog);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<FlagResponse> call = apiInterface.addFlagForComment(tipId, userId, isFlag, flagReason);
        call.enqueue(new Callback<FlagResponse>() {
            @Override
            public void onResponse(Call<FlagResponse> call, Response<FlagResponse> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(String.valueOf(true))) {
                        for (int i = 0; i < commentsList.size(); i++) {
                            if (commentsList.get(i).getId().equalsIgnoreCase(tipId)) {
                                commentsList.remove(i);
                                notifyItemRemoved(i);
                                break;
                            }
                        }
                        commentCountChangedInterface.commentCountChange(commentsList.size());
                        Common.showMessage(context, context.getResources().getString(R.string.flag_success_comment), "Success");
                    } else {
                        Common.showMessage(context, context.getResources().getString(R.string.flag_already_submitted_for_comment), "Error");
                    }
                } else {
                    Toast.makeText(context, "Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FlagResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed,Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                Log.d(Constants.ON_FAILURE_TAG, "CommentAdapter submitFlagForComment: onFailure");
            }

        });
    }

    public void addNewComment(Comment comment) {
        commentsList.add(comment);
        notifyDataSetChanged();
    }

    public void addMoreComment(List<Comment> commentsList) {
        this.commentsList.addAll(commentsList);
        notifyDataSetChanged();
    }


    public void sortComment() {
        Collections.sort(commentsList, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                if (o1.getCreatedOn() < o2.getCreatedOn()) {
                    return -1;
                } else {
                    if (o1.getCreatedOn() > o2.getCreatedOn()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });
        notifyDataSetChanged();
    }

    public List<Comment> getCommentsList() {
        return commentsList;
    }

    public void setUserImage(Uri uri, String userName) {
        for (int i = 0; i < commentsList.size(); i++) {
            Comment comment = commentsList.get(i);
            if (comment.getAppUser().getUserName().equalsIgnoreCase(userName)) {
                comment.setUri(uri);
                notifyItemChanged(i);
            }
        }
    }

    private void setUserImage(SimpleDraweeView simpleDraweeView, Comment comment) {
        Common.setProfileImage(simpleDraweeView, comment.getUserId());
    }
}
