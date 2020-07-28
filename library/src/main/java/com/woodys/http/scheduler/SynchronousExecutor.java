package com.woodys.http.scheduler;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import java.util.concurrent.Executor;

/**
 * An {@link Executor} that runs its commands right away on the current thread.
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class SynchronousExecutor implements Executor {

    private static final SynchronousExecutor DEFAULT = new SynchronousExecutor();

    private SynchronousExecutor() { }

    public static SynchronousExecutor get() {
        return DEFAULT;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        command.run();
    }
}
