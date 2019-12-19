package com.ketchup.tasklist;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ketchup.AppExecutors;
import com.ketchup.di.ActivityScope;
import com.ketchup.model.task.DateGroup;
import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import timber.log.Timber;

@ActivityScope
public class TaskListViewModel extends ViewModel {
    private ExecutorService diskIO;
    private AppExecutors appExecutors;
    private TaskRepository taskRepository;

    private MutableLiveData<List<Task>> _tasks = new MutableLiveData<>();
    private MutableLiveData<Task> _task = new MutableLiveData<>();
    private MutableLiveData<Boolean> _loading = new MutableLiveData<>();

    private LiveData<List<Task>> tasks = _tasks;
    private LiveData<Task> task = _task;
    private LiveData<Boolean> loading = _loading;

    // Added for configuring what type of task should be retrieved.
    private MutableLiveData<DateGroup> _task_filter = new MutableLiveData<>();
    private LiveData<DateGroup> task_filter = _task_filter;


    @Inject
    public TaskListViewModel(TaskRepository taskRepository, AppExecutors appExecutors) {
        this.taskRepository = taskRepository;
        this.appExecutors = appExecutors;
        this.diskIO = appExecutors.diskIO();
    }

    public LiveData<DateGroup> getTaskFilter() {
        return task_filter;
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public LiveData<Task> getTask() {
        return task;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void setTaskType(DateGroup dateGroup) {
        _task_filter.postValue(dateGroup);
    }

    // @Ignore : Only use for LiveData Testing.
    public void testSetLoading(boolean loading) {
        _loading.postValue(loading);
    }

    public void loadTasks() {
        Timber.i("[ loadTasks ] - started");
        _loading.postValue(true);

        // Executor 인스턴스를 ApplicationScope 에서 생성해서 주입받아서 사용하면 더 분리된 코드로 리팩토링 할 수 있다.
        //Executor pool = Executors.newSingleThreadExecutor();
        diskIO.execute(() -> _tasks.postValue(taskRepository.getAllTasks()));

        _loading.postValue(false);
        Timber.i("[ loadTasks ] - finished");
    }

    public void loadTasksByTitle(final String title) {
        _loading.postValue(true);

        diskIO.execute(() ->
                _tasks.postValue(taskRepository.getTasks(title))
        );

        _loading.postValue(false);
    }

    public void loadTaskByUuid(final String uuid) {
        _loading.postValue(true);

        diskIO.execute(() ->
                _task.postValue(taskRepository.getTask(UUID.fromString(uuid)))
        );

        _loading.postValue(false);
    }

    public void loadTasksCompleted(final boolean completed) {
        _loading.postValue(true);

        diskIO.execute(() ->
                _tasks.postValue(taskRepository.getTasksCompleted(completed))
        );
        _loading.postValue(false);
    }

    public void refresh() {
        Timber.i("[ Refresh() ]");
        loadTasks();
    }

    public void insertTask(final Task task) {
        if (task == null)
            return;
        Timber.i("[ insertTask ] : repo.insertTask(Task)");
        taskRepository.insertTask(task);
        _task.postValue(task);
    }

    public void insertTasks(final List<Task> tasks) {
        taskRepository.insertTasks(tasks);
    }

    public void updateTask(final Task task) {
        taskRepository.updateTask(task);
    }

    public void deleteTask(final String uuid) {
        taskRepository.deleteTask(UUID.fromString(uuid));
    }

    public void deleteAllTask() {
        taskRepository.deleteAllTask();
    }

    public void loadTasksInCertainPeriod(DateGroup dateGroup) {
        diskIO.execute(() -> {
            _tasks.postValue(taskRepository.getTasksInCertainPeriod(dateGroup));
        });
    }


    /** Still in testing. */
    public void testLoadTaskAsync() {
        Timber.i( "[ TEST ASYNC: loadTasks ] - started");
        _loading.postValue(true);
        Future<List<Task>> result = taskRepository.getAllTaskAsync();
        try {
            _tasks.postValue(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.getStackTrace();
        }

        _loading.postValue(false);
    }

}
