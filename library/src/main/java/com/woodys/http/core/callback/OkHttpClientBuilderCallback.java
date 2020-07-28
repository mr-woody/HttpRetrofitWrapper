package com.woodys.http.core.callback;

import okhttp3.OkHttpClient;

/**
 * 动态进行OkHttpClient.Builder属性设置
 * @author Created by woodys on 2020/6/08.
 * @email yuetao.315@qq.com
 */
public interface OkHttpClientBuilderCallback {
    /**
     * 对builder进行自定义配置，提供的扩展功能
     * @param builder
     * @return
     */
    void apply(OkHttpClient.Builder builder);
}
