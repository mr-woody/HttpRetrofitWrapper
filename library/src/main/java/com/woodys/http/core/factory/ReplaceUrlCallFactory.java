package com.woodys.http.core.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 功能描述： 替换修改{@link Request#url()}
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
public abstract class ReplaceUrlCallFactory extends CallFactoryProxy {
    private final String TAG = getClass().getSimpleName();
    private static final String BASE_URL_NAME = "BaseUrlName";

    public ReplaceUrlCallFactory(@NonNull Call.Factory delegate) {
        super(delegate);
    }

    @Override
    public final Call newCall(Request request) {
        /*
         * @Headers("BaseUrlName:xxx") for method, or
         * method(@Header("BaseUrlName") String name) for parameter
         */
        String baseUrlName = request.header(BASE_URL_NAME);
        if (baseUrlName != null) {
            okhttp3.HttpUrl newHttpUrl = getNewUrl(baseUrlName, request);
            if (newHttpUrl != null) {
                Request newRequest = request.newBuilder().url(newHttpUrl).build();
                return delegate.newCall(newRequest);
            } else {
                Log.w(TAG, "getNewUrl() return null when baseUrlName==" + baseUrlName);
            }
        }
        return delegate.newCall(request);
    }

    /**
     * @param baseUrlName name to sign baseUrl
     * @return new httpUrl, if null use old httpUrl
     */
    @Nullable
    protected abstract HttpUrl getNewUrl(String baseUrlName, Request request);

}