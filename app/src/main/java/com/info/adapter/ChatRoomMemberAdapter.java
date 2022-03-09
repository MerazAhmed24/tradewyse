package com.info.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.info.model.GroupMemberInfo;
import com.info.sendbird.utils.ImageUtils;
import com.info.tradewyse.R;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomMemberAdapter extends RecyclerView.Adapter<ChatRoomMemberAdapter.ChatRoomMemberViewHolder> {

    private Context context;
    private List<GroupMemberInfo> groupMemberInfoList = new ArrayList<>();

    public ChatRoomMemberAdapter(Context context, List<GroupMemberInfo> groupMemberInfoList) {
        this.context = context;
        this.groupMemberInfoList = groupMemberInfoList;
    }

    @NonNull
    @Override
    public ChatRoomMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_details_layout, parent, false);
        return new ChatRoomMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomMemberViewHolder holder, int position) {

        holder.tvUserName.setText(groupMemberInfoList.get(position).getUserName());
        ImageUtils.displayRoundImageFromUrl(context, groupMemberInfoList.get(position).getProfileImageUrl(),
                holder.imgUserProfile);
    }

    @Override
    public int getItemCount() {
        return groupMemberInfoList.size();
    }

    public class ChatRoomMemberViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserProfile;
        TextView tvUserName;

        public ChatRoomMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserProfile = itemView.findViewById(R.id.userImageSocial);
            tvUserName = itemView.findViewById(R.id.tvUsername);
        }
    }
}
