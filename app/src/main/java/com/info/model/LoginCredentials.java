package com.info.model;

/**
 * Created by ankur on 6/1/2019.
 */

public class LoginCredentials {

    private String userName;
    private String password;
    private String deviceLoginId;
    private String deviceInfo;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceLoginId() {
        return deviceLoginId;
    }

    public void setDeviceLoginId(String deviceLoginId) {
        this.deviceLoginId = deviceLoginId;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
