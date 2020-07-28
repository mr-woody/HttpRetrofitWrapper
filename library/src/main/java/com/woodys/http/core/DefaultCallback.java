package com.woodys.http.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.woodys.http.core.exception.HttpError;
import com.woodys.http.log.Debugger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 *
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
public abstract class DefaultCallback<T> implements Callback<T> {
    private final String TAG = getClass().getSimpleName();

    @NonNull
    @Override
    public HttpError parseThrowable(@Nullable Call<T> call, Throwable t) {
        if (t instanceof HttpError) {
            return (HttpError) t;
        } else if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            final String msg;
            switch (httpException.code()) {
                case 400:
                    msg = "参数错误";
                    break;
                case 401:
                    msg = "身份未授权";
                    break;
                case 403:
                    msg = "禁止访问";
                    break;
                case 404:
                    msg = "地址未找到";
                    break;
                default:
                    msg = "服务异常";
            }
            return new HttpError(msg, httpException);
        } else if (t instanceof UnknownHostException) {
            return new HttpError("网络异常", t);
        } else if (t instanceof ConnectException) {
            return new HttpError("网络异常", t);
        } else if (t instanceof SocketException) {
            return new HttpError("服务异常", t);
        } else if (t instanceof SocketTimeoutException) {
            return new HttpError("响应超时", t);
        } else {
            return new HttpError("请求失败", t);
        }
    }

    @NonNull
    @Override
    public T transform(@Nullable Call<T> call, T t) {
        return t;
    }

    @Override
    public void onCompleted(@Nullable Call<T> call, @Nullable Throwable t) {
        if (t != null) {
            Debugger.w(TAG, "onCompleted-->\n" + getStackTraceString(t));
        }
    }

    private  String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "tr==null";
        }
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}