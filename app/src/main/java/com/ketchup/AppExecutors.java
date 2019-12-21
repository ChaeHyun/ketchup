package com.ketchup;

import android.os.Handler;
import android.os.Looper;

import com.ketchup.model.exception.DiskIOThreadFactory;
import com.ketchup.model.exception.DiskUncaughtExceptionHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class AppExecutors {
    private final ExecutorService diskIO;
    private final Executor mainThread;


    @Inject
    public AppExecutors(DiskIOThreadFactory threadFactory) {
        Timber.d("[ AppExecutors is created. ] ");
        this.diskIO = Executors.newSingleThreadExecutor(threadFactory);
        this.mainThread = new MainThreadExecutor();
    }

    public ExecutorService diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private class MainThreadExecutor implements Executor {
        Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }
}
