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

    private Intent makeIntent(Task task) {
        Intent intent = new Intent(context, RegisteredAlarmReceiver.class);
        intent.setAction(RegisteredAlarmReceiver.ACTION_REGISTER_ALARM);
        intent.putExtra(RegisteredAlarmReceiver.TASK_ID, task.getUuid());

        return intent;
    }

    private PendingIntent makePendingIntent(Task task) {
        Intent intent = makeIntent(task);

        Timber.d("Check the Request Code : %d", task.getUuid().hashCode());
        return PendingIntent.getBroadcast(context, task.getUuid().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void registerAlarm(Task task) {
        PendingIntent pendingIntent = makePendingIntent(task);

        long delay = (task.getDueDate() != null) ? task.getDueDate().getTime() : 0;
        Timber.d("알람을 등록합니다. 시간 : %d", delay);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }

    public void cancelAlarm(Task task) {
        PendingIntent pendingIntent = makePendingIntent(task);
        alarmManager.cancel(pendingIntent);
    }

    public boolean doesPendingIntentExist(Task task) {
        PendingIntent pi = makePendingIntent(task);
        return (pi != null);
    }

}
