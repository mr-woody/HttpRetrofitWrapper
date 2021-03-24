package com.simple.ui

import android.os.Bundle
import com.cz.android.sample.api.RefCategory
import com.cz.android.sample.api.RefRegister
import com.woodys.http.RetrofitWrapperManager
import com.woodys.http.core.Call
import com.woodys.http.core.callback.CommonParamsCallback
import com.woodys.http.core.exception.HttpError
import com.simple.BaseSampleAppCompatActivity
import com.simple.R
import com.simple.net.callback.AnimCallback
import com.simple.net.protocol.CommonApiService
import kotlinx.android.synthetic.main.activity_moudle_http_view.*
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Response

@RefCategory(title=R.string.other,desc = R.string.other_desc, priority = 4)
@RefRegister(title=R.string.custom_module_sample,desc=R.string.custom_module_sample_desc,category = R.string.other,priority = 1)
class ModuleHttpActivity : BaseSampleAppCompatActivity() {
    override fun onActivityCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_moudle_http_view)
        moduleHttp.setOnClickListener {
            getRequestNetWork()
        }

    }

    fun getRequestNetWork() {
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
    }


}
