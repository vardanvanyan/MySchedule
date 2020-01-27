package com.example.myschedule;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

public class BroadReceiver extends BroadcastReceiver {


    NotificationManager notificationManager;
    String desc;
    String name;
    Context context;
    int id;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        name = intent.getStringExtra("NAME_KEY");
        desc = intent.getStringExtra("DESC_KEY");
        id = intent.getIntExtra("ID", 0);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("CHANEL_ID", "CHANEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("chanel description");
            channel.enableLights(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
        sendNotification();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder not = new NotificationCompat.Builder(context, "CHANEL_ID");
            not.setAutoCancel(true).setWhen(System.currentTimeMillis()).
                    setSmallIcon(R.drawable.icons).
                    setContentTitle(name).setContentText(desc).
                    setVisibility(Notification.VISIBILITY_PUBLIC).
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            notificationManager.notify(id, not.build());
        } else {
            Notification.Builder notify = new Notification.Builder(context);
            notify.setCategory(Notification.CATEGORY_MESSAGE).
                    setContentTitle(name).setContentText(desc).setSmallIcon(R.drawable.icons).
                    setAutoCancel(true).
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)).
                    setVisibility(Notification.VISIBILITY_PUBLIC).
                    setWhen(System.currentTimeMillis());
            notificationManager.notify(id, notify.build());
        }
    }
}
