
package com.info.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ValidateCouponModel implements Serializable
{

    @SerializedName("amount_off")
    @Expose
    private Object amountOff;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("currency")
    @Expose
    private Object currency;
    @SerializedName("deleted")
    @Expose
    private Object deleted;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("duration_in_months")
    @Expose
    private Object durationInMonths;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("livemode")
    @Expose
    private Boolean livemode;
    @SerializedName("max_redemptions")
    @Expose
    private Object maxRedemptions;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("object")
    @Expose
    private String object;
    @SerializedName("percent_off")
    @Expose
    public Double percentOff;
    @SerializedName("redeem_by")
    @Expose
    private Object redeemBy;
    @SerializedName("times_redeemed")
    @Expose
    private Integer timesRedeemed;
    @SerializedName("valid")
    @Expose
    private Boolean valid;

    public Object getAmountOff() {
        return amountOff;
    }

    public void setAmountOff(Object amountOff) {
        this.amountOff = amountOff;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Object getCurrency() {
        return currency;
    }

    public void setCurrency(Object currency) {
        this.currency = currency;
    }

    public Object getDeleted() {
        return deleted;
    }

    public void setDeleted(Object deleted) {
        this.deleted = deleted;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Object getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Object durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getLivemode() {
        return livemode;
    }

    public void setLivemode(Boolean livemode) {
        this.livemode = livemode;
    }

    public Object getMaxRedemptions() {
        return maxRedemptions;
    }

    public void setMaxRedemptions(Object maxRedemptions) {
        this.maxRedemptions = maxRedemptions;
    }



    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Double getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(Double percentOff) {
        this.percentOff = percentOff;
    }

    public Object getRedeemBy() {
        return redeemBy;
    }

    public void setRedeemBy(Object redeemBy) {
        this.redeemBy = redeemBy;
    }

    public Integer getTimesRedeemed() {
        return timesRedeemed;
    }

    public void setTimesRedeemed(Integer timesRedeemed) {
        this.timesRedeemed = timesRedeemed;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

}
