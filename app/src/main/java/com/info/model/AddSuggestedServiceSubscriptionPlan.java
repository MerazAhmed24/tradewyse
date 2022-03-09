
package com.info.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddSuggestedServiceSubscriptionPlan implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("serviceSubscriptionPlanId")
    @Expose
    private String serviceSubscriptionPlanId;
    @SerializedName("stripeSubscriptionId")
    @Expose
    private Object stripeSubscriptionId;
    @SerializedName("stripeSubscriptionStatus")
    @Expose
    private String stripeSubscriptionStatus;
    @SerializedName("remark")
    @Expose
    private String remark;
    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;
    @SerializedName("modifiedOn")
    @Expose
    private Long modifiedOn;
    @SerializedName("serviceSubscriptionPlan")
    @Expose
    private Object serviceSubscriptionPlan;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServiceSubscriptionPlanId() {
        return serviceSubscriptionPlanId;
    }

    public void setServiceSubscriptionPlanId(String serviceSubscriptionPlanId) {
        this.serviceSubscriptionPlanId = serviceSubscriptionPlanId;
    }

    public Object getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(Object stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public String getStripeSubscriptionStatus() {
        return stripeSubscriptionStatus;
    }

    public void setStripeSubscriptionStatus(String stripeSubscriptionStatus) {
        this.stripeSubscriptionStatus = stripeSubscriptionStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Object getServiceSubscriptionPlan() {
        return serviceSubscriptionPlan;
    }

    public void setServiceSubscriptionPlan(Object serviceSubscriptionPlan) {
        this.serviceSubscriptionPlan = serviceSubscriptionPlan;
    }

}
