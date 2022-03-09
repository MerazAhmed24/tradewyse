package com.info.commons;

import android.content.Context;
import android.widget.ImageView;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.interfaces.LikeResponseListener;
import com.info.model.LikeResponse;
import com.info.tradewyse.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeHandler {
   private Context context;
    private ImageView imgLike;private String tipId;

    public LikeHandler(Context context, ImageView imgLike, String tipId) {
        this.context = context;
        this.imgLike = imgLike;
        this.tipId = tipId;
    }

    public void likeTip(LikeResponseListener likeResponseListener){
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL,context);
        Call<LikeResponse> call = apiInterface.userTipLike(tipId);
        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response != null && response.body() != null) {
                    LikeResponse likeResponse=response.body();
                    handleResponse(likeResponse,imgLike,likeResponseListener);
                }
            }
            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) { }
        });
    }

    public void unLikeTip(LikeResponseListener likeResponseListener){
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL,context);
        Call<LikeResponse> call = apiInterface.userTipUnLike(tipId);
        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response != null && response.body() != null) {
                    LikeResponse likeResponse=response.body();
                    handleResponse(likeResponse,imgLike,likeResponseListener);
                }
            }
            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) { }
        });
    }


    private static void handleResponse(LikeResponse likeResponse,ImageView imgLike,LikeResponseListener likeResponseListener){
        if (likeResponse.getUserLiked()) {
            imgLike.setImageResource(R.drawable.ic_like_fill);
        } else {
            imgLike.setImageResource(R.drawable.ic_like);
        }
        likeResponseListener.onLikeStatus(likeResponse.getUserLiked());
    }
}
