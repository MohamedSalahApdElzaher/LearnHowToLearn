package com.mohammedsalah.learnhowtolearn;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReciver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        PendingIntent notification = PendingIntent.getActivity(context,
                0, new Intent(context, category.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.notifi)
                .setContentText("You have a new Review!").setContentTitle("Notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setShowWhen(true);

        builder.setContentIntent(notification);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        notificationManager.notify(1, builder.build());


    }
}
