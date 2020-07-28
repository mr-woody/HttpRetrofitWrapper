package com.woodys.http.core.download;

import android.support.annotation.NonNull;
import android.util.Log;

import com.woodys.http.core.response.ProgressResponseBody;
import com.woodys.http.utils.Preconditions;

import java.util.concurrent.Executor;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public final class RealDownloadCall<T> implements DownloadCall<T> {
    private final String TAG = getClass().getSimpleName();
    private final Executor callbackExecutor;
    private final retrofit2.Call<ResponseBody> delegate;
    //default false
    private volatile boolean pauseProgress;

    public RealDownloadCall(Executor callbackExecutor, retrofit2.Call<ResponseBody> delegate) {
        this.callbackExecutor = callbackExecutor;
        this.delegate = delegate;
    }

    @Override
    public void enqueue(DownloadCallback<T> callback) {
        enqueue(0.01f, callback);
    }

    @Override
    public void enqueue(final float increaseOfProgress, final DownloadCallback<T> callback) {
        Preconditions.checkNotNull(callback, "callback==null");
        callbackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onStart(RealDownloadCall.this);
            }
        });
        delegate.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean signalledCallback = false;
                try {
                    if (response.body() == null) {
                        signalledCallback = true;
                        callFailure(new HttpException(response));
                        return;
                    }
                    ResponseBody responseBody = new ProgressResponseBody(response.body()) {
                        long lastProgress;

                        @Override
                        protected void onDownload(long progress, long contentLength, boolean done) {
                            if (pauseProgress) {
                                return;
                            }
                            if (progress - lastProgress > increaseOfProgress * contentLength || done) {
                                lastProgress = progress;
                                callProgress(progress, contentLength, done);
                            }
                        }
                    };
                    T body = callback.convert(RealDownloadCall.this, responseBody);
                    signalledCallback = true;
                    if (body != null) {
                        callSuccess(response,body);
                    } else {
                        callFailure(new NullPointerException("callback.convert return null"));
                    }
                } catch (Throwable t) {
                    //如果为OptionalExecutor，不存在线程调度的情况下callXXX方法可能会在当前线程抛出异常
                    //会导致callFailure方法调用两次
                    //参考okhttp3.RealCall
                    if (!signalledCallback) {
                        callFailure(t);
                    } else {
                        Log.w(TAG, "Callback failure", t);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                callFailure(t);
            }

            private void callProgress(final long progress, final long contentLength, final boolean done) {
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onProgress(RealDownloadCall.this, progress, contentLength, done);
                    }
                });
            }

            private void callSuccess(final Response response,@NonNull final T body) {
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(RealDownloadCall.this,response,body);
                    }
                });
            }

            private void callFailure(@NonNull final Throwable t) {
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(RealDownloadCall.this, t);
                    }
                });
            }
        });
    }

    @Override
    public void pauseProgress() {
        pauseProgress = true;
    }

    @Override
    public void resumeProgress() {
        pauseProgress = false;
    }

    @Override
    public boolean isExecuted() {
        return delegate.isExecuted();
    }

    @Override
    public void cancel() {
        delegate.cancel();
    }

    @Override
    public boolean isCanceled() {
        return delegate.isCanceled();
    }

    @Override
    public Request request() {
        return delegate.request();
    }

    @Override
    public DownloadCall<T> clone() {
        return new RealDownloadCall<>(callbackExecutor, delegate.clone());
    }
}