package com.woodys.http.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.woodys.http.core.Call;
import com.woodys.http.core.Callback;
import com.woodys.http.core.exception.DisposedException;
import com.woodys.http.core.exception.HttpError;
import com.woodys.http.utils.Preconditions;

import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;

public final class RealLifeCall<T> implements LifeCall<T> {
    private final String TAG = getClass().getSimpleName();
    private final Call<T> delegate;
    private final Lifecycle.Event event;
    private final LifecycleProvider provider;
    /**
     * LifeCall是否被释放了
     * like rxAndroid MainThreadDisposable or rxJava ObservableUnsubscribeOn, IoScheduler
     */
    private final AtomicBoolean once = new AtomicBoolean();
    /**
     * 保存最后一次生命周期事件
     */
    private volatile Lifecycle.Event lastEvent;

    public RealLifeCall(Call<T> delegate, Lifecycle.Event event, LifecycleProvider provider) {
        this.delegate = delegate;
        this.event = event;
        this.provider = provider;
        provider.observe(this);
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        Preconditions.checkNotNull(callback, "callback==null");
        delegate.enqueue(new Callback<T>() {
            @Override
            public void onStart(Call<T> call) {
                if (!isDisposed()) {
                    callback.onStart(call);
                }
            }

            @NonNull
            @Override
            public HttpError parseThrowable(Call<T> call, Throwable t) {
                if (!isDisposed()) {
                    return callback.parseThrowable(call, t);
                }
                return new HttpError("Already disposed.", t);
            }

            @NonNull
            @Override
            public T transform(Call<T> call, T t) {
                if (!isDisposed()) {
                    return callback.transform(call, t);
                }
                return t;
            }

            @Override
            public void onSuccess(Call<T> call, Response<T> response, T t) {
                if (!isDisposed()) {
                    callback.onSuccess(call,response, t);
                }
            }

            @Override
            public void onError(Call<T> call, HttpError error) {
                if (!isDisposed()) {
                    callback.onError(call, error);
                }
            }

            @Override
            public void onCompleted(Call<T> call, @Nullable Throwable t) {
                //like okhttp RealCall#timeoutExit
                callback.onCompleted(call, isDisposed() ? new DisposedException(lastEvent, t) : t);
                provider.removeObserver(RealLifeCall.this);
            }
        });
    }

    @NonNull
    @Override
    public T execute() throws Throwable {
        try {
            if (isDisposed()) {
                throw new DisposedException(lastEvent);
            }
            T body = delegate.execute();
            if (isDisposed()) {
                throw new DisposedException(lastEvent);
            }
            return body;
        } catch (Throwable t) {
            if (isDisposed() && !(t instanceof DisposedException)) {
                throw new DisposedException(lastEvent, t);
            }
            throw t;
        } finally {
            provider.removeObserver(this);
        }
    }

    @Override
    public void onChanged(@NonNull Lifecycle.Event event) {
        if (event != Lifecycle.Event.ON_ANY) {
            lastEvent = event;
        }
        if (this.event == event
                || event == Lifecycle.Event.ON_DESTROY
                //Activity和Fragment的生命周期是不会传入 {@code Lifecycle.Event.ON_ANY},
                //可以手动调用此方法传入 {@code Lifecycle.Event.ON_ANY},用于区分是否为手动调用
                || event == Lifecycle.Event.ON_ANY) {
            /*保证原子性*/
            if (once.compareAndSet(false, true)) {
                delegate.cancel();
                Log.d(TAG, "disposed by-->" + event + ", " + delegate.request());
            }
        }
    }

    @Override
    public boolean isDisposed() {
        return once.get();
    }
}