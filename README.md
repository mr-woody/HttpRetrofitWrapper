# HttpRetrofitWrapper for Android 对Retrofit网络库进行功能增强的一个组件库

## 功能

 1. 支持模块化，不单单是单例一种方式
 2. 完全支持Retrofit规范，不破坏其框架特性（不提供快捷方法支持post、get、put、delete等，个人感觉不合理）
 3. 支持公共请求头、通过参数的统一加入，通过回调函数可以进行更灵活的功能定制
 4. 支持将http请求和Activity或Fragment的生命周期相结合，不要担心内存泄漏问题
 5. 支持动态进行BaseUrl的提供，通过回调函数可以有更多扩展，并且可以支持多个BaseUrl修改的能力
 6. 支持自定义Mock数据，方便Mock数据进行测试
 7. 支持文件下载和上传
 8. 支持同步和异步处理
 9. 支持网络Log日志打印功能
 10. 支持对返回结果的统一处理

## 特色
 
 1. 使用灵活，可以应用到大部分场景，只需要熟悉Retrofit即可
 2. 支持Lifecycle,让你不用关注其生命周期
 3. 无缝结合Retrofit框架，进行统一回调管理
 4. 不使用RxJava进行数据转换处理，主要是RxJava接入需要成本，会产生重度依赖关系（除了Retrofit和OkHttp3必须依赖，其他三方库需要尽量避免再依赖）

## 缺点

 * 基于Retrofit框架进行开发，所以受限于Retrofit

### 使用库注意事项
 * [Todo](/document/todo.md)


### 示例介绍
1. 常规请求调用方式示例
    - 常规请求(公共请求头和参数)
    - 同步请求
    - Mock演示
2. 通用下载文件操作示例
    - 下载文件操作
    - 下载图片操作
3. 通用上传文件操作示例
    - 上传文件操作(头像上传)
    - 上传图片操作
4. 支持模块化配置


### 演示下载
[*Sample Apk*](/apk/sample-debug.apk)


### 如何接入


##### 1. 在全局build里添加仓库

```
allprojects {
    repositories {
        ......
        maven { url 'https://jitpack.io' }
    }
}
```

##### 2. (Application或library Module)在app的build里添加依赖

```
dependencies {
    ......
    implementation "com.github.mr-woody:HttpRetrofitWrapper:1.0.0"
}

```


###  如何使用

##### 1. 在Application的onCreate方法中初始化

```
    RetrofitWrapperManager.get()
                // 设置是否开启debug模式
                .setDebugEnable(BuildConfig.DEBUG)
                // 是否支持mock模式
                .setEnableMock(BuildConfig.DEBUG)
                // 对DebugEnable为true时，设置日志输出方式
                .setLogDelegate(new SimpleLogDelegate())
                // 设置Retrofit和OkHttp3相关的配置信息
                .setConfigBuilder(new RetrofitWrapper.ConfigBuilder()
                        // 动态BaseUrl配置的回调
                        .setDynamicBaseUrlCallback(new SimpleDynamicBaseUrlCallback() )
                        // 公共请求头、公共参数配置的回调
                        .setCommonParamsCallback(new SimpleCommonParamsCallback())
                        // OkHttp3扩展配置的回调
                        .setOkHttpClientBuilderCallback(new SimpleOkHttpClientBuilderCallback())
                        // Retrofit扩展配置的回调
                        .setRetrofitBuilderCallback(new SimpleRetrofitBuilderCallback()))
                // 用于配置初始化
                .init();

```

##### 2. HttpRetrofitWrapper对外提供功能的接口和实现

 1. 全局使用方式
 
 ```
  RetrofitWrapperManager.create(CommonApiService::class.java)
                  .getNetWorkDataFormGirl()
                  .bind(this)
                  .enqueue(object: AnimCallback<ResponseBody>(this) {
                      override fun onSuccess(call: Call<ResponseBody>, response: Response<ResponseBody>, t: ResponseBody) {
                          handleResponse(response,t)
                      }
  
                      override fun onError(call: Call<ResponseBody>, error: HttpError) {
                          handleError(call.request(),error)
                      }
                  })
 ```
 
 2. 获取单个对象的方式
 
 ```

        //获取单个retrofitWrapper对象，进行网络操作（可以基于全局配置进行修改对于参数配置）
        val retrofitWrapper = RetrofitWrapperManager.getNewConfigBuilder()
                .setCommonParamsCallback(object : CommonParamsCallback {
                    override fun addCommonParams(originalRequest: Request?, requestBuilder: Request.Builder?) {}

                    override fun addHeader(builder: Request.Builder) {
                        builder.addHeader("key1","test1")
                        builder.addHeader("key2","test2")
                        builder.addHeader("key3","test3")
                    }

                    override fun addQueryParams(originalRequest: Request?, httpUrlBuilder: HttpUrl.Builder?) {}

                })
                .build()

        retrofitWrapper.create(CommonApiService::class.java)
                .getNetWorkDataFormGirl()
                .bind(this)
                .enqueue(object: AnimCallback<ResponseBody>(this) {
                    override fun onSuccess(call: Call<ResponseBody>, response: Response<ResponseBody>, t: ResponseBody) {
                        handleResponse(response,t)
                    }

                    override fun onError(call: Call<ResponseBody>, error: HttpError) {
                        handleError(call.request(),error)
                    }
                })
 ```
 
 3. 同步操作
 ```
    Thread(object : Runnable {
        override fun run() {
            try {
                //同步会阻塞主线程，必须开线程
                val responseBody = RetrofitWrapperManager.create(CommonApiService::class.java)
                        .getNetWorkDataFormGank()
                        .bind(this@SyncHttpActivity)
                        //不传callback即为同步请求
                        .execute()
                ThreadTaskExecutor.create(this@SyncHttpActivity).post( {
                    Toast.makeText(this@SyncHttpActivity, responseBody.string(), Toast.LENGTH_SHORT).show();
                }, Lifecycle.Event.ON_STOP)
            } catch (e: Exception) {
                //异常处理,这里只做简单的演示
                //你可以对throwable做解析分类
                ThreadTaskExecutor.create(this@SyncHttpActivity).post( {
                    Toast.makeText(this@SyncHttpActivity, e.localizedMessage, Toast.LENGTH_SHORT).show();
                }, Lifecycle.Event.ON_STOP)
            }
        }
    }).start()

 ```
 
 4. Mock数据的使用方式（如下三种不同的配置）
 
    先开启mock模式：
    RetrofitWrapperManager.get().setEnableMock(true)
  
  ```
    @Headers("BaseUrlName:girl")
    @Mock(url = "https://gank.io/api/v2/banners")
    @GET("https://gank.io/api/v2/categories/Girl")
    Call<ResponseBody> getNetWorkDataFormGirl1();

    @Headers("BaseUrlName:girl")
    @Mock(value = "{\"data\":[{\"coverImageUrl\":\"http://gank.io/images/30bc3da361ca47fcbe5bc945aae29aa9\",\"desc\":\"- \\u5ff5\\u5ff5\\u4e0d\\u5fd8\\uff0c\\u5fc5\\u6709\\u56de\\u54cd\",\"title\":\"\\u59b9\\u7eb8\",\"type\":\"Girl\"}]}")
    @GET("https://gank.io/api/v2/categories/Girl")
    Call<ResponseBody> getNetWorkDataFormGirl2();

    @Headers("BaseUrlName:girl")
    @Mock(assets = "mock/mock.json")
    @GET("https://gank.io/api/v2/categories/Girl")
    Call<ResponseBody> getNetWorkDataFormGirl3();
  
   ```
 
 5.  单独指定某个请求的日志级别 @Headers("LogLevel:NONE") 或 @Headers("LogLevel:BASIC") 或 @Headers("LogLevel:HEADERS") 或@Headers("LogLevel:BODY")

    ```java
    //  @Headers("LogLevel:NONE")
    //  @Headers("LogLevel:HEADERS")
    //  @Headers("LogLevel:BASIC")
        @Headers("LogLevel:BODY")
        @GET("https://gank.io/api/v2/banners")
        Call<ResponseBody> getNetWorkDataFormGank();
    ```


 
##### 3.技术难点思路描述

* [技术难点思路](/document/help.md)

    



