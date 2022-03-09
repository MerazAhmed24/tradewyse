package com.info.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.info.model.BuyStock;
import com.info.model.SellStock;
import com.info.tradewyse.R;

import java.util.List;

public class MACDSellAdapter extends RecyclerView.Adapter<MACDSellAdapter.ActiveSessionsHolder> {

    private Context context;
    private List<SellStock> sellStockList;
    private OnListClickListener onListClickListener;

    public MACDSellAdapter(Context context, List<SellStock> sellStockList) {
        this.context = context;
        this.sellStockList = sellStockList;
    }

    @NonNull
    @Override
    public ActiveSessionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_macd_alert, parent, false);

        return new ActiveSessionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveSessionsHolder holder, int position) {

        SellStock sellStock = sellStockList.get(position);

        if (!sellStock.getStockSymbol().equalsIgnoreCase("") &&
                !sellStock.getStockSymbol().equalsIgnoreCase("null") &&
                !sellStock.getStockSymbol().equalsIgnoreCase("sell_stock") && sellStock.getStockSymbol() != null) {
            holder.textViewStockSymbol.setText(Html.fromHtml(sellStock.getStockSymbol()));
        }

        if (!sellStock.getStockName().equalsIgnoreCase("") &&
                !sellStock.getStockName().equalsIgnoreCase("null") && sellStock.getStockName() != null) {
            holder.textViewStockName.setText(Html.fromHtml(sellStock.getStockName()));
        }

        if (!sellStock.getClosePrice().equalsIgnoreCase("") &&
                !sellStock.getClosePrice().equalsIgnoreCase("null") && sellStock.getClosePrice() != null) {
            holder.textViewClosingPrice.setText("$" + sellStock.getClosePrice());
        }

    }

    @Override
    public int getItemCount() {
        if (sellStockList.size() >= 5)
            return 5;
        else
            return sellStockList.size();
    }

    class ActiveSessionsHolder extends RecyclerView.ViewHolder {
        TextView textViewStockSymbol, textViewStockName, textViewClosingPrice;
        RelativeLayout layoutRowRoot;

        public ActiveSessionsHolder(@NonNull View itemView) {
            super(itemView);

            textViewStockSymbol = itemView.findViewById(R.id.textViewStockSymbol);
            textViewStockName = itemView.findViewById(R.id.textViewStockName);
            textViewClosingPrice = itemView.findViewById(R.id.textViewClosingPrice);
            layoutRowRoot = itemView.findViewById(R.id.layoutRowRoot);

            layoutRowRoot.setOnClickListener(v-> {
                onListClickListener.onRowClickListener();
            });
        }
    }

    public interface OnListClickListener {
        void onRowClickListener();
    }

    public void SetOnListClickListener(OnListClickListener onListClickListener) {
        this.onListClickListener = onListClickListener;
    }
}
