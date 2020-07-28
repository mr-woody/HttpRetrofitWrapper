package com.simple.net.impls;

import com.woodys.http.core.callback.CommonParamsCallback;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 公共请求头和请求参数处理
 * @author Created by woodys on 2020/6/08.
 * @email yuetao.315@qq.com
 */
public class SimpleCommonParamsCallback implements CommonParamsCallback {
    @Override
    public void addHeader(Request.Builder builder) {
        builder.addHeader("CommonHeader1","value1");
        builder.addHeader("CommonHeader2","value2");
        builder.addHeader("CommonHeader3","value3");
    }

    @Override
    public void addQueryParams(Request originalRequest, HttpUrl.Builder httpUrlBuilder) {
        httpUrlBuilder.addEncodedQueryParameter("QueryParamet1","value1");
        httpUrlBuilder.addEncodedQueryParameter("QueryParamet2","value2");
    }

    @Override
    public void addCommonParams(Request originalRequest, Request.Builder requestBuilder) {

    }
}
