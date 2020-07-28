package com.woodys.http.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.GuardedBy;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.ArrayList;

/**
 * 实现LifecycleObserver监听Activity和Fragment的生命周期
 * It is thread safe.
 *
 * @author woodys
 * @see android.database.Observable
 */
public final class AndroidLifecycle implements LifecycleProvider, LifecycleObserver {
    private final String TAG = getClass().getSimpleName();
    private final Object mLock = new Object();

    @GuardedBy("mLock")
    private final ArrayList<Observer> mObservers = new ArrayList<>();
    /**
     * 缓存当前的Event事件
     */
    @GuardedBy("mLock")
    @Nullable
    private Lifecycle.Event mEvent;

    @MainThread
    public static LifecycleProvider createLifecycleProvider(LifecycleOwner owner) {
        return new AndroidLifecycle(owner);
    }

    public AndroidLifecycle(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onEvent(LifecycleOwner owner, Lifecycle.Event event) {
        synchronized (mLock) {
            //保证线程的可见性
            mEvent = event;
            // since onChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged(event);
            }
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            owner.getLifecycle().removeObserver(this);
        }
    }

    @Override
    public void observe(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        synchronized (mLock) {
            if (mObservers.contains(observer)) {
                return;
            }
            mObservers.add(observer);
            Log.d(TAG, "onCountChanged-->old:" + (mObservers.size() - 1) + ", new:" + mObservers.size() + ", provider:" + this);
            // since onChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just call onChanged() after {@code mObservers.add(observer)}
            if (mEvent != null) {
                observer.onChanged(mEvent);
            }
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        synchronized (mLock) {
            int index = mObservers.indexOf(observer);
            if (index == -1) {
                return;
            }
            mObservers.remove(index);
            Log.d(TAG, "onCountChanged-->old:" + (mObservers.size() + 1) + ", new:" + mObservers.size() + ", provider:" + this);
        }
    }

    @Override
    public String toString() {
        return "AndroidLifecycle@" + Integer.toHexString(hashCode());
    }
}
