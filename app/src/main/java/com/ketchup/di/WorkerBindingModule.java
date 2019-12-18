package com.ketchup.di;

import com.ketchup.worker.ChildWorkerFactory;
import com.ketchup.worker.DailyAlarmRegisterWorker;
import com.ketchup.worker.TestWorker;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(TestWorker.class)
    ChildWorkerFactory bindTestWorker(TestWorker.Factory factory);

    @Binds
    @IntoMap
    @WorkerKey(DailyAlarmRegisterWorker.class)
    ChildWorkerFactory bindDailyAlarmRegisterWorker(DailyAlarmRegisterWorker.Factory factory);
}
