package com.info.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.info.adapter.ServiceAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.interfaces.AddSubscribeSuggestedServiceCallback;
import com.info.interfaces.MentorServiceCount;
import com.info.model.AddSuggestedServiceSubscriptionPlan;
import com.info.model.userServiceResponse.ServiceSubscription;
import com.info.model.userServiceResponse.ServiceSubscriptionPlan;
import com.info.model.userServiceResponse.UserServiceResponse;
import com.info.tradewyse.ProfileTabbedActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.WebViewActivity;
import com.shuhart.stickyheader.StickyHeaderItemDecorator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFragment extends BaseFragment implements View.OnClickListener, AddSubscribeSuggestedServiceCallback {
    private RecyclerView rvService;
    private ProgressBar progressBar;
    private MaterialTextView txtUserMsg;
    private MaterialTextView txtUserName;
    private MaterialTextView txtLink;
    private MaterialTextView txtFirstHeading;
    private MaterialTextView txtLinkHeading;
    private MaterialTextView txtServiceVisit;
    private LinearLayout llCreateService;
    private Context context;
    private String userName;
    private String userId;
    private String isMentor;
    private boolean isSubscribed;
    private String selectedScreen = "";
    private TradWyseSession tradWyseSession;
    MentorServiceCount mentorServiceCount;

    public ServiceFragment(MentorServiceCount mentorServiceCount) {
        this.mentorServiceCount = mentorServiceCount;
    }

    public ServiceFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (((ProfileTabbedActivity) getActivity()).fromLoggedInProfile) {
            (view.findViewById(R.id.frag_service_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
            rvService.setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
        } else {
            (view.findViewById(R.id.frag_service_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
            rvService.setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
        }
    }

    public void initView(View view) {
        rvService = view.findViewById(R.id.rv_service);
        progressBar = view.findViewById(R.id.progress);
        txtUserMsg = view.findViewById(R.id.txt_user_msg);
        txtUserName = view.findViewById(R.id.txt_username);
        txtLink = view.findViewById(R.id.txt_link);
        txtFirstHeading = view.findViewById(R.id.txt_first_heading);
        txtLinkHeading = view.findViewById(R.id.txt_link_heading);
        txtServiceVisit = view.findViewById(R.id.txt_service_visit);
        llCreateService = view.findViewById(R.id.ll_create_service);
        txtLink.setOnClickListener(this);


        tradWyseSession = TradWyseSession.getSharedInstance(context);

        rvService.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        if (getArguments() != null) {
            userName = getArguments().getString("userName");
            userId = getArguments().getString("userId");
            isMentor = getArguments().getString("isMentor");
            isSubscribed = getArguments().getBoolean("isSubscribed");
            selectedScreen = getArguments().getString("selectedScreen");

            if (userName != null) {
                getAllTypeServicesForUser();
            }
        }
    }


    public void getAllTypeServicesForUser() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        Call<UserServiceResponse> call = apiInterface.getAllTypeServicesForUser(userId);
        call.enqueue(new Callback<UserServiceResponse>() {
            @Override
            public void onResponse(@NotNull Call<UserServiceResponse> call, @NotNull Response<UserServiceResponse> response) {
                progressBar.setVisibility(View.GONE);
                int subscribedListCount = 0;
                int suggestedListCount = 0;
                int mentorListCount = 0;

                if (response.body() != null) {
                    UserServiceResponse userServiceResponse = response.body();
                    List<ServiceSubscriptionPlan> userServiceList = new ArrayList<>();
                    if (userServiceResponse.getServiceSubscriptionPlans() != null && userServiceResponse.getServiceSubscriptionPlans().size() > 0) {
                        List<ServiceSubscriptionPlan> list = getServiceSubscriptionPlanList(userServiceResponse.getServiceSubscriptionPlans(), userName + "'s Mentor Services", 0);
                        userServiceList.addAll(list);
                        mentorListCount = list.size();

                    }
                    if (userServiceResponse.getServiceSubscriptions() != null && userServiceResponse.getServiceSubscriptions().size() > 0) {
                        List<ServiceSubscriptionPlan> list = getServiceSubscriptionPlanList(userServiceResponse.getServiceSubscriptions());
                        if (list != null && list.size() > 0) {
                            if (userId.equalsIgnoreCase(tradWyseSession.getUserId())) {
                                list = getServiceSubscriptionPlanList(list, "My Services", mentorListCount);
                            } else {
                                list = getServiceSubscriptionPlanList(list, userName + "'s Services", mentorListCount);
                            }

                            userServiceList.addAll(list);
                            subscribedListCount = list.size();
                        }
                    }
                    if (userServiceResponse.getSuggestedServiceSubscriptionPlans() != null && userServiceResponse.getSuggestedServiceSubscriptionPlans().size() > 0) {
                        List<ServiceSubscriptionPlan> list = getServiceSubscriptionPlanList(userServiceResponse.getSuggestedServiceSubscriptionPlans(), "TradeTips Services", mentorListCount + subscribedListCount);
                        userServiceList.addAll(list);
                        suggestedListCount = userServiceResponse.getSuggestedServiceSubscriptionPlans().size();
                    }
                    mentorServiceCount.mentorServiceCountRecieved(mentorListCount);
                    if (userServiceList.size() > 0) {
                        updateUi(userServiceList);
                        rvService.setVisibility(View.VISIBLE);
                        txtUserMsg.setVisibility(View.GONE);
                    } else {
                        rvService.setVisibility(View.GONE);
                    }
                } else {
                    rvService.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserServiceResponse> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d("error:", "" + t.toString());
                Log.d(Constants.ON_FAILURE_TAG, "ServiceFragment getAllServiceSubscriptionPlanForMentor: onFailure");
                Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                        context.getResources().getString(R.string.JustLetYouKnow));
                rvService.setVisibility(View.GONE);
            }
        });
    }

    public List<ServiceSubscriptionPlan> getServiceSubscriptionPlanList(List<ServiceSubscriptionPlan> serviceSubscriptionList, String headerName, int section) {
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
        ServiceAdapter adapter = new ServiceAdapter(getActivity(), context, userServiceList, userName, isMentor, this, ((ProfileTabbedActivity) getActivity()).fromLoggedInProfile, false, selectedScreen, false);
        rvService.setAdapter(adapter);
        StickyHeaderItemDecorator decorator = new StickyHeaderItemDecorator(adapter);
        decorator.attachToRecyclerView(rvService);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txt_link) {
            startActivity(new Intent(context, WebViewActivity.class)
                    .putExtra("title", "Mentors Login")
                    .putExtra("url", "http://www.tradetips.com/mentorslogin"));
        }
    }


    @Override
    public void addSuggestedService(String serviceSubscriptionPlanId) {
        subscribeSuggestedServiceSubscriptionPlan(serviceSubscriptionPlanId);
    }

    public void subscribeSuggestedServiceSubscriptionPlan(String serviceSubscriptionPlanId) {
        showProgressDialog();
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        Call<AddSuggestedServiceSubscriptionPlan> call = apiInterface.subscribeSeggestedServiceSubscriptionPlan(serviceSubscriptionPlanId);
        call.enqueue(new Callback<AddSuggestedServiceSubscriptionPlan>() {
            @Override
            public void onResponse(@NotNull Call<AddSuggestedServiceSubscriptionPlan> call, @NotNull Response<AddSuggestedServiceSubscriptionPlan> response) {
                dismissProgressDialog();

                if (response.body() != null) {
                    getAllTypeServicesForUser();
                }
            }

            @Override
            public void onFailure(@NotNull Call<AddSuggestedServiceSubscriptionPlan> call, @NotNull Throwable t) {
                dismissProgressDialog();
                Log.d("error:", "" + t.toString());
                Log.d(Constants.ON_FAILURE_TAG, "ServiceFragment getAllServiceSubscriptionPlanForMentor: onFailure");
                Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                        context.getResources().getString(R.string.JustLetYouKnow));

            }
        });
    }
}
