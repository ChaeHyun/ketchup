package com.ketchup;

import androidx.lifecycle.ViewModel;

import com.ketchup.model.task.TaskRepository;

import javax.inject.Inject;

public class AddEditTaskViewModel extends ViewModel {

    private TaskRepository taskRepository;

    @Inject
    public AddEditTaskViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}
