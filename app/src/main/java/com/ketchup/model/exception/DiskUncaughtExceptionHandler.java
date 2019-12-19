package com.ketchup.model.exception;

import timber.log.Timber;


// 각기 다른 UncaughtExceptionHandle을 위해서 여러개가 생성될 수 있다.
public class DiskUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final static String handlerName = "ExceptionHandler : DISK_IO";

    public DiskUncaughtExceptionHandler() {
        Timber.d("Test Uncaught Exception Handler 생성");
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Timber.d("[%s] in Thread(%s)\n  ==> msg : %s", handlerName, t.getName(), e.getMessage());
        if (e.getClass().getSimpleName().equals(RuntimeException.class.getSimpleName())) {
            Timber.d("This exception is [RuntimeException].");
        }
    }

}
