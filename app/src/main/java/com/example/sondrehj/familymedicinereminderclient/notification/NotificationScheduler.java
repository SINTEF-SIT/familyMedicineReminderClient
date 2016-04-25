package com.example.sondrehj.familymedicinereminderclient.notification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by sondre on 25/04/2016.
 */
public class NotificationScheduler {

    public Activity activity;

    public NotificationScheduler(Activity activity) {
        this.activity = activity;
    }

    public void scheduleNotification(Notification notification, Reminder reminder) {

        // A variable containing the reminder date in milliseconds.
        // Used for scheduling the notification.
        Long time = reminder.getDate().getTimeInMillis();

        // Defines the Intent of the notification. The NotificationPublisher class uses this
        // object to retrieve additional information about the notification.
        Intent notificationIntent = new Intent(activity, NotificationPublisher.class);
        // Adds the given notification object to the Intent object.
        // Used to publish the given notification.
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        // Adds the given reminder object to the Intent object
        // Used to cancel and filter Notifications
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_REMINDER, reminder);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_TYPE, "regular");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, reminder.getReminderId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();

        // Schedules a repeating notification on the user specified days.
        if (reminder.getDays().length > 0 && !reminder.getDate().before(cal)) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        // Schedules a non-repeating notification
        else {
            if (!reminder.getDate().before(cal)) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }
        }
    }

    public Notification getNotification(String content, Reminder reminder) {

        // Defines the Intent of the notification
        Intent intent = new Intent(activity, activity.getClass());
        intent.putExtra("notification-reminder", reminder);
        intent.putExtra("notification-action", "regular");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(activity, (int) System.currentTimeMillis(), intent, 0);

        // Snooze intent
        Intent snoozeIntent = new Intent(activity, activity.getClass());
        snoozeIntent.putExtra("notification-reminder", reminder);
        snoozeIntent.putExtra("notification-action", "snooze");
        snoozeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingSnoozeIntent = PendingIntent.getActivity(activity, (int) System.currentTimeMillis(), snoozeIntent, 0);

        // Constructs the notification
        Notification notification = new Notification.Builder(activity)
                .setContentTitle("MYCYFAPP")
                .setContentText(reminder.getName())
                .setSmallIcon(R.drawable.ic_sidebar_pill)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_sidebar_pill, "Take", pIntent)
                .addAction(R.drawable.ic_sidebar_alarm, "Snooze", pendingSnoozeIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        return notification;
    }


    public void snoozeNotification(Notification notification, Reminder reminder, int snoozeTime) {

        Calendar currentTime = new GregorianCalendar();

        Long time = currentTime.getTimeInMillis() + snoozeTime;
        Intent notificationIntent = new Intent(activity, NotificationPublisher.class);

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_REMINDER, reminder);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_TYPE, "snooze");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, -2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }


    public void cancelNotification(int id){
        //Cancel the scheduled reminder
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity,
                id,
                new Intent(activity, NotificationPublisher.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        System.out.println("Reminder: " + id + " was deactivated");
    }

    public void removeNotification(int notificationId){
        //Cancel the scheduled reminder
        NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
    }


}
