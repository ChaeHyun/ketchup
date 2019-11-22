package com.ketchup.di;


import android.app.Application;
import android.content.Context;


import com.ketchup.KetchupApplication;
import com.ketchup.model.AppDatabase;
import com.ketchup.model.task.TaskDao;
import com.ketchup.model.task.TaskRepository;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AppModule.class, RoomModule.class, BroadcastReceiverModule.class,
        AndroidSupportInjectionModule.class,
        ActivityBinder.class})
public interface AppComponent extends AndroidInjector<KetchupApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application app);

        AppComponent build();
    }

    Context context();
    AppDatabase appDatabase();
    TaskDao taskDao();
    TaskRepository taskRepository();

}
