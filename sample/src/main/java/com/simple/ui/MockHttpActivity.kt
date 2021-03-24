package com.simple.ui

import android.os.Bundle
import com.cz.android.sample.api.RefRegister
import com.woodys.http.RetrofitWrapperManager
import com.woodys.http.core.Call
import com.woodys.http.core.exception.HttpError
import com.simple.BaseSampleAppCompatActivity
import com.simple.R
import com.simple.net.callback.AnimCallback
import com.simple.net.protocol.MockApiService
import kotlinx.android.synthetic.main.activity_mock_http_view.*
import okhttp3.ResponseBody
import retrofit2.Response

@RefRegister(title=R.string.general_mock_sample,desc=R.string.general_mock_sample_desc,category = R.string.general_category,priority = 3)
class MockHttpActivity : BaseSampleAppCompatActivity() {
    override fun onActivityCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_mock_http_view)
        setMockModeValue()

        closeMockSupport.setOnClickListener {
            RetrofitWrapperManager.get().setEnableMock(false)
            setMockModeValue()
        }

        openMockSupport.setOnClickListener {
            RetrofitWrapperManager.get().setEnableMock(true)
            setMockModeValue()
        }

        getMockByUrl.setOnClickListener {
            getRequestNetWorkByMock1()
        }
        getMockByValue.setOnClickListener {
            getRequestNetWorkByMock2()
        }
        getMockByAssets.setOnClickListener {
            getRequestNetWorkByMock3()
        }
    }

    fun setMockModeValue(){
        val enableMock = RetrofitWrapperManager.isEnableMock()
        mockModeValue.text = if(enableMock){"Mock数据模式状态：开启"} else {"Mock数据模式状态：关闭"}
    }


    fun getRequestNetWorkByMock1() {
        RetrofitWrapperManager.create(MockApiService::class.java)
                .getNetWorkDataFormGirl1()
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

    fun getRequestNetWorkByMock2() {
        RetrofitWrapperManager.create(MockApiService::class.java)
                .getNetWorkDataFormGirl2()
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

    fun getRequestNetWorkByMock3() {
        RetrofitWrapperManager.create(MockApiService::class.java)
                .getNetWorkDataFormGirl3()
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
