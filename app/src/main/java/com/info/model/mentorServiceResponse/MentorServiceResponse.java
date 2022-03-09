package com.info.model.mentorServiceResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MentorServiceResponse {

    @SerializedName("serviceSubscriptionPlans")
    @Expose
    private List<ServiceSubscriptionPlan> serviceSubscriptionPlans = null;
    @SerializedName("suggestedServiceSubscriptionPlans")
    @Expose
    private List<ServiceSubscriptionPlan> suggestedServiceSubscriptionPlans = null;
    @SerializedName("serviceSubscriptions")
    @Expose
    private List<ServiceSubscriptions> serviceSubscriptions;

    public List<ServiceSubscriptionPlan> getServiceSubscriptionPlans() {
        return serviceSubscriptionPlans;
    }

    public void setServiceSubscriptionPlans(List<ServiceSubscriptionPlan> serviceSubscriptionPlans) {
        this.serviceSubscriptionPlans = serviceSubscriptionPlans;
    }

    public List<ServiceSubscriptionPlan> getSuggestedServiceSubscriptionPlans() {
        return suggestedServiceSubscriptionPlans;
    }

    public void setSuggestedServiceSubscriptionPlans(List<ServiceSubscriptionPlan> suggestedServiceSubscriptionPlans) {
        this.suggestedServiceSubscriptionPlans = suggestedServiceSubscriptionPlans;
    }

    public List<ServiceSubscriptions> getServiceSubscriptions() {
        return serviceSubscriptions;
    }

    public void setServiceSubscriptions(List<ServiceSubscriptions> serviceSubscriptions) {
        this.serviceSubscriptions = serviceSubscriptions;
    }

}
