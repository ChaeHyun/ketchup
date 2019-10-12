package com.ketchup.addedit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskRepository;

import java.util.UUID;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import timber.log.Timber;

public class AddEditTaskViewModel extends ViewModel {

    private TaskRepository taskRepository;

    private MutableLiveData<Task> _task = new MutableLiveData<>();
    private LiveData<Task> task = _task;

    @Inject
    public AddEditTaskViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public LiveData<Task> getTask() {
        return task;
    }

    // EDIT MODE 시에 기존 taskId를 전달받았을 경우 읽어오기
    public void loadTaskByUuid(final String uuid) {
        Executors.newSingleThreadExecutor().execute(() ->
                _task.postValue(taskRepository.getTask(UUID.fromString(uuid)))
        );
    }


    public void updateTask(final Task task) {
        Timber.i("[ insertTask ] : repo.updateTask(Task)");
        Executors.newSingleThreadExecutor().execute(() ->
                taskRepository.updateTask(task)
        );
    }

    public void insertTask(Task task) {
        if (task == null)
            return;
        Executors.newSingleThreadExecutor().execute(() -> {
            Timber.i("[ insertTask ] : repo.insertTask(Task)");
            taskRepository.insertTask(task);
            _task.postValue(task);
        });

    }

    public void deleteTask(final String uuid) {
        Executors.newSingleThreadExecutor().execute(() ->
                taskRepository.deleteTask(UUID.fromString(uuid))
        );
    }
}
