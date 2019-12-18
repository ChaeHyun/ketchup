package com.ketchup.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ketchup.receiver.RegisteredAlarmReceiver;
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

    private PendingIntent makePendingIntent(String taskId, final int pendingIntentFlag) {
        Intent intent = makeIntent(taskId);

        Timber.d("Check the Request Code : %d", taskId.hashCode());
        return PendingIntent.getBroadcast(context, taskId.hashCode(), intent, pendingIntentFlag);
    }

    public void registerAlarm(Task task) {
        PendingIntent pendingIntent = makePendingIntent(task.getUuid(), PendingIntent.FLAG_UPDATE_CURRENT);

        long delay = (task.getDueDate() != null) ? task.getDueDate().getTime() : 0;
        Timber.d("알람을 등록합니다. 시간 : %d", delay);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }

    /* Task.dueDate 값이 아니라 별도의 delay 값을 받아서 Alarm 등록하는 메소드 필요.
    * void registerAlarmForSnooze(Task task, long delay) -> system.currentTime() + delay */
    public void registerAlarmWithDelay(Task task, long delay) {
        PendingIntent pendingIntent = makePendingIntent(task.getUuid(), PendingIntent.FLAG_UPDATE_CURRENT);

        Timber.d("알람을 등록합니다. 시간 param : %d", delay);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }

    public void cancelAlarm(String taskId) {
        PendingIntent pendingIntent = makePendingIntent(taskId, PendingIntent.FLAG_UPDATE_CURRENT);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Timber.d("알람 취소하기");
            Timber.d("PendingIntent도 함께 삭제되었는가? %s", !doesPendingIntentExist(taskId));
        }
    }

    public boolean doesPendingIntentExist(String taskId) {
        PendingIntent pi = makePendingIntent(taskId, PendingIntent.FLAG_NO_CREATE);
        return (pi != null);
    }

}
