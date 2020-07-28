package com.woodys.http.scheduler;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Default Task Executor for executing common tasks in WorkManager
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class WorkTaskExecutor implements TaskExecutor {

    private SerialExecutor mBackgroundExecutor;

    private static final WorkTaskExecutor DEFAULT = new WorkTaskExecutor();

    public static WorkTaskExecutor get() {
        return DEFAULT;
    }

    private WorkTaskExecutor() {
        setBackgroundExecutor(null);
    }

    public WorkTaskExecutor setBackgroundExecutor(Executor backgroundExecutor) {
        if (backgroundExecutor == null) {
            backgroundExecutor = createDefaultExecutor();
        }
        // Wrap it with a serial executor so we have ordering guarantees on commands
        // being executed.
        this.mBackgroundExecutor = new SerialExecutor(backgroundExecutor);
        return this;
    }

    /**
     * 默认创建线程池
     * @return
     */
    public static  @NonNull Executor createDefaultExecutor() {
        // This value is the same as the core pool size for AsyncTask#THREAD_POOL_EXECUTOR.
        int nThreads = Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() - 1, 4));
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    private final Executor mMainThreadExecutor = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            postToMainThread(command);
        }
    };

    @Override
    public void postToMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            mMainThreadHandler.post(runnable);
        }
    }

    public void postToMainThreadDelayed(@NonNull Runnable runnable, long delayMillis) {
        if (isMainThread()) {
            runnable.run();
        } else {
            mMainThreadHandler.postDelayed(runnable, delayMillis);
        }
    }

    @Override
    public Executor getMainThreadExecutor() {
        return mMainThreadExecutor;
    }

    @Override
    public void executeOnBackgroundThread(Runnable runnable) {
        mBackgroundExecutor.execute(runnable);
    }

    @Override
    @NonNull
    public SerialExecutor getBackgroundExecutor() {
        return mBackgroundExecutor;
    }

    private boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}

