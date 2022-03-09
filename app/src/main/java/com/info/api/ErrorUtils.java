package com.info.api;

import android.content.Context;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;


public class ErrorUtils {
    public static APIStatusResponse parseResponse(Response<?> response, Context context) {
        Converter<ResponseBody, APIStatusResponse> converter = APIClient.getRetrofit(context,APIClient.BASE_SERVER_URL).
                responseBodyConverter(APIStatusResponse.class,new Annotation[0]);
        APIStatusResponse error;
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIStatusResponse();
        }

        return error;
    }
}
