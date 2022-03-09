package com.info.tradewyse;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.model.Subscription;

import java.util.List;

import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialOfferActivity extends BaseActivity implements View.OnClickListener {

    TextView price;
    Subscription subscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_offer);
        initView();
    }

    public void initView() {
        TextView details = findViewById(R.id.detail);
        price = findViewById(R.id.txtPrice);

        findViewById(R.id.ll_see_our).setOnClickListener(this);
        findViewById(R.id.btn_continue_special_offer).setOnClickListener(this);
        price.setText("$10");

        if (getIntent() != null) {
            String name = getIntent().getStringExtra("name");
            details.setText(getString(R.string.become_a_trade_tips_member)); // , ""/*StringHelper.capitalFirstLetter(name)*/
        }

        getAllTrialSubscriptionPlan();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_see_our) {
            Common.showMessageWithCenterText(this, getString(R.string.special_offer_dialog_msg), getString(R.string.money_back_guarantee));
        } else if (v.getId() == R.id.btn_continue_special_offer) {
            if (subscription != null) {
                CheckoutActivity.start(SpecialOfferActivity.this, subscription, true, Constants.FROM_SPECIAL_OFFER);
            } else {
                Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getAllTrialSubscriptionPlan() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, SpecialOfferActivity.this);
        Call<List<Subscription>> call = apiInterface.getAllTrialSubscriptionPlan();
        showProgressDialog();
        call.enqueue(new Callback<List<Subscription>>() {
            @Override
            public void onResponse(Call<List<Subscription>> call, Response<List<Subscription>> response) {
                dismissProgressDialog();
                if (response.body() != null) {
                    subscription = response.body().get(response.body().size() - 1);
                    String subscriptionPrice = response.body().get(response.body().size() - 1).getSubscriptionPrice();
                    if (subscriptionPrice != null) {
                        price.setText("$" + subscriptionPrice);
                    }
                } else {
                    Toast.makeText(SpecialOfferActivity.this, "Something went wrong!please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Subscription>> call, Throwable t) {
                Toast.makeText(SpecialOfferActivity.this, "Something went wrong!please try again", Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                Common.showOfflineMemeDialog(SpecialOfferActivity.this, getResources().getString(R.string.memeMsg),
                        getResources().getString(R.string.JustLetYouKnow));
            }
        });
    }
}
