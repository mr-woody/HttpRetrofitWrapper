package com.simple.net.impls;

import com.woodys.http.core.callback.OkHttpClientBuilderCallback;
import com.woodys.http.utils.HttpsUtils;
import com.simple.net.interceptor.HttpGzipInterceptor;
import com.simple.net.interceptor.HttpLogInterceptor;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/**
 * 动态进行OkHttpClient.Builder属性设置
 * @author Created by woodys on 2020/6/16.
 * @email yuetao.315@qq.com
 */
public class SimpleOkHttpClientBuilderCallback implements OkHttpClientBuilderCallback {
    //默认的超时时间
    private final static long DEFAULT_MILLISECONDS = 10 * 1000L;

    @Override
    public void apply(OkHttpClient.Builder builder) {
        builder.addInterceptor(new HttpLogInterceptor(new SimpleHttpLog()));
        builder.addInterceptor(new HttpGzipInterceptor());

        //超时时间设置，默认60秒
        //全局的读取超时时间
        builder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.retryOnConnectionFailure(false);

        // https相关设置，
        // 信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        // 配置https的域名匹配规则，全部放行（不安全有风险）
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }
}
