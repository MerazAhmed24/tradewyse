package com.info.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Amit Gupta on 10,June,2021
 */
public class FirestoreAuthentication {
    @SerializedName("fcmToken")
    private String authToken;

    @SerializedName("userId")
    private String userId;

    public String getAuthToken() {
        return authToken;
    }

    public String getUserId() {
        return userId;
    }
}
