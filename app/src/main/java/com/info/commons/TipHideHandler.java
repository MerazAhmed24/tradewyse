package com.info.commons;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.interfaces.HideTipResponseListener;
import com.info.interfaces.LikeResponseListener;
import com.info.model.HideTipResponseModel;
import com.info.model.LikeResponse;
import com.info.model.PinResponse;
import com.info.model.TipResponse;
import com.info.tradewyse.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TipHideHandler {
   private Context context;
    private String tipId;

    public TipHideHandler(Context context, String tipId) {
        this.context = context;
        this.tipId = tipId;
    }

    public void hideTip(HideTipResponseListener hideTipResponseListener){
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL,context);
        Call<HideTipResponseModel> call = apiInterface.tipHideForUser(tipId);
        call.enqueue(new Callback<HideTipResponseModel>() {
            @Override
            public void onResponse(Call<HideTipResponseModel> call, Response<HideTipResponseModel> response) {
                if (response != null && response.body() != null) {
                    HideTipResponseModel pinResponse=response.body();
                    handleResponse(pinResponse,hideTipResponseListener);
                }
            }
            @Override
            public void onFailure(Call<HideTipResponseModel> call, Throwable t) {
                Log.d("ONFailure", "onFailure: ");
            }
        });
    }

    public void unHideTip(HideTipResponseListener hideTipResponseListener){
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL,context);
        Call<HideTipResponseModel> call = apiInterface.tipUnHideForUser(tipId);
        call.enqueue(new Callback<HideTipResponseModel>() {
            @Override
            public void onResponse(Call<HideTipResponseModel> call, Response<HideTipResponseModel> response) {
                if (response != null && response.body() != null) {
                    HideTipResponseModel pinResponse=response.body();
                    handleResponse(pinResponse,hideTipResponseListener);
                }
            }
            @Override
            public void onFailure(Call<HideTipResponseModel> call, Throwable t) { }
        });
    }


    private static void handleResponse(HideTipResponseModel pinResponse, HideTipResponseListener hideResponseListener){
        if (pinResponse.getIsHide()) {
        } else {
        }
        hideResponseListener.onHideUnHideStatus(pinResponse.getIsHide());
    }
}
