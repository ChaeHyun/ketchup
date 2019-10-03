package com.ketchup.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ketchup.AddEditTaskViewModel;
import com.ketchup.DaggerViewModelFactory;
import com.ketchup.tasklist.TaskListViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(DaggerViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(TaskListViewModel.class)
    abstract ViewModel bindTaskViewModel(TaskListViewModel taskListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AddEditTaskViewModel.class)
    abstract ViewModel bindAddEditTaskViewModel(AddEditTaskViewModel addEditTaskViewModel);
}
