package com.simple

import android.app.Application
import com.woodys.http.RetrofitWrapperManager
import com.woodys.http.core.RetrofitWrapper
import com.woodys.http.utils.ContextHolder
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.simple.net.impls.*


public class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ContextHolder.init(this)
        //网络库初始化
        initNetWork()
    }

    fun initNetWork(){
        //日志打印初始化
        Logger.addLogAdapter(object : AndroidLogAdapter(PrettyFormatStrategy
                .newBuilder()
                .tag("RetrofitWrapper")
                .methodCount(1).showThreadInfo(false).build()) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })


        RetrofitWrapperManager.get()
                .setDebugEnable(BuildConfig.DEBUG)
                .setEnableMock(false)
                .setLogDelegate(SimpleLogDelegate())
                .setConfigBuilder(RetrofitWrapper.ConfigBuilder()
                        .setDynamicBaseUrlCallback(SimpleDynamicBaseUrlCallback())
                        .setCommonParamsCallback(SimpleCommonParamsCallback())
                        .setOkHttpClientBuilderCallback(SimpleOkHttpClientBuilderCallback())
                        .setRetrofitBuilderCallback(SimpleRetrofitBuilderCallback()))
                .init()
    }
}
