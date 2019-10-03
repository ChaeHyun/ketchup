package com.ketchup.di;

import com.ketchup.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBinder {

    @ActivityScope
    @ContributesAndroidInjector(modules = { ViewModelModule.class, FragmentsModule.class })
    abstract MainActivity bindMainActivity();

}
