package com.ketchup.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ketchup.model.category.Category;
import com.ketchup.model.category.CategoryDao;
import com.ketchup.model.category.CategoryTaskCrossRef;
import com.ketchup.model.category.CategoryTaskDao;
import com.ketchup.model.task.Converters;
import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskDao;

@Database(entities = {Task.class, Category.class, CategoryTaskCrossRef.class}, version = AppDatabase.VERSION, exportSchema = false)
@TypeConverters({Converters.class})
abstract public class AppDatabase extends RoomDatabase {
    static final int VERSION = 1;

    // It exposes Dao interfaces to Repository.
    public abstract TaskDao getTaskDao();
    public abstract CategoryDao getCategoryDao();
    public abstract CategoryTaskDao getCategoryTaskDao();
}
