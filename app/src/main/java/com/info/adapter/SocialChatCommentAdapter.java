package com.info.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.info.commons.Constants;
import com.info.commons.DateTimeHelperElapsed;
import com.info.model.SocialChatReply;
import com.info.model.SocialComment;
import com.info.model.SocialMessageDetails;
import com.info.tradewyse.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocialChatCommentAdapter extends RecyclerView.Adapter<SocialChatCommentAdapter.SocialChatViewHolder> {
    private Context context;
    private List<SocialComment> socialChatReplyArrayList;

    public SocialChatCommentAdapter(Context context, List<SocialComment> socialChatReplyArrayList) {
        this.context = context;
        this.socialChatReplyArrayList = socialChatReplyArrayList;
    }

    @NonNull
    @Override
    public SocialChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_reply_view_content, parent, false);
        return new SocialChatCommentAdapter.SocialChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialChatViewHolder holder, int position) {
        Map<String, SocialComment> socialMessageDetails  = (Map<String, SocialComment>) socialChatReplyArrayList.get(position);

        holder.tvUsername.setText(String.valueOf(socialMessageDetails.get(Constants.COMMENT_AUTHOR_NAME)));
        holder.tvDateTime.setText(DateTimeHelperElapsed.convertTime(Long.parseLong(String.valueOf(socialMessageDetails.get(Constants.COMMENT_DATE)))));
        holder.tvSocialComment.setText(Html.fromHtml(String.valueOf(socialMessageDetails.get(Constants.COMMENT_TITLE))));
        holder.socialImage.setImageDrawable(context.getResources().getDrawable(R.drawable.reddit));

    }

    @Override
    public int getItemCount() {
        return socialChatReplyArrayList.size();
    }


    public class SocialChatViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername, tvDateTime, tvSocialComment;
        ImageView socialImage;

        public SocialChatViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvReplyUsername);
            tvDateTime = itemView.findViewById(R.id.tvDateTimeReply);
            tvSocialComment = itemView.findViewById(R.id.tvReplyUserText);
            socialImage = itemView.findViewById(R.id.userImage);

        }
    }
}
