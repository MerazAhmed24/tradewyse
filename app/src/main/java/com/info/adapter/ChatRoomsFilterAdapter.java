package com.info.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.info.commons.Constants;
import com.info.model.ChatRooms;
import com.info.model.ServiceType;
import com.info.tradewyse.R;

import java.util.List;

public class ChatRoomsFilterAdapter extends RecyclerView.Adapter<ChatRoomsFilterAdapter.ServiceFilterHolder> {

    private Context context;
    private List<ChatRooms> chatRoomsList;
    private ChatRoomsListener chatRoomsListener;
    private String selectedGroup = "";

    public ChatRoomsFilterAdapter(Context context, List<ChatRooms> chatRoomsList, String selectedGroup) {
        this.context = context;
        this.chatRoomsList = chatRoomsList;
        this.selectedGroup = selectedGroup;
    }

    @NonNull
    @Override
    public ServiceFilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_rooms_filter, parent, false);

        return new ServiceFilterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceFilterHolder holder, int position) {

        ChatRooms serviceType = chatRoomsList.get(position);

        /*if (!selectedGroup.equalsIgnoreCase("") && selectedGroup.equalsIgnoreCase(Constants.SOCIAL_CHAT_ROOM) && position == 0) {
            holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.defaultTextColor));
            holder.layoutRootTitle.setBackgroundColor(context.getResources().getColor(R.color.blue_color));
        } else */
        if (!selectedGroup.equalsIgnoreCase("") && selectedGroup.equalsIgnoreCase(Constants.STRATEGY_CHAT_ROOM) && position == 0) {
            holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.defaultTextColor));
            holder.layoutRootTitle.setBackgroundColor(context.getResources().getColor(R.color.blue_color));
        } else if (!selectedGroup.equalsIgnoreCase("") && selectedGroup.equalsIgnoreCase(Constants.BASIL_PRIVATE_CHAT_ROOM) && position == 1) {
            holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.defaultTextColor));
            holder.layoutRootTitle.setBackgroundColor(context.getResources().getColor(R.color.blue_color));
        } else {
            holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.textViewTitle.setAlpha(1f);
            holder.layoutRootTitle.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }

        if (!serviceType.getChatRoomName().equalsIgnoreCase("") &&
                !serviceType.getChatRoomName().equalsIgnoreCase("null") && serviceType.getChatRoomName() != null) {
            holder.textViewTitle.setText(serviceType.getChatRoomName());
        }

        if (serviceType.getIsLocked()) {
            holder.imageViewCheck.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewCheck.setVisibility(View.GONE);
        }

        holder.layoutRootTitle.setOnClickListener(v -> {
            chatRoomsListener.onFilterClick(position);
        });

    }

    @Override
    public int getItemCount() {
        return chatRoomsList.size();
    }

    class ServiceFilterHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageView imageViewCheck;
        RelativeLayout layoutRootTitle;

        public ServiceFilterHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            imageViewCheck = itemView.findViewById(R.id.imageViewCheck);
            layoutRootTitle = itemView.findViewById(R.id.layoutRootTitle);
        }
    }

    public interface ChatRoomsListener {
        void onFilterClick(int position);
    }

    public void setOnChatRoomsListener(ChatRoomsListener chatRoomsListener) {
        this.chatRoomsListener = chatRoomsListener;
    }
}
