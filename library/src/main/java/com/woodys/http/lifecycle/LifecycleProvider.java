package com.woodys.http.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

/**
 * 统一分发Activity和 Fragment的生命周期时间.
 * @author woodys
 */
public interface LifecycleProvider {
    /**
     * Adds an observer to the list. The observer cannot be null and it must not already
     * be registered.
     *
     * @param observer the observer to register
     * @throws IllegalArgumentException the observer is null
     */
    void observe(Observer observer);

    /**
     * Removes a previously registered observer. The observer must not be null and it
     * must already have been registered.
     *
     * @param observer the observer to unregister
     * @throws IllegalArgumentException the observer is null
     */
    void removeObserver(Observer observer);

    /**
     * A simple callback that can receive from {@link android.arch.lifecycle.Lifecycle}
     */
    interface Observer {
        /**
         * Called when the event is changed.
         *
         * @param event The new event
         */
        void onChanged(@NonNull Lifecycle.Event event);
    }
}
