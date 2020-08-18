package com.scatl.uestcbbs.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;

import androidx.core.app.NotificationCompat;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.receivers.NotificationReceiver;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/6 10:14
 */
public class NotificationUtil {

    /**
     * author: sca_tl
     * description: 推送通知
     */
    public static void showNotification(Context context,
                                        String action,
                                        String channel_id,
                                        String channel_name,
                                        String title,
                                        String msg) {
        Notification notification;

        Intent intent1 = new Intent(context, NotificationReceiver.class);
        intent1.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(channel_id), intent1, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);

            notification = new Notification.Builder(context, channel_id)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_notification_icon1)
                    .setColor(Color.parseColor("#3593f0"))
                    //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_cpu))
                    //.setTicker("")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        } else {

            notification = new NotificationCompat.Builder(context, channel_id)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_notification_icon1)
                    .setColor(Color.parseColor("#3593f0"))
                    //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_cpu))
                    //.setTicker("")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        if (notificationManager != null) { notificationManager.notify(Integer.parseInt(channel_id), notification); }
    }
}
