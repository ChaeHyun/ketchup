package com.ketchup;


import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;

public class FakeTaskRepository implements TaskRepository {
    /**
     * 테스트에 사용하기 위해 실제 DB와 연결되지 않았지만
     * 데이터를 제공하는 가짜 Repository 를 만든다.
     * */

    HashMap<String, Task> data;
    private Boolean errorState = false;

    public FakeTaskRepository(HashMap<String, Task> initialData) {
        data = initialData;
    }
    
    public void setErrorState(boolean error) {
        errorState = error;
    }
    
    @Override
    public List<Task> getAllTasks() {
        if (errorState)
            return null;

        List<Task> result = new ArrayList<>();
        Set<Map.Entry<String, Task>> entries = data.entrySet();
        for (Map.Entry<String, Task> entry : entries) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public Task getTask(UUID uuid) {
        if (errorState)
            return null;

        if (data.containsKey(uuid.toString()))
            return data.get(uuid.toString());

        return null;
    }

    @Override
    public List<Task> getTasks(String title) {
        if (errorState)
            return null;

        List<Task> result = new ArrayList<>();
        Set<Map.Entry<String, Task>> entries = data.entrySet();
        for (Map.Entry<String, Task> entry : entries) {
            if (title.equals(entry.getValue().getTitle()))
                result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public List<Task> getTasksCompleted(boolean completed) {
        if (errorState)
            return null;

        Set<Map.Entry<String, Task>> entries = data.entrySet();
        List<Task> result = new ArrayList<>();
        for (Map.Entry<String, Task> entry : entries) {
            if (entry.getValue().isCompleted())
                result.add(entry.getValue());
        }

        return result;
    }

    @Override
    public void updateTask(Task task) {
        if (data.containsKey(task.getUuid()))
            data.replace(task.getUuid(), task);
    }

    @Override
    public void insertTask(Task task) {
        data.put(task.getUuid(), task);
    }

    @Override
    public void insertTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            data.put(tasks.get(i).getUuid(), tasks.get(i));
        }
    }

    @Override
    public void deleteTask(UUID uuid) {
        data.remove(uuid.toString());
    }

    @Override
    public void deleteAllTask() {
        data.clear();
    }

    /** Ignore */
    @Override
    public Future<List<Task>> getAllTaskAsync() {
        return null;
    }

}
