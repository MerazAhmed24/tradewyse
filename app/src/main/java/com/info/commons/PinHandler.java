package com.info.commons;

import android.content.Context;
import android.widget.ImageView;

import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.interfaces.LikeResponseListener;
import com.info.interfaces.PinResponseListener;
import com.info.model.LikeResponse;
import com.info.model.PinResponse;
import com.info.tradewyse.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PinHandler {
   private Context context;
    private ImageView imgPin;
    private String tipId;

    public PinHandler(Context context, ImageView imgPin, String tipId) {
        this.context = context;
        this.imgPin = imgPin;
        this.tipId = tipId;
    }

    public void pinTip(PinResponseListener pinResponseListener){
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL,context);
        Call<LikeResponse> call = apiInterface.tipPinForUser(tipId);
        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response != null && response.body() != null) {
                    LikeResponse likeResponse=response.body();
                    handleResponse(likeResponse,imgPin,pinResponseListener);
                }
            }
            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) { }
        });
    }

    public void unPinTip(PinResponseListener pinResponseListener){
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL,context);
        Call<LikeResponse> call = apiInterface.tipUnPinForUser(tipId);
        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response != null && response.body() != null) {
                    LikeResponse likeResponse=response.body();
                    handleResponse(likeResponse,imgPin,pinResponseListener);
                }
            }
            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) { }
        });
    }


    private static void handleResponse(LikeResponse pinResponse, ImageView imgPin, PinResponseListener pinResponseListener){
        if (pinResponse.isPin()) {
            imgPin.setImageResource(R.drawable.ic_pin_save);
        } else {
            imgPin.setImageResource(R.drawable.ic_tag);
        }
        pinResponseListener.onPinStatus(pinResponse.isPin());
    }
}
