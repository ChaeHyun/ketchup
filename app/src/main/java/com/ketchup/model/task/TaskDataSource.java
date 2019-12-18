package com.ketchup.model.task;


import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import javax.inject.Inject;

import timber.log.Timber;

public class TaskDataSource implements TaskRepository {

    TaskDao dao;

    @Inject
    public TaskDataSource(TaskDao taskDao) {
        Timber.v("Constructor[TaskDataSource] : TaskDao is provided to TaskDataSource.");
        this.dao = taskDao;
    }

    @Override
    public List<Task> getAllTasks() {
        return dao.getAllTasks();
    }

    @Override
    public Task getTask(UUID uuid) {
        return dao.getTask(uuid.toString());
    }

    @Override
    public List<Task> getTasks(String title) {
        return dao.getTasksByTitle(title);
    }

    @Override
    public List<Task> getTasksCompleted(boolean completed) {
        return dao.getTasksCompleted(completed);
    }

    @Override
    public void insertTask(Task task) {
        dao.insertTask(task);
    }

    @Override
    public void insertTasks(List<Task> tasks) {
        dao.insertAllTasks(tasks);
    }

    @Override
    public void updateTask(Task task) {
        dao.updateTask(task);
    }

    @Override
    public void deleteTask(UUID uuid) {
        dao.deleteTask(uuid.toString());
    }

    @Override
    public void deleteAllTask() {
        dao.deleteAllTasks();
    }

    @Override
    public List<Task> getTasksInCertainPeriod(DateGroup dateGroup) {
        switch (dateGroup) {
            case TODAY:
                return dao.getTasksDueDateIsToday();
            case UPCOMING:
                return dao.getTasksDueDateIsFuture();
            case OVERDUE:
                return dao.getTasksDueDateIsPast();
            case NOTE:
                return dao.getTasksDueDateIsNull();
            case TOMORROW:
                return dao.getTasksDueDateIsTomorrow();
        }
        return null;
    }

    // Test : ViewModel이 아니라 Repository 에서 Thread 처리를 깔끔하게 하기 위해 Callable 을 사용할 경우
    @Override
    public Future<List<Task>> getAllTaskAsync() {
        Timber.v("** [Async in Repo] getAllTasks2()");
        ExecutorService pool = Executors.newSingleThreadExecutor();

        return pool.submit(() -> dao.getAllTasks());
    }


}
