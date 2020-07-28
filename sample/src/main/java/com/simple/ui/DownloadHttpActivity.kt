package com.simple.ui

import android.os.Bundle
import android.text.format.Formatter
import android.widget.Toast
import com.woodys.http.RetrofitWrapperManager
import com.woodys.http.core.download.DownloadCall
import com.woodys.http.core.download.DownloadCallback
import com.woodys.http.utils.IOUtils
import com.simple.BaseSampleAppCompatActivity
import com.simple.R
import com.simple.net.protocol.DownloadApiService
import kotlinx.android.synthetic.main.activity_download_http_view.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.NumberFormat

class DownloadHttpActivity : BaseSampleAppCompatActivity() {
    private var numberFormat: NumberFormat? = null
    override fun onActivityCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_download_http_view)

        numberFormat = NumberFormat.getPercentInstance()
        numberFormat?.minimumFractionDigits = 2

        fileDownload.setOnClickListener {
            download();
        }
    }

    var call: DownloadCall<File>? = null

    fun download() {
        if (call != null) {
            call?.cancel()
            call = null
            fileDownload.text = "下载文件"
            return
        }
        fileDownload.text = "取消下载"
        val filePath: String = File(applicationContext.getExternalCacheDir(), "test_douyin.apk").getPath()
        RetrofitWrapperManager.create(DownloadApiService::class.java)
                .download("http://shouji.360tpcdn.com/181115/4dc46bd86bef036da927bc59680f514f/com.ss.android.ugc.aweme_330.apk")
                .enqueue(object : DownloadCallback<File> {

                    override fun onStart(call: DownloadCall<File>) {
                        fileDownload.text = "正在下载中"
                    }
                    @Throws(IOException::class)
                    override fun convert(call: DownloadCall<File>, value: ResponseBody): File? {
                        this@DownloadHttpActivity.call = call
                        return IOUtils.writeToFile(value, filePath)
                    }
                    override fun onProgress(call: DownloadCall<File>, progress: Long, contentLength: Long, done: Boolean) {

                        val downloadLength = Formatter.formatFileSize(applicationContext, progress)
                        val totalLength = Formatter.formatFileSize(applicationContext, contentLength)
                        //下载的进度，0-1
                        val fraction = progress * 1.0f / contentLength

                        downloadSize.setText("$downloadLength/$totalLength")
                        tvProgress.setText(numberFormat?.format(fraction))
                        progressView.setMax(100)
                        progressView.setProgress((fraction * 100).toInt())

                    }

                    override fun onError(call: DownloadCall<File>, t: Throwable) {
                        progressView.setProgress(0)
                        fileDownload.text = "下载出错"
                        Toast.makeText(this@DownloadHttpActivity, t.message, Toast.LENGTH_SHORT).show()
                        handleError(call?.request(),t)
                    }

                    override fun onSuccess(call: DownloadCall<File>, response: Response<File>?, file: File?) {
                        fileDownload.text = "下载完成"
                        handleResponse(response,file)
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        call?.resumeProgress()
    }

    override fun onPause() {
        super.onPause()
        call?.pauseProgress()
    }


}
