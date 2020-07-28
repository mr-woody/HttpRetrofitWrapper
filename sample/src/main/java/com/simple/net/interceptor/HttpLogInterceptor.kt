package com.simple.net.interceptor

import android.util.Log
import com.woodys.http.utils.IOUtils
import com.simple.net.IHttpLog
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import java.nio.charset.Charset


/**
 * 网络日志拦截器，方便进行日志信息显示
 * @author Created by woodys on 2020-03-03.
 * @email yuetao.315@qq.com
 */
class HttpLogInterceptor (private val mHttpLog: IHttpLog) : Interceptor {

    val TAG_OKAYNET = "okaynet"
    val TAG_OKAYNET_HEADER = "okayHeader"

    private val UTF8 = Charset.forName("UTF-8")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val headers = request.headers()
        val requestBody = request.body()

        val headerStr = headers.toString().replace("\n", "  ")
        var bodyStr = "requestBody:"
        if (requestBody == null) {
            bodyStr += "null"
        } else {
            var buffer = Buffer()
            requestBody.writeTo(buffer)

            var charset = Charset.forName("UTF-8")
            var contentType: MediaType? = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(Charset.forName("UTF-8"))
            }
            bodyStr += buffer.readString(charset)
        }

        val requestId = request.header("requestid")
        val requestUrl = request.url()
        mHttpLog.d(TAG_OKAYNET, "${request.url()} 上行requestid=$requestId body: $bodyStr")

        mHttpLog.d(TAG_OKAYNET_HEADER, "${request.url()} 上行requestid=$requestId header: $headerStr")

        var startMs = System.currentTimeMillis()
        try {
            var response = chain.proceed(request)
            var tookMs = System.currentTimeMillis() - startMs


            val responseHeader = response.headers()
            var responseHeaderStr = responseHeader.toString().replace("\n", "  ")

            var responseBody: ResponseBody? = response.body()
            var responseBodyStr = ""

            if (HttpHeaders.hasBody(response)) {
                if (responseBody == null) return response
                if (isPlaintext(responseBody.contentType())) {
                    val bytes = IOUtils.toByteArray(responseBody.byteStream())
                    val contentType = responseBody.contentType()
                    responseBodyStr = String(bytes, getCharset(contentType))
                    responseBody = ResponseBody.create(responseBody.contentType(), bytes)
                    response = response.newBuilder().body(responseBody).build()
                } else {
                    responseBodyStr = "\tbody: maybe [binary body], omitted!"
                }
            }

            mHttpLog.d(
                TAG_OKAYNET,
                "${response.request().url()} tookMs=$tookMs 下行：requestid=$requestId  body: $responseBodyStr"
            )

            mHttpLog.d(
                TAG_OKAYNET_HEADER,
                "${response.request().url()} tookMs=$tookMs 下行: requestid=$requestId header：$responseHeaderStr"
            )

            return response
        } catch (t: Throwable) {
            mHttpLog.d(
                TAG_OKAYNET,
                "$requestUrl 下行requestid=$requestId exception: $t cause: ${t.cause} stackTrace: ${Log.getStackTraceString(
                    t
                )}"
            )
            throw t
        }
    }

    private fun getCharset(contentType: MediaType?): Charset {
        return (if (contentType != null) contentType.charset(UTF8) else UTF8) ?: UTF8
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private fun isPlaintext(mediaType: MediaType?): Boolean {
        if (mediaType == null) return false
        if (mediaType.type() != null && mediaType.type() == "text") {
            return true
        }
        var subtype: String? = mediaType.subtype()
        if (subtype != null) {
            subtype = subtype.toLowerCase()
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html"))
            //
                return true
        }
        return false
    }
}