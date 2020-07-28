package com.simple

import android.app.Application
import com.woodys.http.RetrofitWrapperManager
import com.woodys.http.core.RetrofitWrapper
import com.woodys.http.utils.ContextHolder
import com.okay.sampletamplate.configurtion.TemplateConfiguration
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.simple.ui.*
import com.simple.net.impls.*


public class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ContextHolder.init(this)
        //网络库初始化
        initNetWork()

        TemplateConfiguration.init(this){
            category {
                title = "常规请求调用方式示例"
                desc = "常规请求(公共请求头和参数)、同步请求、Mock演示"
                item {
                    title = "常规请求(公共请求头和参数)"
                    desc = "演示常规的post、get(配合公共请求头和参数)"
                    clazz = GeneralHttpActivity::class.java
                }
                item {
                    title = "同步请求"
                    desc = "会阻塞主线程，需要在子线程中进行创建"
                    clazz = SyncHttpActivity::class.java
                }
                item {
                    title = "Mock演示"
                    desc = "Mock数据的使用方式"
                    clazz = MockHttpActivity::class.java
                }
            }

            category {
                title = "通用下载文件操作示例"
                desc = "演示下载相关实例"
                item {
                    title = "下载文件操作"
                    desc = "演示文件下载过程"
                    clazz = DownloadHttpActivity::class.java
                }
                item {
                    title = "下载图片操作"
                    desc = "演示图片下载过程"
                    clazz = DownloadImageActivity::class.java
                }
            }


            category {
                title = "通用上传文件操作示例"
                desc = "演示项目中头像上传和第三方图片上传"
                item {
                    title = "上传文件操作(头像上传)"
                    desc = "演示文件上传过程"
                    clazz = UploadHttpActivity::class.java
                }
                item {
                    title = "上传图片操作"
                    desc = "演示图片上传过程"
                    clazz = UploadImageActivity::class.java
                }
            }

            item {
                title = "支持模块化配置"
                desc = "支持创建多个RetrofitWrapper，并且可以自定义ConfigBuilder"
                clazz =  ModuleHttpActivity::class.java
            }

        }
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
