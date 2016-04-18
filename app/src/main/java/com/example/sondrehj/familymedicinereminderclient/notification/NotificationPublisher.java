package com.example.sondrehj.familymedicinereminderclient.notification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION = "notification";
    public static String NOTIFICATION_REMINDER = "notification-reminder";
    public Context context;

    public void onReceive(Context context, Intent intent) {

        this.context = context;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Retrieves data from the given Intent object
        Reminder reminder = (Reminder) intent.getSerializableExtra(NOTIFICATION_REMINDER);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = reminder.getReminderId();
        int[] days = reminder.getDays();
        GregorianCalendar endCal = reminder.getEndDate();

        System.out.println("Reminder: " + reminder);

        // Today's date
        Calendar cal = Calendar.getInstance();
        int currentDay = (cal.get(Calendar.DAY_OF_WEEK));

        System.out.println("Day of week: " + currentDay);
        System.out.println("Reminder: " + id + " scheduled for days: " + Arrays.toString(days));

        // Checks if the user has specified days for the reminder
        if (days.length == 0) {
            notificationManager.notify(id, notification);
            System.out.println("Publishing Notification with ID: " + id);
        } else {
            // Cancel the notification if the reminder has expired
            if (!cal.before(endCal)) {
                notificationManager.notify(id, notification);
                cancelNotification(id);
            } else {
                    for (int day : days) {
                        // If the user has specified days, we check if today is one of
                        // the days. The notification is published if true.
                        if (day == currentDay) {
                            notificationManager.notify(id, notification);
                            System.out.println("Publishing Notification with ID: " + id);
                            break;
                        }
                    }
            }
        }
    }

    public void cancelNotification(int id) {
        //Cancel the scheduled reminder
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                id,
                new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        System.out.println("Reminder: " + id + " was deactivated");
    }
}