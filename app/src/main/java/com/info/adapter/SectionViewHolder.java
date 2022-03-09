package com.info.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.info.tradewyse.R;

public class SectionViewHolder extends RecyclerView.ViewHolder {
    TextView txtStockName;
    public SectionViewHolder(View itemView) {
        super(itemView);
        txtStockName=itemView.findViewById(R.id.txtSectionName);
    }
}
