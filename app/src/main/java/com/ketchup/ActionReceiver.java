package com.ketchup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskRepository;
import com.ketchup.utils.AlarmUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.android.DaggerBroadcastReceiver;
import timber.log.Timber;

public class ActionReceiver extends DaggerBroadcastReceiver {
    public static final String ACTION = "ketchup.broadcastreceiver.foraction";
    public static final String ACTION_DELETE = "ketchup.notification.action.delete.task";
    public static final String ACTION_COMPLETE = "ketchup.notification.action.complete.task";
    public static final String ACTION_SNOOZE = "ketch.notification.action.snooze.task";

    public static final String NOTIFICATION_ID = "ketchup.notification.id";
    public static final String SNOOZE_DELAY = "ketch.notification.snooze.delay";

    public final int SECOND_PER_MIN = 60;
    public final long MILLISECOND_PER_SEC = 1000L;

    /*
    * Timber로 로그 확인후
    * DI에 추가해서 taskRepository 접근권한 얻는다.
    * 실제 DB에 있는 task를 수정한다.
    * */
    @Inject
    TaskRepository taskRepository;

    @Inject
    AlarmUtils alarmUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getStringExtra(ACTION);
        final String taskId = intent.getStringExtra(RegisteredAlarmReceiver.TASK_ID);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);

        if (action.equals(ACTION_DELETE)) {
            Timber.d("This is Receiver that captures ACTION_DELETE : %s", taskId);
            Executors.newSingleThreadExecutor().execute(() -> {
                taskRepository.deleteTask(UUID.fromString(taskId));
            });

        }
        else if (action.equals(ACTION_COMPLETE)) {
            Timber.d("This is Receiver that captures ACTION_COMPLETE : %s", taskId);
            Executors.newSingleThreadExecutor().execute(() -> {
                Task task = taskRepository.getTask(UUID.fromString(taskId));
                if (task != null) {
                    task.setCompleted(true);
                    taskRepository.updateTask(task);
                }
            });
        }
        else if (action.equals(ACTION_SNOOZE)) {
            Timber.d("This is Receiver that captures ACTION_SNOOZE : %s", taskId);
            Executors.newSingleThreadExecutor().execute(() -> {
               Task task = taskRepository.getTask(UUID.fromString(taskId));
               if (task == null)
                   return;

               // System 시간을 읽는다.
               Date now = new Date();
               int minutes = 10;       // ONLY OPTION FOR NOW
               long delay = minutes * SECOND_PER_MIN * MILLISECOND_PER_SEC;

               alarmUtils.registerAlarmWithDelay(task, delay + now.getTime());
            });
        }

        NotificationCreator notificationCreator = new NotificationCreator(context, NotificationCreator.CHANNEL_NAME, NotificationCreator.CHANNEL_ID);
        notificationCreator.cancelNotification(notificationId);

    }
}
