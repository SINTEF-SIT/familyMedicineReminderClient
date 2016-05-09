package com.example.sondrehj.familymedicinereminderclient.notification;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.fragments.MedicationListFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.utility.Converter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by sondre on 25/04/2016.
 */
public class NotificationScheduler {

    public Context context;

    public NotificationScheduler(Context context) {
        this.context = context;
    }

    /**
     * Schedules a repeating or non-repeating local notification based on the given reminder.
     *
     * @param notification the notification to be published.
     * @param reminder provides date, time and days (schedule) for when to publish the notification.
     */
    public void scheduleNotification(Notification notification, Reminder reminder) {

        // A variable containing the reminder date in milliseconds.
        // Used for scheduling the notification.
        Long time = reminder.getDate().getTimeInMillis();

        // Defines the Intent of the notification. The NotificationPublisher class uses this
        // object to retrieve additional information about the notification.
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        // Adds the given notification object to the Intent object.
        // Used to publish the given notification.
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        // Adds the given reminder object to the Intent object
        // Used to cancel and filter Notifications
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_REMINDER, reminder);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_TYPE, "regular");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder.getReminderId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        System.out.println("Scheduling: " + reminder);
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

    /**
     * Creates an instance of Notification given a reminder.
     *
     * @param reminder provides information such as name and content for the reminder.
     */
    public Notification getNotification(String content, Reminder reminder) {


        if(!reminder.getOwnerId().equals(AccountManager.get(context).getUserData(MainActivity.getAccount(context), "userId"))) {
            return getGuardianNotification(content, reminder);
        }

        // Defines the Intent of the notification
        Intent intent = new Intent(context, context.getClass());
        intent.putExtra("notification-reminder", reminder);
        intent.putExtra("notification-action", "notificationRegular");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        // Snooze intent
        Intent snoozeIntent = new Intent(context, context.getClass());
        snoozeIntent.putExtra("notification-reminder", reminder);
        snoozeIntent.putExtra("notification-action", "notificationSnooze");
        snoozeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingSnoozeIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), snoozeIntent, 0);

        // Constructs the notification
        Notification notification = new Notification.Builder(context)
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

    public Notification getGuardianNotification(String content, Reminder reminder) {

        // Defines the Intent of the notification
        Intent intent = new Intent(context, context.getClass());
        intent.putExtra("notification-reminder", reminder);
        intent.putExtra("notification-action", "notificationGuardian");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        // Constructs the notification
        Notification notification = new Notification.Builder(context)
                .setContentTitle("MYCYFAPP (Guardian)")
                .setContentText("Your child has a reminder: " + reminder.getName())
                .setSmallIcon(R.drawable.ic_sidebar_pill)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        return notification;
    }


    /**
     * Snoozes/Reschedules a notification with a given snooze time. A simplified version of
     * {@link #scheduleNotification(Notification, Reminder)}.
     *
     * @param notification the notification to be published.
     * @param reminder the reminder attached to the notification. Passed to NotificationPublisher
     *                 with scheduling information
     * @param snoozeTime The amount of time in milliseconds the notification should be delayed
     */
    public void snoozeNotification(Notification notification, Reminder reminder, int snoozeTime) {

        Calendar currentTime = new GregorianCalendar();

        System.out.println(snoozeTime);
        Long time = currentTime.getTimeInMillis() + (snoozeTime * 60000);
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_REMINDER, reminder);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_TYPE, "snooze");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, -2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    /**
     * Cancel/Removes a notification from {@link AlarmManager} given the ID of the notification.
     *
     * @param id The id of the notification to be canceled.
     */
    public void cancelNotification(int id) {
        //Cancel the scheduled reminder
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                id,
                new Intent(context, NotificationPublisher.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        System.out.println("Reminder: " + id + " was deactivated");
    }

    /**
     * Removes a notification from the notification drop-down menu.
     *
     * @param notificationId id of the notification to be removed.
     */
    public void removeNotification(int notificationId) {
        //Cancel the scheduled reminder
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
    }

    /**
     * The functionality of the notification's snooze button.
     * Called from {@link com.example.sondrehj.familymedicinereminderclient.MainActivity#onNewIntent(Intent)}
     * when snooze is clicked. Utilizes {@link #snoozeNotification(Notification, Reminder, int)} for
     * rescheduling.
     *
     * @param reminder the instance of Reminder provided by the notification. Used as parameter in
     *                 {@link #snoozeNotification(Notification, Reminder, int)}.
     */
    public void handleNotificationSnoozeClick(Reminder reminder) {

        // Get user specified snoozeTime from account settings
        SharedPreferences prefs = context.getSharedPreferences("AccountSettings", Context.MODE_PRIVATE);
        int snoozeTime = prefs.getInt("snoozeDelay", 5);

        // Schedule a "new" notification with the given snooze time
        this.snoozeNotification(
                this.getNotification("", reminder),
                reminder,
                snoozeTime);
        this.removeNotification(reminder.getReminderId());

        // Display toaster
        String toastText = "Snoozing for " + snoozeTime + " minutes";
        if(snoozeTime == 1)
            toastText = "Snoozing for " + snoozeTime + " minute";
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
    }

    /**
     * The functionality of the notification's main button
     * Called from {@link com.example.sondrehj.familymedicinereminderclient.MainActivity#onNewIntent(Intent)}
     * when the main notification button is clicked. Reduces the amount of the medication provided by the
     * given reminder.
     *
     * @param reminder the instance of reminder provided by the notification.
     */
    public void handleNotificationMainClick(Reminder reminder) {

        // Check if the reminder got a medication "attached"
        if (reminder.getMedicine() != null) {

            // We reduce the amount of the medication by the given dosage.
            reminder.getMedicine().setCount(reminder.getMedicine().getCount() - reminder.getDosage());
            GregorianCalendar currentTime = new GregorianCalendar();
            reminder.setTimeTaken(currentTime);

            // Updates the DB
            MySQLiteHelper db = new MySQLiteHelper(context);
            db.updateAmountMedication(reminder.getMedicine());
            db.setReminderTimeTaken(reminder);
            BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONS));
            BusService.getBus().post(new DataChangedEvent(DataChangedEvent.DASHBOARDCHANGED));

            System.out.println(currentTime.getTime().toString());
            // Display toaster
            Toast.makeText(context, "Registered as taken", Toast.LENGTH_LONG).show();
        }
    }
}
