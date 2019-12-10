package com.ketchup.di;

import com.ketchup.ChildWorkerFactory;
import com.ketchup.TestWorker;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(TestWorker.class)
    ChildWorkerFactory bindTestWorker(TestWorker.Factory factory);
}
