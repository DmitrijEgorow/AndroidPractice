package ru.myitschool.lab23;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DownloadService extends JobService {

    private final String NOTIFICATION_CHANNEL_ID = "property";
    private final String NOTIFICATION_CHANNEL_NAME = "Update requested";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        createNotificationChanel();
        sendNotification(jobParameters.getExtras().getString(MainActivity.keyText));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


    public void createNotificationChanel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

    }

    public void sendNotification(String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(NOTIFICATION_CHANNEL_NAME)
                .setContentText(message)
                .setContentIntent(
                        PendingIntent.getActivity(this, 0,
                                new Intent(this, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground);
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }

}
