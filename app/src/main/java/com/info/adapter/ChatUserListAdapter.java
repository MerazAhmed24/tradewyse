package com.info.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.info.commons.Common;
import com.info.fragment.CreateRoomFragmentStep2;
import com.info.model.User;
import com.info.tradewyse.CreateRoomActivity;
import com.info.tradewyse.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Amit Gupta on 03,September,2020
 */
public class ChatUserListAdapter extends RecyclerView.Adapter<ChatUserListAdapter.ViewHolder> {
    ArrayList<User> userList;
    Context context;
    CreateRoomFragmentStep2 createRoomFragmentStep2;
    CreateRoomActivity createRoomActivity;

    public ChatUserListAdapter(ArrayList<User> userList, Context context, CreateRoomFragmentStep2 createRoomFragmentStep2, CreateRoomActivity createRoomActivity) {
        this.userList = userList;
        this.context = context;
        this.createRoomFragmentStep2 = createRoomFragmentStep2;
        this.createRoomActivity = createRoomActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(context).inflate(R.layout.user_list_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> map = new HashMap<>();
        if (map.get("imageURL") != null && !map.get("imageURL").toString().isEmpty()) {
            holder.userImageTv.setVisibility(View.GONE);
            holder.userImage.setVisibility(View.VISIBLE);
            Common.setCharRoomImage(holder.userImage, map.get("imageURL").toString());
        } else {
            holder.userImageTv.setVisibility(View.VISIBLE);
            holder.userImage.setVisibility(View.GONE);
            holder.userImageTv.setText(getCharForImage(map.get("name") != null ? map.get("name").toString() : ""));
        }
        holder.userName.setText(map.get("name") != null ? map.get("name").toString() : "");

        holder.rowParentRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ivAccept.getVisibility() == View.VISIBLE) {
                    holder.ivAccept.setVisibility(View.GONE);
//                    createRoomActivity.removedSpecificUserObj(userList.get(position));
                } else {
                    holder.ivAccept.setVisibility(View.VISIBLE);
//                    createRoomActivity.addUserObjInList(userList.get(position));
                }
            }
        });

    }

    private String getCharForImage(String name) {
        String returnValue = "";
        Pattern p = Pattern.compile("\\b[a-zA-Z]");
        Matcher m = p.matcher(name);
        while (m.find()) {
            if (returnValue.length() < 2) {
                returnValue += m.group();
            }
        }
        return returnValue;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView userImage;
        TextView userImageTv, userName;
        LinearLayout rowParentRl;
        ImageView ivAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageTv = itemView.findViewById(R.id.user_image_tv);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            rowParentRl = itemView.findViewById(R.id.row_parent_rl);
            ivAccept = itemView.findViewById(R.id.iv_accept);
        }
    }
}
