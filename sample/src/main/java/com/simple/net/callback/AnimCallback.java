package com.simple.net.callback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonSyntaxException;
import com.woodys.http.core.Call;
import com.woodys.http.core.DefaultCallback;
import com.woodys.http.core.exception.DisposedException;
import com.woodys.http.core.exception.HttpError;

/**
 * Created by yeutao on 2017/9/24.
 */
public abstract class AnimCallback<T> extends DefaultCallback<T> {
    private ILoadingView mLoadingView;

    public AnimCallback(@Nullable ILoadingView loadingView) {
        this.mLoadingView = loadingView;
    }

    @Override
    public void onStart(Call<T> call) {
        if (mLoadingView != null) {
            mLoadingView.showLoading();
        }
    }

    @Override
    public void onCompleted(@Nullable Call<T> call, @Nullable Throwable t) {
        super.onCompleted(call, t);
        if (DisposedException.isDestroyed(t)) {
            return;
        }
        if (mLoadingView != null) {
            mLoadingView.hideLoading();
        }
    }

    @NonNull
    @Override
    public HttpError parseThrowable(Call<T> call, Throwable t) {
        HttpError filterError;
        if (t instanceof JsonSyntaxException) {
            filterError = new HttpError("解析异常", t);
        } else {
            filterError = super.parseThrowable(call, t);
        }
        return filterError;
    }
}
