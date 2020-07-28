package com.simple.ui

import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.widget.Toast
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.lzy.imagepicker.ui.ImageGridActivity
import com.woodys.http.RetrofitWrapperManager
import com.woodys.http.core.exception.HttpError
import com.woodys.http.core.request.ProgressRequestBody
import com.woodys.http.scheduler.ThreadTaskExecutor
import com.simple.BaseSampleAppCompatActivity
import com.simple.R
import com.simple.net.callback.AnimCallback
import com.simple.net.config.HttpHeadUtil
import com.simple.net.protocol.UploadApiService
import com.simple.utils.GlideImageLoader
import com.woodys.http.core.Call
import kotlinx.android.synthetic.main.activity_upload_http_view.*
import kotlinx.android.synthetic.main.activity_upload_http_view.downloadSize
import kotlinx.android.synthetic.main.activity_upload_http_view.tvProgress
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class UploadHttpActivity : BaseSampleAppCompatActivity() {
    private var imageItems: ArrayList<ImageItem> = ArrayList<ImageItem>()
    private var numberFormat: NumberFormat? = null
    override fun onActivityCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_upload_http_view)

        numberFormat = NumberFormat.getPercentInstance()
        numberFormat?.minimumFractionDigits = 2

        formUpload.setOnClickListener {
            formUpload();
        }

        selectImage.setOnClickListener {
            selectImage();
        }
    }

    fun formUpload() {

        val uid: String? = ""
        val token: String? = ""

        val map = HashMap<String, String?>()
        map["type"] = "file"
        map["uid"] = uid
        map["token"] = token

        val fileMap = HashMap<String, File>()
        if (imageItems != null && imageItems.size > 0) {
            for (i in imageItems.indices) {
                fileMap["avatar"] = File(imageItems[i].path)
            }
        }
        uploadAvatar(map,fileMap)
    }


    fun selectImage() {
        val imagePicker = ImagePicker.getInstance()
        imagePicker.imageLoader = GlideImageLoader()
        imagePicker.isMultiMode = false //多选
        imagePicker.isShowCamera = true //显示拍照按钮
        imagePicker.selectLimit = 1 //最多选择9张
        imagePicker.isCrop = false //不进行裁剪
        val intent = Intent(this, ImageGridActivity::class.java)
        startActivityForResult(intent, 100)
    }

    fun uploadAvatar(
            params: Map<String, String?>,
            files: Map<String, File>
    ) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)//表单类型
        for (key in files.keys) {
            files[key]?.let {
                val imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), it)
                builder.addFormDataPart(key, it.name, object: ProgressRequestBody(imageBody){
                    override fun onUpload(progress: Long, contentLength: Long, done: Boolean) {
                        Log.e("uploadAvatar", "onUpload:progress=$progress,contentLength=$contentLength,done=$done")

                        ThreadTaskExecutor.create(this@UploadHttpActivity).post({
                            val downloadLength = Formatter.formatFileSize(applicationContext, progress)
                            val totalLength = Formatter.formatFileSize(applicationContext, contentLength)
                            //下载的进度，0-1
                            val fraction = progress * 1.0f / contentLength

                            downloadSize.setText("$downloadLength/$totalLength")
                            tvProgress.setText(numberFormat?.format(fraction))
                            progressView.setMax(100)
                            progressView.setProgress((fraction * 100).toInt())
                        }, Lifecycle.Event.ON_DESTROY)
                    }
                })
            }
        }
        for (key in params.keys) {
            params[key]?.let {
                builder.addFormDataPart(key, it)
            }
        }
        val parts = builder.build().parts()

        RetrofitWrapperManager.create(UploadApiService::class.java)
                .uploadAvatar(HttpHeadUtil.fileHeadMap(), parts)
                .enqueue(object: AnimCallback<ResponseBody>(this) {
                    override fun onSuccess(call: Call<ResponseBody>, response: Response<ResponseBody>, t: ResponseBody) {
                        handleResponse(response,t)
                    }

                    override fun onError(call: Call<ResponseBody>, error: HttpError) {
                        handleError(call.request(),error)
                    }
                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                imageItems = data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as ArrayList<ImageItem>
                if (imageItems != null && imageItems.size > 0) {
                    val sb = StringBuilder()
                    for (i in imageItems.indices) {
                        if (i == imageItems.size - 1) sb.append("图片").append(i + 1).append(" ： ").append(imageItems.get(i).path) else sb.append("图片").append(i + 1).append(" ： ").append(imageItems.get(i).path).append("\n")
                    }
                    images.setText(sb.toString())
                } else {
                    images.setText("--")
                }
            } else {
                Toast.makeText(this, "没有选择图片", Toast.LENGTH_SHORT).show()
                images.setText("--")
            }
        }
    }

}
