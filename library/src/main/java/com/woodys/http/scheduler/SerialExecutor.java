package com.woodys.http.scheduler;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

/**
 * A {@link Executor} which delegates to another {@link Executor} but ensures that tasks are
 * executed serially, like a single threaded executor.
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
public class SerialExecutor implements Executor {
    private final ArrayDeque<Task> mTasks;
    private final Executor mExecutor;
    private final Object mLock;
    private volatile Runnable mActive;

    public SerialExecutor(@NonNull Executor executor) {
        mExecutor = executor;
        mTasks = new ArrayDeque<>();
        mLock = new Object();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        synchronized (mLock) {
            mTasks.add(new Task(this, command));
            if (mActive == null) {
                scheduleNext();
            }
        }
    }

    void scheduleNext() {
        synchronized (mLock) {
            if ((mActive = mTasks.poll()) != null) {
                mExecutor.execute(mActive);
            }
        }
    }

    /**
     * @return {@code true} if there are tasks to execute in the queue.
     */
    public boolean hasPendingTasks() {
        synchronized (mLock) {
            return !mTasks.isEmpty();
        }
    }

    @NonNull
    @VisibleForTesting
    public Executor getDelegatedExecutor() {
        return mExecutor;
    }

    /**
     * A {@link Runnable} which tells the {@link SerialExecutor} to schedule the next command
     * after completion.
     */
    static class Task implements Runnable {
        final SerialExecutor mSerialExecutor;
        final Runnable mRunnable;

        Task(@NonNull SerialExecutor serialExecutor, @NonNull Runnable runnable) {
            mSerialExecutor = serialExecutor;
            mRunnable = runnable;
        }

        @Override
        public void run() {
            try {
                mRunnable.run();
            } finally {
                mSerialExecutor.scheduleNext();
            }
        }
    }
}

