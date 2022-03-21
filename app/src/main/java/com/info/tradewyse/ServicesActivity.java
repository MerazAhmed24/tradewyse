package com.info.tradewyse;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.info.adapter.ServiceAdapter;
import com.info.adapter.ServiceFilterAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.interfaces.AddSubscribeSuggestedServiceCallback;
import com.info.model.FooterModel;
import com.info.model.MyServices;
import com.info.model.NotificationCountModel;
import com.info.model.ServiceType;
import com.info.model.userServiceResponse.ServiceSubscription;
import com.info.model.userServiceResponse.ServiceSubscriptionPlan;
import com.info.model.userServiceResponse.UserServiceResponse;
import com.info.tradewyse.databinding.ActivityServicesBinding;
import com.shuhart.stickyheader.StickyHeaderItemDecorator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesActivity extends BaseActivity implements AddSubscribeSuggestedServiceCallback {

    private ActivityServicesBinding activityServicesBinding;
    private RecyclerView recyclerViewServices;
    private ProgressBar progressBar;
    private TradWyseSession tradWyseSession;
    List<ServiceType> serviceTypeList = new ArrayList<>();
    List<ServiceSubscriptionPlan> filterdList = new ArrayList<>();
    List<ServiceSubscriptionPlan> allServiceList = new ArrayList<>();
    List<ServiceSubscriptionPlan> myServicesList = new ArrayList<>();
    private String ALL_SERVICE_TYPE = "-1";
    private TextView textViewAllServices, textViewMyServices;
    private int selectedTab = 1;
    private ScrollView scrollViewEmpty;
    private LinearLayout layoutEmpty;
    private TextView textViewEmptyTitle, textViewEmptyDes, textViewEmptyDesLink;
    private TextView otherAction;
    private boolean isFilterList = false;
    private String isMentor;
    LinearLayout bottomLinearLayout;

    public static void CallServicesActivity(Context mContext) {
        Intent mIntent = new Intent(mContext, ServicesActivity.class);
        mContext.startActivity(mIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityServicesBinding = DataBindingUtil.setContentView(this, R.layout.activity_services);
        FooterModel footerModel = new FooterModel(false, false, true, false, false);
        activityServicesBinding.setFooterModel(footerModel);
        tradWyseSession = TradWyseSession.getSharedInstance(this);
        isMentor = tradWyseSession.getIsMentor();


        setToolBarAddTip(getString(R.string.services));
        otherAction = findViewById(R.id.otherAction);
        findViewById(R.id.backAction).setVisibility(View.GONE);
        otherAction.setVisibility(View.VISIBLE);
        otherAction.setText("Filter");
        otherAction.setOnClickListener(v -> {
            if (serviceTypeList.size() > 0) {
                showFiltersDialog();
            }
        });

        recyclerViewServices = findViewById(R.id.recyclerViewServices);
        progressBar = findViewById(R.id.progress);
        textViewAllServices = findViewById(R.id.textViewAllServices);
        textViewMyServices = findViewById(R.id.textViewMyServices);
        scrollViewEmpty = findViewById(R.id.scrollViewEmpty);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        textViewEmptyTitle = findViewById(R.id.textViewEmptyTitle);
        textViewEmptyDes = findViewById(R.id.textViewEmptyDes);
        textViewEmptyDesLink = findViewById(R.id.textViewEmptyDesLink);
        bottomLinearLayout = findViewById(R.id.bottomView);
        //progressBar.setVisibility(View.VISIBLE);
       // Common.BottomTabColorChange(ServicesActivity.this,bottomLinearLayout);



        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        textViewAllServices.setOnClickListener(v-> {
            AllServicesClicked();
        });

        textViewMyServices.setOnClickListener(v-> {
            MyServicesClicked();
        });

        textViewEmptyDesLink.setOnClickListener(v-> {
            AllServicesClicked();
        });

        getNotificationCount();

        getAllServiceType();
        getAllTypeServicesForUser("AllServices");
        //ChangeTestBottomColor();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void AllServicesClicked() {
        textViewAllServices.setBackground(getResources().getDrawable(R.drawable.bg_service_selector_blue));
        textViewAllServices.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        textViewMyServices.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        textViewMyServices.setTextColor(getResources().getColor(R.color.color_text_dark_layout));
        selectedTab = 1;

        if (ALL_SERVICE_TYPE.equalsIgnoreCase("-1")) {
            getAllTypeServicesForUser("AllServices");
        } else {
            getAllOtherServiceListFilter(ALL_SERVICE_TYPE);
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void MyServicesClicked() {
        textViewMyServices.setBackground(getResources().getDrawable(R.drawable.bg_service_selector_blue));
        textViewMyServices.setTextColor(getResources().getColor(R.color.color_text_dark_layout));

        textViewAllServices.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        textViewAllServices.setTextColor(getResources().getColor(R.color.color_text_dark_layout));
        selectedTab = 2;

        if (ALL_SERVICE_TYPE.equalsIgnoreCase("-1")) {
            getAllTypeServicesForUser("MyServices");
            //getAllOtherServiceListFilter(ALL_SERVICE_TYPE);
        } else {
            getMyServiceListFilter(ALL_SERVICE_TYPE);
        }
    }

    public void getAllTypeServicesForUser(String type) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<UserServiceResponse> call = apiInterface.getAllTypeServicesForUser(tradWyseSession.getUserId());
        call.enqueue(new Callback<UserServiceResponse>() {
            @Override
            public void onResponse(@NotNull Call<UserServiceResponse> call, @NotNull Response<UserServiceResponse> response) {
                progressBar.setVisibility(View.GONE);
                int subscribedListCount = 0;
                int suggestedListCount = 0;
                int mentorListCount = 0;
                int myServicesCount = 0;
                allServiceList.clear();
                myServicesList.clear();

                if (response.body() != null) {
                    UserServiceResponse userServiceResponse = response.body();

                    if (type.equalsIgnoreCase("MyServices")) {

                        // User purchased
                        if (userServiceResponse.getServiceSubscriptions() != null && userServiceResponse.getServiceSubscriptions().size() > 0) {
                            List<ServiceSubscriptionPlan> list = getServiceSubscriptionPlanList(userServiceResponse.getServiceSubscriptions());
                            if (list != null && list.size() > 0) {
                                list = getServiceSubscriptionPlanList(list, "Purchased Services", mentorListCount);
                                myServicesList.addAll(list);
                                myServicesCount = list.size();
                            }
                        }

                        // User created
                        if (userServiceResponse.getServiceSubscriptionPlans() != null && userServiceResponse.getServiceSubscriptionPlans().size() > 0) {

                            List<ServiceSubscriptionPlan> list = getServiceSubscriptionPlanList(userServiceResponse.getServiceSubscriptionPlans(), "Created Services ", myServicesCount);

                            myServicesList.addAll(list);
                        }

                        if (myServicesList.size() > 0) {
                            isFilterList = false;
                            updateUi(myServicesList);
                            recyclerViewServices.setVisibility(View.VISIBLE);
                            scrollViewEmpty.setVisibility(View.GONE);
                            otherAction.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewServices.setVisibility(View.GONE);
                            scrollViewEmpty.setVisibility(View.VISIBLE);
                            textViewEmptyTitle.setText(getResources().getString(R.string.add_your_first_service));
                            textViewEmptyDes.setText(getResources().getString(R.string.my_service_empty_text));
                            textViewEmptyDesLink.setText(Html.fromHtml("<u>"+getResources().getString(R.string.choose_your_first_service)+"</u>"));
                            otherAction.setVisibility(View.GONE);
                        }

                    } else {


                        if (userServiceResponse.getOtherServiceSubscriptionPlans() != null && userServiceResponse.getOtherServiceSubscriptionPlans().size() > 0) {
                            List<ServiceSubscriptionPlan> list = getServiceSubscriptionPlanList(userServiceResponse.getOtherServiceSubscriptionPlans(), "All Services", 0);
                            allServiceList.addAll(list);
                            mentorListCount = list.size();

                        }

                        if (userServiceResponse.getSuggestedServiceSubscriptionPlans() != null && userServiceResponse.getSuggestedServiceSubscriptionPlans().size() > 0) {
                            List<ServiceSubscriptionPlan> list = getServiceSubscriptionPlanList(userServiceResponse.getSuggestedServiceSubscriptionPlans(), "TradeTips Services", mentorListCount + subscribedListCount);
                            allServiceList.addAll(list);
                            suggestedListCount = userServiceResponse.getSuggestedServiceSubscriptionPlans().size();
                        }

                        if (allServiceList.size() > 0) {
                            isFilterList = false;
                            updateUi(allServiceList);
                            recyclerViewServices.setVisibility(View.VISIBLE);
                            scrollViewEmpty.setVisibility(View.GONE);
                            otherAction.setVisibility(View.VISIBLE);
                            //txtUserMsg.setVisibility(View.GONE);
                        } else {
                            recyclerViewServices.setVisibility(View.GONE);
                            scrollViewEmpty.setVisibility(View.VISIBLE);
                            textViewEmptyTitle.setText("No Services available");
                            textViewEmptyDes.setText("Please check again later");
                            textViewEmptyDesLink.setText("");
                            otherAction.setVisibility(View.GONE);
                        }
                    }


                    findViewById(R.id.imageViewFilter).setVisibility(View.GONE);
                } else {
                    recyclerViewServices.setVisibility(View.GONE);
                    scrollViewEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserServiceResponse> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d("error:", "" + t.toString());
                Log.d(Constants.ON_FAILURE_TAG, "ServiceFragment getAllServiceSubscriptionPlanForMentor: onFailure");
                Common.showOfflineMemeDialog(ServicesActivity.this, getResources().getString(R.string.memeMsg),
                        getResources().getString(R.string.JustLetYouKnow));
                recyclerViewServices.setVisibility(View.GONE);
                scrollViewEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    public List<ServiceSubscriptionPlan> getServiceSubscriptionPlanList(List<ServiceSubscriptionPlan> serviceSubscriptionList, String headerName, int section) {
        List<ServiceSubscriptionPlan> userServiceList = new ArrayList<>();

        ServiceSubscriptionPlan serviceSubscriptionPlanHeader = new ServiceSubscriptionPlan();

        for (int i = 0; i < serviceSubscriptionList.size(); i++) {

            ServiceSubscriptionPlan serviceSubscriptionPlan = serviceSubscriptionList.get(i);
            serviceSubscriptionPlan.setServiceType(Constants.SERVICE_LIST);
            serviceSubscriptionPlan.setSection(section);
            userServiceList.add(serviceSubscriptionPlan);

        }

        serviceSubscriptionPlanHeader.setServiceType(Constants.SERVICE_HEADER);
        serviceSubscriptionPlanHeader.setServiceHeaderTitle(headerName);
        serviceSubscriptionPlanHeader.setSection(section);
        userServiceList.add(0, serviceSubscriptionPlanHeader);

        return userServiceList;
    }

    public List<ServiceSubscriptionPlan> getServiceSubscriptionPlanListFiltered(List<ServiceSubscriptionPlan> serviceSubscriptionList, String headerName, int section) {
        List<ServiceSubscriptionPlan> userServiceList = new ArrayList<>();

        for (int i = 0; i < serviceSubscriptionList.size(); i++) {

            ServiceSubscriptionPlan serviceSubscriptionPlan = serviceSubscriptionList.get(i);
            serviceSubscriptionPlan.setServiceType(Constants.SERVICE_LIST);
            serviceSubscriptionPlan.setSection(section);
            userServiceList.add(serviceSubscriptionPlan);

        }
        ServiceSubscriptionPlan serviceSubscriptionPlanHeader = new ServiceSubscriptionPlan();
        serviceSubscriptionPlanHeader.setServiceType(Constants.SERVICE_HEADER);
        serviceSubscriptionPlanHeader.setServiceHeaderTitle(headerName);
        serviceSubscriptionPlanHeader.setSection(section);
        userServiceList.add(0, serviceSubscriptionPlanHeader);

        return userServiceList;
    }

    public List<ServiceSubscriptionPlan> getServiceSubscriptionPlanList(List<ServiceSubscription> serviceSubscriptionList) {
        List<ServiceSubscriptionPlan> userServiceList = new ArrayList<>();
        for (int i = 0; i < serviceSubscriptionList.size(); i++) {
            ServiceSubscriptionPlan serviceSubscriptionPlan = serviceSubscriptionList.get(i).getServiceSubscriptionPlan();
            userServiceList.add(serviceSubscriptionPlan);
        }

        return userServiceList;
    }

    private void updateUi(List<ServiceSubscriptionPlan> userServiceList) {
        ServiceAdapter adapter = new ServiceAdapter(this, this, userServiceList, "", isMentor, ServicesActivity.this, true, true, Constants.SERVICES, isFilterList);
        recyclerViewServices.setAdapter(adapter);
        StickyHeaderItemDecorator decorator = new StickyHeaderItemDecorator(adapter);
        decorator.attachToRecyclerView(recyclerViewServices);
    }

    public void homeTabClicked(View v) {
        DashBoardActivity.CallDashboardActivity(this, false);
        finish();
    }

    public void chatTabClicked(View v) {
        ChatActivity.starChatActivity(this,false);
        //TabbedChatActivity.CallTabbedChatActivity(this, true);
        finish();
    }

    public void servicesTabClicked(View v) {
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

    @Override
    public void addSuggestedService(String serviceSubscriptionPlanId) {

    }

    public void getAllServiceType() {
        //progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<List<ServiceType>> call = apiInterface.getAllServiceType();
        call.enqueue(new Callback<List<ServiceType>>() {
            @Override
            public void onResponse(@NotNull Call<List<ServiceType>> call, @NotNull Response<List<ServiceType>> response) {

                if (response.body() != null) {
                    List<ServiceType> serviceTypeResponse = response.body();
                    serviceTypeList.addAll(serviceTypeResponse);
                    //progressBar.setVisibility(View.GONE);
                }
                else{
                    //layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ServiceType>> call, @NotNull Throwable t) {
                //progressBar.setVisibility(View.GONE);
                //layoutEmpty.setVisibility(View.VISIBLE);
                Common.showOfflineMemeDialog(ServicesActivity.this, getResources().getString(R.string.memeMsg),
                        getResources().getString(R.string.JustLetYouKnow));
                Log.d("error:", "" + t.toString());
            }
        });
    }

    private void showFiltersDialog() {
        try {
            BottomSheetDialog filterOption = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.service_filter_option, null);
            RecyclerView recyclerViewFilter = sheetView.findViewById(R.id.recyclerViewFilter);
            TextView optionCancel = sheetView.findViewById(R.id.optionCancel);
            TextView filterResetTips = sheetView.findViewById(R.id.filterResetTips);
            filterOption.setContentView(sheetView);
            /*BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) sheetView.getParent());
            mBehavior.setPeekHeight(1200);*/

            if (!ALL_SERVICE_TYPE.equalsIgnoreCase("-1")) {
                filterResetTips.setVisibility(View.VISIBLE);
            } else {
                filterResetTips.setVisibility(View.GONE);
            }

            // Set data to adapter
            ServiceFilterAdapter adapter = new ServiceFilterAdapter(this, serviceTypeList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerViewFilter.setLayoutManager(layoutManager);
            recyclerViewFilter.setAdapter(adapter);
            adapter.setOnSubscriptionListener(new ServiceFilterAdapter.ServiceFilterListener() {
                @Override
                public void onFilterClick(int position) {
                    ALL_SERVICE_TYPE = serviceTypeList.get(position).getId();

                    allServiceList.clear();

                    if (selectedTab == 1) {
                        getAllOtherServiceListFilter(ALL_SERVICE_TYPE);
                    } else {
                        getMyServiceListFilter(ALL_SERVICE_TYPE);
                    }

                    filterOption.cancel();

                    for (int i = 0; i < serviceTypeList.size(); i++) {
                        if (serviceTypeList.get(i).isSelected()) {
                            serviceTypeList.get(i).setSelected(false);
                        }
                    }
                    serviceTypeList.get(position).setSelected(true);

                    adapter.notifyDataSetChanged();

                }
            });

            filterResetTips.setOnClickListener(v -> {
                ALL_SERVICE_TYPE = "-1";
                for (int i = 0; i < serviceTypeList.size(); i++) {
                    if (serviceTypeList.get(i).isSelected()) {
                        serviceTypeList.get(i).setSelected(false);
                    }
                }
                if (selectedTab == 1) {
                    getAllTypeServicesForUser("AllServices");
                } else {
                    getAllTypeServicesForUser("MyServices");
                }

                filterOption.cancel();
            });

            optionCancel.setOnClickListener(view -> filterOption.cancel());
            filterOption.show();

        } catch (Exception e) {
        }

    }

    public void getMyServiceListFilter(String serviceTypeId) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<MyServices> call = apiInterface.getMyServiceListFilterByServiceMasterId(serviceTypeId);
        call.enqueue(new Callback<MyServices>() {
            @Override
            public void onResponse(@NotNull Call<MyServices> call, @NotNull Response<MyServices> response) {
                progressBar.setVisibility(View.GONE);
                int subscribedListCount = 0;
                int mentorListCount = 0;
                filterdList.clear();

                if (response.body() != null) {
                    MyServices myServices = response.body();

                    if (myServices.getMyServiceSubscriptionPlans() != null && myServices.getMyServiceSubscriptionPlans().size() > 0) {
                        List<ServiceSubscriptionPlan> list = myServices.getMyServiceSubscriptionPlans();
                        if (list != null && list.size() > 0) {

                            list = getServiceSubscriptionPlanListFiltered(list, "Purchased Services", mentorListCount);

                            filterdList.addAll(list);
                            subscribedListCount = list.size();
                        }
                    }
                    if (myServices.getServiceSubscriptionMyMentorPlans() != null && myServices.getServiceSubscriptionMyMentorPlans().size() > 0) {

                        List<ServiceSubscriptionPlan> list = getServiceSubscriptionPlanListFiltered(myServices.getServiceSubscriptionMyMentorPlans(), "Created Services ", subscribedListCount);
                        filterdList.addAll(list);
                    }

                    isFilterList = false;
                    updateUi(filterdList);

                    if (filterdList.size() == 0) {
                        scrollViewEmpty.setVisibility(View.VISIBLE);
                        textViewEmptyTitle.setText("No results were found in this category.");
                        textViewEmptyDes.setText(Html.fromHtml("<u>"+"Click here to see all Services again."+"</u>"));
                        textViewEmptyDesLink.setText("");

                        textViewEmptyDes.setOnClickListener(v-> {
                            ALL_SERVICE_TYPE = "-1";
                            for (int i = 0; i < serviceTypeList.size(); i++) {
                                if (serviceTypeList.get(i).isSelected()) {
                                    serviceTypeList.get(i).setSelected(false);
                                }
                            }
                            if (selectedTab == 1) {
                                getAllTypeServicesForUser("AllServices");
                            } else {
                                getAllTypeServicesForUser("MyServices");
                            }

                            // Hide the filter icon.
                            findViewById(R.id.imageViewFilter).setVisibility(View.GONE);
                        });

                    } else {
                        scrollViewEmpty.setVisibility(View.GONE);
                        textViewEmptyTitle.setText("");
                        textViewEmptyDes.setText("");
                        textViewEmptyDesLink.setText("");
                    }

                    // Show the filter icon.
                    findViewById(R.id.imageViewFilter).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<MyServices> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d("error:", "" + t.toString());
            }
        });
    }

    public void getAllOtherServiceListFilter(String serviceTypeId) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<List<ServiceSubscriptionPlan>> call = apiInterface.getAllOtherServiceListFilterByServiceMasterId(serviceTypeId);
        call.enqueue(new Callback<List<ServiceSubscriptionPlan>>() {
            @Override
            public void onResponse(@NotNull Call<List<ServiceSubscriptionPlan>> call, @NotNull Response<List<ServiceSubscriptionPlan>> response) {
                progressBar.setVisibility(View.GONE);
                int subscribedListCount = 0;
                int mentorListCount = 0;
                filterdList.clear();

                if (response.body() != null) {
                    List<ServiceSubscriptionPlan> myServices = response.body();

                    if (myServices != null && myServices.size() > 0) {

                        List<ServiceSubscriptionPlan> list = getServiceSubscriptionPlanListFiltered(myServices, myServices.get(0).getServiceTypeMaster().getTitle(), 0);
                        filterdList.addAll(list);
                        mentorListCount = list.size();
                    }

                    isFilterList = true;
                    updateUi(filterdList);

                    if (filterdList.size() == 0) {
                        scrollViewEmpty.setVisibility(View.VISIBLE);
                        textViewEmptyTitle.setText("No results were found in this category.");
                        textViewEmptyDes.setText(Html.fromHtml("<u>"+"Click here to see all Services again."+"</u>"));
                        textViewEmptyDesLink.setText("");

                        textViewEmptyDes.setOnClickListener(v-> {
                            ALL_SERVICE_TYPE = "-1";
                            for (int i = 0; i < serviceTypeList.size(); i++) {
                                if (serviceTypeList.get(i).isSelected()) {
                                    serviceTypeList.get(i).setSelected(false);
                                }
                            }
                            if (selectedTab == 1) {
                                getAllTypeServicesForUser("AllServices");
                            } else {
                                getAllTypeServicesForUser("MyServices");
                            }

                            // Hide the filter icon.
                            findViewById(R.id.imageViewFilter).setVisibility(View.GONE);
                        });

                    } else {
                        scrollViewEmpty.setVisibility(View.GONE);
                        textViewEmptyTitle.setText("");
                        textViewEmptyDes.setText("");
                        textViewEmptyDesLink.setText("");
                    }

                    // Show the filter icon.
                    findViewById(R.id.imageViewFilter).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ServiceSubscriptionPlan>> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d("error:", "" + t.toString());
            }
        });
    }
}