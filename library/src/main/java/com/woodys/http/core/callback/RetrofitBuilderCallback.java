package com.woodys.http.core.callback;

import retrofit2.Retrofit;

/**
 * 动态进行Retrofit.Builder属性设置
 * @author Created by woodys on 2020/6/08.
 * @email yuetao.315@qq.com
 */
public interface RetrofitBuilderCallback {
    /**
     * 对builder进行自定义配置，提供的扩展功能
     * @param builder
     * @return
     */
    void apply(Retrofit.Builder builder);
}
