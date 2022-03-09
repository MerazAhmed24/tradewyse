package com.info.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.tradewyse.BuildConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by dinesh on 5/7/2019.
 */

public class APIClient {

    public static final String BASE_URL_TRADEWYSE ="https://cloud.iexapis.com/";
    private static OkHttpClient okHttpClient;
    private static OkHttpClient.Builder httpClientBuilder;

    public static final String DEV_BASE_SERVER_URL ="https://apistest.tradetipsapp.com/api/";
    public static final String PROD_BASE_SERVER_URL ="https://apis.tradetipsapp.com/api/";

    public static final String DEV_BASE_SERVER_URL_IMAGE ="https://apistest.tradetipsapp.com";
    public static final String PROD_BASE_SERVER_URL_IMAGE ="https://apis.tradetipsapp.com";

    public static final String DEV_BASE_SERVER_URL_PREDICTION ="https://apistest.tradetipsapp.com/api/";
    public static final String PROD_BASE_SERVER_URL_PREDICTION ="https://apis.tradetipsapp.com/api/";

    public static final String NEW_USER_IN_APP_NOTIFICATION_REDIRECTION = "https://tradetipsapp.com/getting-started/";

    public static String BASE_SERVER_URL_PREDICTION = Constants.IS_PRODUCTION ? PROD_BASE_SERVER_URL_PREDICTION : DEV_BASE_SERVER_URL_PREDICTION;
    public static String BASE_SERVER_URL = Constants.IS_PRODUCTION ? PROD_BASE_SERVER_URL:DEV_BASE_SERVER_URL ;
    public static String BASE_SERVER_URL_IMAGE = Constants.IS_PRODUCTION ? PROD_BASE_SERVER_URL_IMAGE:DEV_BASE_SERVER_URL_IMAGE ;

    public static Retrofit retrofitCloud;
    public static Retrofit retrofitTradeTip;

    public static ApiInterface getAPIClient(String baseURL) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseURL)
                .build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        return service;
    }

    public static ApiInterface getTradeAPIClient(String baseURL, final Context context) {
        ApiInterface service = getRetrofit(context, baseURL).create(ApiInterface.class);
        return service;
    }


    private static OkHttpClient.Builder getHttpBuilder(Context context) {
        if (httpClientBuilder == null) {
            httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.addInterceptor(chain -> {
                TradWyseSession tradWyseSession = TradWyseSession.getSharedInstance(context);
                String token = tradWyseSession.getLoginAccessToken();
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(request);
            });

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            if (BuildConfig.DEBUG) {
                logging.level(HttpLoggingInterceptor.Level.BODY);
            } else {
                logging.level(HttpLoggingInterceptor.Level.NONE);
            }
            httpClientBuilder.addInterceptor(logging);
        }
        return httpClientBuilder;
    }


    private static OkHttpClient getHttpClient(Context context) {
        if (okHttpClient == null) {
            okHttpClient = getHttpBuilder(context).connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }

    public static Retrofit getRetrofit(Context context, String baseURL) {

      /*  Gson gson = new GsonBuilder()
                .setLenient()
                .create();*/

        switch (baseURL) {
            case BASE_URL_TRADEWYSE:
                if (retrofitTradeTip == null) {
                    retrofitTradeTip = new Retrofit.Builder()
                            .client(getHttpClient(context))
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(baseURL)
                            .build();
                }
                return retrofitTradeTip;

            case DEV_BASE_SERVER_URL:
            case PROD_BASE_SERVER_URL:
                if (retrofitCloud == null) {
                    retrofitCloud = new Retrofit.Builder()
                            .client(getHttpClient(context))
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(baseURL)
                            .build();
                }
                return retrofitCloud;
        }
        return new Retrofit.Builder()
                .client(getHttpClient(context))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
    }

}

