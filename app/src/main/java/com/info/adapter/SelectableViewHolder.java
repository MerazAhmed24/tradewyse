package com.info.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.model.SelectableStocks;
import com.info.tradewyse.R;

/**
 * Created by ankur on 3/4/19.
 */

public class SelectableViewHolder extends RecyclerView.ViewHolder {

    TextView txtStockName, txtStockCompany;
    ImageView imgCheck;
    SelectableStocks mStocks;
    OnStocksSelectedListener itemSelectedListener;
    public boolean clickable;


    public SelectableViewHolder(View view, OnStocksSelectedListener listener) {
        super(view);
        itemSelectedListener = listener;
        txtStockName = view.findViewById(R.id.txtStockName);
        txtStockCompany = view.findViewById(R.id.txtStockCompany);
        imgCheck = view.findViewById(R.id.checkImage);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickable) {
                    if (mStocks.isSelected()) {
                        setChecked(false);
                    } else {
                        setChecked(true);
                    }
                    itemSelectedListener.onStocksSelected(mStocks);

                }
            }
        });
    }

    public void setChecked(boolean value) {
        if (value) {
            imgCheck.setImageResource(R.drawable.ic_check);
        } else {
            imgCheck.setImageResource(R.drawable.ic_unchecked);
        }
        mStocks.setSelected(value);
    }


    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public interface OnStocksSelectedListener {
        void onStocksSelected(SelectableStocks item);
    }

}