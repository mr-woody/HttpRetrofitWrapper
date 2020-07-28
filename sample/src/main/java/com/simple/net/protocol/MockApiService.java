package com.simple.net.protocol;

import com.woodys.http.core.Call;
import com.woodys.http.core.mock.Mock;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;


public interface MockApiService {

    @Headers("BaseUrlName:girl")
    @Mock(url = "https://gank.io/api/v2/banners")
    @GET("https://gank.io/api/v2/categories/Girl")
    Call<ResponseBody> getNetWorkDataFormGirl1();

    @Headers("BaseUrlName:girl")
    @Mock(value = "{\"data\":[{\"coverImageUrl\":\"http://gank.io/images/30bc3da361ca47fcbe5bc945aae29aa9\",\"desc\":\"- \\u5ff5\\u5ff5\\u4e0d\\u5fd8\\uff0c\\u5fc5\\u6709\\u56de\\u54cd\",\"title\":\"\\u59b9\\u7eb8\",\"type\":\"Girl\"}]}")
    @GET("https://gank.io/api/v2/categories/Girl")
    Call<ResponseBody> getNetWorkDataFormGirl2();

    @Headers("BaseUrlName:girl")
    @Mock(assets = "mock/mock.json")
    @GET("https://gank.io/api/v2/categories/Girl")
    Call<ResponseBody> getNetWorkDataFormGirl3();

}
