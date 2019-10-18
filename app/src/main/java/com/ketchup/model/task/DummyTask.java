package com.ketchup.model.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

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
        dummyTask.add(new Task(UUID.randomUUID().toString(), "할일 2", false, -2937041));
        dummyTask.add(new Task(UUID.randomUUID().toString(), "세탁물 맡기기", false));
        dummyTask.add(new Task(UUID.randomUUID().toString(), "물 8잔 마시기", true));
        String fixedUuid = "e3110931-571e-4357-a80a-8e3853ab1a28";
        dummyTask.add(new Task(fixedUuid, "저녁약속 장소 찾기", true, -15108398));

        dummyTask.add(new Task(UUID.randomUUID().toString(), "달리기 3km", true));
        dummyTask.add(new Task(UUID.randomUUID().toString(), "마트에서 우유 사오기", false));
        dummyTask.add(new Task(UUID.randomUUID().toString(), "약속시간 확정하기", false));
        dummyTask.add(new Task(UUID.randomUUID().toString(), "티켓팅하기", true));

        return dummyTask;
    }
}
