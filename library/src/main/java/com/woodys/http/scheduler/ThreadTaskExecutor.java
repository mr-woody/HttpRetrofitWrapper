package com.woodys.http.scheduler;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.woodys.http.lifecycle.AndroidLifecycle;
import com.woodys.http.lifecycle.LifecycleProvider;

/**
 * 绑定生命周期，安全的切换到UI线程
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
public final class ThreadTaskExecutor implements LifecycleProvider.Observer {

    private final LifecycleProvider provider;
    @Nullable
    private volatile Lifecycle.Event mEvent;

    public static ThreadTaskExecutor create(LifecycleOwner owner) {
        return new ThreadTaskExecutor(owner);
    }

    private ThreadTaskExecutor(LifecycleOwner owner) {
        this.provider = createLifecycleProvider(owner);
        provider.observe(this);
    }

    @MainThread
    public static LifecycleProvider createLifecycleProvider(LifecycleOwner owner) {
        return new AndroidLifecycle(owner);
    }


    public void post(@NonNull Runnable action, Lifecycle.Event event) {
        postDelayed(action, event, 0);
    }

    /**
     * @param action the action to run on the UI thread
     * @param event  if {@code mEvent==event}, action will not be invoked
     */
    public void postDelayed(@NonNull final Runnable action, final Lifecycle.Event event, long delayMillis) {
        WorkTaskExecutor.get().postToMainThreadDelayed(new Runnable() {
            @Override
            public void run() {
                if (mEvent == event || mEvent == Lifecycle.Event.ON_DESTROY) {
                    return;
                }
                action.run();
            }
        }, delayMillis);
    }

    @Override
    public void onChanged(@NonNull Lifecycle.Event event) {
        this.mEvent = event;
        if (event == Lifecycle.Event.ON_DESTROY) {
            provider.removeObserver(this);
        }
    }
}
