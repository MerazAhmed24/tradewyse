
package com.info.model.serviceResponse;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceResponse implements Serializable
{
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("descriptionOne")
    @Expose
    private String descriptionOne;
    @SerializedName("descriptionTwo")
    @Expose
    private String descriptionTwo;
    @SerializedName("frequency")
    @Expose
    private String frequency;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("imageDetails")
    @Expose
    private String imageDetails;
    @SerializedName("mentorId")
    @Expose
    private String mentorId;
    @SerializedName("serviceTypeId")
    @Expose
    private String serviceTypeId;
    @SerializedName("serviceSubscriptionStatus")
    @Expose
    private Object serviceSubscriptionStatus;
    @SerializedName("stripeProductId")
    @Expose
    private String stripeProductId;
    @SerializedName("stripeProductName")
    @Expose
    private String stripeProductName;
    @SerializedName("stripePriceId")
    @Expose
    private String stripePriceId;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;
    @SerializedName("modifiedOn")
    @Expose
    private Long modifiedOn;
    @SerializedName("serviceTypeMaster")
    @Expose
    private ServiceTypeMaster serviceTypeMaster;

    @SerializedName("subscribed")
    @Expose
    private Boolean subscribed;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescriptionOne() {
        return descriptionOne;
    }

    public void setDescriptionOne(String descriptionOne) {
        this.descriptionOne = descriptionOne;
    }

    public String getDescriptionTwo() {
        return descriptionTwo;
    }

    public void setDescriptionTwo(String descriptionTwo) {
        this.descriptionTwo = descriptionTwo;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImageDetails() {
        return imageDetails;
    }

    public void setImageDetails(String imageDetails) {
        this.imageDetails = imageDetails;
    }

    public String getMentorId() {
        return mentorId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public Object getServiceSubscriptionStatus() {
        return serviceSubscriptionStatus;
    }

    public void setServiceSubscriptionStatus(Object serviceSubscriptionStatus) {
        this.serviceSubscriptionStatus = serviceSubscriptionStatus;
    }

    public String getStripeProductId() {
        return stripeProductId;
    }

    public void setStripeProductId(String stripeProductId) {
        this.stripeProductId = stripeProductId;
    }

    public String getStripeProductName() {
        return stripeProductName;
    }

    public void setStripeProductName(String stripeProductName) {
        this.stripeProductName = stripeProductName;
    }

    public String getStripePriceId() {
        return stripePriceId;
    }

    public void setStripePriceId(String stripePriceId) {
        this.stripePriceId = stripePriceId;
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

    public ServiceTypeMaster getServiceTypeMaster() {
        return serviceTypeMaster;
    }

    public void setServiceTypeMaster(ServiceTypeMaster serviceTypeMaster) {
        this.serviceTypeMaster = serviceTypeMaster;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }
}
