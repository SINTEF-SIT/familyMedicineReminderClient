package com.example.sondrehj.familymedicinereminderclient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.dummy.MedicationListContent;
import com.example.sondrehj.familymedicinereminderclient.dummy.ReminderListContent;
import com.example.sondrehj.familymedicinereminderclient.modals.EndDatePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.modals.MedicationPickerFragment;
import com.example.sondrehj.familymedicinereminderclient.modals.SelectDaysDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.modals.SelectUnitDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.models.User;
import com.example.sondrehj.familymedicinereminderclient.notification.NotificationPublisher;
import com.example.sondrehj.familymedicinereminderclient.sqlite.MySQLiteHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MedicationCabinetFragment.OnFragmentInteractionListener,
        AccountAdministrationFragment.OnFragmentInteractionListener, NewReminderFragment.OnNewReminderInteractionListener,
        ReminderListFragment.OnReminderListFragmentInteractionListener, MedicationListFragment.OnListFragmentInteractionListener,
        WelcomeFragment.OnFragmentInteractionListener, MedicationStorageFragment.OnFragmentInteractionListener,
        TimePickerFragment.TimePickerListener, DatePickerFragment.DatePickerListener, SelectUnitDialogFragment.OnUnitDialogResultListener,
        SelectDaysDialogFragment.OnDaysDialogResultListener, EndDatePickerFragment.EndDatePickerListener, MedicationPickerFragment.OnMedicationPickerDialogResultListener {

    private static Account account;
    NotificationManager manager;
    Notification myNotication;
    Boolean started = false;


    /**
     *
     * Main entry point of the application. When onCreate is run, view is filled with the
     * layout activity_main in res. The fragment container which resides in the contentView is
     * changed to "MediciationListFragment()" with the changeFragment() function call.
     * <p/>
     * In addition, the Sidebar/Drawer is instantiated.
     * <p/>
     * Portrait mode is enforced because if the screen is rotated you loose a lot of references
     * when the instance is redrawn.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeFragment(new MedicationListFragment());

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // The items inside the grey area of the drawer.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Enforce rotation-lock.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Read and display data from local database. (Flyttes?)
        MySQLiteHelper db = new MySQLiteHelper(this);
        //Medications
        ArrayList<Medication> meds = db.getMedications();
        Collections.reverse(meds);
        MedicationListContent.ITEMS.addAll(meds);
        //Reminders
        ArrayList<Reminder> reminders = db.getReminders();
        Collections.reverse(reminders);
        ReminderListContent.ITEMS.addAll(reminders);

        /**
         * This is a dummy account for the SyncAdapter - should be moved to application init,
         * not stay in the UI thread.
         */
        account = new Account("Account", "com.example.sondrehj.familymedicinereminderclient");
        AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(account, null, null);

        ContentResolver.setIsSyncable(account, "com.example.sondrehj.familymedicinereminderclient.content", 1);
        ContentResolver.setSyncAutomatically(account, "com.example.sondrehj.familymedicinereminderclient.content", true);
        Log.d("Sync", "Sync set to automatic.");
    }

    /**
     * Gets the instantiazed account of the system, used with the SyncAdapter and
     * ContentResolver, might have to be moved sometime.
     * @return
     */
    public static Account getAccount(){
        return account;
    }

    //@Override
    //public void onBackPressed() {
    //    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    //    if (drawer.isDrawerOpen(GravityCompat.START)) {
    //        drawer.closeDrawer(GravityCompat.START);
    //    } else {
    //        super.onBackPressed();
    //    }
    //}

    /**
     *
     * Closes the drawer when the back button is pressed.
     */
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * +
     * Inflate the options menu. This adds items to the action bar if it is present. The one with
     * the three buttons, which resides physically on Samsung phones.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * +
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    /**
     * +
     * Takes in a fragment which is to replace the fragment which is already in the fragmentcontainer
     * of MainActivity.
     *
     * @param fragment
     */
    public void changeFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        System.out.println(fragment.getClass().getSimpleName());
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            //Animation
            transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, 0, 0);

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            transaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
            transaction.addToBackStack(backStateName);

            //Commit the transaction
            transaction.commit();
        }
    }

    /**
     *
     * Handles the selection of items in the drawer and replaces the fragment container of
     * MainActivity with the fragment corresponding to the Item selected. The drawer is then closed.
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        System.out.print(id);

        if (id == R.id.nav_reminders) {
            System.out.print("nav_reminders");
            changeFragment(ReminderListFragment.newInstance(1));

        } else if (id == R.id.nav_medication) {
            System.out.print("nav_medication");
            changeFragment(new MedicationListFragment());

        } else if (id == R.id.nav_symptoms) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMedicationListFragmentInteraction(Medication medication) {
        Fragment fragment = MedicationStorageFragment.newInstance(medication);
        changeFragment(fragment);
    }

    @Override
    public void onAccountAdminFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSaveNewReminder(Reminder r) {

        if (r.getIsActive()) {

            // Activate the reminder
            scheduleNotification(getNotification("Take your medication"), r);
            r.setIsActive(true);
            System.out.println("Reminder: " + r.getReminderId() + " was activated");
        }
        // Updates the DB
        MySQLiteHelper db = new MySQLiteHelper(this);
        db.updateReminder(r);

        changeFragment(ReminderListFragment.newInstance(1));
    }

    @Override
    public String newReminderListGetSelectedDaysText(int[] reminder_days) {
        return getSelectedDaysText(reminder_days);
    }

    @Override
    public void onMedicationCabinetFragmentInteraction(Uri uri) {

    }

    @Override
    public void onWelcomeFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPositiveUnitDialogResult(int unit) {
        String[] units = getResources().getStringArray(R.array.unit_items);
        MedicationStorageFragment sf = (MedicationStorageFragment) getFragmentManager().findFragmentByTag("MedicationStorageFragment");
        sf.setUnitText(units[unit]);
    }

    @Override
    public void onNegativeUnitDialogResult() {

    }

    @Override
    public void onMedicationStorageFragmentInteraction(Uri uri) {

    }

    @Override
    public void onReminderListItemClicked(Reminder reminder) {
        System.out.print("Reminder was clicked");
        changeFragment(NewReminderFragment.newInstance(reminder));
    }

    @Override
    public void onNewReminderButtonClicked() {
        changeFragment(NewReminderFragment.newInstance(null));
    }

    @Override
    public void onReminderListSwitchClicked(Reminder reminder) {

        if (reminder.getIsActive()) {

            // Cancel the scheduled reminder
            cancelNotification(reminder.getReminderId());
            reminder.setIsActive(false);
        } else {

            // Activate the reminder
            scheduleNotification(getNotification("Take your medication"), reminder);
            reminder.setIsActive(true);
            System.out.println("Reminder: " + reminder.getReminderId() + " was activated");
        }
        // Updates the DB
        MySQLiteHelper db = new MySQLiteHelper(this);
        db.updateReminder(reminder);
    }

    @Override
    public String ReminderListGetSelectedDaysText(int[] reminder_days) {
        return getSelectedDaysText(reminder_days);
    }

    @Override
    public void addMedicationToMedicationList(Medication medication) {

    }

    /**
     * +
     * Called by timepicker in NewReminder
     *
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void setTime(int hourOfDay, int minute) {
        NewReminderFragment newReminderFragment = (NewReminderFragment) getFragmentManager().findFragmentByTag("NewReminderFragment");
        newReminderFragment.setTimeOnLayout(hourOfDay, minute);
    }

    /**
     * +
     * Called by datepicker in NewReminder
     *
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void setDate(int year, int month, int day) {
        NewReminderFragment newReminderFragment = (NewReminderFragment) getFragmentManager().findFragmentByTag("NewReminderFragment");
        newReminderFragment.setDateOnLayout(year, month, day);
    }

    @Override
    public void setEndDate(int year, int month, int day) {
        NewReminderFragment newReminderFragment = (NewReminderFragment) getFragmentManager().findFragmentByTag("NewReminderFragment");
        newReminderFragment.setEndDateOnLayout(year, month, day);

    }

    private void scheduleNotification(Notification notification, Reminder reminder) {

        // A variable containing the reminder date in milliseconds.
        // Used for scheduling the notification.
        Long time = reminder.getDate().getTimeInMillis();

        // Defines the Intent of the notification. The NotificationPublisher class uses this
        // object to retrieve additional information about the notification.
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        // Adds the given notification object to the Intent object.
        // Used to publish the given notification.
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        // Adds the given reminder object to the Intent object
        // Used to cancel and filter Notifications
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_REMINDER, reminder);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminder.getReminderId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();

        // Schedules a repeating notification on the user specified days.
        if (reminder.getDays().length > 0 && !reminder.getDate().before(cal)) {
            System.out.println("Alarm set");
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        // Schedules a non-repeating notification
        else {
            if (!reminder.getDate().before(cal)) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }
        }
    }

    private Notification getNotification(String content) {

        //Defines the Intent of the notification
        Intent intent = new Intent(this, this.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        //Constructs the notification
        Notification notification = new Notification.Builder(MainActivity.this)
                .setContentTitle("MYCYFAPP")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_sidebar_pill)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_sidebar_pill, "Register as taken", pIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        return notification;
    }

    public void cancelNotification(int id){
        //Cancel the scheduled reminder
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                id,
                new Intent(this, NotificationPublisher.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        System.out.println("Reminder: " + id + " was deactivated");
    }

    public String getSelectedDaysText(int[] reminder_days) {
        String[] days_abb = new String[]{"Sa", "Su", "Mo", "Tu", "We", "Th", "Fr"};
        String[] reminder_days_abb = new String[reminder_days.length];
        String s = "";
        if (reminder_days.length >= 1) {
            for (int i = 0; i < reminder_days.length; i++) {
                reminder_days_abb[i] = days_abb[reminder_days[i]];
            }
            for (String day_abb : days_abb) {
                if (!day_abb.equals("Sa") && !day_abb.equals("Su")) {
                    if (Arrays.asList(reminder_days_abb).contains(day_abb)) {
                        s += "<b>" + day_abb + ", </b>";
                    } else {
                        s += "<font color=\"#c5c5c5\">" + day_abb + ", " + "</font>";
                    }
                }
            }
            if (Arrays.asList(reminder_days_abb).contains("Sa")) {
                s += "<b>Sa, </b>";
            } else {
                s += "<font color=\"#c5c5c5\">" + "Sa, " + "</font>";
            }
            if(Arrays.asList(reminder_days_abb).contains("Su")) {
                s += "<b>Su</b>";
            } else {
                s += "<font color=\"#c5c5c5\">" + "Su" + "</font>";
            }
        } else {
            s = "<font color=\"#c5c5c5\">" + "Mo, Tu, We, Th, Fr, Sa, Su" + "</font>";
            //s = "Mo, Tu, We, Th, Fr, Sa, Su";
        }
        return s;
    }

    @Override
    public void onPositiveDaysDialogResult(ArrayList selectedDays) {
        NewReminderFragment nrf = (NewReminderFragment) getFragmentManager().findFragmentByTag("NewReminderFragment");
        nrf.setDaysOnLayout(selectedDays);
    }

    @Override
    public void onNegativeDaysDialogResult() {

    }

    @Override
    public void onPositiveMedicationPickerDialogResult(Medication med) {
        NewReminderFragment nrf = (NewReminderFragment) getFragmentManager().findFragmentByTag("NewReminderFragment");
        nrf.setMedicationOnLayout(med);
    }
}

