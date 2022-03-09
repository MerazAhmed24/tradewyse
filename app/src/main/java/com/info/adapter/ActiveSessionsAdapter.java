package com.info.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.info.commons.DateTimeHelperElapsed;
import com.info.interfaces.LogoutCallback;
import com.info.model.DeviceDetail;
import com.info.model.DeviceInfoModel;
import com.info.tradewyse.R;

import java.util.List;

public class ActiveSessionsAdapter extends RecyclerView.Adapter<ActiveSessionsAdapter.ActiveSessionsHolder> {

    private Context context;
    private List<DeviceDetail> deviceDetailArrayList;
    private LogoutCallback logoutCallback;

    public ActiveSessionsAdapter(Context context, List<DeviceDetail> deviceDetailArrayList, LogoutCallback logoutCallback) {
        this.context = context;
        this.deviceDetailArrayList = deviceDetailArrayList;
        this.logoutCallback = logoutCallback;
    }

    @NonNull
    @Override
    public ActiveSessionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_active_sessions, parent, false);

        return new ActiveSessionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveSessionsHolder holder, int position) {

        DeviceDetail deviceDetail = deviceDetailArrayList.get(position);

        DeviceInfoModel deviceInfoModel = new Gson().fromJson(deviceDetail.getDeviceInfo(), DeviceInfoModel.class);
        holder.appVersion.setText("App Version: " + (deviceInfoModel != null ? deviceInfoModel.getAppVersion() : "NA"));
        holder.device.setText("Device: " + (deviceInfoModel != null ? deviceInfoModel.getDeviceName() : "NA"));
        holder.model.setText("Model: " +  (deviceInfoModel != null ? deviceInfoModel.getDeviceModel() : "NA"));
        holder.loggedInTime.setText("Logged in: " + DateTimeHelperElapsed.getElapsedTime(deviceDetail.getCreatedOn()));

        holder.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logoutCallback.onLogout(deviceDetail.getId(), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceDetailArrayList.size();
    }

    class ActiveSessionsHolder extends RecyclerView.ViewHolder {
        AppCompatTextView device;
        AppCompatTextView model;
        AppCompatTextView appVersion;
        AppCompatTextView loggedInTime;
        AppCompatTextView logout;

        public ActiveSessionsHolder(@NonNull View itemView) {
            super(itemView);

            device = itemView.findViewById(R.id.device);
            model = itemView.findViewById(R.id.model);
            appVersion = itemView.findViewById(R.id.app_version);
            loggedInTime = itemView.findViewById(R.id.logged_in_time);
            logout = itemView.findViewById(R.id.logout);
        }
    }
}
