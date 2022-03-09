package com.info.model;

import java.io.Serializable;
import java.util.List;

public class DeviceLimitExceedModel implements Serializable {

    private String title;
    private String message;
    private List<DeviceDetail>deviceDetails;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DeviceDetail> getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(List<DeviceDetail> deviceDetails) {
        this.deviceDetails = deviceDetails;
    }
}
