package com.ketchup.model.exception;

import java.util.concurrent.ThreadFactory;

import timber.log.Timber;


// 이 팩토리는 UncaughtExceptonHandler를 set해주는 역할만 담당한다.
public class DiskIOThreadFactory implements ThreadFactory {
    private static final String THREAD_NAME = "DISK_IO";
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public DiskIOThreadFactory(Thread.UncaughtExceptionHandler exceptionHandler) {
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
