package com.ketchup.di;

import com.ketchup.AddEditTaskFragment;
import com.ketchup.tasklist.TaskListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract public class FragmentsModule {
    @ContributesAndroidInjector
    abstract TaskListFragment contributesTaskListFragment();

    @ContributesAndroidInjector
    abstract AddEditTaskFragment contributesAddEditTaskFragment();
}
