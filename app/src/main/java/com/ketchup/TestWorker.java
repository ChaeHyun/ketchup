package com.ketchup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskRepository;
import com.ketchup.utils.AlarmUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Provider;

import timber.log.Timber;

public class TestWorker extends Worker {

    /* 2 Objects that we want to inject into this test worker. */
    AlarmUtils alarmUtils;
    TaskRepository taskRepository;

    public TestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams
    , TaskRepository taskRepository, AlarmUtils alarmUtils) {
        super(context, workerParams);
        this.taskRepository = taskRepository;
        this.alarmUtils = alarmUtils;
    }

    @NonNull
    @Override
    public Result doWork() {
        Timber.d("Worker Start");
        Executors.newSingleThreadExecutor().execute(() -> {
//            List<Task> tasks = taskRepository.getTasksCompleted(false);
//            if (tasks != null && !tasks.isEmpty()) {
//                for (Task task : tasks) {
//                    Timber.d("Title : %s\n", task.getTitle());
//                }
//
//                long delay = System.currentTimeMillis() + 2 * 60 * 1_000L;
//                alarmUtils.registerAlarmWithDelay(tasks.get(0), delay);
//            }

            Timber.d("새로운 Task 추가하기.");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 HH시 mm분 ss초");
            String now = dateFormat.format(System.currentTimeMillis());
            Task task = new Task(UUID.randomUUID().toString(), now);
            task.setDescription("Auto-generated");

            taskRepository.insertTask(task);
        });
        return Result.success();
    }

    /* Inner Class로 Factory를 만든다.
    * 여기서 임의로 만든 CustomWorker 의 인스턴스를 생성하는 법을 알려준다.*/
    public static class Factory implements ChildWorkerFactory {

        private final Provider<AlarmUtils> alarmUtilsProvider;
        private final Provider<TaskRepository> taskRepositoryProvider;

        @Inject
        public Factory(Provider<AlarmUtils> alarmUtilsProvider, Provider<TaskRepository> taskRepositoryProvider) {
            this.alarmUtilsProvider = alarmUtilsProvider;
            this.taskRepositoryProvider = taskRepositoryProvider;
        }

        @Override
        public ListenableWorker create(Context appContext, WorkerParameters workerParameters) {
            return new TestWorker(appContext, workerParameters, taskRepositoryProvider.get(), alarmUtilsProvider.get());
        }
    }
}
