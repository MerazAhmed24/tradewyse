package com.info.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.info.commons.Common;
import com.info.fragment.YourRoomFragment;
import com.info.tradewyse.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Amit Gupta on 03,September,2020
 */
public class YourRoomAdapter extends RecyclerView.Adapter<YourRoomAdapter.ViewHolder> {
//    List<Channel> channelList;
    Context context;
    YourRoomFragment yourRoomFragment;

    public YourRoomAdapter(/*List<Channel> channelList,*/ Context context, YourRoomFragment yourRoomFragment) {
//        this.channelList = channelList;
        this.context = context;
        this.yourRoomFragment = yourRoomFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(context).inflate(R.layout.chanel_list_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*if(((ProfileTabbedActivity) context).fromLoggedInProfile) {
            holder.txtFollowBtn.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg));
        }else{
            holder.txtFollowBtn.setBackgroundColor(context.getResources().getColor(R.color.color_other_profile_bg));
        }*/
        Map<String, Object> map = new HashMap<>() /*channelList.get(position).getExtraData()*/;
        if (map.get("imageURL") != null && !map.get("imageURL").toString().isEmpty()) {
            holder.roomImageTv.setVisibility(View.GONE);
            holder.roomImage.setVisibility(View.VISIBLE);
            Common.setCharRoomImage(holder.roomImage, map.get("imageURL").toString());
        } else {
            holder.roomImageTv.setVisibility(View.VISIBLE);
            holder.roomImage.setVisibility(View.GONE);
            holder.roomImageTv.setText(getCharForImage(map.get("name") != null ? map.get("name").toString() : ""));
        }
        int memberCount = 1;

        holder.parentRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent intent = new Intent(context, ChatActivity.class);
                context.startActivity(intent);*/
            }
        });
        holder.parentRl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
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
        return /*channelList.size()*/0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView roomImage;
        TextView roomImageTv, roomName, roomCreatedTime, roomLastMessage;
        RelativeLayout parentRl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImageTv = itemView.findViewById(R.id.room_image_tv);
            roomImage = itemView.findViewById(R.id.room_image);
            roomName = itemView.findViewById(R.id.room_name);
            roomCreatedTime = itemView.findViewById(R.id.room_created_time);
            roomLastMessage = itemView.findViewById(R.id.room_last_message);
            parentRl = itemView.findViewById(R.id.parent_rl);
        }
    }
}
