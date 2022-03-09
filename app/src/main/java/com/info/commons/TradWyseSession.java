//===============================================================================
// © 2019 TradWyse Apps.  ALL rights reserved.
// Main Author: Ankur Sharma
// Original DATE: 05/05/2019
//===============================================================================
package com.info.commons;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.info.model.serviceResponse.ServiceResponse;
import com.info.model.userServiceResponse.UserServiceResponse;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * It initialize all session variable.
 */
public class TradWyseSession {
    public static final String PLATFORM_APPLICATION_ID = "6C7CAF42-6A0D-416B-9BE9-3B91F648562A";
    public static final String EMPLOYEE_APPLICATION_ID = "A8920FC7-4874-4854-BCE1-4C4909C6C824";
    private static final String authenticationtype = "AUTHENTICATIONTYPE";
    private static TradWyseSession sharedInstance;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static Context mContext;
    private static String loginAccessToken;
    private static String accessTokenStreamIO;
    private static String fcmToken;
    private static String systemTime;
    private boolean isConnectedToSendBird = false;
    private static String emailId;
    private static String isSuscribedMember;
    Date currentTime = Calendar.getInstance().getTime();
    //Test commit
    public static TradWyseSession getSharedInstance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new TradWyseSession();
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            editor = preferences.edit();
            mContext = context;
        }
        return sharedInstance;
    }


    public void setLoginSession(String email, String password, boolean isLoggedIn) {
        editor.putString("LoggedInUserEmail", String.valueOf(email));
        editor.putString("password", String.valueOf(password));
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.commit();
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.commit();
    }

    public void setIsGoIntroScreen(boolean isGoIntroScreen) {
        editor.putBoolean("isGoIntroScreen", isGoIntroScreen);
        editor.commit();
    }

    public boolean isGoIntroScreen() {
        return preferences.getBoolean("isGoIntroScreen", true);
    }

    /**
     * It will call when user click on Logout button.
     * It will clear the session variables.
     */
    public void clearSession() {
        SharedPreferences.Editor editor = preferences.edit();
        // clear editor
        editor.clear();

    }

    /**
     * it is used to set User Login.
     */
    public void setUserLogin(boolean isUserLogin) {
        editor.putBoolean("isLoggedIn", isUserLogin);
        editor.commit();
    }

    public static String getLoggedInEmail() {
        return preferences.getString("LoggedInUserEmail", "");
    }

    public static String getPassword()
    {
        return preferences.getString("password", "");
    }
    public void setPassword(String userpassword) {
        editor.putString("password", userpassword);
        editor.commit();
    }

    public static boolean isLoggedIn() {
        return preferences.getBoolean("isLoggedIn", false);
    }

    public String getLoginAccessToken() {
        loginAccessToken = preferences.getString("loginAccessToken", "");
        return loginAccessToken;
    }

    public static String getAccessTokenStreamIO() {
        accessTokenStreamIO = preferences.getString("accessTokenStreamIO", "");
        return accessTokenStreamIO;
    }

    public static void setAccessTokenStreamIO(String accessTokenStreamIO) {
        TradWyseSession.accessTokenStreamIO = accessTokenStreamIO;
        editor.putString("accessTokenStreamIO", TradWyseSession.accessTokenStreamIO);
        editor.commit();
    }

    public void setLoginAccessToken(String loginAccessToken) {
        TradWyseSession.loginAccessToken = loginAccessToken;
        editor.putString("loginAccessToken", TradWyseSession.loginAccessToken);
        editor.commit();
    }

    public static String getFcmToken() {
        fcmToken = preferences.getString("fcmToken", "");
        return fcmToken;
    }

    public static void setFcmToken(String fcmToken) {
        TradWyseSession.fcmToken = fcmToken;
        editor.putString("fcmToken", TradWyseSession.fcmToken);
        editor.commit();

    }
    public String getEmailId() {
        String emailId = preferences.getString("emailId", "");
        return emailId;
    }

    public static void setEmailId(String emailId) {
        TradWyseSession.emailId = emailId;
        editor.putString("emailId", emailId);
        editor.commit();

    }
    public static String getSystemTime() {
        String systemTime = preferences.getString("systemTime", "");
        return systemTime;
    }
    public static void setSystemTime(String systemTime) {
        TradWyseSession.systemTime = systemTime;
        editor.putString("systemTime", String.valueOf(systemTime));
        editor.commit();
    }


    public static String getLoginInfoId() {
        String loginInfoId = preferences.getString("loginInfoId", "");
        return loginInfoId;
    }

    public static void setLoginInfoId(String loginInfoId) {
        editor.putString("loginInfoId", loginInfoId);
        editor.commit();

    }


    public void setUserIdString(String userId) {
        TradWyseSession.loginAccessToken = loginAccessToken;
        editor.putString("userId", userId);
        editor.commit();
    }

    public String getUserId() {
        String userId = preferences.getString("userId", "");
        return userId;
    }

    public void setUserName(String userId) {
        TradWyseSession.loginAccessToken = loginAccessToken;
        editor.putString("userName", userId);
        editor.commit();
    }

    public void setFirstName(String firstName) {
        TradWyseSession.loginAccessToken = loginAccessToken;
        editor.putString("firstName", firstName);
        editor.commit();
    }

    public void setLastName(String lastName) {
        TradWyseSession.loginAccessToken = loginAccessToken;
        editor.putString("lastName", lastName);
        editor.commit();
    }

    public void setUserImage(String userImage) {
        TradWyseSession.loginAccessToken = loginAccessToken;
        editor.putString("userImage", userImage);
        editor.commit();
    }

    public String getUserImage() {
        String userId = preferences.getString("userImage", "");
        return userId;
    }

    public String getUserName() {
        String userId = preferences.getString("userName", "");
        return userId;
    }

    public void setUserFollowingCount(int count) {
        editor.putInt("followingCount", count);
        editor.commit();
    }

    public void setStockNameFromNotification(String stockName) {
        editor.putString("NotificationStockName", stockName);
        editor.commit();
    }

    public String getNotificationStockName() {
        String stockName = preferences.getString("NotificationStockName", "");
        return stockName;
    }

    public int getUserFollowingCount() {
        return preferences.getInt("followingCount", 0);
    }

    public void setUserFollowersCount(int count) {
        editor.putInt("followersCount", count);
        editor.commit();
    }

    public int getUserFollowersCount() {
        return preferences.getInt("followersCount", 0);
    }


    public String getFirstName() {
        String firstName = preferences.getString("firstName", "");
        return firstName;
    }

    public String getLastName() {
        String lastName = preferences.getString("lastName", "");
        return lastName;
    }

    public void setIsMentor(String isMentor) {
        TradWyseSession.loginAccessToken = loginAccessToken;
        editor.putString("isMentor", isMentor);
        editor.commit();
    }

    public void setOpenMentorDialogCount(int count) {
        TradWyseSession.loginAccessToken = loginAccessToken;
        editor.putInt("count", count);
        editor.commit();
    }


    public int getReviewCount() {
        return preferences.getInt("visitCount", 0);
    }
    public void setReviewCount(int visitCount) {
        editor.putInt("visitCount", visitCount);
        editor.commit();
    }


    public int getOpenMentorDialogCount() {
        return preferences.getInt("count", 0);
    }


    public String getIsMentor() {
        String isMentor = preferences.getString("isMentor", "");
        return isMentor;
    }

    public void setInternalSubscribedMember(boolean isSubscribed) {
        editor.putBoolean("isInternalSubscribedMember", isSubscribed);
        editor.commit();
    }

    public boolean isInternalSubscribedMember() {
        return preferences.getBoolean("isInternalSubscribedMember", false);
    }

    /**
     * it is used to set User Login.
     */
    public void setSubscribedMember(boolean isSubscribed) {
        editor.putBoolean("isSubscribedMember", isSubscribed);
        editor.commit();
    }

    public boolean isSubscribedMember() {
        boolean isSubscribed = preferences.getBoolean("isSubscribedMember", false);
        if (isSubscribed)
            return true;
        isSubscribed = isInternalSubscribedMember();
        if (isSubscribed)
            return true;

        return false;
    }

    /**
     * it is used to set social chat room confirmation dialog before post any msg.
     */
    public void setSocialChatRoomNeedToShowDialog(boolean isNeeded) {
        editor.putBoolean("isNeedToShowDialog", isNeeded);
        editor.commit();
    }

    public boolean isSocialChatRoomNeedToShowDialog() {
        boolean isNeeded = preferences.getBoolean("isNeedToShowDialog", false);
        if (isNeeded)
            return true;

        return false;
    }

    /**
     * it is used to set MACD Service purchased user.
     */
    public void setMACDServicePurchased(boolean isPurchased) {
        editor.putBoolean("isMACDServicePurchased", isPurchased);
        editor.commit();
    }

    public boolean isMACDServicePurchased() {
        boolean isPurchased = preferences.getBoolean("isMACDServicePurchased", false);
        if (isPurchased)
            return true;

        return false;
    }

    public boolean isConnectedToSendBird() {
        return isConnectedToSendBird;
    }

    public void setConnectedToSendBird(boolean connectedToSendBird) {
        isConnectedToSendBird = connectedToSendBird;
    }


    public void setServiceList(String serviceList) {
        editor.putString("serviceList", serviceList);
        editor.commit();
    }
    public void setRateNow(boolean isSubmitted) {
        editor.putBoolean("isSubmitted", isSubmitted);
        editor.commit();
    }
    public boolean getRateNow()
    {
        return preferences.getBoolean("isSubmitted", false);

    }
    public void setBannerVisible(boolean isVisible) {
        editor.putBoolean("isVisible", isVisible);
        editor.commit();
    }
    public boolean getBannerVisibility()
    {
        return preferences.getBoolean("isVisible", false);

    }

    public List<ServiceResponse> getServiceList() {
        String json = preferences.getString("serviceList", null);
        Type type = new TypeToken<List<ServiceResponse>>() {
        }.getType();
        List<ServiceResponse> arrayList = new Gson().fromJson(json, type);
        return arrayList;
    }


    public void setUserServiceList(String serviceList) {
        editor.putString("userServiceList", serviceList);
        editor.commit();
    }

    public List<UserServiceResponse> getUserServiceList() {
        String json = preferences.getString("userServiceList", null);
        Type type = new TypeToken<List<UserServiceResponse>>() {
        }.getType();
        List<UserServiceResponse> arrayList = new Gson().fromJson(json, type);
        return arrayList;
    }


}