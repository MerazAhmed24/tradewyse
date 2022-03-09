package com.info.adapter;

import static com.info.tradewyse.TradwyseApplication.browserPackageName;

import android.content.Context;
import android.content.Intent;
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
import com.info.commons.DateTimeHelperElapsed;
import com.info.model.SocialComment;
import com.info.model.SocialMessageDetails;
import com.info.sendbird.utils.DateUtils;
import com.info.sendbird.utils.ImageUtils;
import com.info.tradewyse.FullScreenImagePinchZoomActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.WebViewActivity;
import com.nimbusds.jose.util.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hanks.library.bang.SmallBangView;

public class SocialMessageAdapter extends RecyclerView.Adapter<SocialMessageAdapter.SocialMessageViewHolder> {
    private List<SocialMessageDetails> socialMessageDetailsArrayList;
    Context context;
    private OnSocialChatListener onSocialChatListener;
    private String loggedInUserName;
    private boolean isLikedByCurrentUser = false;
    public static final int MAX_LINES = 3;
    public static final String TWO_SPACES = " ";

    public SocialMessageAdapter(List<SocialMessageDetails> socialMessageDetailsArrayList, Context context, String loggedInUserName) {
        this.socialMessageDetailsArrayList = socialMessageDetailsArrayList;
        this.context = context;
        this.loggedInUserName = loggedInUserName;
    }

    public SocialMessageAdapter(Context context, String loggedInUserName) {
        socialMessageDetailsArrayList = new ArrayList<>();
        this.context = context;
        this.loggedInUserName = loggedInUserName;
    }

    @NonNull
    @Override
    public SocialMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social_chat_view_main, parent, false);
        return new SocialMessageAdapter.SocialMessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialMessageViewHolder holder, int position) {
        SocialMessageDetails socialMessageDetails = socialMessageDetailsArrayList.get(position);
        int defaultcount = 0;
        boolean isNewDay = false;
        // If there is at least one item preceding the current one, check the previous message.
        if (position < socialMessageDetailsArrayList.size() - 1) {
            SocialMessageDetails prevMessage = socialMessageDetailsArrayList.get(position + 1);

            if (!DateUtils.hasSameDate(socialMessageDetails.getPublishDateTime(), prevMessage.getPublishDateTime())) {
                isNewDay = true;
            }
        } else if (position == socialMessageDetailsArrayList.size() - 1) {
            isNewDay = true;
        }


        holder.textViewDate.setText(DateUtils.isToday(socialMessageDetails.getPublishDateTime()) ? "Today" :
                DateUtils.isYesterday(socialMessageDetails.getPublishDateTime()) ? "Yesterday" :
                        DateUtils.getChatDateForDateChange(socialMessageDetails.getPublishDateTime()));
        holder.layoutDateView.setVisibility(isNewDay ? View.VISIBLE : View.GONE);


        holder.tvUsername.setText(socialMessageDetails.getAuthorName());
        holder.tvdateTimeMessage.setText(DateTimeHelperElapsed.convertTime(socialMessageDetailsArrayList.get(position).getPublishDateTime()));
        if (socialMessageDetails.getLikedByUserName() == null) {
            holder.textViewLikeCount.setText("" + defaultcount);
        } else {
            holder.textViewLikeCount.setText(socialMessageDetails.getLikedByUserName().size() + "");
        }
        //List<String> listUrlss = new ArrayList<>();

        String finalText = "";

        if (socialMessageDetails.getDescription() != null &&
                socialMessageDetails.getDescription().length() > 0 &&
                !socialMessageDetails.getDescription().equalsIgnoreCase("") &&
                !socialMessageDetails.getDescription().equalsIgnoreCase("No Summary are Available")) {

            finalText = Html.fromHtml(socialMessageDetails.getCommentBody()) + "\n" +
                    Html.fromHtml(socialMessageDetails.getDescription());


        } else {
            finalText = Html.fromHtml(socialMessageDetails.getCommentBody()).toString();
            //holder.tvSocialMessage.setText(finalText);
        }
        if (finalText.length() >= 260) {
            holder.tvSeeMoreView.setVisibility(View.VISIBLE);
            if (holder.tvSeeMoreView.getText().equals(context.getResources().getString(R.string.see_more))) {
                String seeMoreTextLimit = finalText.substring(0, 260);
                holder.tvSocialMessage.setText(seeMoreTextLimit);
                holder.tvSeeMoreView.setText(context.getResources().getString(R.string.see_more));
            } else {
                holder.tvSocialMessage.setText(finalText);
                holder.tvSeeMoreView.setText(context.getResources().getString(R.string.see_less));
            }
        } else {
            holder.tvSeeMoreView.setVisibility(View.GONE);
            holder.tvSocialMessage.setText(finalText);
        }

        String finalText1 = finalText;
        holder.tvSeeMoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tvSeeMoreView.getText().equals(context.getResources().getString(R.string.see_more))) {
                    holder.tvSocialMessage.setText(finalText1);
                    holder.tvSeeMoreView.setText(context.getResources().getString(R.string.see_less));
                    holder.tvSeeMoreView.setPadding(0,80,0,0);
                    holder.tvSeeMoreView.setBackgroundColor(context.getResources().getColor(R.color.transparent));

                } else {
                    String seeMoreTextLimit = finalText1.substring(0, 260);
                    holder.tvSocialMessage.setText(seeMoreTextLimit);
                    holder.tvSeeMoreView.setText(context.getResources().getString(R.string.see_more));
                    holder.tvSeeMoreView.setVisibility(View.VISIBLE);
                    holder.tvSeeMoreView.setPadding(0,30,0,0);
                    holder.tvSeeMoreView.setBackground(context.getResources().getDrawable(R.drawable.see_more_drawable));

                }
            }
        });


        if (socialMessageDetails.getUrl() != null && !socialMessageDetails.getUrl().equalsIgnoreCase("")) {

            //holder.tvSocialDescription.setVisibility(View.VISIBLE);
            //holder.tvSocialDescription.setText(socialMessageDetails.getRadditUrlName());
            holder.imgSocialSite.setOnClickListener(v -> {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("title", "TradeTips")
                        .putExtra("url", socialMessageDetails.getUrl()));
            });
            holder.userImageSocial.setOnClickListener(v -> {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("title", "TradeTips")
                        .putExtra("url", socialMessageDetails.getUrl()));
            });

            holder.tvUsername.setOnClickListener(v -> {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("title", "TradeTips")
                        .putExtra("url", socialMessageDetails.getUrl()));
            });
            holder.tvdateTimeMessage.setOnClickListener(v -> {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("title", "TradeTips")
                        .putExtra("url", socialMessageDetails.getUrl()));
            });

        } else if (socialMessageDetails.getLink() != null && !socialMessageDetails.getLink().equalsIgnoreCase("")) {

            //holder.tvSocialDescription.setVisibility(View.VISIBLE);
            //holder.tvSocialDescription.setText(socialMessageDetails.getLink());
            holder.imgSocialSite.setOnClickListener(v -> {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("title", "TradeTips")
                        .putExtra("url", socialMessageDetails.getLink()));
            });
            holder.userImageSocial.setOnClickListener(v -> {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("title", "TradeTips")
                        .putExtra("url", socialMessageDetails.getLink()));
            });
            holder.tvUsername.setOnClickListener(v -> {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("title", "TradeTips")
                        .putExtra("url", socialMessageDetails.getLink()));
            });
            holder.tvdateTimeMessage.setOnClickListener(v -> {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("title", "TradeTips")
                        .putExtra("url", socialMessageDetails.getLink()));
            });

        } else {
            holder.tvSocialDescription.setVisibility(View.GONE);
        }

        if (socialMessageDetails.getComment() != null) {
            if (socialMessageDetails.getComment().size() == 0) {
                holder.layoutCommentCount.setVisibility(View.GONE);
            } else if (socialMessageDetails.getComment().size() == 1) {
                Map<String, SocialComment> listData = (Map<String, SocialComment>) socialMessageDetails.getComment().get(0);
                if (String.valueOf(listData.get(Constants.COMMENT_AUTHOR_NAME)).equalsIgnoreCase("No Comment Available") &&
                        String.valueOf(listData.get(Constants.COMMENT_TITLE)).equalsIgnoreCase("No Comment Available")) {

                    holder.layoutCommentCount.setVisibility(View.GONE);
                } else {
                    holder.layoutCommentCount.setVisibility(View.VISIBLE);
                    holder.tvCountReplies.setText(socialMessageDetails.getComment().size() + " Comment");
                }

            } else {
                holder.layoutCommentCount.setVisibility(View.VISIBLE);
                if (socialMessageDetails.getComment().size() == 1) {
                    holder.tvCountReplies.setText(socialMessageDetails.getComment().size() + " Comment");
                } else {
                    holder.tvCountReplies.setText(socialMessageDetails.getComment().size() + " Comments");
                }
            }
        } else {
            holder.layoutCommentCount.setVisibility(View.GONE);
        }

        if (socialMessageDetails.getSocialSiteName().equalsIgnoreCase("Reddit")) {
            holder.imgSocialSite.setVisibility(View.VISIBLE);
            holder.imgSocialSite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_open_in_new));
            holder.userImageSocial.setImageDrawable(context.getResources().getDrawable(R.drawable.reddit));
        } else if (socialMessageDetails.getSocialSiteName().equalsIgnoreCase("Twitter")) {
            holder.imgSocialSite.setVisibility(View.VISIBLE);
            holder.imgSocialSite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_open_in_new));
            holder.userImageSocial.setImageDrawable(context.getResources().getDrawable(R.drawable.twitter));
        } else if (socialMessageDetails.getSocialSiteName().equalsIgnoreCase("Discord")) {
            holder.imgSocialSite.setVisibility(View.GONE);
            holder.imgSocialSite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_open_in_new));
            holder.userImageSocial.setImageDrawable(context.getResources().getDrawable(R.drawable.discordicon));
        } else if (socialMessageDetails.getSocialSiteName().equalsIgnoreCase("TradeTips")) {
            holder.imgSocialSite.setVisibility(View.GONE);
            //holder.imgSocialSite.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher_round));
            Glide.with(context).load(socialMessageDetails.getProfileImage()).circleCrop().placeholder(context.getResources().getDrawable(R.drawable.ic_user_default)).into(holder.userImageSocial);
        }

        if (socialMessageDetails.getMessageType().equalsIgnoreCase(Constants.ImageMessageType)) {

            holder.imgTypeSocialChatImage.setVisibility(View.VISIBLE);
            holder.imgTypeSocialChatVideo.setVisibility(View.GONE);
            holder.layoutSocialChatDoc.setVisibility(View.GONE);

            if (!socialMessageDetails.getCaption().equalsIgnoreCase("")) {
                holder.tvSocialMessage.setVisibility(View.VISIBLE);
                holder.tvSocialMessage.setText(socialMessageDetails.getCaption());
            } else {
                holder.tvSocialMessage.setVisibility(View.GONE);
            }

            ImageUtils.displayRoundCornerImageFromUrl(context, socialMessageDetails.getCommentBody(), holder.imgTypeSocialChatImage);

            holder.imgTypeSocialChatImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(context, socialMessageDetails.getCommentBody(), Constants.ImageMessageType);
                }
            });

        } else if (socialMessageDetails.getMessageType().equalsIgnoreCase(Constants.VideoMessageType)) {

            holder.imgTypeSocialChatImage.setVisibility(View.GONE);
            holder.imgTypeSocialChatVideo.setVisibility(View.VISIBLE);
            holder.layoutSocialChatDoc.setVisibility(View.GONE);

            if (!socialMessageDetails.getCaption().equalsIgnoreCase("")) {
                holder.tvSocialMessage.setVisibility(View.VISIBLE);
                holder.tvSocialMessage.setText(socialMessageDetails.getCaption());
            } else {
                holder.tvSocialMessage.setVisibility(View.GONE);
            }

            holder.imgTypeSocialChatVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImagePinchZoomActivity.startFullScreenImagePinchZoomActivity(context, socialMessageDetails.getCommentBody(), Constants.VideoMessageType);
                }
            });

        } else if (socialMessageDetails.getMessageType().equalsIgnoreCase(Constants.DocMessageType)) {

            holder.imgTypeSocialChatImage.setVisibility(View.GONE);
            holder.imgTypeSocialChatVideo.setVisibility(View.GONE);
            holder.layoutSocialChatDoc.setVisibility(View.VISIBLE);

            if (!socialMessageDetails.getCaption().equalsIgnoreCase("")) {
                holder.tvSocialMessage.setVisibility(View.VISIBLE);
                holder.tvSocialMessage.setText(socialMessageDetails.getCaption());
            } else {
                holder.tvSocialMessage.setVisibility(View.GONE);
            }

            String fileName = socialMessageDetails.getCommentBody().substring(socialMessageDetails.getCommentBody().lastIndexOf('%') + 3);
            String fName = fileName.substring(0, fileName.indexOf("?"));
            holder.other_user_file_name.setText(fName);

            holder.layoutSocialChatDoc.setOnClickListener(new View.OnClickListener() {
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
                    customTabsIntent.launchUrl(context, Uri.parse(socialMessageDetails.getCommentBody()));
                }
            });

        } else {
            holder.tvSocialMessage.setVisibility(View.VISIBLE);
            holder.imgTypeSocialChatImage.setVisibility(View.GONE);
            holder.imgTypeSocialChatVideo.setVisibility(View.GONE);
            holder.layoutSocialChatDoc.setVisibility(View.GONE);
        }

        holder.tvCountReplies.setOnClickListener(v -> {
            onSocialChatListener.onClickReplies(position, socialMessageDetailsArrayList.get(position));
        });

        // Auto fill liked button.
        if (socialMessageDetails.getLikedByUserName() != null) {

            for (int i = 0; i < socialMessageDetails.getLikedByUserName().size(); i++) {
                if (socialMessageDetails.getLikedByUserName().get(i).get(Constants.UserName).equals(loggedInUserName) &&
                        socialMessageDetails.getLikeCount() > 0) {
                    holder.imageViewLike.setSelected(true);
                    break;
                } /*else {
                    holder.imageViewLike.setSelected(false);
                }*/
            }
        } else {
            holder.imageViewLike.setSelected(false);
        }

        /*if (isLikedByCurrentUser) {
            holder.imageViewLike.setSelected(true);
        } else {
            holder.imageViewLike.setSelected(false);
        }*/

        holder.layoutlikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imageViewLike.isSelected()) {
                    holder.imageViewLike.setSelected(false);
                    holder.imageViewLike.stopAnimation();
                    onSocialChatListener.onClickLike(position, false, socialMessageDetailsArrayList.get(position));
                    holder.textViewLikeCount.setText(socialMessageDetails.getLikeCount() + "");
                } else {
                    // if not selected only then show animation.
                    holder.imageViewLike.setSelected(true);
                    holder.imageViewLike.likeAnimation();
                    holder.imageViewLike.stopAnimation();
                    onSocialChatListener.onClickLike(position, true, socialMessageDetailsArrayList.get(position));
                    holder.textViewLikeCount.setText(socialMessageDetails.getLikeCount() + "");
                }
            }
        });

    }

    public int addMoreChat(ArrayList<SocialMessageDetails> socialMessageDetails) {
        socialMessageDetailsArrayList.addAll(socialMessageDetails);
        return socialMessageDetailsArrayList.size() - 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return socialMessageDetailsArrayList.size();
    }

    public void setMessageList(List<SocialMessageDetails> mMessageList) {
        this.socialMessageDetailsArrayList = mMessageList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class SocialMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvdateTimeMessage, tvCountReplies, tvSocialDescription, other_user_file_name, textViewDate, textViewLikeCount;
        ImageView userImageSocial, imgTypeSocialChatImage, imgTypeSocialChatVideo;
        CircleImageView imgSocialSite;
        LinearLayout layoutCommentCount, layoutMain, layoutlikeView;
        RelativeLayout layoutSocialChatDoc, layoutDateView;
        SmallBangView imageViewLike;
        TextView tvSocialMessage, tvSeeMoreView;

        public SocialMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvSocialDescription = itemView.findViewById(R.id.tvSocialDescription);
            tvdateTimeMessage = itemView.findViewById(R.id.tvDateTimeMessage);
            tvSocialMessage = itemView.findViewById(R.id.tvSocialMessage);
            tvCountReplies = itemView.findViewById(R.id.tvCountReplies);
            imgSocialSite = itemView.findViewById(R.id.imgSocialSite);
            layoutCommentCount = itemView.findViewById(R.id.layoutCommentCount);
            userImageSocial = itemView.findViewById(R.id.userImageSocial);
            imgTypeSocialChatImage = itemView.findViewById(R.id.imgTypeSocialChatImage);
            imgTypeSocialChatVideo = itemView.findViewById(R.id.imgTypeSocialChatVideo);
            layoutSocialChatDoc = itemView.findViewById(R.id.layoutSocialChatDoc);
            other_user_file_name = itemView.findViewById(R.id.other_user_file_name);
            layoutMain = itemView.findViewById(R.id.layoutMain);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            layoutDateView = itemView.findViewById(R.id.layoutDateView);
            imageViewLike = itemView.findViewById(R.id.imageViewAnimation);
            textViewLikeCount = itemView.findViewById(R.id.tvCountLike);
            layoutlikeView = itemView.findViewById(R.id.layoutlikeView);
            tvSeeMoreView = itemView.findViewById(R.id.seemoreText);
        }
    }

    public interface OnSocialChatListener {
        void onClickReplies(int position, SocialMessageDetails socialMessageDetails);

        void onClickLike(int position, boolean isLiked, SocialMessageDetails socialMessageDetails);
    }

    public void SetOnSocialChatListener(OnSocialChatListener onSocialChatListener) {
        this.onSocialChatListener = onSocialChatListener;
    }
}
