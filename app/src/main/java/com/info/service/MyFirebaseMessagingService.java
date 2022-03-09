package com.info.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.info.commons.AppSingletonClass;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.interfaces.OnNotificationReceived;
import com.info.commons.TradWyseSession;
import com.info.tradewyse.ChatReplyActivity;
import com.info.tradewyse.DashBoardActivity;
import com.info.tradewyse.GraphStockPrediction;
import com.info.tradewyse.R;
import com.info.tradewyse.TipDetailActivity;
import com.info.tradewyse.TradwyseApplication;

import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANGED_STOCKS_LIST_KEY = "ChangedStockList";
    public static final String TAG = "FCM";
    public static final String CHANNEL_ID_AD_BANNER = "com.tradetips.Banner_Notifications";
    public static final String CHANNEL_ID_STOCK_PREDICTION = "com.tradetips.Stock_Prediction";
    public static final String CHANNEL_ID_GENERAL_NOTIFICATION = "com.tradetips.General_Notification";
    public static final String CHANNEL_ID_SOCIAL_CHAT= "com.tradetips.Social_Chat";
    String GROUP_KEY_BANNER_NOTIFICATION = "tradetips.Banner_Notifications_group";
    String GROUP_KEY_STOCK_PREDICTION_NOTIFICATION = "tradetips.stock_predection_Group";
    String GROUP_KEY_TIP_COMMENT_NOTIFICATION = "tradetips.comment_notification_group";
    String GROUP_KEY_SOCIAL_CHAT_NOTIFICATION = "tradetips.social_chat_notification_group";

    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        
        String predectionStockName = "";
        String stockName = "";
        int notificationId = (int) System.currentTimeMillis();
        Log.d(TAG, "Notification From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().containsKey(CHANGED_STOCKS_LIST_KEY)) {
            List<String> list = new Gson().fromJson(remoteMessage.getData().get(CHANGED_STOCKS_LIST_KEY), new TypeToken<List<String>>() {
            }.getType());
            new NotificationProvider().createChangedStocksNotification(this, list, remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));

        } else if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String notificationType = remoteMessage.getData().get("notificationType");
            if (notificationType.equalsIgnoreCase("AdBanner")) {
                String AddBannerId = remoteMessage.getData().get("AddBannerId");
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                createAndDisplayAdBannerNotification(title, body, AddBannerId, remoteMessage.getData().get("click_action"), CHANNEL_ID_AD_BANNER);
                /*String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                createAndDisplayAdBannerNotification(title, body, AddBannerId, remoteMessage.getNotification().getClickAction(), CHANNEL_ID_AD_BANNER);*/
            } else if (notificationType.equalsIgnoreCase("Comment")) {
                String tipId = remoteMessage.getData().get("tipId");
                String notificationsId = remoteMessage.getData().get("notificationId");
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                createAndDisplayTipCommentNotification(title, body, tipId, remoteMessage.getData().get("click_action"), notificationsId, CHANNEL_ID_GENERAL_NOTIFICATION);
                /*String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                createAndDisplayTipNotification(title, body, tipId, remoteMessage.getNotification().getClickAction(), notificationsId, CHANNEL_ID_GENERAL_NOTIFICATION);*/
            } else if (notificationType.equalsIgnoreCase("Tip")) {
                String tipId = remoteMessage.getData().get("tipId");
                String notificationsId = remoteMessage.getData().get("notificationId");
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                createAndDisplayTipNotification(title, body, tipId, remoteMessage.getData().get("click_action"), notificationsId, CHANNEL_ID_GENERAL_NOTIFICATION);
            } else if (notificationType.equalsIgnoreCase("SocialChat")) {
                String notificationsId = remoteMessage.getData().get("notificationId");
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                String messageId = remoteMessage.getData().get("messageId");
                String groupId = remoteMessage.getData().get("groupId");
                String groupName = remoteMessage.getData().get("groupName");
                String postedByUserName = remoteMessage.getData().get("postedByUserName");
                boolean isBasilPrivateRoom;
                if (groupId.equals("Test") || groupId.equals("Production")) {
                    isBasilPrivateRoom = true;
                } else {
                    isBasilPrivateRoom = false;
                }
                String notificationReferenceId = remoteMessage.getData().get("notificationReferenceId");
                createAndDisplaySocialChatNotification(title, body, remoteMessage.getData().get("click_action"), notificationsId, messageId, groupId,groupName, postedByUserName, notificationReferenceId, isBasilPrivateRoom, CHANNEL_ID_SOCIAL_CHAT);
            } else {
                try {
                    predectionStockName = remoteMessage.getData().get("prediction");
                    Log.d(TAG, "Stock Name" + predectionStockName);
                    JSONObject jsonObject = new JSONObject(predectionStockName);
                    stockName = jsonObject.getString("stockName");
                    String id = jsonObject.getString("id");
                    String normalizedPrice = jsonObject.getString("normalizedPrice");
                    String newsAffected = jsonObject.getString("newsAffected");
                    String dates = jsonObject.getString("dates");
                    String vxxAffected = jsonObject.getString("VXXAffected");
                    TradWyseSession.getSharedInstance((TradwyseApplication) getApplication()).setStockNameFromNotification(stockName);
                    ((TradwyseApplication) getApplication()).storeStockNameWhosePredectionIsReady(stockName);
                    Log.d(TAG, "Content of notification: " + "ID:" + id + ", " + "StockName:" + stockName + ", " + "NormalizedPrice" + normalizedPrice + ", " + "NewsAffected:" + newsAffected + ", " + "Dates:" + dates + ", " + "VxxAffected" + vxxAffected);
                    createAndDisplayNotification(jsonObject.getString("title"), jsonObject.getString("body"), stockName, jsonObject.getString("click_action"), notificationId, CHANNEL_ID_STOCK_PREDICTION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Action To be Perform for Open Activity:" + remoteMessage.getNotification().getClickAction());
            Log.d(TAG, "Stock Name" + predectionStockName);

            createAndDisplayNotification(remoteMessage.getNotification().getTitle(),  remoteMessage.getNotification().getBody(), stockName, remoteMessage.getNotification().getClickAction(), notificationId, CHANNEL_ID_GENERAL_NOTIFICATION);

        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
        TradWyseSession.getSharedInstance(this).setFcmToken(s);
    }

    private void createAndDisplaySocialChatNotification(String title, String body, String click_action, String notificationsId, String messageId, String groupId, String groupName, String postedByUserName, String notificationReferenceId, boolean isBasilPrivateRoom, String channelIdSocialChat) {
        int notificationId = (int) System.currentTimeMillis();
        if (click_action != null && click_action.equals("SocialChat")) {

            Intent intent = new Intent(getApplicationContext(), ChatReplyActivity.class);
            intent.putExtra("notificationId", notificationsId);
            intent.putExtra(Constants.FROM_NOTIFICATION_CLICK, true);
            intent.putExtra(Constants.FROM_PUSH_NOTIFICATION_CLICK, true);
            intent.putExtra(Constants.MESSAGE_REFERENCE_ID, notificationReferenceId);
            intent.putExtra(Constants.MessageId, messageId);
            intent.putExtra(Constants.GROUP_ID, groupId);
            intent.putExtra(Constants.GROUP_NAME, groupName);
            intent.putExtra(Constants.POSTED_BY_USERNAME, postedByUserName);
            intent.putExtra("isBasilPrivateRoom", isBasilPrivateRoom);
            intent.setAction(Long.toString(System.currentTimeMillis()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            createNotification(channelIdSocialChat, title, body, body, pendingIntent, notificationId, GROUP_KEY_SOCIAL_CHAT_NOTIFICATION);

        } else {
            createNotification(channelIdSocialChat, title, body, body, null, notificationId, "");
        }
    }

    public void createAndDisplayTipCommentNotification(String title, String content, String tipId, String click_action, String notificationsId, String CHANNEL_ID) {
        Intent intent;
        int notificationId = (int) System.currentTimeMillis();
        if (click_action != null && click_action.equals("COMMENT")) {
            intent = new Intent(this, TipDetailActivity.class);
            intent.putExtra("tipId", tipId);
            intent.putExtra("notificationId", notificationsId);
            intent.putExtra("fromNotificationClick", true);
            intent.putExtra("selectedScreen", Constants.HOME);
            intent.setAction(Long.toString(System.currentTimeMillis()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
            createNotification(CHANNEL_ID, title, content, content, pendingIntent, notificationId, GROUP_KEY_TIP_COMMENT_NOTIFICATION);
        } else {
            createNotification(CHANNEL_ID, title, content, content, null, notificationId, "");
        }
    }

    public void createAndDisplayTipNotification(String title, String content, String tipId, String click_action, String notificationsId, String CHANNEL_ID) {
        Intent intent;
        int notificationId = (int) System.currentTimeMillis();
        if (click_action != null && click_action.equalsIgnoreCase("TIP")) {
            intent = new Intent(this, TipDetailActivity.class);
            intent.putExtra("tipId", tipId);
            intent.putExtra("notificationId", notificationsId);
            intent.putExtra("fromNotificationClick", true);
            intent.putExtra("selectedScreen", Constants.HOME);
            intent.setAction(Long.toString(System.currentTimeMillis()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
            createNotification(CHANNEL_ID, title, content, content, pendingIntent, notificationId, GROUP_KEY_TIP_COMMENT_NOTIFICATION);
        } else {
            createNotification(CHANNEL_ID, title, content, content, null, notificationId, "");
        }
    }

    public void createAndDisplayAdBannerNotification(String title, String content, String adbannerId, String click_action, String CHANNEL_ID) {
        Intent intent;
        int notificationId = (int) System.currentTimeMillis();
        if (click_action != null && click_action.equals("ADBANNER")) {
            intent = new Intent(this, DashBoardActivity.class);
            intent.putExtra("adbannerId", adbannerId);
            intent.putExtra("fromNotificationClick", true);
            intent.setAction(Long.toString(System.currentTimeMillis()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
            createNotification(CHANNEL_ID, title, content, content, pendingIntent, notificationId, GROUP_KEY_BANNER_NOTIFICATION);
            //EventBus.getDefault().post(new MessageEvent(adbannerId, notificationId));
        } else {
            createNotification(CHANNEL_ID, title, content, content, null, notificationId, "");
        }
    }

    public void createAndDisplayNotification(String title, String content, String stockName, String click_action, int notificationId, String CHANNEL_ID) {
        if (click_action != null && click_action.equals("PREDICTION_MESSAGE")) {
            Intent intent = new Intent(this, GraphStockPrediction.class);
            intent.putExtra("StockName", stockName);
            intent.putExtra("fromNotificationClick", true);
            intent.setAction(Long.toString(System.currentTimeMillis()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //AppSingletonClass.getInstance().getOnNotificationReceived().onNotificationReceived(intent);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
            createNotification(CHANNEL_ID, title, content, content, pendingIntent, notificationId, GROUP_KEY_STOCK_PREDICTION_NOTIFICATION);

        } else {
            createNotification(CHANNEL_ID, title, content, content, null, notificationId, "");
        }
    }

    private void createNotification(String channelId, String title, String text, String bigText, PendingIntent pendingIntent, int notificationId, String groupId) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.splash_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.splash_logo))
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setChannelId(channelId)
                .setOngoing(false)
                .setAutoCancel(true);
        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.splash_logo);
            builder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            builder.setSmallIcon(R.drawable.splash_logo);
        }
        if (!groupId.isEmpty()) {
            builder.setGroup(groupId);
        }

        notificationManagerCompat.notify(notificationId, builder.build());
    }

}
