package com.ketchup.di;

import com.ketchup.ActionReceiver;
import com.ketchup.RegisteredAlarmReceiver;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class BroadcastReceiverModule {

    @ContributesAndroidInjector
    abstract RegisteredAlarmReceiver contributesRegisteredAlarmReceiver();

    @ContributesAndroidInjector
    abstract ActionReceiver contributesActionReceiver();

}
