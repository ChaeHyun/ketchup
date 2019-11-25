package com.ketchup.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ketchup.RegisteredAlarmReceiver;
import com.ketchup.model.task.Task;

import timber.log.Timber;


public class AlarmUtils {

    Context context;
    private AlarmManager alarmManager;

    public AlarmUtils(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private Intent makeIntent(String taskId) {
        Intent intent = new Intent(context, RegisteredAlarmReceiver.class);
        intent.setAction(RegisteredAlarmReceiver.ACTION_REGISTER_ALARM);
        intent.putExtra(RegisteredAlarmReceiver.TASK_ID, taskId);

        return intent;
    }

    private PendingIntent makePendingIntent(String taskId) {
        Intent intent = makeIntent(taskId);

        Timber.d("Check the Request Code : %d", taskId.hashCode());
        return PendingIntent.getBroadcast(context, taskId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void registerAlarm(Task task) {
        PendingIntent pendingIntent = makePendingIntent(task.getUuid());

        long delay = (task.getDueDate() != null) ? task.getDueDate().getTime() : 0;
        Timber.d("알람을 등록합니다. 시간 : %d", delay);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }

    public void cancelAlarm(String taskId) {
        PendingIntent pendingIntent = makePendingIntent(taskId);
        alarmManager.cancel(pendingIntent);
        Timber.d("알람 취소하기");
    }

    public boolean doesPendingIntentExist(String taskId) {
        PendingIntent pi = makePendingIntent(taskId);
        return (pi != null);
    }

}
