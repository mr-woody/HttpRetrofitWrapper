package com.woodys.http.core;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.woodys.http.core.exception.HttpError;
import com.woodys.http.lifecycle.AndroidLifecycle;
import com.woodys.http.lifecycle.LifeCall;
import com.woodys.http.lifecycle.RealLifeCall;
import com.woodys.http.utils.Preconditions;

import java.util.concurrent.Executor;

import okhttp3.Request;
import retrofit2.HttpException;
import retrofit2.Response;

final class RealCall<T> implements Call<T> {
    private final Executor callbackExecutor;
    private final retrofit2.Call<T> delegate;

    RealCall(Executor callbackExecutor, retrofit2.Call<T> delegate) {
        this.callbackExecutor = callbackExecutor;
        this.delegate = delegate;
    }

    @NonNull
    @Override
    public T execute() throws Throwable {
        Response<T> response = delegate.execute();
        T body = response.body();
        if (body != null) {
            return body;
        }
        throw new HttpException(response);
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        Preconditions.checkNotNull(callback, "callback==null");
        callbackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onStart(RealCall.this);
            }
        });
        delegate.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(retrofit2.Call<T> call, Response<T> response) {
                //response.isSuccessful() 不能保证 response.body() != null,反之可以
                if (response.body() != null) {
                    callSuccess(response,response.body());
                } else {
                    callFailure(new HttpException(response));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<T> call, Throwable t) {
                callFailure(t);
            }

            private void callSuccess(final Response<T> response,@NonNull final T body) {
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        T transformer = callback.transform(RealCall.this, body);
                        //noinspection ConstantConditions
                        Preconditions.checkNotNull(transformer == null, "transformer==null");
                        callback.onSuccess(RealCall.this,response, transformer);
                        callback.onCompleted(RealCall.this, null);
                    }
                });
            }

            private void callFailure(@NonNull final Throwable t) {
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        HttpError error = callback.parseThrowable(RealCall.this, t);
                        //noinspection ConstantConditions
                        Preconditions.checkNotNull(error == null, "error==null");
                        callback.onError(RealCall.this, error);
                        callback.onCompleted(RealCall.this, t);
                    }
                });
            }
        });
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

    @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
    @Override
    public Call<T> clone() {
        return new RealCall<>(callbackExecutor, delegate.clone());
    }

    @Override
    public Request request() {
        return delegate.request();
    }

    @Override
    public LifeCall<T> bind(LifecycleOwner owner, Lifecycle.Event event) {
        Preconditions.checkNotNull(owner, "owner==null");
        Preconditions.checkNotNull(event, "event==null");
        if (event == Lifecycle.Event.ON_ANY) {
            throw new IllegalArgumentException("ON_ANY event is not allowed.");
        }
        return new RealLifeCall<>(clone(), event, AndroidLifecycle.createLifecycleProvider(owner));
    }

    @Override
    public LifeCall<T> bind(LifecycleOwner owner) {
        return bind(owner, Lifecycle.Event.ON_DESTROY);
    }
}