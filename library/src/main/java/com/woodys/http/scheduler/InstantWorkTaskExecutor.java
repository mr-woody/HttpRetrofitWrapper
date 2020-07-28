package com.woodys.http.scheduler;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

/**
 * A TaskExecutor that runs instantly.
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
 class InstantWorkTaskExecutor implements TaskExecutor {

    private Executor mSynchronousExecutor =  SynchronousExecutor.get();
    private SerialExecutor mSerialExecutor = new SerialExecutor(mSynchronousExecutor);

    @NonNull
    Executor getSynchronousExecutor() {
        return mSynchronousExecutor;
    }

    @Override
    public void postToMainThread(Runnable runnable) {
        runnable.run();
    }


    @Override
    public Executor getMainThreadExecutor() {
        return mSynchronousExecutor;
    }

    @Override
    public void executeOnBackgroundThread(Runnable runnable) {
        mSerialExecutor.execute(runnable);
    }

    @Override
    public SerialExecutor getBackgroundExecutor() {
        return mSerialExecutor;
    }
}
