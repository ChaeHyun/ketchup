package com.ketchup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.ketchup.addedit.AddEditTaskFragment;
import com.ketchup.model.task.Task;

import timber.log.Timber;

public class NotificationCreator {
    public static final String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    public static final String CHANNEL_NAME = "REMINDER_NAME";

    private String notificationChannelName;
    private String notificationChannelId;

    private int notificationImportance;

    private Context context;

    public NotificationCreator(Context context, String channelName, String channelId) {
        this.context = context;
        this.notificationChannelName = channelName;
        this.notificationChannelId = channelId;

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // String id, String name, int importance
            NotificationChannel channel = new NotificationChannel(notificationChannelId, notificationChannelName, importance);

            String description = "This is a notification channel for Ketchup.";
            channel.setDescription(description);

            // 생성한 채널을 시스템에 등록하기
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

        }
    }

    private Notification createNotification(Task task) {
        NotificationCompat.Action deleteAction = buildNotificationAction(context.getString(R.string.noti_action_delete), R.drawable.ic_delete_black_24dp, ActionReceiver.ACTION_DELETE, task.getUuid());
        NotificationCompat.Action snoozeAction = buildNotificationAction(context.getString(R.string.noti_action_snooze), R.drawable.ic_snooze_black_24dp, ActionReceiver.ACTION_SNOOZE, task.getUuid());
        NotificationCompat.Action completeAction = buildNotificationAction(context.getString(R.string.noti_action_complete), R.drawable.ic_done_black_24dp, ActionReceiver.ACTION_COMPLETE, task.getUuid());

        PendingIntent contentPendingIntent = makeContentPendingIntent(task.getUuid());

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(R.drawable.ic_menu_send)
                .setContentTitle(task.getTitle())
                .setContentText(task.getDescription() == null ? "empty" : task.getDescription())
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)
                .addAction(deleteAction)
                .addAction(snoozeAction)
                .addAction(completeAction)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return notificationBuilder.build();
    }

    public void notifyNotification(Task task) {
        Notification notification = createNotification(task);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify( "TAG", task.getUuid().hashCode(), notification);
    }

    private PendingIntent makeContentPendingIntent(String taskId) {
        Timber.d(" ** Create Content Pending Intent : AddEditTaskFragment Screen");
        Bundle bundle = new Bundle();
        bundle.putString(AddEditTaskFragment.TASK_ID, taskId);

        PendingIntent pendingIntent = new NavDeepLinkBuilder(context)
                .setGraph(R.navigation.navigation)
                .setDestination(R.id.addEditTaskFragment)
                .setArguments(bundle)
                .createPendingIntent();

        return pendingIntent;
    }

    private Intent makeActionIntent(String action, String taskId) {
        Intent actionIntent = new Intent(context, ActionReceiver.class);
        actionIntent.putExtra(ActionReceiver.ACTION, action);
        actionIntent.putExtra(RegisteredAlarmReceiver.TASK_ID, taskId);
        actionIntent.putExtra(ActionReceiver.NOTIFICATION_ID, taskId.hashCode());

        return actionIntent;
    }

    private PendingIntent makeActionPendingIntent(String action, String taskId) {
        Intent intent = makeActionIntent(action, taskId);

        int requestCode = (action + taskId).hashCode();
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Action buildNotificationAction(String actionName, int resIcon, String action, String taskId) {
        PendingIntent pendingIntent = makeActionPendingIntent(action, taskId);

        NotificationCompat.Action notificationAction = new NotificationCompat.Action.Builder(
                resIcon,
                actionName,
                pendingIntent
        ).build();

        return notificationAction;
    }

    /* TAG를 입력해서 Notification 발행할 경우, TAG가 선행되어야 검색 조건에 ID(ELSE IF)가 부합한다. */
    public void cancelNotification(int notificationId) {
        Timber.d("노티피케이션 닫기 %d", notificationId);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel("TAG", notificationId);
    }
}
