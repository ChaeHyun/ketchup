package com.ketchup.di;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ketchup.AppExecutors;
import com.ketchup.model.AppDatabase;
import com.ketchup.model.category.Category;
import com.ketchup.model.category.CategoryDao;
import com.ketchup.model.category.CategoryDataSource;
import com.ketchup.model.category.CategoryRepository;
import com.ketchup.model.category.CategoryTaskDao;
import com.ketchup.model.task.DummyTask;
import com.ketchup.model.task.TaskDao;
import com.ketchup.model.task.TaskDataSource;
import com.ketchup.model.task.TaskRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

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
                .addCallback(addDefaultCategoriesAtCreation())
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
    public CategoryRepository providesCategoryRepository(CategoryDao categoryDao, CategoryTaskDao relationDao, AppExecutors appExecutors) {
        Timber.v("[ CategoryRepository ] is provided.");
        return new CategoryDataSource(categoryDao, relationDao, appExecutors);
    }

    private RoomDatabase.Callback addDefaultCategoriesAtCreation() {
        Timber.v("[ Add Default Categories ]");
        RoomDatabase.Callback callbackAddCategories = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                Timber.v("[ ROOM.Callback : This should be run only once at DB creation. ]");

                Executors.newSingleThreadExecutor().execute(() -> {
                    Category categoryUncompleted = new Category(UUID.randomUUID().toString(), "uncompleted");
                    Category categoryCompleted = new Category(UUID.randomUUID().toString(), "completed");

                    List<Category> defaultCategories = Arrays.asList(categoryUncompleted, categoryCompleted);
                    getAppDatabaseInstance().getCategoryDao().insertAllCategory(defaultCategories);
                });
            }
        };

        return callbackAddCategories;
    }

    /* About the entity: [CategoryTaskCrossRef] */
    @Singleton
    @Provides
    public CategoryTaskDao provideCategoryTaskDao(AppDatabase appDatabase) {
        return appDatabase.getCategoryTaskDao();
    }
}
