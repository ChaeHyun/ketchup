package com.ketchup.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ketchup.model.task.DateGroup;
import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskDataSource;
import com.ketchup.model.task.TaskRepository;
import com.ketchup.utils.AlarmUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Provider;

import timber.log.Timber;

public class DailyAlarmRegisterWorker extends Worker {
    private TaskRepository taskRepository;
    private AlarmUtils alarmUtils;

    public DailyAlarmRegisterWorker(@NonNull Context context, @NonNull WorkerParameters workerParams,
                                    TaskRepository taskRepository, AlarmUtils alarmUtils) {
        super(context, workerParams);
        this.taskRepository = taskRepository;
        this.alarmUtils = alarmUtils;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Executors.newSingleThreadExecutor().execute(() -> {
                int cnt = 0;
                List<Task> taskList = taskRepository.getTasksInCertainPeriod(DateGroup.TOMORROW);

                if (taskList == null) {
                    Timber.d("DB에서 NULL값");
                    // throw exception.
                    //throw new RuntimeException("This is RuntimeException");
                    return;
                }
                if (!taskList.isEmpty()) {
                    for (Task task : taskList) {
                        Timber.d("[DailyWorker] Task 확인 : %s / (%s)", task.getTitle(), task.getDueDate());
                        if (!alarmUtils.doesPendingIntentExist((task.getUuid()))) {
                            alarmUtils.registerAlarm(task);
                        }
                        cnt++;
                    }

                    addNewTaskAsLogging(taskRepository, cnt);
                }
            });
        } catch (RuntimeException e) {
            Timber.d("There is no Tasks for TOMORROW in the Database.");
        }

        return Result.success();
    }

    // On behalf of logging, add a new task easily to check this worker has been operated.
    private void addNewTaskAsLogging(TaskRepository taskRepository, int cnt) {
        Timber.d("새로운 Task 추가하기.");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 HH시 mm분 ss초");
        String now = dateFormat.format(System.currentTimeMillis());
        Task task = new Task(UUID.randomUUID().toString(), now);
        task.setDescription("DailyAlarmRegisterWorker에 의해 생성됨\n"
                + cnt + "개의 알람이 등록되었습니다.");

        taskRepository.insertTask(task);
    }

    public static class Factory implements ChildWorkerFactory {

        private final Provider<AlarmUtils> alarmUtilsProvider;
        private final Provider<TaskRepository> taskRepositoryProvider;

        @Inject
        public Factory(Provider<TaskRepository> taskRepositoryProvider, Provider<AlarmUtils> alarmUtilsProvider) {
            this.taskRepositoryProvider = taskRepositoryProvider;
            this.alarmUtilsProvider = alarmUtilsProvider;
        }

        @Override
        public ListenableWorker create(Context appContext, WorkerParameters workerParameters) {
            return new DailyAlarmRegisterWorker(appContext, workerParameters, taskRepositoryProvider.get(), alarmUtilsProvider.get());
        }
    }
}
