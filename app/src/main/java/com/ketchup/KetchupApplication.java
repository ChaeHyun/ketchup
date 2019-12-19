package com.ketchup;


import androidx.work.Configuration;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.ketchup.di.DaggerAppComponent;
import com.ketchup.model.task.TaskRepository;
import com.ketchup.worker.DaggerWorkerFactory;
import com.ketchup.worker.DailyAlarmRegisterWorker;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import timber.log.Timber;


public class KetchupApplication extends DaggerApplication {

    @Inject
    DaggerWorkerFactory factory;

    @Inject
    TaskRepository taskRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        configureWorkManager();
        PeriodicWorkRequest testPeriodicWorkRequest = new PeriodicWorkRequest.Builder(
                DailyAlarmRegisterWorker.class, 12, TimeUnit.HOURS, 1, TimeUnit.HOURS)
                .addTag("DailyAlarmRegister")
                .build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueueUniquePeriodicWork("DailyWorker", ExistingPeriodicWorkPolicy.KEEP, testPeriodicWorkRequest);
        workManager.cancelUniqueWork("DailyWorker");

    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder()
                .application(this)
                .build();
    }

    private void configureWorkManager() {
        Timber.d("Let WorkManager(Android Framework) use this factory(DaggerWorkerFactory) to initialize Workers(ex.TestWorker) that I customized.");
        Configuration config = new Configuration.Builder()
                .setWorkerFactory(factory)
                .build();

        WorkManager.initialize(this, config);
    }
}
