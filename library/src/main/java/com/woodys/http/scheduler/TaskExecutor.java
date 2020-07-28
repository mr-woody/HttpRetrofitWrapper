package com.woodys.http.scheduler;

import android.support.annotation.RestrictTo;

import java.util.concurrent.Executor;

/**
 * Interface for executing common tasks in WorkManager.
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface TaskExecutor {

    /**
     * @param runnable {@link Runnable} to post to the main thread
     */
    void postToMainThread(Runnable runnable);

    /**
     * @return The {@link Executor} for main thread task processing
     */
    Executor getMainThreadExecutor();

    /**
     * @param runnable {@link Runnable} to execute on a background thread pool
     */
    void executeOnBackgroundThread(Runnable runnable);

    /**
     * @return The {@link SerialExecutor} for background task processing
     */
    SerialExecutor getBackgroundExecutor();
}
