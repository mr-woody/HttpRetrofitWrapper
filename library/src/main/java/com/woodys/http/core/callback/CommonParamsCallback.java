package com.woodys.http.core.callback;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 公共请求头和请求参数处理
 * @author Created by woodys on 2020/6/08.
 * @email yuetao.315@qq.com
 */
public interface CommonParamsCallback {

    /**
     * 设置公共请求头
     * @param builder
     */
    void addHeader(Request.Builder builder);

    /**
     * 设置url后面追加的公共参数
     * @param originalRequest
     * @param httpUrlBuilder
     */
    void addQueryParams(Request originalRequest, HttpUrl.Builder httpUrlBuilder);

    /**
     * 设置公共请求参数
     * @param originalRequest
     * @param requestBuilder
     */
    void addCommonParams(Request originalRequest, Request.Builder requestBuilder);
}
