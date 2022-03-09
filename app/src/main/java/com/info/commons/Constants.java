package com.info.commons;

import java.util.ArrayList;


public class Constants {

    //TODO: Change the isProduction to true for production build
    public static final boolean IS_PRODUCTION = false;
    public static final String IEX_TOKEN = "pk_dd324da3fb5f4428a47b05ab12f23ce2"; //licence account token.
    public static final int SECTION_VIEW = 1;
    public static final int STOCK_VIEW = 2;
    public static final String NY_ZONE = "America/New_York";
    public static final String NYT = "NYT";

    //Stripe publish key for test API keys.
    public static final String STRIPE_TOKEN = Constants.IS_PRODUCTION ? "pk_live_7b9zLcAaGBVeu14tr9Jueznl00HCPZZOU1" : "pk_test_Pbri8k4HUNcegrgjAohigZKF002BpByODh";

    //Sendbird AppId.
    public static final String SENDBIRD_APP_ID = "30AC907B-4526-46CC-884F-14880440CA82";

    public static final int DEVICE_HEIGHT = 1;
    public static final int DEVICE_WIDTH = 2;
    public static final String ON_FAILURE_TAG = "ON_FAILURE";

    public static final int REQUEST_CODE_FOLLOW_SCREEN = 400;

    public static final int NORMALIZED_PRICE_COLOR = 1;
    public static final int NEWS_AFFECTED_COLOR = 2;
    public static final int VXX_AFFECTED_COLOR = 3;

    public static final String ACTIVITY_TYPE_FOLLOW_USER = "FollowUser";
    public static final String ACTIVITY_TYPE_TIP_TWEET_DETAIL = "TipTweetDetails";
    public static final String ACTIVITY_TYPE_TIP_PIN = "TipPin";
    public static final String ACTIVITY_TYPE_COMMENT = "Comment";
    public static final String ACTIVITY_TYPE_DATE_ROW = "DateRow";
    public static final String SERVICE_LIST = "serviceList";
    public static final String SERVICE_HEADER = "serviceHeader";

    public static final String EXTRA_CHANNEL_TYPE = "com.info.tradetips.CHANNEL_TYPE";
    public static final String EXTRA_CHANNEL_ID = "com.info.tradetips.CHANNEL_ID";

    public static final int TIP_DETAIL_REQUEST_CODE = 131;

    public static ArrayList<String> VisitedProfileUserNameList = new ArrayList<>();

    public static String MENTOR_PUBLIC = "public";

    public static String MENTOR_ALL_ROOM = "mentorAll";
    public static String MENTOR_SELECTED_ROOM = "mentorSelected";

    public static String ROOM_IMAGE_BUCKET = "imagebucket";

    //DeepLinking Parameters
    public static String domainUriPrefix = "https://tradetipsapp.page.link/";
    public static String link = "https://tradetipsapp.com/";
    public static String androidPackageName = "com.info.tradetips";
    public static int androidMinVersion = 2;
    public static String iOSPackageName = "com.tradetips.tradetips";
    public static String iOSAppStoreId = "1500107937";
    public static String iOSMinVersion = "2.0.1";
    public static String PLATFORM = "Android";
    public static String FROM = "from";
    public static String FROM_DASHBOARD = "fromDashboard";
    public static String FROM_GRAPH_STOCK_PREDICTION = "fromGraphStockPrediction";
    public static String FROM_SETTING = "fromSetting";
    public static String FROM_PROFILE_TABBED_ACTIVITY = "fromProfileTabbed";

    public static String GROUP_NAME = "groupName";
    public static String POSTED_BY_USERNAME = "postedByUserName";

    public static String OPEN_GROUP = "openGroups";
    public static String GROUP_ID = "groupId";
    public static String GROUP_DESCRIPTION = "groupDesc";
    public static String GROUP_IMAGE = "groupImage";
    public static String GROUP_TITLE = "groupTitle";
    public static String GROUP_CODE = "groupCode";
    public static String PRODUCTION_GROUP_ID = "roomOne";
    public static String DEVELOPMENT_GROUP_ID = "demoOpenGroup1";
    public static String PRODUCTION_SOCIAL_GROUP_ID = "Production";
    public static String DEVELOPMENT_SOCIAL_GROUP_ID = "Test";
    public static String SOCIAL_CHAT = "socialchat";
    public static String SOCIAL_GROUP_ID = "socialGroupId";
    public static String BASIL_PRIVATE_GROUP = "basilPrivateGroup";
    public static String PRODUCTION_BASIL_PRIVATE_GROUP_ID = "Production";
    public static String DEVELOPMENT_BASIL_PRIVATE_GROUP_ID = "Test";

    public static String CREATED_ON = "createdOn";
    public static String AUTHOR_NAME = "authorName";
    public static String COMMENT_BODY = "commentBody";
    public static String DESCRIPTION = "description";
    public static String ID = "id";
    public static String LINK = "link";
    public static String MODIFIED_ON = "modifiedOn";
    public static String PUBLISH_DATE_TIME = "publishDateTime";
    public static String SOCIAL_SITE_NAME = "socialSiteName";
    public static String STOCK_NAME = "stockName";
    public static String RADDIT_URL_NAME = "radditUrlName";
    public static String URL = "url";
    public static String STOCK_SYMBOL = "stockSymbol";
    public static String PROFILE_IMAGE_URL = "profileImageUrl";
    public static String COMMENT = "comment";
    public static String LIKE_COUNT = "likeCount";
    public static String LIKEED_BY_USER_NAME = "likedByUserName";

    public static String COMMENT_DATE = "date";
    public static String COMMENT_AUTHOR_NAME = "authername";
    public static String COMMENT_TITLE = "title";

    public static String IMAGE_URL = "imageUrl";

    public static String MessageCreatedDate = "createdDate";
    public static String MessageText = "message";
    public static String MessageId = "messageId";
    public static String MessageType = "messageType";
    public static String MessageProfileImageUrl = "profileImageUrl";
    public static String MessageUserId = "userId";
    public static String MessageUserName = "userName";
    public static String MessageCaption = "caption";
    public static String MessageSource = "messageSource";
    public static String MessageReplyId = "messageReplyId";
    public static String MessageReplyUserId = "messageReplyUserId";
    public static String MessageReplyUserName = "messageReplyUserName";
    public static String MessageFlag = "messageFlag";
    public static String MessageFlagMsg = "messageFlagMsg";
    public static String MessageSocialReferenceId = "messageSocialReferenceId";
    public static String MessageFlagedUserName = "messageFlagedUserName";
    public static String MessageFlagedUserId = "messageFlagedUserId";
    public static String Flag = "flag";
    public static String deviceType = "Android";
    public static String UserName = "userName";

    public static String TextMessageType = "text";
    public static String ImageMessageType = "photo";
    public static String VideoMessageType = "video";
    public static String DocMessageType = "audio";
    public static String AudioMessageType = "audio";
    public static String USERNAME = "username";
    public static String FOLLOWING = "following";
    public static String FOLLOWERS = "followers";
    public static String FOR = "for";
    public static String FROM_SPECIAL_OFFER = "from_special_offer";
    public static String FROM_SUBSCRIPTION_LIST = "from_subscription_list";
    public static String FROM_SERVICE_LIST = "from_service_list";
    public static String TIP_ID = "tipId";
    public static String SERVICE_ID = "serviceSubscriptionPlanId";
    public static String GOTO_SUBSCRIPTION_PAGE = "gotosubscriptionPage";

    public static String PENDING_DYNAMIC_LIST_PREF = "pendingDynamicListPref";
    public static String PENDING_DYNAMIC_LIST_PREF_OBJ = "pendingDynamicListPrefObj";
    public static String PREDECTION_READY_STOCK_NAME_PREF = "predectionReadyStockNamePref";
    public static String PREDECTION_READY_STOCK_NAME_PREF_OBJ = "predectionReadyStockNamePrefObj";

    public static final String STOCK_CHANGE_PAYLOAD_IDENTIFIER = "showStockChanges";

    public static String FIRESTORE_AUTH_PREF = "firestore_auth_pref";
    public static String FIRESTORE_AUTH_PREF_OBJ = "firestore_auth_pref_obj";
    public static String ADD_BANNER_PREF = "addBannerPref";
    public static String ADD_BANNER_PREF_OBJ = "addBannerPrefObj";


    public static String FROM_NOTIFICATION_CLICK = "fromNotificationClick";
    public static String FROM_PUSH_NOTIFICATION_CLICK = "fromPushNotificationClick";
    public static String MESSAGE_REFERENCE_ID = "notificationReferenceId";

    // Screen Names
    public static String HOME = "home";
    public static String MORE_TAB = "more_tab";
    public static String NOTIFICATION = "notification";
    public static String SERVICES = "services";

    // Chat group names
    public static String SOCIAL_CHAT_ROOM = "social_chat_rom";
    public static String STRATEGY_CHAT_ROOM = "strategy_chat_rom";
    public static String BASIL_PRIVATE_CHAT_ROOM = "basil_private_chat_rom";

}
