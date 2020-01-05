package com.ketchup.di;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ketchup.AppExecutors;
import com.ketchup.model.AppDatabase;
import com.ketchup.model.CategoryDao;
import com.ketchup.model.CategoryDataSource;
import com.ketchup.model.CategoryRepository;
import com.ketchup.model.task.DummyTask;
import com.ketchup.model.task.TaskDao;
import com.ketchup.model.task.TaskDataSource;
import com.ketchup.model.task.TaskRepository;

import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module
public class RoomModule {
    private static final String DB_NAME = "DB_TEST.db";

    private AppDatabase appDatabase;

    @Singleton
    @Provides
    public AppDatabase provideAppDatabase(Context context) {
        Timber.v( " ** [DB CREATED] " + DB_NAME + " is initialized. **");
        Timber.v( "[ AppDatabase ] is provided. ");

        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                //.addCallback(populateDummyData())
                .build();

        return appDatabase;
    }

    /* About the entity: [Task]  */

    @Singleton
    @Provides
    public TaskDao provideTaskDao(AppDatabase appDatabase) {
        Timber.v("[ TaskDao ] is provided. ");
        return appDatabase.getTaskDao();
    }

    @Singleton
    @Provides
    public TaskRepository providesTaskRepository(TaskDao taskDao, AppExecutors appExecutors) {
        Timber.v("[ TaskRepository ] is provided. ");
        return new TaskDataSource(taskDao, appExecutors);
    }

    private AppDatabase getAppDatabaseInstance() {
        return appDatabase;
    }

    private RoomDatabase.Callback populateDummyData() {
        Timber.v("[ provide Callback ]");
        RoomDatabase.Callback populateDbCallback = new RoomDatabase.Callback() {
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        Timber.v( "[ ROOM.Callback : All DB data is wiped out. ]");
                        getAppDatabaseInstance().clearAllTables();
                        Timber.v("[ ROOM.Callback : Populate Dummy Data ]");
                        getAppDatabaseInstance().getTaskDao().insertAllTasks(new DummyTask().getDummyTaskDetail());
                    }
                });
            }
        };

        return populateDbCallback;
    }

    /* About the entity: [Category] */

    @Singleton
    @Provides
    public CategoryDao provideCategoryDao(AppDatabase appDatabase) {
        Timber.v("[ CategoryDao ] is provided. ");
        return appDatabase.getCategoryDao();
    }

    @Singleton
    @Provides
    public CategoryRepository providesCategoryRepository(CategoryDao categoryDao, AppExecutors appExecutors) {
        Timber.v("[ CategoryRepository ] is provided.");
        return new CategoryDataSource(categoryDao, appExecutors);
    }
}
