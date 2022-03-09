package com.info.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.info.model.BuyStock;
import com.info.model.ServiceType;
import com.info.tradewyse.R;

import java.util.List;

public class ServiceFilterAdapter extends RecyclerView.Adapter<ServiceFilterAdapter.ServiceFilterHolder> {

    private Context context;
    private List<ServiceType> serviceTypeList;
    private ServiceFilterListener serviceFilterListener;

    public ServiceFilterAdapter(Context context, List<ServiceType> serviceTypeList) {
        this.context = context;
        this.serviceTypeList = serviceTypeList;
    }

    @NonNull
    @Override
    public ServiceFilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_service_filter, parent, false);

        return new ServiceFilterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceFilterHolder holder, int position) {

        ServiceType serviceType = serviceTypeList.get(position);

        if (!serviceType.getTitle().equalsIgnoreCase("") &&
                !serviceType.getTitle().equalsIgnoreCase("null") && serviceType.getTitle() != null) {
            holder.textViewTitle.setText(serviceType.getTitle());
        }

        if (serviceType.isSelected()) {
            holder.imageViewCheck.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewCheck.setVisibility(View.GONE);
        }

        holder.layoutRootTitle.setOnClickListener(v -> {
            serviceFilterListener.onFilterClick(position);
        });

    }

    @Override
    public int getItemCount() {
        return serviceTypeList.size();
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

    public interface ServiceFilterListener {
        void onFilterClick(int position);
    }

    public void setOnSubscriptionListener(ServiceFilterListener serviceFilterListener) {
        this.serviceFilterListener = serviceFilterListener;
    }
}
