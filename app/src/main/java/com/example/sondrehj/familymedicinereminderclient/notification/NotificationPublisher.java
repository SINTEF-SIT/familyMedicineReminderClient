package com.example.sondrehj.familymedicinereminderclient.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.Calendar;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String NOTIFICATION_DAYS = "notification-days";
    public static String NOTIFICATION_REPEAT = "notification-repeat";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        int[] days = intent.getIntArrayExtra(NOTIFICATION_DAYS);
        Calendar cal = Calendar.getInstance();
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);

        System.out.println("Day of week: " + currentDay);
        System.out.println("Alarm set for" + Arrays.toString(days));

        //Checks if the user has specified days for the reminder
        if (days.length == 0) {
            notificationManager.notify(id, notification);
            System.out.println("Publishing Notification with ID: " + id);
        } else {
            //If the user has specified days, we check if today is one of
            //the days. The notification is published if true.
            for (int day : days) {
                if (day == currentDay) {
                    notificationManager.notify(id, notification);
                    System.out.println("Publishing Notification with ID: " + id);
                    break;
                }
            }
        }
    }
}