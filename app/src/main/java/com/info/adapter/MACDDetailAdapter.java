package com.info.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.info.model.BuyStock;
import com.info.model.SellStock;
import com.info.tradewyse.R;

import java.util.List;

public class MACDDetailAdapter extends RecyclerView.Adapter<MACDDetailAdapter.ActiveSessionsHolder> {

    private Context context;
    private List<BuyStock> buyStockList;
    private List<SellStock> sellStockList;

    public MACDDetailAdapter(Context context, List<BuyStock> buyStockList, List<SellStock> sellStockList) {
        this.context = context;
        this.buyStockList = buyStockList;
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

        if (buyStockList != null && buyStockList.size() > 0) {
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

        } else if (sellStockList != null && sellStockList.size() > 0) {
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

    }

    @Override
    public int getItemCount() {
        if (buyStockList != null && buyStockList.size() > 0)
            return buyStockList.size();
        else if (sellStockList != null && sellStockList.size() > 0)
            return sellStockList.size();
        return 0;
    }

    class ActiveSessionsHolder extends RecyclerView.ViewHolder {
        TextView textViewStockSymbol, textViewStockName, textViewClosingPrice;

        public ActiveSessionsHolder(@NonNull View itemView) {
            super(itemView);

            textViewStockSymbol = itemView.findViewById(R.id.textViewStockSymbol);
            textViewStockName = itemView.findViewById(R.id.textViewStockName);
            textViewClosingPrice = itemView.findViewById(R.id.textViewClosingPrice);
        }
    }
}
