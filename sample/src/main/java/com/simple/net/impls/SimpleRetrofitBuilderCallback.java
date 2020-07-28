package com.simple.net.impls;

import com.woodys.http.core.callback.RetrofitBuilderCallback;
import com.simple.net.converter.GsonConverterFactory;

import retrofit2.Retrofit;

/**
 * 动态进行Retrofit.Builder属性设置
 * @author Created by woodys on 2020/6/16.
 * @email yuetao.315@qq.com
 */
public class SimpleRetrofitBuilderCallback implements RetrofitBuilderCallback {
    @Override
    public void apply(Retrofit.Builder builder) {
        builder.baseUrl("https://stupad-hotfix.xk12.cn/")
                .addConverterFactory(GsonConverterFactory.create());
    }
}
