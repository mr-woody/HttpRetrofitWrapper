package com.woodys.http.core;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import com.woodys.http.lifecycle.LifeCall;

import okhttp3.Request;

/**
 * 支持生命周期绑定的Call{@link retrofit2.Call}
 * @param <T> Successful response body type.
 *
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
public interface Call<T> extends Callable<T>, Cloneable {

    boolean isExecuted();

    void cancel();

    boolean isCanceled();

    Call<T> clone();

    Request request();

    /**
     * 绑定生命周期
     *
     * @param owner LifecycleOwner
     * @param event {@link Lifecycle.Event}, {@link Lifecycle.Event#ON_ANY} is not allowed
     * @return LifeCall
     */
    LifeCall<T> bind(LifecycleOwner owner, Lifecycle.Event event);

    /**
     * default event is {@link Lifecycle.Event#ON_DESTROY}
     *
     * @param owner LifecycleOwner
     * @return LifeCall
     * @see #bind(LifecycleOwner, Lifecycle.Event)
     */
    LifeCall<T> bind(LifecycleOwner owner);
}
