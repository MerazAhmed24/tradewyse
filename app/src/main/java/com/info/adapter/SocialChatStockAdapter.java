package com.info.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.info.model.SocialChatStocksDetails;
import com.info.tradewyse.R;

import java.util.ArrayList;

public class SocialChatStockAdapter extends RecyclerView.Adapter<SocialChatStockAdapter.TopViewHolder> {
    private ArrayList<SocialChatStocksDetails> socialChatStocksDetailsArrayList;
    Context context;

    public SocialChatStockAdapter(ArrayList<SocialChatStocksDetails> socialChatStocksDetailsArrayList, Context context) {
        this.socialChatStocksDetailsArrayList = socialChatStocksDetailsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_social_chat_stock, parent, false);
        return new TopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopViewHolder holder, int position) {

        holder.textViewStockSymbol.setText(socialChatStocksDetailsArrayList.get(position).getStockSymbol());

        if (position == 0) {
            holder.textViewPosition.setText(position + 1 + "st");
        } else if (position == 1) {
            holder.textViewPosition.setText(position + 1 + "nd");
        } else if (position == 2) {
            holder.textViewPosition.setText(position + 1 + "rd");
        }
        else if (position == 7)
        {
            holder.layoutMain.setBackground(null);
            holder.textViewPosition.setText(position + 1 + "th");
        }else {
            holder.textViewPosition.setText(position + 1 + "th");
        }

        /*// For hide the last position view
        if (position == getItemCount() - 1) {
            holder.layoutMain.setBackground(null);
        } else {
            holder.layoutMain.setBackground(context.getResources().getDrawable(R.drawable.rounded_border_black));
        }*/
    }

    @Override
    public int getItemCount() {
        if (socialChatStocksDetailsArrayList.size() >= 8) {
            return 8;
        } else {
            return socialChatStocksDetailsArrayList.size();
        }
    }

    public class TopViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStockSymbol, textViewPosition;
        RelativeLayout layoutMain;

        public TopViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStockSymbol = itemView.findViewById(R.id.textViewStockSymbol);
            textViewPosition = itemView.findViewById(R.id.textViewPosition);
            layoutMain = itemView.findViewById(R.id.layoutMain);
        }
    }
}
