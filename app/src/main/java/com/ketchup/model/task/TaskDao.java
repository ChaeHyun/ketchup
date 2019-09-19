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

    @Query("DELETE from task ")
    void deleteAllTasks();

    @Query("SELECT * from task")
    List<Task> getAllTasks();

    @Query("SELECT * from task WHERE uuid = :uuid")
    Task getTask(String uuid);

    @Query("SELECT * from task WHERE title = :title")
    List<Task> getTasksByTitle(String title);

    @Query("SELECT * from task WHERE completed = :completed")
    List<Task> getTasksCompleted(boolean completed);
}
