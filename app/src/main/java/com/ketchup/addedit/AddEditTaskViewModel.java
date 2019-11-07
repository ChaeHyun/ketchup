package com.ketchup.addedit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskRepository;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import timber.log.Timber;


public class AddEditTaskViewModel extends ViewModel {

    private TaskRepository taskRepository;

    private MutableLiveData<Boolean> _loading = new MutableLiveData<>();
    private LiveData<Boolean> loading = _loading;

    private MutableLiveData<Task> _task = new MutableLiveData<>();
    private LiveData<Task> task = _task;

    private MutableLiveData<String> _title = new MutableLiveData<>();
    private LiveData<String> title = _title;

    private MutableLiveData<String> _description = new MutableLiveData<>();
    private LiveData<String> description = _description;

    private MutableLiveData<Integer> _color = new MutableLiveData<>();   // color Layout은 color 값에 따라서 View에서 지정한다.
    private LiveData<Integer> color = _color;

    private MutableLiveData<Date> _dueDate = new MutableLiveData<>();
    private LiveData<Date> dueDate = _dueDate;

    private MutableLiveData<Boolean> _completed = new MutableLiveData<>();
    private LiveData<Boolean> completed = _completed;

    private MutableLiveData<String> _saved = new MutableLiveData<>();
    private LiveData<String> saved = _saved;

    private boolean isAddMode = false;
    private boolean isDataLoaded = false;
    private String taskId = null;


    @Inject
    public AddEditTaskViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        _loading.setValue(false);
    }

    public LiveData<String> getTitle() {
        return title;
    }

    public void setTitle(String title) {
        _title.setValue(title);
    }

    public LiveData<String> getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        _description.setValue(desc);
    }

    public LiveData<Integer> getColor() {
        return color;
    }

    public void setColor(int color) {
        _color.postValue(color);
    }

    public LiveData<Date> getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        _dueDate.setValue(dueDate);
    }

    public LiveData<Boolean> getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        _completed.postValue(completed);
    }

    public LiveData<Task> getTask() {
        return task;
    }

    public LiveData<String> getSaved() {
        return saved;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void isLoading(boolean isLoading) {
        _loading.postValue(isLoading);
    }

    public void load(final String uuid) {
        if (_loading.getValue() == null || _loading.getValue())
            return;

        if (uuid == null) {
            isAddMode = true;
            return;
        }

        // EditMode
        isAddMode = false;
        _loading.postValue(true);

        taskId = uuid;
        loadTaskByUuid(taskId);
    }

    // when : taskId != null
    private void loadTaskByUuid(final String uuid) {
        Timber.d("[ loadTaskByUuid] : %s", uuid);
        Executors.newSingleThreadExecutor().execute(() -> {
            final Task result = taskRepository.getTask(UUID.fromString(uuid));
            _task.postValue(result);
            updateLoadedTaskToView(result);
        });
    }

    private void updateLoadedTaskToView(final Task task) {
        if (task != null) {
            _title.postValue(task.getTitle());
            _description.postValue(task.getDescription());
            _color.postValue(task.getColorLabel());
            _dueDate.postValue(task.getDueDate());
            _completed.postValue(task.isCompleted());
        }

        _loading.postValue(false);      // loading finished
        isDataLoaded = true;
    }

    // Called by fab.onClick()
    public void saveTask() {
        // View에 입력된 값을 Task Obj로 묶는 로직
        String title = _title.getValue();
        if (title == null || title.length() <= 0) {
            _saved.postValue(AddEditTaskFragment.SAVED_FAIL);
        }
        String description = _description.getValue();
        int colorLable = _color.getValue() == null ? Task.DEFAULT_COLOR : _color.getValue();
        boolean completed = _completed.getValue() == null ? false : _completed.getValue();
        Date dueDate = _dueDate.getValue();

        if (isAddMode)
            taskId = UUID.randomUUID().toString();
        Task saveTask = new Task(taskId, title, completed, colorLable);
        saveTask.setDescription(description);
        saveTask.setDueDate(dueDate);

        if (isAddMode)
            insertTask(saveTask);
        else
            updateTask(saveTask);

        _saved.postValue(AddEditTaskFragment.SAVED_OK);
    }


    private void updateTask(final Task task) {
        if (task == null)
            return;
        Timber.i("[ updateTask ] : repo.updateTask(Task)");
        Executors.newSingleThreadExecutor().execute(() ->
                taskRepository.updateTask(task)
        );
    }

    private void insertTask(final Task task) {
        if (task == null) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            Timber.i("[ insertTask ] : repo.insertTask(Task)");
            taskRepository.insertTask(task);
        });
    }

    public void deleteTask(final String uuid) {
        if (uuid == null) {
            _saved.postValue(AddEditTaskFragment.SAVED_FAIL);
            return;
        }
        Executors.newSingleThreadExecutor().execute(() ->
                taskRepository.deleteTask(UUID.fromString(uuid))
        );
        _saved.postValue(AddEditTaskFragment.SAVED_OK);
    }
}
