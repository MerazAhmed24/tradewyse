package com.info.adapter;

import static com.info.adapter.ChatActivityAdapter.MESSAGE_TYPE_IMAGE;
import static com.info.adapter.ChatActivityAdapter.MESSAGE_TYPE_TEXT;
import static com.info.adapter.ChatActivityAdapter.MESSAGE_TYPE_VIDEO;
import static com.info.adapter.ChatActivityAdapter.MESSAGE_TYPE_DOC;

import static com.info.adapter.ChatActivityAdapter.MESSAGE_TYPE_AUDIO;
import static com.info.tradewyse.TradwyseApplication.browserPackageName;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.model.BuyStock;
import com.info.model.ChatModel;
import com.info.sendbird.utils.DateUtils;
import com.info.sendbird.utils.ImageUtils;
import com.info.tradewyse.FullScreenImagePinchZoomActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.WebViewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatReplyAdapter extends RecyclerView.Adapter<ChatReplyAdapter.ChatReplyViewHolder> {

    private Context context;
    private List<ChatModel> chatModelArrayList;
    private OnListClickListener onListClickListener;

    public ChatReplyAdapter(Context context) {
        chatModelArrayList = new ArrayList<>();
        this.context = context;
    }

    public void setMessageList(List<ChatModel> messages) {
        chatModelArrayList = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.chat_reply_view_content, parent,
                false);
        return new ChatReplyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatReplyViewHolder holder, int position) {
        ChatModel messageModel = chatModelArrayList.get(position);
        List<String> listUrls = Common.extractUrls(messageModel.getMessage());

        // ImageUtils.displayRoundImageFromUrl(context, messageModel.getProfileImageUrl(), holder.userImage);
        Glide.with(context).load(messageModel.getProfileImageUrl()).circleCrop().placeholder(context.getResources().getDrawable(R.drawable.ic_user_default)).into(holder.userImage);

        holder.tvReplyUsername.setText(messageModel.getUserName());
        holder.tvDateTimeReply.setText(DateUtils.convertTime(messageModel.getCreatedDate()));

        if (messageModel.getMessageType().equalsIgnoreCase(MESSAGE_TYPE_TEXT)) {

            holder.imgTypeReplyImage.setVisibility(View.GONE);
            holder.imgTypeReplyVideo.setVisibility(View.GONE);
            holder.layoutReplyDoc.setVisibility(View.GONE);
            holder.tvReplyUserText.setVisibility(View.VISIBLE);
            holder.tvReplyUserText.setText(Html.fromHtml(messageModel.getMessage()));
            if (listUrls.size() == 1) {
                holder.tvReplyUserText.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, WebViewActivity.class)
                            .putExtra("title", "TradeTips")
                            .putExtra("url", listUrls.get(0)));
                });
            }

        } else if (messageModel.getMessageType().equalsIgnoreCase(MESSAGE_TYPE_IMAGE)) {

            holder.imgTypeReplyImage.setVisibility(View.VISIBLE);
            holder.imgTypeReplyVideo.setVisibility(View.GONE);
            holder.layoutReplyDoc.setVisibility(View.GONE);

            ImageUtils.displayRoundCornerImageFromUrl(context, messageModel.getMessage(), holder.imgTypeReplyImage);
            Glide.with(context)
                    .load(messageModel.getMessage()).placeholder(context.getResources().getDrawable(R.drawable.ic_image_select))
                    .into(holder.imgTypeReplyImage);

            if (messageModel.getCaption() != null && !messageModel.getCaption().isEmpty()) {
                holder.tvReplyUserText.setVisibility(View.VISIBLE);
                holder.tvReplyUserText.setText(Html.fromHtml(messageModel.getCaption()));
            } else {
                holder.tvReplyUserText.setVisibility(View.GONE);
            }

            holder.imgTypeReplyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(context, messageModel.getMessage(), Constants.ImageMessageType);
                }
            });

        } else if (messageModel.getMessageType().equalsIgnoreCase(MESSAGE_TYPE_VIDEO)) {

            holder.imgTypeReplyImage.setVisibility(View.GONE);
            holder.imgTypeReplyVideo.setVisibility(View.VISIBLE);
            holder.layoutReplyDoc.setVisibility(View.GONE);

            if (messageModel.getCaption() != null && !messageModel.getCaption().isEmpty()) {
                holder.tvReplyUserText.setVisibility(View.VISIBLE);
                holder.tvReplyUserText.setText(Html.fromHtml(messageModel.getCaption()));
            } else {
                holder.tvReplyUserText.setVisibility(View.GONE);
            }

            holder.imgTypeReplyVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(context, messageModel.getMessage(), Constants.VideoMessageType);
                }
            });

        } else if (messageModel.getMessageType().equalsIgnoreCase(MESSAGE_TYPE_AUDIO)) {

            holder.imgTypeReplyImage.setVisibility(View.GONE);
            holder.imgTypeReplyVideo.setVisibility(View.GONE);
            holder.layoutReplyDoc.setVisibility(View.VISIBLE);
            String fileName = messageModel.getMessage().substring(messageModel.getMessage().lastIndexOf('%') + 3);
            String fName = fileName.substring(0, fileName.indexOf("?"));
            holder.reply_file_name.setText(fName);

            File file = new File(messageModel.getMessage());
            int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
            holder.reply_file_size.setText(file_size + "KB");

            if (messageModel.getCaption() != null && !messageModel.getCaption().isEmpty()) {
                holder.tvReplyUserText.setVisibility(View.VISIBLE);
                holder.tvReplyUserText.setText(Html.fromHtml(messageModel.getCaption()));

            } else {
                holder.tvReplyUserText.setVisibility(View.GONE);
            }

            holder.layoutReplyDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String browser = "";
                    for (int i = 0; i < browserPackageName.size(); i++) {
                        if (browserPackageName.get(i).equals("com.android.chrome")) {
                            browser = browserPackageName.get(i);
                            break;
                        } else {
                            browser = browserPackageName.get(0);
                        }
                    }

                    CustomTabsIntent customTabsIntent = Common.getCustomTabBuilder(context).build();
                    customTabsIntent.intent.setPackage(browser);
                    customTabsIntent.launchUrl(context, Uri.parse(messageModel.getMessage()));
                }
            });
        }

        holder.rl_content_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onListClickListener.onMessageLongClick(messageModel, position);
                return true;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return chatModelArrayList.size();
    }


    public class ChatReplyViewHolder extends RecyclerView.ViewHolder {
        TextView tvReplyUsername, tvDateTimeReply, tvReplyUserText, reply_file_name, reply_file_size;
        LinearLayout layoutReplyDoc;
        RelativeLayout rl_content_view;
        ImageView userImage, imgTypeReplyImage, imgTypeReplyVideo;

        public ChatReplyViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            tvReplyUsername = itemView.findViewById(R.id.tvReplyUsername);
            tvDateTimeReply = itemView.findViewById(R.id.tvDateTimeReply);
            tvReplyUserText = itemView.findViewById(R.id.tvReplyUserText);
            imgTypeReplyImage = itemView.findViewById(R.id.imgTypeReplyImage);
            imgTypeReplyVideo = itemView.findViewById(R.id.imgTypeReplyVideo);
            layoutReplyDoc = itemView.findViewById(R.id.layoutReplyDoc);
            rl_content_view = itemView.findViewById(R.id.rl_content_view);
            reply_file_name = itemView.findViewById(R.id.reply_file_name);
            reply_file_size = itemView.findViewById(R.id.reply_file_size);

        }
    }

    public interface OnListClickListener {
        void onMessageLongClick(ChatModel messageModel, int position);
    }

    public void SetOnListClickListener(OnListClickListener onListClickListener) {
        this.onListClickListener = onListClickListener;
    }

}
