package com.woodys.http;

import android.support.annotation.NonNull;
import android.util.Log;

import com.woodys.http.core.RetrofitWrapper;
import com.woodys.http.log.Debugger;
import com.woodys.http.log.LogDelegate;
import com.woodys.http.utils.Preconditions;

/**
 * 管理全局的Retrofit实例,外观模式
 *
 * @author Created by woodys on 2020/6/05.
 * @email yuetao.315@qq.com
 */
public final class RetrofitWrapperManager {

    private static volatile RetrofitWrapperManager singleton;
    /**
     * 全局配置信息保留
     */
    private final Builder builder = new Builder();

    /**
     * 获取全局RetrofitWrapper对象
     */
    private RetrofitWrapper retrofitWrapper = null;

    private RetrofitWrapperManager() { }

    public static RetrofitWrapperManager get() {
        if (singleton == null) {
            synchronized (RetrofitWrapperManager.class) {
                if (singleton == null) {
                    singleton = new RetrofitWrapperManager();
                }
            }
        }
        return singleton;
    }

    /**
     * Log开关。建议测试环境开启，线上环境应该关闭。
     * @param isEnable
     */
    public RetrofitWrapperManager setDebugEnable(boolean isEnable) {
        this.builder.setDebugEnable(isEnable);
        return this;
    }

    /**
     * Log扩展接口，方便做日志输出定制（不设置，默认使用DefaultLogDelegate）
     * @param delegate
     */
    public RetrofitWrapperManager setLogDelegate(@NonNull LogDelegate delegate) {
        this.builder.setLogDelegate(delegate);
        return this;
    }

    public static boolean isDebugEnable() {
        return get().builder.isDebugEnable;
    }

    /**
     * 设置是否支持mock数据模式
     * @param enableMock
     * @return
     */
    public RetrofitWrapperManager setEnableMock(boolean enableMock)  {
        this.builder.setEnableMock(enableMock);
        return this;
    }

    /**
     * 获取当前是否支持mock数据模式，默认false不开启，需要配合注解Mock进行使用
     * @return
     */
    public static boolean isEnableMock() {
        return get().builder.isEnableMock;
    }

    /**
     * 设置RetrofitWrapper.ConfigBuilder相关的配置信息
     * @param configBuilder
     * @return
     */
    public RetrofitWrapperManager setConfigBuilder(@NonNull RetrofitWrapper.ConfigBuilder configBuilder)  {
        this.builder.setConfigBuilder(configBuilder);
        return this;
    }

    /**
     * 用于获取单个新的RetrofitWrapper..ConfigBuilder,方便进行单个RetrofitWrapper对象获取
     * @return
     */
    public static RetrofitWrapper.ConfigBuilder getNewConfigBuilder() {
        RetrofitWrapper.ConfigBuilder configBuilder = get().builder.configBuilder;
        Preconditions.checkNotNull(configBuilder, "configBuilder==null");
        return  configBuilder.clone();
    }

    /**
     * 全局初始化操作，建议在Application#onCreate方法中进行
     */
    public void init() {
        synchronized (RetrofitWrapperManager.class) {
            RetrofitWrapper.ConfigBuilder configBuilder = this.builder.configBuilder;
            Preconditions.checkNotNull(configBuilder, "configBuilder==null,请检查是否设置RetrofitWrapperManager#setConfigBuilder");
            this.retrofitWrapper = this.builder.build();
        }
    }


    /**
     * 全局进行Retrofit创建Service对象，方便进行对于的网络操作
     * @param service
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> service) {
        RetrofitWrapper retrofitWrapper = get().retrofitWrapper;
        Preconditions.checkNotNull(retrofitWrapper, "retrofitWrapper==null,请检查是否调用RetrofitWrapperManager#init");
        return retrofitWrapper.create(service);
    }


    /**
     * 全局配置信息Builder类，方便进行参数管理
     */
    public static final class Builder {
        // 是否开启调试模式
        private boolean isDebugEnable;
        // 是否开始Mock模式
        private boolean isEnableMock;
        // 获取全局配置
        private RetrofitWrapper.ConfigBuilder configBuilder;
        // log输出回调
        private LogDelegate logDelegate;

        public Builder() {
            isDebugEnable = false;
            isEnableMock = false;
        }

        public Builder(Builder builder) {
            this.isDebugEnable = builder.isDebugEnable;
            this.isEnableMock = builder.isEnableMock;
            this.configBuilder = builder.configBuilder;
            this.logDelegate = builder.logDelegate;
        }


        public Builder setDebugEnable(boolean debugEnable) {
            isDebugEnable = debugEnable;
            return this;
        }


        public Builder setEnableMock(boolean enableMock) {
            isEnableMock = enableMock;
            return this;
        }


        public Builder setLogDelegate(LogDelegate logDelegate) {
            this.logDelegate = logDelegate;
            return this;
        }

        public Builder setConfigBuilder(RetrofitWrapper.ConfigBuilder configBuilder) {
            this.configBuilder = configBuilder;
            return this;
        }

        @Override
        public Builder clone(){
            return new Builder(this);
        }


        public RetrofitWrapper build() {
            Debugger.setEnableLog(isDebugEnable);
            Debugger.setLogLevel(Log.DEBUG);
            if(logDelegate != null){
                Debugger.setLogDelegate(logDelegate);
            }
            Preconditions.checkNotNull(configBuilder, "configBuilder==null");
            return configBuilder.build();
        }
    }
}
