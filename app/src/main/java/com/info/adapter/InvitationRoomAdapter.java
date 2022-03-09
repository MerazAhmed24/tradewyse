package com.info.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.info.commons.Common;
import com.info.fragment.InvitationFragment;
import com.info.tradewyse.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Amit Gupta on 03,September,2020
 */
public class InvitationRoomAdapter extends RecyclerView.Adapter<InvitationRoomAdapter.ViewHolder> {
    //List<Channel> channelList;
    Context context;
    InvitationFragment invitationFragment;

    public InvitationRoomAdapter(/*List<Channel> channelList,*/ Context context, InvitationFragment invitationFragment) {
        //this.channelList = channelList;
        this.context = context;
        this.invitationFragment = invitationFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(context).inflate(R.layout.chanel_list_inivitation_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> map = new HashMap<>()/*channelList.get(position).getExtraData()*/;
        if (map.get("imageURL") != null && !map.get("imageURL").toString().isEmpty()) {
            holder.roomImageTv.setVisibility(View.GONE);
            holder.roomImage.setVisibility(View.VISIBLE);
            Common.setCharRoomImage(holder.roomImage, map.get("imageURL").toString());
        } else {
            holder.roomImageTv.setVisibility(View.VISIBLE);
            holder.roomImage.setVisibility(View.GONE);
            holder.roomImageTv.setText(getCharForImage(map.get("name") != null ? map.get("name").toString() : ""));
        }
        holder.roomName.setText(map.get("name") != null ? map.get("name").toString() : "");

        holder.acceptCancelRl.setVisibility(View.VISIBLE);
        holder.progressRl.setVisibility(View.INVISIBLE);

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.wheel.setBarColor(Color.GREEN);
                holder.acceptCancelRl.setVisibility(View.INVISIBLE);
                holder.progressRl.setVisibility(View.VISIBLE);
            }
        });
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.wheel.setBarColor(Color.RED);
                holder.acceptCancelRl.setVisibility(View.INVISIBLE);
                holder.progressRl.setVisibility(View.VISIBLE);
            }
        });

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (countDownTimer != null)
                    countDownTimer.cancel();*/
                holder.acceptCancelRl.setVisibility(View.VISIBLE);
                holder.progressRl.setVisibility(View.INVISIBLE);
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView roomImage;
        TextView roomImageTv, roomName, roomLastMessage, tvCancel;
        RelativeLayout parentRl, btnCancel, btnAccept, acceptCancelRl;
        ProgressWheel wheel;
        LinearLayout progressRl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImageTv = itemView.findViewById(R.id.room_image_tv);
            roomImage = itemView.findViewById(R.id.room_image);
            roomName = itemView.findViewById(R.id.room_name);
            roomLastMessage = itemView.findViewById(R.id.room_last_message);
            parentRl = itemView.findViewById(R.id.parent_rl);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            wheel = itemView.findViewById(R.id.progress_wheel);
            progressRl = itemView.findViewById(R.id.progress_rl);
            acceptCancelRl = itemView.findViewById(R.id.accept_cancel_rl);
            tvCancel = itemView.findViewById(R.id.tv_cancel);
        }
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
}
