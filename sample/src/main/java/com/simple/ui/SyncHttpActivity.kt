package com.simple.ui

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.cz.android.sample.api.RefRegister
import com.woodys.http.RetrofitWrapperManager
import com.woodys.http.scheduler.ThreadTaskExecutor
import com.simple.R
import com.simple.net.protocol.CommonApiService
import kotlinx.android.synthetic.main.activity_synchro_http_view.*

@RefRegister(title=R.string.general_sync_sample,desc=R.string.general_sync_sample_desc,category = R.string.general_category,priority = 2)
class SyncHttpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchro_http_view)
        sync.setOnClickListener {
            syncRequestNetWork()
        }
    }

    fun syncRequestNetWork(){
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
    }

}
