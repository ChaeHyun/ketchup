package com.ketchup.di;

import android.app.Application;
import android.content.Context;
import android.util.Log;


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


}
