package com.woodys.http.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.woodys.http.core.callback.CommonParamsCallback;
import com.woodys.http.core.callback.DynamicBaseUrlCallback;
import com.woodys.http.core.callback.OkHttpClientBuilderCallback;
import com.woodys.http.core.callback.RetrofitBuilderCallback;
import com.woodys.http.core.download.DownloadCallAdapterFactory;
import com.woodys.http.core.factory.ReplaceUrlCallFactory;
import com.woodys.http.core.interceptor.FullLoggingInterceptor;
import com.woodys.http.core.interceptor.HttpAddCommonArgsInterceptor;
import com.woodys.http.core.interceptor.MockInterceptor;
import com.woodys.http.utils.Preconditions;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Retrofit的增强核心类
 *
 * @author Created by woodys on 2020/6/05.
 * @email yuetao.315@qq.com
 */
public final class RetrofitWrapper {
    private static final String TAG = RetrofitWrapper.class.getSimpleName();

    /**
     *  保存配置信息
     */
    private ConfigBuilder configBuilder;
    private RetrofitWrapper(@NonNull ConfigBuilder builder){
        this.configBuilder = builder;
    }

    public  <T> T create(Class<T> service) {
        Retrofit retrofit = configBuilder.retrofit;
        Preconditions.checkState(retrofit != null, "retrofit没有初始化化，请查看是否RetrofitWrapperManager#init");
        return retrofit.create(service);
    }

    /**
     * 配置信息类
     */
    public static class ConfigBuilder {
        /**
         * 动态进行BaseUrl修改的Callback
         */
        private DynamicBaseUrlCallback dynamicBaseUrlCallback;
        /**
         * 进行公共参数设定，其中包含公共请求头，公共url拼接参数，公共请求参数（需要自己进行post，get,put等区分）
         */
        private CommonParamsCallback commonParamsCallback;
        /**
         * 对OkHttpClient.Builder进行自定义配置，提供的更多的扩展功能
         */
        private OkHttpClientBuilderCallback okHttpClientBuilderCallback;
        /**
         * 对Retrofit.Builder进行自定义配置，提供的更多的扩展功能
         */
        private RetrofitBuilderCallback retrofitBuilderCallback;
        private Retrofit retrofit;

        public ConfigBuilder(){ }

        public ConfigBuilder(ConfigBuilder builder) {
            this.dynamicBaseUrlCallback = builder.dynamicBaseUrlCallback;
            this.commonParamsCallback = builder.commonParamsCallback;
            this.okHttpClientBuilderCallback = builder.okHttpClientBuilderCallback;
            this.retrofitBuilderCallback = builder.retrofitBuilderCallback;
            this.retrofit = builder.retrofit;
        }

        /**
         * 设置动态替换BaseUrl的回调函数
         * @param dynamicBaseUrlCallback
         * @return
         */
        public ConfigBuilder setDynamicBaseUrlCallback(DynamicBaseUrlCallback dynamicBaseUrlCallback) {
            this.dynamicBaseUrlCallback = dynamicBaseUrlCallback;
            return this;
        }

        /**
         * 设置公共请求头、公共参数的回调函数
         * @param commonParamsCallback
         * @return
         */
        public ConfigBuilder setCommonParamsCallback(CommonParamsCallback commonParamsCallback) {
            this.commonParamsCallback = commonParamsCallback;
            return this;
        }

        /**
         * 设置OkHttpClient.Builder进行自定义配置的回调函数
         * @param okHttpClientBuilderCallback
         * @return
         */
        public ConfigBuilder setOkHttpClientBuilderCallback(OkHttpClientBuilderCallback okHttpClientBuilderCallback) {
            this.okHttpClientBuilderCallback = okHttpClientBuilderCallback;
            return this;
        }

        /**
         * 设置Retrofit.Builder进行自定义配置的回调函数
         * @param retrofitBuilderCallback
         * @return
         */
        public ConfigBuilder setRetrofitBuilderCallback(RetrofitBuilderCallback retrofitBuilderCallback) {
            this.retrofitBuilderCallback = retrofitBuilderCallback;
            return this;
        }

        @Override
        public ConfigBuilder clone(){
            return new ConfigBuilder(this);
        }

        public RetrofitWrapper build(){
            //初始化okHttpClientBuilder对象
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(new MockInterceptor())
                    .addInterceptor(new HttpAddCommonArgsInterceptor(commonParamsCallback))
                    .addNetworkInterceptor(new FullLoggingInterceptor(HttpLoggingInterceptor.Level.BODY));
            //okHttpClientBuilder对外提供扩展能力
            if (okHttpClientBuilderCallback!=null){
                okHttpClientBuilderCallback.apply(okHttpClientBuilder);
            }

            OkHttpClient okHttpClient = okHttpClientBuilder.build();
            Preconditions.checkNotNull(okHttpClient, "okHttpClient==null");

            //初始化retrofitBuilder对象
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .client(okHttpClient)
                    .callFactory(new ReplaceUrlCallFactory(okHttpClient) {
                        @Nullable
                        @Override
                        protected HttpUrl getNewUrl(String baseUrlName, Request request) {
                            return dynamicBaseUrlCallback!=null ? dynamicBaseUrlCallback.transform(baseUrlName,request):null;
                        }
                    })
                    .addCallAdapterFactory(CallAdapterFactory.INSTANCE)
                    .addCallAdapterFactory(DownloadCallAdapterFactory.INSTANCE);

            if (retrofitBuilderCallback!=null){
                retrofitBuilderCallback.apply(retrofitBuilder);
            }
            this.retrofit = retrofitBuilder.build();
            Preconditions.checkNotNull( this.retrofit, "retrofit==null");
            return new RetrofitWrapper(this);
        }

    }
}
