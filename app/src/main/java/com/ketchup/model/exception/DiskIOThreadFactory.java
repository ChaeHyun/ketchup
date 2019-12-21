package com.ketchup.model.exception;

import java.util.concurrent.ThreadFactory;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;


public class DiskIOThreadFactory implements ThreadFactory {
    private static final String THREAD_NAME = "DISK_IO";
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    @Inject
    public DiskIOThreadFactory(@Named("diskIO") Thread.UncaughtExceptionHandler exceptionHandler) {
        Timber.d("TestThreadFactory 생성");
        this.uncaughtExceptionHandler = exceptionHandler;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, THREAD_NAME);
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);

        return thread;
    }
}
