package com.simple.net.interceptor

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.IOException
import java.nio.charset.Charset
import java.util.zip.GZIPOutputStream

/**
 * OkHttp拦截器，判断是否需要压缩请求body并压缩
 * @author Created by woodys on 2020-03-03.
 * @email yuetao.315@qq.com
 */
class HttpGzipInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val key = "needGzip"

        val needGzip: String? = request.header(key)
        if (!TextUtils.isEmpty(needGzip) && needGzip == "true") {

            var charset: Charset? = Charset.forName("UTF-8")
            val contentType = request.body()?.contentType()
            if (contentType != null) {
                charset = contentType.charset(Charset.forName("UTF-8"))
            }
            val buffer = Buffer()
            request.body()?.writeTo(buffer)
            var requestBodyStr = charset?.let { buffer.readString(it) }

            var newRequestBody = RequestBody.create(
                MediaType.parse("charset=utf-8"),
                requestBodyStr?.toByteArray()?.let { compress(it) }!!
            )

            val newBuilder = request.newBuilder()
            newBuilder.removeHeader(key)
            var newRequest = newBuilder.method(request.method(), newRequestBody).build()

            return chain.proceed(newRequest)
        } else {
            return chain.proceed(request)
        }
    }

    /**
     * GZIP 压缩
     *
     * @param srcData 期望压缩的原始数据
     * @return 压缩后的数据; 或返回null
     */
    private fun compress(srcData: ByteArray): ByteArray? {
        var byteOutStream: ByteArrayOutputStream? = null
        var byteInStream: ByteArrayInputStream? = null
        var gos: GZIPOutputStream? = null
        try {
            byteOutStream = ByteArrayOutputStream()
            gos = GZIPOutputStream(byteOutStream)
            byteInStream = ByteArrayInputStream(srcData)
            var count: Int = 0
            val data = ByteArray(4096)
            while ({ count = byteInStream.read(data);count }() != -1) {
                gos.write(data, 0, count)
            }

            gos.finish()
            return byteOutStream.toByteArray()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        } finally {
            closeStream(byteOutStream)
            closeStream(byteInStream)
            closeStream(gos)
        }
        return null
    }

    private fun closeStream(stream: Closeable?) {
        if (stream != null) {
            try {
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}