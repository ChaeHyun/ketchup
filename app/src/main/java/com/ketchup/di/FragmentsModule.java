package com.ketchup.di;

import com.ketchup.TaskListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract public class FragmentsModule {
    @ContributesAndroidInjector
    abstract TaskListFragment contributesTaskListFragment();
}
