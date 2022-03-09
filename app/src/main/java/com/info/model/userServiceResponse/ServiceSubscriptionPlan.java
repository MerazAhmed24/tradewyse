
package com.info.model.userServiceResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceSubscriptionPlan implements Parcelable {

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
    private Double price;
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
    @SerializedName("stripePriceId")
    @Expose
    private String stripePriceId;
    @SerializedName("stripeProductName")
    @Expose
    private String stripeProductName;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;
    @SerializedName("modifiedOn")
    @Expose
    private Long modifiedOn;
    @SerializedName("serviceTypeMaster")
    @Expose
    private ServiceTypeMaster_ serviceTypeMaster;
    @SerializedName("mentorName")
    @Expose
    private String mentorName;
    @SerializedName("subscribed")
    @Expose
    private Boolean subscribed;
    @SerializedName("suggested")
    @Expose
    private Boolean suggested;
    @SerializedName("trial")
    @Expose
    private Boolean trial;
    @SerializedName("thumbnailImageUrl")
    @Expose
    private String thumbnailImageUrl;

    private String serviceType;
    private String serviceHeaderTitle;

    private int section;

    public ServiceSubscriptionPlan() {
    }

    protected ServiceSubscriptionPlan(Parcel in) {
        id = in.readString();
        title = in.readString();
        descriptionOne = in.readString();
        descriptionTwo = in.readString();
        frequency = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        imageDetails = in.readString();
        mentorId = in.readString();
        serviceTypeId = in.readString();
        stripeProductId = in.readString();
        stripePriceId = in.readString();
        stripeProductName = in.readString();
        if (in.readByte() == 0) {
            createdOn = null;
        } else {
            createdOn = in.readLong();
        }
        if (in.readByte() == 0) {
            modifiedOn = null;
        } else {
            modifiedOn = in.readLong();
        }
        serviceTypeMaster = in.readParcelable(ServiceTypeMaster_.class.getClassLoader());
        mentorName = in.readString();
        byte tmpSubscribed = in.readByte();
        subscribed = tmpSubscribed == 0 ? null : tmpSubscribed == 1;
        byte tmpSuggested = in.readByte();
        suggested = tmpSuggested == 0 ? null : tmpSuggested == 1;
        byte tmpTrial = in.readByte();
        trial = tmpTrial == 0 ? null : tmpTrial == 1;
        thumbnailImageUrl = in.readString();
        serviceType = in.readString();
        serviceHeaderTitle = in.readString();
        section = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(descriptionOne);
        dest.writeString(descriptionTwo);
        dest.writeString(frequency);
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }
        dest.writeString(imageDetails);
        dest.writeString(mentorId);
        dest.writeString(serviceTypeId);
        dest.writeString(stripeProductId);
        dest.writeString(stripePriceId);
        dest.writeString(stripeProductName);
        if (createdOn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(createdOn);
        }
        if (modifiedOn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(modifiedOn);
        }
        dest.writeParcelable(serviceTypeMaster, flags);
        dest.writeString(mentorName);
        dest.writeByte((byte) (subscribed == null ? 0 : subscribed ? 1 : 2));
        dest.writeByte((byte) (suggested == null ? 0 : suggested ? 1 : 2));
        dest.writeByte((byte) (trial == null ? 0 : trial ? 1 : 2));
        dest.writeString(thumbnailImageUrl);
        dest.writeString(serviceType);
        dest.writeString(serviceHeaderTitle);
        dest.writeInt(section);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ServiceSubscriptionPlan> CREATOR = new Creator<ServiceSubscriptionPlan>() {
        @Override
        public ServiceSubscriptionPlan createFromParcel(Parcel in) {
            return new ServiceSubscriptionPlan(in);
        }

        @Override
        public ServiceSubscriptionPlan[] newArray(int size) {
            return new ServiceSubscriptionPlan[size];
        }
    };

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceHeaderTitle() {
        return serviceHeaderTitle;
    }

    public void setServiceHeaderTitle(String serviceHeaderTitle) {
        this.serviceHeaderTitle = serviceHeaderTitle;
    }

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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

    public String getStripePriceId() {
        return stripePriceId;
    }

    public void setStripePriceId(String stripePriceId) {
        this.stripePriceId = stripePriceId;
    }

    public String getStripeProductName() {
        return stripeProductName;
    }

    public void setStripeProductName(String stripeProductName) {
        this.stripeProductName = stripeProductName;
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

    public ServiceTypeMaster_ getServiceTypeMaster() {
        return serviceTypeMaster;
    }

    public void setServiceTypeMaster(ServiceTypeMaster_ serviceTypeMaster) {
        this.serviceTypeMaster = serviceTypeMaster;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Boolean getTrial() {
        return trial;
    }

    public void setTrial(Boolean trial) {
        this.trial = trial;
    }

    public Boolean getSuggested() {
        return suggested;
    }

    public void setSuggested(Boolean suggested) {
        this.suggested = suggested;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }
}
