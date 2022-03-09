package com.info.adapter;

import static com.info.commons.Constants.DocMessageType;
import static com.info.commons.Constants.ImageMessageType;
import static com.info.commons.Constants.VideoMessageType;
import static com.info.tradewyse.TradwyseApplication.browserPackageName;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.MediaController;

import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.model.ChatModel;
import com.info.sendbird.utils.DateUtils;
import com.info.sendbird.utils.ImageUtils;
import com.info.tradewyse.ChatActivity;
import com.info.tradewyse.FullScreenImagePinchZoomActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.ServiceDetailsScreen;
import com.info.tradewyse.TradwyseApplication;
import com.info.tradewyse.WebViewActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Amit Gupta on 20,January,2021
 */
public class ChatActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static String MESSAGE_TYPE_TEXT = "text";
    public static String MESSAGE_TYPE_IMAGE = "photo";
    public static String MESSAGE_TYPE_VIDEO = "video";
    public static String MESSAGE_TYPE_DOC = "doc";
    public static String MESSAGE_TYPE_AUDIO = "audio";
    private static final int CURRENT_USER_MESSAGE_TYPE_TEXT = 1;
    private static final int OTHER_USER_MESSAGE_TYPE_TEXT = 2;
    private static final int CURRENT_USER_MESSAGE_TYPE_IMAGE = 3;
    private static final int OTHER_USER_MESSAGE_TYPE_IMAGE = 4;
    private static final int CURRENT_USER_MESSAGE_TYPE_VIDEO = 5;
    private static final int OTHER_USER_MESSAGE_TYPE_VIDEO = 6;
    private static final int CURRENT_USER_MESSAGE_TYPE_DOC = 7;
    private static final int OTHER_USER_MESSAGE_TYPE_DOC = 8;
    private Context mContext;
    private List<ChatModel> mMessageList;
    TradWyseSession tradWyseSession;
    private int file_size = 0, replyCount = 0;
    private String groupId;
    private OnClickListener onClickListener;
    private boolean isBasilPrivateRoom;


    public ChatActivityAdapter(Context context, String groupId, boolean isBasilPrivateRoom) {
        mMessageList = new ArrayList<>();
        mContext = context;
        this.groupId = groupId;
        this.isBasilPrivateRoom = isBasilPrivateRoom;
        tradWyseSession = TradWyseSession.getSharedInstance(mContext);
    }

    public void setMessageList(List<ChatModel> messages) {
        mMessageList = messages;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CURRENT_USER_MESSAGE_TYPE_TEXT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_user_text_chat_message, parent, false);
            return new CurrentUserTextMessageHolder(view);
        } else if (viewType == OTHER_USER_MESSAGE_TYPE_TEXT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.other_user_text_chat_message, parent, false);
            return new OtherUserTextMessageHolder(view);
        } else if (viewType == CURRENT_USER_MESSAGE_TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_user_image_chat_message, parent, false);
            return new CurrentUserImageMessageHolder(view);
        } else if (viewType == OTHER_USER_MESSAGE_TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.other_user_image_chat_message, parent, false);
            return new OtherUserImageMessageHolder(view);
        } else if (viewType == CURRENT_USER_MESSAGE_TYPE_VIDEO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_user_video_chat_message, parent, false);
            return new CurrentUserVideoMessageHolder(view);
        } else if (viewType == OTHER_USER_MESSAGE_TYPE_VIDEO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.other_user_video_chat_message, parent, false);
            return new OtherUserVideoMessageHolder(view);
        } else if (viewType == CURRENT_USER_MESSAGE_TYPE_DOC) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_user_doc_chat_message, parent, false);
            return new CurrentUserDocMessageHolder(view);
        } else if (viewType == OTHER_USER_MESSAGE_TYPE_DOC) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.other_user_doc_chat_message, parent, false);
            return new OtherUserDocMessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.empty_view, parent, false);
            return new EmptyViewModel(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel message = mMessageList.get(position);
        boolean isNewDay = false;
        // If there is at least one item preceding the current one, check the previous message.
        if (position < mMessageList.size() - 1) {
            ChatModel prevMessage = mMessageList.get(position + 1);

            if (!DateUtils.hasSameDate(message.getCreatedDate(), prevMessage.getCreatedDate())) {
                isNewDay = true;
            }
        } else if (position == mMessageList.size() - 1) {
            isNewDay = true;
        }

        if (message.isFlag() && message.getFlagedUserName().equalsIgnoreCase(tradWyseSession.getUserName())) {

            mMessageList.remove(position);

        } else {
            switch (holder.getItemViewType()) {
                case CURRENT_USER_MESSAGE_TYPE_TEXT:
                    ((CurrentUserTextMessageHolder) holder).bind(mContext, message, isNewDay, onClickListener, position);
                    break;
                case OTHER_USER_MESSAGE_TYPE_TEXT:
                    ((OtherUserTextMessageHolder) holder).bind(mContext, message, isNewDay, onClickListener, position);
                    break;
                case CURRENT_USER_MESSAGE_TYPE_IMAGE:
                    ((CurrentUserImageMessageHolder) holder).bind(mContext, message, isNewDay, onClickListener, position);
                    break;
                case OTHER_USER_MESSAGE_TYPE_IMAGE:
                    ((OtherUserImageMessageHolder) holder).bind(mContext, message, isNewDay, onClickListener, position);
                    break;
                case CURRENT_USER_MESSAGE_TYPE_VIDEO:
                    ((CurrentUserVideoMessageHolder) holder).bind(mContext, message, isNewDay, onClickListener, position);
                    break;
                case OTHER_USER_MESSAGE_TYPE_VIDEO:
                    ((OtherUserVideoMessageHolder) holder).bind(mContext, message, isNewDay, onClickListener, position);
                    break;
                case CURRENT_USER_MESSAGE_TYPE_DOC:
                    ((CurrentUserDocMessageHolder) holder).bind(mContext, message, isNewDay, onClickListener, position);
                    break;
                case OTHER_USER_MESSAGE_TYPE_DOC:
                    ((OtherUserDocMessageHolder) holder).bind(mContext, message, isNewDay, onClickListener, position);
                    break;
                /*case -1:
                    break;*/

                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position).isFlag() && mMessageList.get(position).getFlagedUserName().equalsIgnoreCase(tradWyseSession.getUserName())) {

            mMessageList.remove(position);

        } else {
            if (mMessageList.get(position).getUserId().equals(tradWyseSession.getUserId())) {
                if (mMessageList.get(position).getMessageType().equalsIgnoreCase(MESSAGE_TYPE_TEXT)) {
                    return CURRENT_USER_MESSAGE_TYPE_TEXT;
                } else if (mMessageList.get(position).getMessageType().equalsIgnoreCase(MESSAGE_TYPE_IMAGE)) {
                    return CURRENT_USER_MESSAGE_TYPE_IMAGE;
                } else if (mMessageList.get(position).getMessageType().equalsIgnoreCase(MESSAGE_TYPE_VIDEO)) {
                    return CURRENT_USER_MESSAGE_TYPE_VIDEO;
                } else { //if (mMessageList.get(position).getMessageType().equalsIgnoreCase(MESSAGE_TYPE_DOC)) {
                    return CURRENT_USER_MESSAGE_TYPE_DOC;
                }

            } else {
                if (mMessageList.get(position).getMessageType().equalsIgnoreCase(MESSAGE_TYPE_TEXT)) {
                    return OTHER_USER_MESSAGE_TYPE_TEXT;
                } else if (mMessageList.get(position).getMessageType().equalsIgnoreCase(MESSAGE_TYPE_IMAGE)) {
                    return OTHER_USER_MESSAGE_TYPE_IMAGE;
                } else if (mMessageList.get(position).getMessageType().equalsIgnoreCase(MESSAGE_TYPE_VIDEO)) {
                    return OTHER_USER_MESSAGE_TYPE_VIDEO;
                } else { //if (mMessageList.get(position).getMessageType().equalsIgnoreCase(MESSAGE_TYPE_DOC)) {
                    return OTHER_USER_MESSAGE_TYPE_DOC;
                }
            }
        }
        return -1;
    }

    private class CurrentUserTextMessageHolder extends RecyclerView.ViewHolder {
        TextView currentUserMessageDate, currentUserMessageName, currentUserMessage, currentUserMessageTime, currentUserMessageEdited;
        ImageView currentUserImage;
        RelativeLayout rlReplyCurrentUserTextMsg, layoutReplyAttachment, layoutCurrentUserRoot,layoutCurrentUserDtSeprater;
        TextView tvReplyUsername, tvReplyUserTextAndType;
        ImageView imgTypeReplyImage, imgTypeReplyVideo, imgTypeReplyDoc;

        public CurrentUserTextMessageHolder(@NonNull View itemView) {
            super(itemView);
            currentUserMessageDate = itemView.findViewById(R.id.current_user_chat_message_date);
            currentUserMessageName = itemView.findViewById(R.id.current_user_chat_message_nickname);
            currentUserMessage = itemView.findViewById(R.id.current_user_chat_message);
            currentUserMessageTime = itemView.findViewById(R.id.current_user_chat_message_time);
            currentUserMessageEdited = itemView.findViewById(R.id.current_user_chat_message_edited);
            currentUserImage = itemView.findViewById(R.id.current_user_image);
            layoutReplyAttachment = itemView.findViewById(R.id.layoutReplyAttachment);
            layoutCurrentUserRoot = itemView.findViewById(R.id.layoutCurrentUserRoot);
            layoutCurrentUserDtSeprater = itemView.findViewById(R.id.sepraterCurrentUserText);
            tvReplyUsername = itemView.findViewById(R.id.tvReplyUsername);
            tvReplyUserTextAndType = itemView.findViewById(R.id.tvReplyUserTextAndType);
            imgTypeReplyImage = itemView.findViewById(R.id.imgTypeReplyImage);
            imgTypeReplyVideo = itemView.findViewById(R.id.imgTypeReplyVideo);
            imgTypeReplyDoc = itemView.findViewById(R.id.imgTypeReplyDoc);
        }

        void bind(Context context, final ChatModel messageModel, boolean isNewDay,
                  @Nullable final ChatActivityAdapter.OnClickListener longClickListener, final int position) {
            currentUserMessageDate.setText(DateUtils.isToday(messageModel.getCreatedDate()) ? "Today" :DateUtils.isYesterday(messageModel.getCreatedDate()) ? "Yesterday" : DateUtils.getChatDateForDateChange(messageModel.getCreatedDate()));
            layoutCurrentUserDtSeprater.setVisibility(isNewDay ? View.VISIBLE : View.GONE);
            currentUserMessageName.setText(messageModel.getUserName());
            currentUserMessage.setText(Html.fromHtml(messageModel.getMessage()));
            List<String> listUrls = Common.extractUrls(messageModel.getMessage());
            if (listUrls.size() == 1) {
                currentUserMessage.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, WebViewActivity.class)
                            .putExtra("title", "TradeTips")
                            .putExtra("url", listUrls.get(0)));
                });
            }
            currentUserMessageTime.setText(DateUtils.convertTime(messageModel.getCreatedDate()));
            currentUserMessageEdited.setVisibility(View.GONE);
            ImageUtils.displayRoundImageFromUrl(context, messageModel.getProfileImageUrl(), currentUserImage);

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onMessageLongClick(messageModel, position);
                        //longClickListener.onMessageReplyClick(messageModel, position);
                        return true;
                    }
                });
            }

            getReplyCount(messageModel.getDocumentReferenceId(), currentUserMessageTime, DateUtils.convertTime(messageModel.getCreatedDate()), messageModel, position);

        }
    }

    private class OtherUserTextMessageHolder extends RecyclerView.ViewHolder {
        TextView otherUserMessageDate, otherUserMessageName, otherUserMessage, otherUserMessageTime, otherUserMessageEdited;
        ImageView otherUserImage;
        RelativeLayout rlReplyOtherUserTextMsg, layoutReplyAttachment, layoutOtherUserRoot,layoutOtherUserSeprater;
        TextView tvReplyUsername, tvReplyUserTextAndType;
        ImageView imgTypeReplyImage, imgTypeReplyVideo, imgTypeReplyDoc;

        public OtherUserTextMessageHolder(@NonNull View itemView) {
            super(itemView);
            otherUserMessageDate = itemView.findViewById(R.id.other_user_chat_message_date);
            otherUserMessageName = itemView.findViewById(R.id.other_user_chat_message_nickname);
            otherUserMessage = itemView.findViewById(R.id.other_user_chat_message);
            otherUserMessageTime = itemView.findViewById(R.id.other_user_chat_message_time);
            otherUserMessageEdited = itemView.findViewById(R.id.other_user_chat_message_edited);
            otherUserImage = itemView.findViewById(R.id.other_user_image);
            layoutReplyAttachment = itemView.findViewById(R.id.layoutReplyAttachment);
            layoutOtherUserRoot = itemView.findViewById(R.id.layoutOtherUserRoot);
            tvReplyUsername = itemView.findViewById(R.id.tvReplyUsername);
            layoutOtherUserSeprater = itemView.findViewById(R.id.sepraterView);
            tvReplyUserTextAndType = itemView.findViewById(R.id.tvReplyUserTextAndType);
            imgTypeReplyImage = itemView.findViewById(R.id.imgTypeReplyImage);
            imgTypeReplyVideo = itemView.findViewById(R.id.imgTypeReplyVideo);
            imgTypeReplyDoc = itemView.findViewById(R.id.imgTypeReplyDoc);

        }

        void bind(Context context, final ChatModel messageModel, boolean isNewDay,
                  @Nullable final ChatActivityAdapter.OnClickListener longClickListener, final int position) {
            otherUserMessageDate.setText(DateUtils.isToday(messageModel.getCreatedDate()) ? "Today" :DateUtils.isYesterday(messageModel.getCreatedDate()) ? "Yesterday" : DateUtils.getChatDateForDateChange(messageModel.getCreatedDate()));
            layoutOtherUserSeprater.setVisibility(isNewDay ? View.VISIBLE : View.GONE);
            otherUserMessageName.setText(messageModel.getUserName());
            otherUserMessage.setText(Html.fromHtml(messageModel.getMessage()));
            List<String> listUrls = Common.extractUrls(messageModel.getMessage());
            otherUserMessageTime.setText(DateUtils.convertTime(messageModel.getCreatedDate()));
            if (listUrls.size() == 1) {
                otherUserMessage.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, WebViewActivity.class)
                            .putExtra("title", "TradeTips")
                            .putExtra("url", listUrls.get(0)));
                });
            }
            otherUserMessageEdited.setVisibility(View.GONE);
            ImageUtils.displayRoundImageFromUrl(context, messageModel.getProfileImageUrl(), otherUserImage);

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onMessageLongClick(messageModel, position);
                        //longClickListener.onMessageReplyClick(messageModel, position);
                        return true;
                    }
                });
            }

            getReplyCount(messageModel.getDocumentReferenceId(), otherUserMessageTime, DateUtils.convertTime(messageModel.getCreatedDate()), messageModel, position);

        }
    }

    private class CurrentUserImageMessageHolder extends RecyclerView.ViewHolder {
        TextView currentUserMessageDate, currentUserMessageName, currentUserMessageTime, currentUserMessageEdited, currentUserChatCaptionText;
        ImageView currentUserImage, currentUserMessage;
        RelativeLayout  layoutReplyAttachment, layoutCurrentUserImageRoot,layoutCurrentUserImageDate;
        TextView tvReplyUsername, tvReplyUserTextAndType;
        ImageView imgTypeReplyImage, imgTypeReplyVideo, imgTypeReplyDoc;


        public CurrentUserImageMessageHolder(@NonNull View itemView) {
            super(itemView);
            currentUserMessageDate = itemView.findViewById(R.id.current_user_chat_message_date);
            currentUserMessageName = itemView.findViewById(R.id.current_user_chat_message_nickname);
            currentUserMessage = itemView.findViewById(R.id.current_user_chat_message);
            currentUserMessageTime = itemView.findViewById(R.id.current_user_chat_message_time);
            currentUserMessageEdited = itemView.findViewById(R.id.current_user_chat_message_edited);
            currentUserImage = itemView.findViewById(R.id.current_user_image);
            layoutCurrentUserImageDate = itemView.findViewById(R.id.sepraterCurrentUserImage);
            currentUserChatCaptionText = itemView.findViewById(R.id.current_user_chat_caption_text);
            layoutReplyAttachment = itemView.findViewById(R.id.layoutReplyAttachment);
            layoutCurrentUserImageRoot = itemView.findViewById(R.id.layoutCurrentUserImageRoot);
            tvReplyUsername = itemView.findViewById(R.id.tvReplyUsername);
            tvReplyUserTextAndType = itemView.findViewById(R.id.tvReplyUserTextAndType);
            imgTypeReplyImage = itemView.findViewById(R.id.imgTypeReplyImage);
            imgTypeReplyVideo = itemView.findViewById(R.id.imgTypeReplyVideo);
            imgTypeReplyDoc = itemView.findViewById(R.id.imgTypeReplyDoc);

        }

        void bind(Context context, final ChatModel messageModel, boolean isNewDay,
                  @Nullable final ChatActivityAdapter.OnClickListener longClickListener, final int position) {
            currentUserMessageDate.setText(DateUtils.isToday(messageModel.getCreatedDate()) ? "Today" :DateUtils.isYesterday(messageModel.getCreatedDate()) ? "Yesterday" : DateUtils.getChatDateForDateChange(messageModel.getCreatedDate()));
            layoutCurrentUserImageDate.setVisibility(isNewDay ? View.VISIBLE : View.GONE);
            currentUserMessageName.setText(messageModel.getUserName());
            currentUserMessageTime.setText(DateUtils.convertTime(messageModel.getCreatedDate()));
            currentUserMessageEdited.setVisibility(View.GONE);

            Glide.with(context)
                    .load(messageModel.getMessage())
                    .placeholder(R.drawable.ic_image_select)
                    .into(currentUserMessage);
            // ImageUtils.displayRoundCornerImageFromUrl(context, messageModel.getMessage(), currentUserMessage);
            ImageUtils.displayRoundImageFromUrl(context, messageModel.getProfileImageUrl(), currentUserImage);


            if (messageModel.getCaption() != null && !messageModel.getCaption().isEmpty()) {
                currentUserChatCaptionText.setVisibility(View.VISIBLE);
                currentUserChatCaptionText.setText(Html.fromHtml(messageModel.getCaption()));
            } else {
                currentUserChatCaptionText.setVisibility(View.GONE);
            }

            currentUserMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(mContext, messageModel.getMessage(), Constants.ImageMessageType);
                }
            });
            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onMessageLongClick(messageModel, position);
                        //longClickListener.onMessageReplyClick(messageModel, position);
                        return true;
                    }
                });
            }

            getReplyCount(messageModel.getDocumentReferenceId(), currentUserMessageTime, DateUtils.convertTime(messageModel.getCreatedDate()), messageModel, position);


        }
    }

    private class OtherUserImageMessageHolder extends RecyclerView.ViewHolder {
        TextView otherUserMessageDate, otherUserMessageName, otherUserMessageTime, otherUserMessageEdited, otherUserChatCaptionText;
        ImageView otherUserImage, otherUserMessage;
        RelativeLayout layoutOtherUserImageDate;

        public OtherUserImageMessageHolder(@NonNull View itemView) {
            super(itemView);
            otherUserMessageDate = itemView.findViewById(R.id.other_user_chat_message_date);
            otherUserMessageName = itemView.findViewById(R.id.other_user_chat_message_nickname);
            otherUserMessage = itemView.findViewById(R.id.other_user_chat_message);
            otherUserMessageTime = itemView.findViewById(R.id.other_user_chat_message_time);
            otherUserMessageEdited = itemView.findViewById(R.id.other_user_chat_message_edited);
            otherUserImage = itemView.findViewById(R.id.other_user_image);
            otherUserChatCaptionText = itemView.findViewById(R.id.other_user_chat_caption_text);
            layoutOtherUserImageDate = itemView.findViewById(R.id.sepraterOtherUserImage);
        }

        void bind(Context context, final ChatModel messageModel, boolean isNewDay,
                  @Nullable final ChatActivityAdapter.OnClickListener longClickListener, final int position) {
            otherUserMessageDate.setText(DateUtils.isToday(messageModel.getCreatedDate()) ? "Today" :DateUtils.isYesterday(messageModel.getCreatedDate()) ? "Yesterday" : DateUtils.getChatDateForDateChange(messageModel.getCreatedDate()));
            layoutOtherUserImageDate.setVisibility(isNewDay ? View.VISIBLE : View.GONE);
            otherUserMessageName.setText(messageModel.getUserName());
            otherUserMessageTime.setText(DateUtils.convertTime(messageModel.getCreatedDate()));
            otherUserMessageEdited.setVisibility(View.GONE);
            Glide.with(context)
                    .load(messageModel.getMessage())
                    .placeholder(R.drawable.ic_image_select)
                    .into(otherUserMessage);

            //  ImageUtils.displayRoundCornerImageFromUrl(context, messageModel.getMessage(), otherUserMessage);
            ImageUtils.displayRoundImageFromUrl(context, messageModel.getProfileImageUrl(), otherUserImage);

            if (messageModel.getCaption() != null && !messageModel.getCaption().isEmpty()) {
                otherUserChatCaptionText.setVisibility(View.VISIBLE);
                otherUserChatCaptionText.setText(Html.fromHtml(messageModel.getCaption()));
            } else {
                otherUserChatCaptionText.setVisibility(View.GONE);
            }

            otherUserMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(mContext, messageModel.getMessage(), Constants.ImageMessageType);
                }
            });
            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onMessageLongClick(messageModel, position);
                        //longClickListener.onMessageReplyClick(messageModel, position);
                        return true;
                    }
                });
            }

            getReplyCount(messageModel.getDocumentReferenceId(), otherUserMessageTime, DateUtils.convertTime(messageModel.getCreatedDate()), messageModel, position);

            /*if (getReplyCount(messageModel.getDocumentReferenceId()) == 0) {
                otherUserMessageTime.setText(DateUtils.getChatTimeForMessage(messageModel.getCreatedDate()));
            } else {
                otherUserMessageTime.setText(DateUtils.getChatTimeForMessage(messageModel.getCreatedDate()) + " | " +
                        (getReplyCount(messageModel.getDocumentReferenceId()) == 1 ? " 1 Reply" : getReplyCount(messageModel.getDocumentReferenceId()) + " Replies"));
            }*/

        }
    }

    private class CurrentUserVideoMessageHolder extends RecyclerView.ViewHolder {
        TextView currentUserMessageDate, currentUserMessageName, currentUserMessageTime, currentUserMessageEdited, currentUserChatCaptionText;
        ImageView currentUserImage;
        ImageView current_user_video_message;
        ProgressBar progress;
        RelativeLayout rlReplyCurrentUserVideoMsg, layoutReplyAttachment, layoutCurrentUserVideoRoot,layoutCurrentUserVideoDate;
        TextView tvReplyUsername, tvReplyUserTextAndType;
        ImageView imgTypeReplyImage, imgTypeReplyVideo, imgTypeReplyDoc;


        public CurrentUserVideoMessageHolder(@NonNull View itemView) {
            super(itemView);
            currentUserMessageDate = itemView.findViewById(R.id.current_user_chat_message_date);
            currentUserMessageName = itemView.findViewById(R.id.current_user_chat_message_nickname);
            current_user_video_message = itemView.findViewById(R.id.current_user_video_message);
            currentUserMessageTime = itemView.findViewById(R.id.current_user_chat_message_time);
            currentUserMessageEdited = itemView.findViewById(R.id.current_user_chat_message_edited);
            currentUserImage = itemView.findViewById(R.id.current_user_image);
            currentUserChatCaptionText = itemView.findViewById(R.id.current_user_chat_caption_text);

            layoutReplyAttachment = itemView.findViewById(R.id.layoutReplyAttachment);
            layoutCurrentUserVideoDate = itemView.findViewById(R.id.sepraterCurrentUserVideo);
            layoutCurrentUserVideoRoot = itemView.findViewById(R.id.layoutCurrentUserVideoRoot);
            tvReplyUsername = itemView.findViewById(R.id.tvReplyUsername);
            tvReplyUserTextAndType = itemView.findViewById(R.id.tvReplyUserTextAndType);
            imgTypeReplyImage = itemView.findViewById(R.id.imgTypeReplyImage);
            imgTypeReplyVideo = itemView.findViewById(R.id.imgTypeReplyVideo);
            imgTypeReplyDoc = itemView.findViewById(R.id.imgTypeReplyDoc);

            progress = itemView.findViewById(R.id.progress);

        }

        void bind(Context context, final ChatModel messageModel, boolean isNewDay,
                  @Nullable final ChatActivityAdapter.OnClickListener longClickListener, final int position) {
            currentUserMessageDate.setText(DateUtils.isToday(messageModel.getCreatedDate()) ? "Today" :DateUtils.isYesterday(messageModel.getCreatedDate()) ? "Yesterday" : DateUtils.getChatDateForDateChange(messageModel.getCreatedDate()));
            layoutCurrentUserVideoDate.setVisibility(isNewDay ? View.VISIBLE : View.GONE);
            currentUserMessageName.setText(messageModel.getUserName());
            currentUserMessageTime.setText(DateUtils.convertTime(messageModel.getCreatedDate()));
            currentUserMessageEdited.setVisibility(View.GONE);

            ImageUtils.displayRoundImageFromUrl(context, messageModel.getProfileImageUrl(), currentUserImage);


            if (messageModel.getCaption() != null && !messageModel.getCaption().isEmpty()) {
                currentUserChatCaptionText.setVisibility(View.VISIBLE);
                currentUserChatCaptionText.setText(Html.fromHtml(messageModel.getCaption()));
            } else {
                currentUserChatCaptionText.setVisibility(View.GONE);
            }

            current_user_video_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(mContext, messageModel.getMessage(), Constants.VideoMessageType);

                }
            });
            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onMessageLongClick(messageModel, position);
                        //longClickListener.onMessageReplyClick(messageModel, position);
                        return true;
                    }
                });
            }

            getReplyCount(messageModel.getDocumentReferenceId(), currentUserMessageTime, DateUtils.convertTime(messageModel.getCreatedDate()), messageModel, position);

        }
    }

    private class OtherUserVideoMessageHolder extends RecyclerView.ViewHolder {
        TextView otherUserMessageDate, otherUserMessageName, otherUserMessageTime, otherUserMessageEdited, otherUserChatCaptionText;
        ImageView otherUserImage;
        ImageView other_user_video_message;
        ProgressBar progress;
        RelativeLayout layoutOtherrUserVideoDate;


        public OtherUserVideoMessageHolder(@NonNull View itemView) {
            super(itemView);
            otherUserMessageDate = itemView.findViewById(R.id.other_user_chat_message_date);
            otherUserMessageName = itemView.findViewById(R.id.other_user_chat_message_nickname);
            other_user_video_message = itemView.findViewById(R.id.other_user_video_message);
            otherUserMessageTime = itemView.findViewById(R.id.other_user_chat_message_time);
            otherUserMessageEdited = itemView.findViewById(R.id.other_user_chat_message_edited);
            otherUserImage = itemView.findViewById(R.id.other_user_image);
            otherUserChatCaptionText = itemView.findViewById(R.id.other_user_chat_caption_text);
            layoutOtherrUserVideoDate = itemView.findViewById(R.id.sepratorOtherUserVideo);
            progress = itemView.findViewById(R.id.progress);

        }

        void bind(Context context, final ChatModel messageModel, boolean isNewDay,
                  @Nullable final ChatActivityAdapter.OnClickListener longClickListener, final int position) {
            otherUserMessageDate.setText(DateUtils.isToday(messageModel.getCreatedDate()) ? "Today" :DateUtils.isYesterday(messageModel.getCreatedDate()) ? "Yesterday" : DateUtils.getChatDateForDateChange(messageModel.getCreatedDate()));
            layoutOtherrUserVideoDate.setVisibility(isNewDay ? View.VISIBLE : View.GONE);
            otherUserMessageName.setText(messageModel.getUserName());
            otherUserMessageTime.setText(DateUtils.convertTime(messageModel.getCreatedDate()));
            otherUserMessageEdited.setVisibility(View.GONE);

            ImageUtils.displayRoundImageFromUrl(context, messageModel.getProfileImageUrl(), otherUserImage);

            if (messageModel.getCaption() != null && !messageModel.getCaption().isEmpty()) {
                otherUserChatCaptionText.setVisibility(View.VISIBLE);
                otherUserChatCaptionText.setText(Html.fromHtml(messageModel.getCaption()));
            } else {
                otherUserChatCaptionText.setVisibility(View.GONE);
            }

            other_user_video_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(mContext, messageModel.getMessage(), Constants.VideoMessageType);

                }
            });

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onMessageLongClick(messageModel, position);
                        //longClickListener.onMessageReplyClick(messageModel, position);
                        return true;
                    }
                });
            }

            getReplyCount(messageModel.getDocumentReferenceId(), otherUserMessageTime, DateUtils.convertTime(messageModel.getCreatedDate()), messageModel, position);

        }
    }

    private class CurrentUserDocMessageHolder extends RecyclerView.ViewHolder {
        TextView currentUserMessageDate, currentUserMessageName, currentUserMessageTime, currentUserMessageEdited, currentUserChatCaptionText;
        ImageView currentUserImage;
        TextView current_user_file_name, current_user_file_size;
        LinearLayout container;
        RelativeLayout rlReplyCurrentUserDocMsg, layoutReplyAttachment, layoutCurrentUserDocRoot,layoutCurrentUserDocDate;
        TextView tvReplyUsername, tvReplyUserTextAndType;
        ImageView imgTypeReplyImage, imgTypeReplyVideo, imgTypeReplyDoc;

        public CurrentUserDocMessageHolder(@NonNull View itemView) {
            super(itemView);
            currentUserMessageDate = itemView.findViewById(R.id.current_user_chat_message_date);
            currentUserMessageName = itemView.findViewById(R.id.current_user_chat_message_nickname);
            current_user_file_name = itemView.findViewById(R.id.current_user_file_name);
            current_user_file_size = itemView.findViewById(R.id.current_user_file_size);
            currentUserMessageTime = itemView.findViewById(R.id.current_user_chat_message_time);
            currentUserMessageEdited = itemView.findViewById(R.id.current_user_chat_message_edited);
            currentUserImage = itemView.findViewById(R.id.current_user_image);
            currentUserChatCaptionText = itemView.findViewById(R.id.current_user_chat_caption_text);
            container = itemView.findViewById(R.id.container);
            layoutReplyAttachment = itemView.findViewById(R.id.layoutReplyAttachment);
            layoutCurrentUserDocRoot = itemView.findViewById(R.id.layoutCurrentUserDocRoot);
            layoutCurrentUserDocDate = itemView.findViewById(R.id.sepraterCurrentUserDoc);
            tvReplyUsername = itemView.findViewById(R.id.tvReplyUsername);
            tvReplyUserTextAndType = itemView.findViewById(R.id.tvReplyUserTextAndType);
            imgTypeReplyImage = itemView.findViewById(R.id.imgTypeReplyImage);
            imgTypeReplyVideo = itemView.findViewById(R.id.imgTypeReplyVideo);
            imgTypeReplyDoc = itemView.findViewById(R.id.imgTypeReplyDoc);

        }

        void bind(Context context, final ChatModel messageModel, boolean isNewDay,
                  @Nullable final ChatActivityAdapter.OnClickListener longClickListener, final int position) {
            currentUserMessageDate.setText(DateUtils.isToday(messageModel.getCreatedDate()) ? "Today" :DateUtils.isYesterday(messageModel.getCreatedDate()) ? "Yesterday" : DateUtils.getChatDateForDateChange(messageModel.getCreatedDate()));
            layoutCurrentUserDocDate.setVisibility(isNewDay ? View.VISIBLE : View.GONE);
            currentUserMessageName.setText(messageModel.getUserName());
            currentUserMessageTime.setText(DateUtils.convertTime(messageModel.getCreatedDate()));
            currentUserMessageEdited.setVisibility(View.GONE);

            String fileName = messageModel.getMessage().substring(messageModel.getMessage().lastIndexOf('%') + 3);
            String fName = fileName.substring(0, fileName.indexOf("?"));
            current_user_file_name.setText(fName);

            ImageUtils.displayRoundImageFromUrl(context, messageModel.getProfileImageUrl(), currentUserImage);

            if (messageModel.getCaption() != null && !messageModel.getCaption().isEmpty()) {
                currentUserChatCaptionText.setVisibility(View.VISIBLE);
                currentUserChatCaptionText.setText(Html.fromHtml(messageModel.getCaption()));
            } else {
                currentUserChatCaptionText.setVisibility(View.GONE);
            }

            container.setOnClickListener(new View.OnClickListener() {
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
            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onMessageLongClick(messageModel, position);
                        //longClickListener.onMessageReplyClick(messageModel, position);
                        return true;
                    }
                });
            }

            getReplyCount(messageModel.getDocumentReferenceId(), currentUserMessageTime, DateUtils.convertTime(messageModel.getCreatedDate()), messageModel, position);
        }
    }

    private class OtherUserDocMessageHolder extends RecyclerView.ViewHolder {
        TextView otherUserMessageDate, otherUserMessageName, otherUserMessageTime, otherUserMessageEdited, otherUserChatCaptionText;
        ImageView otherUserImage;
        TextView other_user_file_name, other_user_file_size;
        LinearLayout container;
        RelativeLayout layoutOtherUserDocDate;

        public OtherUserDocMessageHolder(@NonNull View itemView) {
            super(itemView);
            otherUserMessageDate = itemView.findViewById(R.id.other_user_chat_message_date);
            otherUserMessageName = itemView.findViewById(R.id.other_user_chat_message_nickname);
            other_user_file_name = itemView.findViewById(R.id.other_user_file_name);
            other_user_file_size = itemView.findViewById(R.id.other_user_file_size);
            otherUserMessageTime = itemView.findViewById(R.id.other_user_chat_message_time);
            otherUserMessageEdited = itemView.findViewById(R.id.other_user_chat_message_edited);
            otherUserImage = itemView.findViewById(R.id.other_user_image);
            otherUserChatCaptionText = itemView.findViewById(R.id.other_user_chat_caption_text);
            container = itemView.findViewById(R.id.container);
            layoutOtherUserDocDate = itemView.findViewById(R.id.sepratorOtherUserDoc);
        }

        void bind(Context context, final ChatModel messageModel, boolean isNewDay,
                  @Nullable final ChatActivityAdapter.OnClickListener longClickListener, final int position) {
            otherUserMessageDate.setText(DateUtils.isToday(messageModel.getCreatedDate()) ? "Today" :DateUtils.isYesterday(messageModel.getCreatedDate()) ? "Yesterday" : DateUtils.getChatDateForDateChange(messageModel.getCreatedDate()));
            layoutOtherUserDocDate.setVisibility(isNewDay ? View.VISIBLE : View.GONE);
            otherUserMessageName.setText(messageModel.getUserName());
            otherUserMessageTime.setText(DateUtils.convertTime(messageModel.getCreatedDate()));
            otherUserMessageEdited.setVisibility(View.GONE);

            String fileName = messageModel.getMessage().substring(messageModel.getMessage().lastIndexOf('%') + 3);
            String fName = fileName.substring(0, fileName.indexOf("?"));
            other_user_file_name.setText(fName);
            File file = new File(messageModel.getMessage());
            int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
            other_user_file_size.setText(file_size + "KB");

            ImageUtils.displayRoundImageFromUrl(context, messageModel.getProfileImageUrl(), otherUserImage);

            if (messageModel.getCaption() != null && !messageModel.getCaption().isEmpty()) {
                otherUserChatCaptionText.setVisibility(View.VISIBLE);
                otherUserChatCaptionText.setText(Html.fromHtml(messageModel.getCaption()));
            } else {
                otherUserChatCaptionText.setVisibility(View.GONE);
            }

            container.setOnClickListener(new View.OnClickListener() {
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

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onClickListener.onMessageLongClick(messageModel, position);
                        //longClickListener.onMessageReplyClick(messageModel, position);
                        return true;
                    }
                });
            }

            getReplyCount(messageModel.getDocumentReferenceId(), otherUserMessageTime, DateUtils.convertTime(messageModel.getCreatedDate()), messageModel, position);

        }
    }

    private class EmptyViewModel extends RecyclerView.ViewHolder {
        View view;

        public EmptyViewModel(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    private File getFileFromBitmap(Bitmap bitmap) {
        OutputStream outStream = null;

        File file = new File(mContext.getExternalCacheDir() + "cachedBitmap" + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(mContext.getExternalCacheDir() + "cachedBitmap" + ".png");
            Log.e("file exist", "" + file + ",Bitmap= " + bitmap);
        }
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;
    }

    private int getSizeOfFile(String url, TextView textView) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myUrl = new URL(url);
                    URLConnection urlConnection = myUrl.openConnection();
                    urlConnection.connect();
                    file_size = urlConnection.getContentLength();
                    Log.i("file", "file_size = " + file_size);
                    textView.setText(file_size + " KB");

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        return file_size;
    }

    private void getReplyCount(String documentReferenceId, TextView currentUserMessageTime, String time,
                               ChatModel messageModel, int position) {
        TradwyseApplication.getFirestoreDb()
                .collection(isBasilPrivateRoom ? Constants.BASIL_PRIVATE_GROUP : Constants.OPEN_GROUP)
                .document(groupId)
                .collection("messages")
                .document(documentReferenceId)
                .collection("replies")
                .orderBy(Constants.MessageCreatedDate, Query.Direction.ASCENDING)
                //.limit(30)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentChange> documentChangeList = value.getDocumentChanges();
                        if (!documentChangeList.isEmpty()) {
                            replyCount = documentChangeList.size();
                        } else {
                            replyCount = 0;
                        }

                        if (documentChangeList.size() == 0) {
                            currentUserMessageTime.setText(time);
                        } else {
                            currentUserMessageTime.setText(time + " | " +
                                    (documentChangeList.size() == 1 ? " 1 Reply" : documentChangeList.size() + " Replies"));

                            Common.makeLinkOfChatReplyCount(mContext, currentUserMessageTime, currentUserMessageTime.getText().toString(),
                                    onClickListener, messageModel, position, true);
                        }

                    }
                });
    }

    public interface OnClickListener {
        void onReplyMsgClick(ChatModel chatModel, int position, boolean isReplyAvailable);
        void onMessageLongClick(ChatModel chatModel, int position);
    }

    public void SetOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
