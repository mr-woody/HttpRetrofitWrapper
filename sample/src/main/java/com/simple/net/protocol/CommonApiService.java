package com.simple.net.protocol;

import com.woodys.http.core.Call;
import com.simple.net.UrlConstant;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface CommonApiService {

    @POST(UrlConstant.URI_GET_USER_INFO)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getUserInfo(@HeaderMap  Map<String, String> headers, @Body  Map<String, String> requestBody);


    @POST(UrlConstant.URI_USER_CHECK_AVATAR)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> checkAvatar(@HeaderMap  Map<String, String> headers, @Body Map<String, String> requestBody);


    @Headers("BaseUrlName:girl")
    @GET("https://gank.io/api/v2/categories/Girl")
    Call<ResponseBody> getNetWorkDataFormGirl();

    @Headers("BaseUrlName:gank")
    @GET("https://gank.io/api/v2/banners")
    Call<ResponseBody> getNetWorkDataFormGank();







}
