package com.ketchup.model.task;


import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

public interface TaskRepository {
    // Methods to open to the TaskListViewModel
    List<Task> getAllTasks();

    Task getTask(UUID uuid);
    List<Task> getTasks(String title);

    List<Task> getTasksCompleted(boolean completed);

    void insertTask(Task task);
    void insertTasks(List<Task> tasks);

    void updateTask(Task task);

    void deleteTask(UUID uuid);
    void deleteAllTask();

    List<Task> getTasksInCertainPeriod(int flag);

    //test
    Future<List<Task>> getAllTaskAsync() ;

}
