package com.info.tradewyse;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info.adapter.ActiveSessionsAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.interfaces.LogoutCallback;
import com.info.model.DeviceDetail;
import com.info.model.DeviceLimitExceedModel;
import com.info.model.LoginResponse;
import com.info.model.LogoutResponse;
import com.info.model.NotificationModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveSessionsActivity extends BaseActivity implements LogoutCallback {

    private RecyclerView recyclerView;
    private TextView done;
    private List<DeviceDetail> deviceDetailArrayList;
    ActiveSessionsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        initView();
    }


    public void  initView()
    {
        recyclerView=findViewById(R.id.rv_logged_in);
        done=findViewById(R.id.done);
        if (getIntent()!=null)
        {
            DeviceLimitExceedModel deviceLimitExceedModel= (DeviceLimitExceedModel) getIntent().getSerializableExtra("deviceLimitExceedModel");

            if (deviceLimitExceedModel!=null)
            {

                deviceDetailArrayList=deviceLimitExceedModel.getDeviceDetails();
                Common.showMessage(ActiveSessionsActivity.this, deviceLimitExceedModel.getMessage(), deviceLimitExceedModel.getTitle());

                recyclerView.setLayoutManager(new LinearLayoutManager(ActiveSessionsActivity.this,LinearLayoutManager.VERTICAL,false));
                 adapter=new ActiveSessionsAdapter(ActiveSessionsActivity.this,deviceDetailArrayList,this);
                recyclerView.setAdapter(adapter);
            }

        }


          done.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  finish();
              }
          });

    }

    @Override
    public void onLogout(String id,int position) {
        userLogout(id,position);
    }



    public void userLogout(String deviceLoginId,int position) {

        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, ActiveSessionsActivity.this);
        Call<LogoutResponse> call = apiInterface.logoutUser(deviceLoginId);
        showProgressDialog();
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                dismissProgressDialog();
                if (response.body() != null) {
                    if (response.body().getStatus().equals("true") && response.body().getTitle().equals("Success")) {
                     //   Common.logout(ActiveSessionsActivity.this);

                        if (deviceDetailArrayList!=null && adapter!=null)
                        {
                            deviceDetailArrayList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        if (deviceDetailArrayList != null && deviceDetailArrayList.size() == 0 ) {
                            finish();
                        }

                    }
                    else
                    {
                        Common.showMessage(ActiveSessionsActivity.this, response.body().getMessage(), response.body().getTitle());
                    }
                }
                else
                {
                    Toast.makeText(ActiveSessionsActivity.this, "Something went wrong!please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                Toast.makeText(ActiveSessionsActivity.this,"Something went wrong!please try again",Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                Common.showOfflineMemeDialog(ActiveSessionsActivity.this, getResources().getString(R.string.memeMsg),
                        getResources().getString(R.string.JustLetYouKnow));

            }
        });

    }
}
