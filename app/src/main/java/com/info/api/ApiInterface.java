package com.info.api;

import com.info.model.AdBannerModel;
import com.info.model.AddSuggestedServiceSubscriptionPlan;
import com.info.model.ChangePassModel;
import com.info.model.Comment;
import com.info.model.DeleteAllNotificationModel;
import com.info.model.FirestoreAuthentication;
import com.info.model.FlagResponse;
import com.info.model.FollowUserModel;
import com.info.model.FollowingFollowersUserCount;
import com.info.model.HideTipResponseModel;
import com.info.model.IsFollowResponse;
import com.info.model.LikeResponse;
import com.info.model.LoginResponse;
import com.info.model.LogoutResponse;
import com.info.model.MyServices;
import com.info.model.NotificationBundle;
import com.info.model.NotificationCountModel;
import com.info.model.NotificationModel;
import com.info.model.Quote;
import com.info.model.RefundRequestResponse;
import com.info.model.SectorNews;
import com.info.model.ServiceSubscrptionForMACD;
import com.info.model.ServiceType;
import com.info.model.SignUpResponse;
import com.info.model.SocialChatStocksDetails;
import com.info.model.SocialMessageDetails;
import com.info.model.StockChangeResponse;
import com.info.model.StockChartModel;
import com.info.model.StockHistory;
import com.info.model.Stocks;
import com.info.model.Subscription;
import com.info.model.TipClass;
import com.info.model.TipOuterResponse;
import com.info.model.Tips;
import com.info.model.User;
import com.info.model.UserSubscriptionDetail;
import com.info.model.ValidateCouponModel;
import com.info.model.analyticsModel.UserOpenAppModel;
import com.info.model.paymentresponse.StripePaymentResponse;
import com.info.model.stockPrediction.StockPredictionResponse;
import com.info.model.timelineModels.TimeLineDataModel;
import com.info.model.userServiceResponse.ServiceSubscriptionPlan;
import com.info.model.userServiceResponse.UserServiceResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Created by dinesh on 5/7/2019.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("stocksearchdetails/searchStockSymbol")
    Call<List<Stocks>> searchStocks(@Field("stockSymbol") String searchTerm);

    @GET("stable/stock/{stock_name}/chart/1d")
    Call<List<StockChartModel>> getStockChartData(@Path("stock_name") String stock_name, @Query("token") String token);

    @FormUrlEncoded
    @POST("getcsvfile/getstockjsonfile")
    Call<List<StockHistory>> getStockHistoryData(@Field("stockName") String stockName);



    @FormUrlEncoded
    @POST("GraphParameter/getNormalPriceBySubscription")
    Call<StockPredictionResponse> getStockPredictionData(@Field("stockName") String stockName);



    @FormUrlEncoded
    @POST("auth/appNewSignIn")
    Call<LoginResponse> loginUser(@Field("userName") String username, @Field("password") String password, @Field("deviceToken") String deviceToken, @Field("deviceLoginId") String deviceLoginId, @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("auth/deviceLogout")
    Call<LogoutResponse> logoutUser(@Field("deviceLoginId") String deviceLoginId);


    @FormUrlEncoded
    @POST("appUser/getAppUserByUserName")
    Call<User> getAppUserByUserName(@Field("appuserName") String username);

    @FormUrlEncoded
    @POST("appUser/getAppUserByUserName")
    Observable<User> getAppUserByUserName2(@Field("appuserName") String username);

    @FormUrlEncoded
    @POST("appUser/newSignup")
    Call<SignUpResponse> signupUser(@Field("email") String email, @Field("userName") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/changePassword")
    Call<ChangePassModel> changepassword(@Field("userName") String username, @Field("oldPassword") String oldpassword, @Field("newPassword") String newpassword);


    //stable/stock/aapl/quote
    @GET("stable/stock/{stock_name}/quote")
    Call<Quote> getStockDetailData(@Path("stock_name") String stock_name, @Query("token") String token);

    @GET("stable/stock/{stock_name}/quote")
    Single<Quote> getStockDetailData2(@Path("stock_name") String stock_name, @Query("token") String token);


    @POST("tip/getAllTipsWithoutComments")
    Call<List<Tips>> getAllTips();

    @FormUrlEncoded
    @POST("tip/getAllTipFeatureDetailsByTipId")
    Call<TipClass> getTipByTipId(@Field("tipId") String tipId);

    @FormUrlEncoded
    //@POST("tip/getAllTipFeaturePaginationForUser")
    @POST("tip/getAllTipFeaturePaginationForUserWithMacdGif")
    Call<TipOuterResponse> getAllTipsOfUserWithPagination(@Field("offset") int offset, @Field("limit") int limit);

    @FormUrlEncoded
    @POST("sectorNewsSentiment/getSentimentandSMAByStockSymbolResultSet")
    Call<List<SectorNews>> getSectorNewsSentiment(@Field("stockSymbol") String stockSymbol);

    @FormUrlEncoded
    @POST("timeline/getTimeLineWithPaginationForUser")
    Call<ArrayList<TimeLineDataModel>> getTimeLine(@Field("userName") String userName, @Field("offset") int offset, @Field("limit") int limit);

    @FormUrlEncoded
    @POST("timeline/getTimeLineWithPaginationForUser")
    Call<ArrayList<TimeLineDataModel>> getTimeLineActivityWise(@Field("activityType") String activityType, @Field("userName") String userName, @Field("offset") int offset, @Field("limit") int limit);


    @FormUrlEncoded
    @POST("timeline/getTimeLineWithPaginationForMentor")
    Call<ArrayList<TimeLineDataModel>> getTimeLineForMentor(@Field("userName") String userName, @Field("offset") int offset, @Field("limit") int limit);

    @FormUrlEncoded
    @POST("timeline/getTimeLineWithPaginationForMentor")
    Call<ArrayList<TimeLineDataModel>> getTimeLineForMentorActivityWise(@Field("activityType") String activityType, @Field("userName") String userName, @Field("offset") int offset, @Field("limit") int limit);


    @FormUrlEncoded
    @POST("comment/getCommentsByTipIdPagination")
    Call<List<Comment>> getTipsComment(@Field("tipId") String tipId, @Field("offset") int offset, @Field("limit") int limit);


    @Streaming
    @FormUrlEncoded
    @POST("tip/getTipImageByToken")
    Call<ResponseBody> getTipsImage(@Field("imagetoken") String imagetoken);


    @FormUrlEncoded
    @POST("comment/addCommentOnTip")
    Call<Comment> addCommentOnTip(@Field("tipId") String tipId, @Field("commentDetails") String commentDetails, @Field("status") String status, @Field("imageDetails") String imageDetails, @Field("userid") String userId);

    @FormUrlEncoded
    @POST("stockDetail/getAllStockOfUser")
    Call<List<Stocks>> getAllStockOfUser(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("stockDetail/getAllStockOfUser")
    Observable<List<Stocks>> getAllStockOfUser2(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("stockDetail/deleteStock")
    Call<List<HashMap<String, String>>> deleteStock(@Field("stockId") String stockId);

    @POST("stockDetail/addMultipleStock")
    Call<List<Stocks>> addMultipleStock(@Body HashMap<String, ArrayList<Stocks>> newStocks);

    @FormUrlEncoded
    @POST("stockDetail/deleteStock")
    Call<List<Stocks>> deleteMultipleStock(@Field("stockId") String stockId);

    @POST("tip/addTip")
    Call<Tips> addTip(@Body RequestBody requestBody);

    @POST("appUser/uploadUserImageByUserName")
    Call<ResponseBody> addProfilePhoto(@Body RequestBody requestBody);

    @POST("fcmToken/getFCMToken")
    Call<FirestoreAuthentication> getFirestoreAuth();

    @POST("notificationActivity/getCountOfAllUnreadNotificationActivityDetails")
    Call<NotificationCountModel> getCountOfAllUnreadNotificationActivityDetails();

    @POST("adBannerSimple/getActiveSimpleAdBannerDetails")
    Call<List<AdBannerModel>> getActiveSimpleAdBannerDetails();

    @FormUrlEncoded
    @POST("adBannerSimple/getSimpleAdBannerById")
    Call<AdBannerModel> getSimpleAdBannerById(@Field("adBannerId") String adBannerId);

    @FormUrlEncoded
    @POST("notificationActivity/getAllNotificationActivityDetailsWithPagination")
        //@POST("notificationActivity/getAllUnreadBundleNotificationActivityDetailsWithPagination")
    Call<List<NotificationModel>> getAllNotificationActivityDetailsWithPagination(@Field("userName") String userName, @Field("offset") String offset, @Field("limit") String limit);

    @FormUrlEncoded
    @POST("notificationActivity/getAllUnreadBundleNotificationActivityMessage")
    Call<List<NotificationBundle>> getAllUnreadBundleNotificationActivityMessage(@Field("userName") String userName);

    @FormUrlEncoded
    @POST("notificationActivity/createNotificationAsReadByNotificationId")
    Call<List<NotificationModel>> createNotificationAsReadByNotificationId(@Field("notificationId") String notificationId);

    @FormUrlEncoded
    @POST("notificationActivity/createNotificationAsReadByNotificationId")
    Call<List<NotificationModel>> createNotificationAsReadByNotificationId2(@Field("notificationId") String notificationId,
                                                                            @Field("commentReadFromPN") String commentReadFromPN);

    @FormUrlEncoded
    @POST("notificationActivity/createNotificationAsReadByNotificationId")
    Call<List<NotificationModel>> createNotificationAsReadByNotificationId(@Field("notificationId") String notificationId,
                                                                           @Field("bundleNotificationId") String bundleNotificationId);

    @FormUrlEncoded
    @POST("notificationActivity/deleteNotificationDetailsById")
    Call<List<NotificationModel>> deleteNotificationDetailsById(@Field("notificationId") String notificationId);

    @FormUrlEncoded
    @POST("notificationActivity/deleteNotificationDetailsById")
    Call<List<NotificationModel>> deleteNotificationDetailsById(@Field("notificationId") String notificationId,
                                                                @Field("bundleNotificationId") String bundleNotificationId);

    @FormUrlEncoded
    @POST("notificationActivity/deleteAllNotificationForLoginUser")
    Call<List<DeleteAllNotificationModel>> deleteAllNotificationForLoginUser(@Field("userId") String userId);

    @Streaming
    @FormUrlEncoded
    @POST("appUser/getUserImageByUserName")
    Call<ResponseBody> getProfileImage(@Field("appUserName") String appUserName);

    @FormUrlEncoded
    @POST("appUser/getUserImageByUserName")
    Call<ResponseBody> getUserImageByUserName(@Field("appUserName") String userName);

    @FormUrlEncoded
    @POST("tipCommentFlag/addTipFlag")
    Call<FlagResponse> addFlagForTip(@Field("tipId") String tipId, @Field("userId") String userId,
                                     @Field("isFlag") boolean isFlag, @Field("flagReason") String flagReason);

    @FormUrlEncoded
    @POST("auth/forgotPassword")
    Call<FlagResponse> forgotPassword(@Field("userName") String username);


    @FormUrlEncoded
    @POST("tipCommentFlag/addCommentFlag")
    Call<FlagResponse> addFlagForComment(@Field("commentId") String tipId, @Field("userId") String userId,
                                         @Field("isFlag") boolean isFlag, @Field("flagReason") String flagReason);


    @FormUrlEncoded
    @POST("followUserList/addFollowUser")
    Call<FollowUserModel> followUser(@Field("followUserName") String username, @Field("status") String status,
                                     @Field("description") String description);

    @FormUrlEncoded
    @POST("followUserList/AddUnFollowUser")
    Call<FollowUserModel> unfollowUser(@Field("followUserName") String username, @Field("status") String status,
                                       @Field("description") String description);

    @FormUrlEncoded
    @POST("followUserList/isLoginUserFollowGivenUser")
    Call<IsFollowResponse> isLoginUserFollowGivenUser(@Field("userName") String username);

    @FormUrlEncoded
    @POST("followUserList/getAllFollowUserListByUserName")
    Call<List<FollowUserModel>> getAllFollowingUserListByUserName(@Field("userName") String username, @Field("offset") int offset, @Field("limit") int limit);

    @FormUrlEncoded
    @POST("followUserList/getAllFollowUserListByUserName")
    Call<List<FollowUserModel>> getAllFollowerUserListByUserName(@Field("followUserName") String username, @Field("offset") int offset, @Field("limit") int limit);

    //@POST("subscriptionPlan/getAllSubscriptionPlan")
    @POST("subscriptionPlan/getAllNewSubscriptionPlan")
    Call<List<Subscription>> getAllSubscriptionPlan();


    //@POST("subscriptionPlan/getAllTrialSubscriptionPlan")
    @POST("subscriptionPlan/getAllNewTrialSubscriptionPlan")
    Call<List<Subscription>> getAllTrialSubscriptionPlan();


    @FormUrlEncoded
    @POST("stripe/createStripePayment")
    Call<StripePaymentResponse> createStripePayment(@Field("userName") String username, @Field("paymentId") String paymentId, @Field("subscriptionPlanId") String subscriptionPlanId, @Field("couponId") String couponId);


    @FormUrlEncoded
    @POST("stripe/validateCoupon")
    Call<ValidateCouponModel> validateCoupon(@Field("couponId") String couponId);


    @FormUrlEncoded
    @POST("tipFeature/tipLikeForUser")
    Call<LikeResponse> userTipLike(@Field("tipId") String tipId);

    @FormUrlEncoded
    @POST("tipFeature/tipUnLikeForUser")
    Call<LikeResponse> userTipUnLike(@Field("tipId") String tipId);

    @FormUrlEncoded
    @POST("tipFeature/tipPinForUser")
    Call<LikeResponse> tipPinForUser(@Field("tipId") String tipId);

    @FormUrlEncoded
    @POST("tipFeature/tipUnPinForUser")
    Call<LikeResponse> tipUnPinForUser(@Field("tipId") String tipId);

    @FormUrlEncoded
    @POST("tipFeature/tipHideForUser")
    Call<HideTipResponseModel> tipHideForUser(@Field("tipId") String tipId);

    @FormUrlEncoded
    @POST("tipTweetDetails/addTipTweetDetails")
    Call<HideTipResponseModel> addTipTweetDetails(@Field("tipId") String tipId, @Field("tweetId") String tweetId, @Field("tweetWebLink") String tweetWebLink, @Field("tweetAccountId") String tweetAccountId, @Field("tweetText") String tweetText, @Field("tweetDateTime") String tweetDateTime);

    @FormUrlEncoded
    @POST("tipFeature/tipUnHideForUser")
    Call<HideTipResponseModel> tipUnHideForUser(@Field("tipId") String tipId);


    @FormUrlEncoded
    @POST("stripe/getProductSubscriptionsByUserId")
    Call<List<UserSubscriptionDetail>> getProductSubscriptionsByUserId(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("refundRequest/createRefundRequest")
    Call<RefundRequestResponse> createRefundRequest(@Field("userId") String userId, @Field("reason") String reason, @Field("productSubscriptionId") String productSubscriptionId);



    @FormUrlEncoded
    @POST("serviceMentorSubscriptionPlan/getAllTypeServicesForUser")
    Call<UserServiceResponse> getAllTypeServicesForUser(@Field("userId") String userId);


    @FormUrlEncoded
    @POST("stripe/subscribeSeggestedServiceSubscriptionPlan")
    Call<AddSuggestedServiceSubscriptionPlan> subscribeSeggestedServiceSubscriptionPlan(@Field("serviceSubscriptionPlanId") String serviceSubscriptionPlanId);


    @FormUrlEncoded
    @POST("followUserList/getFollowUserCountByUserName")
    Call<FollowingFollowersUserCount> getFollowerAndFollowingUserCount(@Field("userName") String userName);


    @FormUrlEncoded
    @POST("stripe/createServiceSubscriptionPayment")
    Call<StripePaymentResponse> createServiceSubscriptionPayment(@Field("userName") String username, @Field("paymentId") String paymentId, @Field("serviceSubscriptionPlanId") String serviceSubscriptionPlanId, @Field("couponId") String couponId);


    @FormUrlEncoded
    @POST("serviceMentorSubscriptionPlan/getServiceSubscriptionPlanInfoById")
    Call<ServiceSubscriptionPlan> serviceMentorSubscriptionPlanDetails(@Field("serviceSubscriptionPlanId") String serviceSubscriptionPlanId);

    @FormUrlEncoded
    @POST("smaStockChange/getWatchListStockChangeDetailsByUserName")
    Single<List<StockChangeResponse>> getChangedStocksForUser(@Field("userName") String username);

    @POST("serviceTypeMaster/getAllServiceType")
    Call<List<ServiceType>> getAllServiceType();

    @FormUrlEncoded
    @POST("serviceMentorSubscriptionPlan/getMyServiceListFilterByServiceMasterId")
    Call<MyServices> getMyServiceListFilterByServiceMasterId(@Field("serviceTypeId") String serviceTypeId);

    @FormUrlEncoded
    @POST("serviceMentorSubscriptionPlan/getAllOtherServiceListFilterByServiceMasterId")
    Call<List<ServiceSubscriptionPlan>> getAllOtherServiceListFilterByServiceMasterId(@Field("serviceTypeId") String serviceTypeId);

    @POST("serviceMentorSubscriptionPlan/getServiceSubscriptionPlanForMacd")
    Call<ServiceSubscrptionForMACD> callGetServiceSubscriptionPlanForMacd();

    @POST("socialChatStockDetails/getAllSocialChatStockDetailsList")
    Call<List<SocialChatStocksDetails>> getAllSocialChatStockDetailsList();

    @FormUrlEncoded
    @POST("socialChatStockDetails/getSocialChatStockDetailsForDiscordRedditTwitter")
    Call<List<SocialMessageDetails>> getSocialChatStockDetailsForDiscordRedditTwitter(@Field("offset") int offset, @Field("limit") int limit);

    @FormUrlEncoded
    @POST("socialChatStockDetails/sendFCMNotificationForSocialChatWithUserId")
    Call<ResponseBody> sendFCMNotificationForSocialChatWithUserId(@Field("messageId") String messageId, @Field("deviceType") String deviceType,
                                                                  @Field("userName") String userName, @Field("groupId") String groupId,
                                                                  @Field("notificationReferenceId") String notificationReferenceId,
                                                                  @Field("groupName") String groupName, @Field("postedByUserName") String postedByUserName);

    @FormUrlEncoded
    @POST("userChatGroupDetail/addUserChatGroupDetail")
    Call<ResponseBody> addUserChatGroupDetail(@Field("userName") String userName, @Field("groupId") String groupId,
                                                            @Field("groupName") String groupName);

    @POST("userAppConnectionRecord/addUserAppConnectionRecord")
    Call<UserOpenAppModel> setAppUserDataForAnalytics();

}
