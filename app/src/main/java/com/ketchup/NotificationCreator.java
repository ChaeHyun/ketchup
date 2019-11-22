package com.ketchup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.ketchup.model.task.Task;

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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(R.drawable.ic_menu_send)
                .setContentTitle(task.getTitle())
                .setContentText(task.getDescription() == null ? "empty" : task.getDescription())
                .setAutoCancel(true)
                //.setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return notificationBuilder.build();
    }

    public void notifyNotification(Task task) {
        Notification notification = createNotification(task);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify( "TAG", task.getUuid().hashCode(), notification);
    }
}
