package com.simple.net.protocol;

import com.woodys.http.core.Call;
import com.woodys.http.core.download.DownloadCall;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface DownloadApiService {

    //下载文件
    @Streaming
    @Headers("LogLevel:BASIC")
    @GET
    DownloadCall<File> download(@Url String url);


    @GET("https://gank.io/images/882afc997ad84f8ab2a313f6ce0f3522")
    Call<ResponseBody> requestImageByGirl();
}
