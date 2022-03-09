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
import com.info.tradewyse.R;

import java.util.List;

public class MACDBuyAdapter extends RecyclerView.Adapter<MACDBuyAdapter.ActiveSessionsHolder> {

    private Context context;
    private List<BuyStock> buyStockList;
    private OnListClickListener onListClickListener;


    public MACDBuyAdapter(Context context, List<BuyStock> buyStockList) {
        this.context = context;
        this.buyStockList = buyStockList;
    }

    @NonNull
    @Override
    public ActiveSessionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_macd_alert, parent, false);

        return new ActiveSessionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveSessionsHolder holder, int position) {

        BuyStock buyStock = buyStockList.get(position);

        if (!buyStock.getStockSymbol().equalsIgnoreCase("") &&
                !buyStock.getStockSymbol().equalsIgnoreCase("null") &&
                !buyStock.getStockSymbol().equalsIgnoreCase("buy_stock") && buyStock.getStockSymbol() != null) {
            holder.textViewStockSymbol.setText(Html.fromHtml(buyStock.getStockSymbol()));
        }

        if (!buyStock.getStockName().equalsIgnoreCase("") &&
                !buyStock.getStockName().equalsIgnoreCase("null") && buyStock.getStockName() != null) {
            holder.textViewStockName.setText(Html.fromHtml(buyStock.getStockName()));
        }

        if (!buyStock.getClosePrice().equalsIgnoreCase("") &&
                !buyStock.getClosePrice().equalsIgnoreCase("null") && buyStock.getClosePrice() != null) {
            holder.textViewClosingPrice.setText("$" + buyStock.getClosePrice());
        }

    }

    @Override
    public int getItemCount() {
        if (buyStockList.size() >= 5)
            return 5;
        else
            return buyStockList.size();
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
