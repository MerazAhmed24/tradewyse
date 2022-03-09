package com.info.eventBus;

/**
 * Created by Amit Gupta on 22,June,2021
 */
public class MessageEvent {
    private String AdBannerId;
    private int notificationId;

    public MessageEvent(String adBannerId, int notificationId) {
        AdBannerId = adBannerId;
        this.notificationId = notificationId;
    }

    public String getAdBannerId() {
        return AdBannerId;
    }

    public void setAdBannerId(String adBannerId) {
        AdBannerId = adBannerId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
