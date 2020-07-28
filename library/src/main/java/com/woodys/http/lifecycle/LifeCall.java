package com.woodys.http.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.woodys.http.core.Callable;

/**
 * 创建时间：2018/4/8
 * 功能描述：实现生命周期绑定的Call{@link retrofit2.Call}
 * @author woodys
 */
public interface LifeCall<T> extends Callable<T>, LifecycleProvider.Observer {

    /**
     * Returns true if this call has been disposed.
     *
     * @return true if this call has been disposed
     */
    boolean isDisposed();

    /**
     * The method may be called concurrently from multiple
     * threads; the method must be thread safe. Calling this method multiple
     * times has no effect.
     * <p>
     * like {@code Observable#doOnDispose(Action)},{@code SingleSubject#onSuccess(Object)}
     * <p>
     * you can invoke with {@link Lifecycle.Event#ON_ANY} to dispose from outside immediately.
     */
    @Override
    void onChanged(@NonNull Lifecycle.Event event);
}
