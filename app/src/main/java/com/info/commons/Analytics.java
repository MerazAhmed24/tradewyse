package com.info.commons;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.model.analyticsModel.UserOpenAppModel;
import com.info.tradewyse.DashBoardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Analytics {

    public static void AnalyticsDataSetOpenApp(Context context) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        Call<UserOpenAppModel> call = apiInterface.setAppUserDataForAnalytics();
        call.enqueue(new Callback<UserOpenAppModel>() {
            @Override
            public void onResponse(Call<UserOpenAppModel> call, Response<UserOpenAppModel> response) {
                if (response.isSuccessful()) {
                    Log.i("AnalyticsSuccess", "result is:Success" + response.body().toString());
                } else {
                    // Log.i("AnalyticsFailed", "result is:failed" + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<UserOpenAppModel> call, Throwable t) {

            }
        });
    }
}
