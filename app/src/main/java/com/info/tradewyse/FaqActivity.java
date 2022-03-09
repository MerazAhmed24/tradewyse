package com.info.tradewyse;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info.adapter.FaqRvAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.model.FaqAnswersModels;
import com.info.model.FaqQuestionModel;
import com.info.model.FooterModel;
import com.info.model.NotificationCountModel;
import com.info.tradewyse.databinding.ActivityFaqBinding;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqActivity extends BaseActivity {
    RecyclerView recyclerView;
    public FaqRvAdapter faqRvAdapter;
    ActivityFaqBinding faqBinding;
    LinearLayout bottomLinearLayout;
    int lastCompletelyVisibleItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        faqBinding = DataBindingUtil.setContentView(this, R.layout.activity_faq);
        FooterModel footerModel = new FooterModel(false, false, false, false, true);
        faqBinding.setFooterModel(footerModel);
        getNotificationCount();
        recyclerView = findViewById(R.id.faq_rv);
        bottomLinearLayout = findViewById(R.id.bottomView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        faqRvAdapter = new FaqRvAdapter(getFaqList(), FaqActivity.this, recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(faqRvAdapter);
        setToolBarAddTip(getString(R.string.faqs));
        Common.BottomTabColorChange(this,bottomLinearLayout);


    }

    private List<? extends ExpandableGroup> getFaqList() {

        return Arrays.asList(new FaqQuestionModel(getResources().getString(R.string.faq1), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq1_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq2), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq2_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq3), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq3_answer)))),

                // new FaqQuestionModel(getResources().getString(R.string.faq4), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq4_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq5), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq5_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq5_1), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq5_1_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq6), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq6_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq7), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq7_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq8), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq8_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq9), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq9_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq10), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq10_answer)))),

                new FaqQuestionModel(getResources().getString(R.string.faq11), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq11_answer))))/*,

                new FaqQuestionModel(getResources().getString(R.string.faq12), Arrays.asList(new FaqAnswersModels(getResources().getString(R.string.faq12_answer))))*/);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        faqRvAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        faqRvAdapter.onRestoreInstanceState(savedInstanceState);
    }

    public void homeTabClicked(View v) {
        DashBoardActivity.CallDashboardActivity(this, false);
        finish();
    }

    public void chatTabClicked(View v) {
        ChatActivity.starChatActivity(this, false);
        //TabbedChatActivity.CallTabbedChatActivity(this, true);
        finish();
    }

    public void servicesTabClicked(View v) {
        ServicesActivity.CallServicesActivity(this);
        finish();
    }

    public void notificationTabClicked(View v) {
        NotificationActivity.starNotificationActivity(this);
        finish();
    }

    public void moreTabClicked(View v) {
        MoreTabActivity.startMoreTabActivity(this);
        finish();
    }

    private void getNotificationCount() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<NotificationCountModel> call = apiInterface.getCountOfAllUnreadNotificationActivityDetails();
        call.enqueue(new Callback<NotificationCountModel>() {
            @Override
            public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                if (response != null && response.body() != null) {
                    NotificationCountModel notificationCountModel = response.body();
                    if (notificationCountModel.getUnReadCount() > 0) {
                        (findViewById(R.id.nav_unread_badge)).setVisibility(View.VISIBLE);
                    } else {
                        (findViewById(R.id.nav_unread_badge)).setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationCountModel> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getUserProfile: onFailure", t.fillInStackTrace());
            }
        });
    }
}