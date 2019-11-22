package com.ketchup.di;

import android.app.Application;
import android.content.Context;
import android.util.Log;


import com.ketchup.utils.AlarmUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module
public class AppModule {

    @Provides
    Context provideContext(Application application) {
        Timber.v("[ AppModule ] ApplicationContext is provided. ");
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    AlarmUtils provideAlarmUtils(Context context) {
        Timber.d("[ AlarmUtils is provided. ]");
        return new AlarmUtils(context);
    }

}
