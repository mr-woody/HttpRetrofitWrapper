package com.woodys.http.core.interceptor;

import android.text.TextUtils;

import com.woodys.http.RetrofitWrapperManager;
import com.woodys.http.core.mock.Mock;
import com.woodys.http.core.mock.MockHelper;
import com.woodys.http.utils.ContextHolder;

import java.io.IOException;
import java.lang.reflect.Method;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * mock 拦截器
 * @author Created by woodys on 2020/6/08.
 * @email yuetao.315@qq.com
 */
public class MockInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // 判断是否支持Mock数据的功能
        if (!RetrofitWrapperManager.isEnableMock()){
            return chain.proceed(request);
        }

        // 获取 retrofit 中定义 method
        Method retrofitMethod = MockHelper.getRetrofitMethod(request);
        if(null == retrofitMethod){
            return chain.proceed(request);
        }

        // 根据 method 获取它的 mock 注解
        Mock mock = MockHelper.getMock(retrofitMethod);
        if(null == mock){
            return chain.proceed(request);
        }

        // 获取 mockEnable 值判断当前方法是否支持mock
        if(!MockHelper.getMockEnable(mock)){
            return chain.proceed(request);
        }

        // 获取 mockUrl 进行重定向
        String url = MockHelper.getMockUrl(mock);
        if (!TextUtils.isEmpty(url)) {
            return chain.proceed(mockRequest(request, url));
        }

        // 根据 mock 注解获取 mockData
        String mockData = MockHelper.getMockData(ContextHolder.getContext(),mock);

        // 如果 mockData 不为空就短路拦截器
        if (!TextUtils.isEmpty(mockData)) {
            return new Response.Builder()
                    .protocol(Protocol.HTTP_1_0)
                    .code(200)
                    .request(request)
                    .message("OK")
                    .body(ResponseBody.create(null,mockData))
                    .build();
        }
        return chain.proceed(request);
    }

    private Request mockRequest(Request request ,String mockUrl) {
        return request.newBuilder().url(mockUrl).build();
    }
}
