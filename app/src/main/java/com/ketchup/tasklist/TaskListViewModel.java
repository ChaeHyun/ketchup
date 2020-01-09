package com.ketchup.tasklist;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ketchup.AppExecutors;
import com.ketchup.di.ActivityScope;
import com.ketchup.model.Category;
import com.ketchup.model.CategoryRepository;
import com.ketchup.model.CategoryWithTasks;
import com.ketchup.model.task.DateGroup;
import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private MutableLiveData<Boolean> _loading = new MutableLiveData<>();

    private LiveData<Boolean> loading = _loading;

    // Added for configuring what type of task should be retrieved.
    private MutableLiveData<DateGroup> _task_filter = new MutableLiveData<>();
    private LiveData<DateGroup> task_filter = _task_filter;

    private MutableLiveData<List<CategoryWithTasks>> _processedTasks = new MutableLiveData<>();
    private LiveData<List<CategoryWithTasks>> processedTasks = _processedTasks;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    public TaskListViewModel(TaskRepository taskRepository, AppExecutors appExecutors) {
        this.taskRepository = taskRepository;
        this.appExecutors = appExecutors;
        this.diskIO = appExecutors.diskIO();
    }

    public LiveData<DateGroup> getTaskFilter() {
        return task_filter;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<List<CategoryWithTasks>> getProcessedTasks() {
        return processedTasks;
    }

    public void setTaskType(DateGroup dateGroup) {
        _task_filter.postValue(dateGroup);
    }

    // Retrieve tasks by TODAY / UPCOMING / OVERDUE / TOMORROW
    public void loadTasksInCertainPeriod(DateGroup dateGroup) {
        diskIO.execute(() -> {
            List<Task> taskList = taskRepository.getTasksInCertainPeriod(dateGroup);
            // taskList -> List<CategoryWithTasks>
            // 카테고리 : completed / uncompleted 는 언제, 어디서 만드는게 베스트일까.
            CategoryWithTasks uncompleted = wrapTaskListWithCategory(taskList, false);
            CategoryWithTasks completed = wrapTaskListWithCategory(taskList, true);

            List<CategoryWithTasks> categoryWithTasksList = Arrays.asList(uncompleted, completed);
            _processedTasks.postValue(categoryWithTasksList);
        });
    }

    public void loadAllCategoryWithTasks() {
        diskIO.execute(() -> _processedTasks.postValue(categoryRepository.getAllCategoryWithTasksData()));
    }

    private CategoryWithTasks wrapTaskListWithCategory(final List<Task> data, final boolean completed) {
        String categoryName = completed ? "완료" : "미완료";
        Category category = new Category(UUID.randomUUID().toString(), categoryName);

        List<Task> taskList = new ArrayList<>();
        // list filtering.
        for (Task t : data) {
            if (t.isCompleted() == completed)
                taskList.add(t);
        }

        return new CategoryWithTasks(category, taskList, false);
    }
}
