package com.ketchup.di;

import com.ketchup.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBinder {

    // AppComponent 의 SubComponent 를 생성한다.
    @ActivityScope
    @ContributesAndroidInjector(modules = { ViewModelModule.class })
    abstract MainActivity bindMainActivity();


}
