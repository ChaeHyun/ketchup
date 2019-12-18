package com.ketchup.receiver;

import android.content.Context;
import android.content.Intent;

import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskRepository;
import com.ketchup.utils.NotificationCreator;

import java.util.UUID;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.android.DaggerBroadcastReceiver;
import timber.log.Timber;

// This is a Receiver for Broadcast from AlarmManager.
// The role of this receiver is registering a notification.
// notificatoin info is Task object.
public class RegisteredAlarmReceiver extends DaggerBroadcastReceiver {
    public static final String ACTION_REGISTER_ALARM = "REGISTER_ALARM";
    public static final String TASK_ID = "TASK_ID";

    @Inject
    TaskRepository taskRepository;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // intent will have TASK_ID value.
        // Shoold get a TASK object by using TASK_ID. Not pass the whole object through intent.
        String receivedTaskId = intent.getStringExtra(TASK_ID);
        Timber.d("체크 for TaskId at Receiver : %s", receivedTaskId);
        Executors.newSingleThreadExecutor().execute(() -> {
            Task task = taskRepository.getTask(UUID.fromString(receivedTaskId));

            if (task != null) {
                Timber.d(" Task 확인 : %s", task.getTitle());

                Timber.d("Notification 발행하기.");
                NotificationCreator notificationCreator = new NotificationCreator(context, NotificationCreator.CHANNEL_NAME, NotificationCreator.CHANNEL_ID);
                notificationCreator.notifyNotification(task);
            }
        });

    }
}
