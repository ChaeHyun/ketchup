package com.ketchup.model.task;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTasks(List<Task> tasks);

    @Query("DELETE FROM task WHERE uuid = :uuid")
    void deleteTask(@NonNull String uuid);

    @Update
    void updateTask(@NonNull Task task);

    @Query("DELETE FROM task ")
    void deleteAllTasks();

    @Query("SELECT * FROM task")
    List<Task> getAllTasks();

    @Query("SELECT * FROM task WHERE uuid = :uuid")
    Task getTask(String uuid);

    @Query("SELECT * FROM task WHERE title = :title")
    List<Task> getTasksByTitle(String title);

    @Query("SELECT * FROM task WHERE completed = :completed")
    List<Task> getTasksCompleted(boolean completed);

    @Query("SELECT * FROM task WHERE date(dueDate / 1000 , 'unixepoch', 'localtime') < date('now', 'localtime')")
    List<Task> getTasksDueDateIsPast();

    @Query("SELECT * FROM task WHERE date(dueDate / 1000 , 'unixepoch', 'localtime') = date('now', 'localtime')")
    List<Task> getTasksDueDateIsToday();

    @Query("SELECT * FROM task WHERE date(dueDate / 1000 , 'unixepoch', 'localtime') = date('now', '+1 day', 'localtime')")
    List<Task> getTasksDueDateIsTomorrow();

    @Query("SELECT * FROM task WHERE date(dueDate / 1000 , 'unixepoch', 'localtime') >= date('now', '+1 day', 'localtime')")
    List<Task> getTasksDueDateIsFuture();

    @Query("SELECT * FROM task WHERE dueDate IS NULL")
    List<Task> getTasksDueDateIsNull();


    // Control CategoryWithTasks
    // where categoryId == personal의 하면 or
    // where categoryName = :nameOfCategory
    // 특정 카테고리에 속한 Task List를 결과물로 얻을 수 있다.
//    @Query("SELECT * FROM category")
//    List<CategoryWithTasks> getCategoryWithTasks();
}
