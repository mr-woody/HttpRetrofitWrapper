package com.simple.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import com.woodys.http.RetrofitWrapperManager
import com.woodys.http.core.Call
import com.woodys.http.core.exception.HttpError
import com.simple.BaseSampleAppCompatActivity
import com.simple.R
import com.simple.net.callback.AnimCallback
import com.simple.net.protocol.DownloadApiService
import kotlinx.android.synthetic.main.activity_download_image_view.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.InputStream


class DownloadImageActivity : BaseSampleAppCompatActivity() {
    override fun onActivityCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_download_image_view)

        requestImage.setOnClickListener {
            requestImageByGirl();
        }
    }


    fun requestImageByGirl() {
        RetrofitWrapperManager.create(DownloadApiService::class.java)
                .requestImageByGirl()
                .bind(this)
                .enqueue(object: AnimCallback<ResponseBody>(this) {
                    override fun onSuccess(call: Call<ResponseBody>, response: Response<ResponseBody>, t: ResponseBody) {
                        handleResponse(response,t)
                        val inputStream: InputStream = t.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        imageView.setImageBitmap(bitmap)
                    }

                    override fun onError(call: Call<ResponseBody>, error: HttpError) {
                        handleError(call.request(),error)
                    }
                })
    }

}
