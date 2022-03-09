package com.info.commons;

import com.info.interfaces.OnNotificationReceived;

public class AppSingletonClass {
    private OnNotificationReceived onNotificationReceived;

    private static AppSingletonClass ourInstance;

    public static AppSingletonClass getInstance() {
        if(ourInstance == null)
            ourInstance = new AppSingletonClass();
        return ourInstance;
    }

    private AppSingletonClass() {
    }

    public OnNotificationReceived getOnNotificationReceived() {
        return onNotificationReceived;
    }

    public void setOnNotificationReceived(OnNotificationReceived onNotificationReceived) {
        this.onNotificationReceived = onNotificationReceived;
    }
}
