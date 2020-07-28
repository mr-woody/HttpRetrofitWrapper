package com.woodys.http.core.interceptor;

import com.woodys.http.core.callback.CommonParamsCallback;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 公共请求头和参数处理拦截器
 * @author Created by woodys on 2020/6/08.
 * @email yuetao.315@qq.com
 */
public class HttpAddCommonArgsInterceptor implements Interceptor {
    private CommonParamsCallback paramsCallback;
    public HttpAddCommonArgsInterceptor(CommonParamsCallback paramsCallback){
        this.paramsCallback = paramsCallback;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();

        if (paramsCallback != null) {
            paramsCallback.addHeader(requestBuilder);
            HttpUrl.Builder httpUrlBuilder = originalRequest.url().newBuilder();
            paramsCallback.addQueryParams(originalRequest, httpUrlBuilder);
            requestBuilder.url(httpUrlBuilder.build());
            paramsCallback.addCommonParams(originalRequest, requestBuilder);
        }

        return chain.proceed(requestBuilder.build());
    }
}
