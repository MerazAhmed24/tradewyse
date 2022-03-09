
package com.info.model.userServiceResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceTypeMaster_ implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("serviceType")
    @Expose
    private String serviceType;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;
    @SerializedName("modifiedOn")
    @Expose
    private Long modifiedOn;

    protected ServiceTypeMaster_(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        serviceType = in.readString();
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(serviceType);
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
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ServiceTypeMaster_> CREATOR = new Creator<ServiceTypeMaster_>() {
        @Override
        public ServiceTypeMaster_ createFromParcel(Parcel in) {
            return new ServiceTypeMaster_(in);
        }

        @Override
        public ServiceTypeMaster_[] newArray(int size) {
            return new ServiceTypeMaster_[size];
        }
    };

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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

}
