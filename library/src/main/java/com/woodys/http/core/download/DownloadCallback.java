package com.woodys.http.core.download;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.woodys.http.core.Callback;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 监听下载进度 {@link Callback}
 * @param <T> Successful response body type.
 *
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
public interface DownloadCallback<T> {
    /** 请求网络开始前，UI线程 */
    void onStart(DownloadCall<T> call);

    @Nullable
    @WorkerThread
    T convert(DownloadCall<T> call, ResponseBody value) throws IOException;

    /** 下载中进度回调，get请求不回调，UI线程 */
    void onProgress(DownloadCall<T> call, long progress, long contentLength, boolean done);

    /** 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程 */
    void onError(DownloadCall<T> call, Throwable t);

    /** 对返回数据进行操作的回调， UI线程 */
    void onSuccess(DownloadCall<T> call, Response<T> response, T t);
}