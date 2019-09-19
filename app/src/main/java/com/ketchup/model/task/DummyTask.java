package com.ketchup.model.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DummyTask {
    int num = 10;
    List<Task> dummyTask = new ArrayList<>();

    public DummyTask() {
        initDummyTask();
    }

    private void initDummyTask() {
        for (int i = 0; i < num; i++) {
            Task task = new Task(UUID.randomUUID().toString(), "title " + i);
            dummyTask.add(task);
        }
    }

    public List<Task> getDummyTask() {
        initDummyTask();
        return dummyTask;
    }

    public List<Task> getDummyTaskDetail() {
        dummyTask.clear();

        dummyTask.add(new Task(UUID.randomUUID().toString(), "할일 1"));
        dummyTask.add(new Task(UUID.randomUUID().toString(), "할일 2"));
        dummyTask.add(new Task(UUID.randomUUID().toString(), "세탁물 맡기기", false));
        dummyTask.add(new Task(UUID.randomUUID().toString(), "물 8잔 마시기", true));
        dummyTask.add(new Task(UUID.randomUUID().toString(), "저녁약속 장소 찾기", true));

        return dummyTask;
    }
}
