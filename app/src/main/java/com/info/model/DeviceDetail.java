
package com.info.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DeviceDetail implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("deviceInfo")
    @Expose
    private String deviceInfo;
    @SerializedName("createdOn")
    @Expose
    private long createdOn;
    @SerializedName("modifiedOn")
    @Expose
    private long modifiedOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Integer createdOn) {
        this.createdOn = createdOn;
    }

    public long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Integer modifiedOn) {
        this.modifiedOn = modifiedOn;
    }


}
