package com.example.sondrehj.familymedicinereminderclient.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String NOTIFICATION_DAYS = "notification-days";
    public static String NOTIFICATION_REPEAT = "notification-repeat";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        int[] days = intent.getIntArrayExtra(NOTIFICATION_DAYS);

        for (int d : days) {
            System.out.println(d);
        }

        //TODO: Check if today is one of the days the notification should be published.

        //
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        System.out.println("Day of week: " + dayOfWeek);
        System.out.println("Publishing Notification with ID: " + id);
        notificationManager.notify(id, notification);

    }
}