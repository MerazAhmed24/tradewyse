
package com.info.model.userServiceResponse;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserServiceResponse {

    // User created services.
    @SerializedName("serviceSubscriptionPlans")
    @Expose
    private List<ServiceSubscriptionPlan> serviceSubscriptionPlans = null;

    // Recommended
    @SerializedName("suggestedServiceSubscriptionPlans")
    @Expose
    private List<ServiceSubscriptionPlan> suggestedServiceSubscriptionPlans = null;

    // User purchased services
    @SerializedName("serviceSubscriptions")
    @Expose
    private List<ServiceSubscription> serviceSubscriptions = null;

    // All others
    @SerializedName("otherServiceSubscriptionPlans")
    @Expose
    private List<ServiceSubscriptionPlan> otherServiceSubscriptionPlans  = null;

    public List<ServiceSubscriptionPlan> getSuggestedServiceSubscriptionPlans() {
        return suggestedServiceSubscriptionPlans;
    }

    public void setSuggestedServiceSubscriptionPlans(List<ServiceSubscriptionPlan> suggestedServiceSubscriptionPlans) {
        this.suggestedServiceSubscriptionPlans = suggestedServiceSubscriptionPlans;
    }

    public List<ServiceSubscription> getServiceSubscriptions() {
        return serviceSubscriptions;
    }

    public void setServiceSubscriptions(List<ServiceSubscription> serviceSubscriptions) {
        this.serviceSubscriptions = serviceSubscriptions;
    }

    public List<ServiceSubscriptionPlan> getServiceSubscriptionPlans() {
        return serviceSubscriptionPlans;
    }

    public void setServiceSubscriptionPlans(List<ServiceSubscriptionPlan> serviceSubscriptionPlans) {
        this.serviceSubscriptionPlans = serviceSubscriptionPlans;
    }

    public List<ServiceSubscriptionPlan> getOtherServiceSubscriptionPlans() {
        return otherServiceSubscriptionPlans;
    }

    public void setOtherServiceSubscriptionPlans(List<ServiceSubscriptionPlan> otherServiceSubscriptionPlans) {
        this.otherServiceSubscriptionPlans = otherServiceSubscriptionPlans;
    }
}
