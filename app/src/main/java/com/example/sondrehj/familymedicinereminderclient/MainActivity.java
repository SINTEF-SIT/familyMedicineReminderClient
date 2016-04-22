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
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.LinkingRequestEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MedicationListContent;
import com.example.sondrehj.familymedicinereminderclient.database.ReminderListContent;
import com.example.sondrehj.familymedicinereminderclient.fragments.AccountAdministrationFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.DatePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.fragments.GuardianDashboardFragment;
import com.example.sondrehj.familymedicinereminderclient.fragments.LinkingFragment;
import com.example.sondrehj.familymedicinereminderclient.fragments.MedicationListFragment;
import com.example.sondrehj.familymedicinereminderclient.fragments.MedicationStorageFragment;
import com.example.sondrehj.familymedicinereminderclient.fragments.NewReminderFragment;
import com.example.sondrehj.familymedicinereminderclient.fragments.ReminderListFragment;
import com.example.sondrehj.familymedicinereminderclient.fragments.WelcomeFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.EndDatePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.LinkingDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.MedicationPickerFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.SelectDaysDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.SelectUnitDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.TimePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.notification.NotificationPublisher;
import com.example.sondrehj.familymedicinereminderclient.playservice.RegistrationIntentService;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.sync.SyncReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewReminderFragment.OnNewReminderInteractionListener,
        ReminderListFragment.OnReminderListFragmentInteractionListener, MedicationListFragment.OnListFragmentInteractionListener,
        TimePickerFragment.TimePickerListener, DatePickerFragment.DatePickerListener, SelectUnitDialogFragment.OnUnitDialogResultListener,
        SelectDaysDialogFragment.OnDaysDialogResultListener,
        EndDatePickerFragment.EndDatePickerListener, MedicationPickerFragment.OnMedicationPickerDialogResultListener, WelcomeFragment.OnWelcomeListener {

    private SyncReceiver syncReceiver;
    NotificationManager manager;

    /**
     * Main entry point of the application. When onCreate is run, view is filled with the
     * layout activity_main in res. The fragment container which resides in the contentView is
     * changed to "MediciationListFragment()" with the changeFragment() function call.
     *
     * In addition, the Sidebar/Drawer is instantiated.
     *
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

        //get the accountmanager
        Account account = MainActivity.getAccount(this);

        //Checks if there are accounts on the device. If there aren't, the user is redirected to the welcomeFragment.
        if(account == null) {
            changeFragment(new WelcomeFragment());
            //TODO: Disable drawer and navigation when in welcomeFragment
        }
        else {
            ContentResolver.setIsSyncable(account, "com.example.sondrehj.familymedicinereminderclient.content", 1);
            ContentResolver.setSyncAutomatically(account, "com.example.sondrehj.familymedicinereminderclient.content", true);
            changeFragment(new MedicationListFragment());
        }

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

        //Make sure the medication and reminder content is in sync with the database
        refreshMedicationContent(db);
        MainActivity.refreshReminderContent(db);
        //TODO: Look more at how database changes can be broadcasted to the system
    }

    public void refreshMedicationContent(MySQLiteHelper db) {
        ArrayList<Medication> meds = db.getMedications();
        Collections.reverse(meds);
        MedicationListContent.ITEMS.clear();
        MedicationListContent.ITEMS.addAll(meds);
        //MedicationListFragment fragment = (MedicationListFragment) getFragmentManager().findFragmentByTag("MedicationListFragment");
        //fragment.notifyChanged();
    }

    public static void refreshReminderContent(MySQLiteHelper db) {
        ArrayList<Reminder> reminders = db.getReminders();
        Collections.reverse(reminders);
        ReminderListContent.ITEMS.addAll(reminders);
    }

    /**
     * Gets the instantiazed account of the system, used with the SyncAdapter and
     * ContentResolver, might have to be moved sometime.
     * @return
     */

    public static Account getAccount(Context context) {
        return AccountManager.get(context).getAccountsByType("com.example.sondrehj.familymedicinereminderclient")[0];
    }

    @Override
    public void onResume(){
        super.onResume();

        //registering the event bus
        BusService.getBus().register(this);

        /**
         * Registering the SyncReceiver to receive intents from the SyncAdapter, we need this
         * because the Bus cannot register to the SyncAdaptar (it is another process altogether).
         */
        syncReceiver = new SyncReceiver();
        IntentFilter intentFilter = new IntentFilter("openDialog");
        registerReceiver(syncReceiver, intentFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        //unregistering to prevent errors
        BusService.getBus().unregister(this);
        unregisterReceiver(syncReceiver);
    }

    @Subscribe
    public void handleLinkingRequest(LinkingRequestEvent event) {
        Log.d("Main", "Handled linking request");
        FragmentManager fm = getSupportFragmentManager();
        LinkingDialogFragment linkingDialogFragment = new LinkingDialogFragment();
        linkingDialogFragment.show(fm, "linking_request_fragment");
    }

    /**
     *
     * Closes the drawer when the back button is pressed.
     */
    //TODO: Make the application quit after the last fragment is popped from the fragmentStack, instead of showing the activity's content
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

    /**
     * Takes in a fragment which is to replace the fragment which is already in the fragmentcontainer
     * of MainActivity.
     *
     * @param fragment
     */
    public void changeFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        System.out.println("Navigated to: " + fragment.getClass().getSimpleName());
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
     * Called by timepicker in NewReminder
     *
     * @param hourOfDay
     * @param minute
     */

    //TODO: Move out of MainActivity if possible
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
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        // Schedules a non-repeating notification
        else {
            if (!reminder.getDate().before(cal)) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }
        }
    }

    private Notification getNotification(String content, Reminder reminder) {

        //Defines the Intent of the notification
        Intent intent = new Intent(this, this.getClass());
        intent.putExtra("notification-reminder", reminder);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        //Constructs the notification
        Notification notification = new Notification.Builder(MainActivity.this)
                .setContentTitle("MYCYFAPP")
                .setContentText(reminder.getName())
                .setSmallIcon(R.drawable.ic_sidebar_pill)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_sidebar_pill, "Register as taken", pIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        return notification;
    }

    /**
     * Called when a notification is clicked. If the intent contains a reminder with a medication attached,
     * the amount of "units" decreases by the given dosage.
     *
     * @param intent the intent instance created by getNotification(String content, Reminder reminder)
     */
    protected void onNewIntent(Intent intent) {

        Reminder reminder = (Reminder) intent.getSerializableExtra("notification-reminder");
        if(reminder != null) {
            System.out.println("--------Notification Pressed--------");
            System.out.println(" Notification for reminder: " + reminder.getName());
            if (reminder.getMedicine() != null) {
                System.out.println(" Medication attached: " + reminder.getMedicine().getName());
                System.out.println(" Number of medication units: " + reminder.getMedicine().getCount());
                System.out.println(" Reducing by: " + reminder.getDosage());
                reminder.getMedicine().setCount(reminder.getMedicine().getCount() - reminder.getDosage());
                System.out.println(" New value: " + reminder.getMedicine().getCount());

                // Updates MedicationListViewFragment with new data.
                for (int i = 0; i < MedicationListContent.ITEMS.size(); i++) {
                    if (MedicationListContent.ITEMS.get(i).getMedId() == reminder.getMedicine().getMedId()) {
                        MedicationListContent.ITEMS.set(i, reminder.getMedicine());
                    }
                }

                // Updates the DB
                MySQLiteHelper db = new MySQLiteHelper(this);
                db.updateAmountMedication(reminder.getMedicine());
                Toast.makeText(this, "Registered as taken", Toast.LENGTH_LONG).show();
            }
            System.out.println("------------------------------------");
        }
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

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i("main", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void OnNewAccountCreated(String userId, String password, String userRole) {
        Account newAccount = new Account(userId, "com.example.sondrehj.familymedicinereminderclient");
        AccountManager manager = AccountManager.get(this);
        boolean saved = manager.addAccountExplicitly(newAccount, password, null);
        if (saved){
            manager.setUserData(newAccount, "passtoken", password);
            manager.setUserData(newAccount, "userId", userId);
            manager.setUserData(newAccount, "userRole", userRole);
            System.out.println(newAccount.toString());
            System.out.println(AccountManager.get(getApplicationContext()).getUserData(newAccount, "userRole"));
        }
        ContentResolver.setIsSyncable(newAccount, "com.example.sondrehj.familymedicinereminderclient.content", 1);
        ContentResolver.setSyncAutomatically(newAccount, "com.example.sondrehj.familymedicinereminderclient.content", true);

        //check if google play services are enabled (required for GCM).
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM and
            // associate user's token with the user on the back-end server.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        changeFragment(new MedicationListFragment());
    }

    @Override
    public void onPositiveDaysDialogResult(ArrayList selectedDays) {
        NewReminderFragment nrf = (NewReminderFragment) getFragmentManager().findFragmentByTag("NewReminderFragment");
        nrf.setDaysOnLayout(selectedDays);
    }

    @Override
    public void onPositiveMedicationPickerDialogResult(Medication med) {
        NewReminderFragment nrf = (NewReminderFragment) getFragmentManager().findFragmentByTag("NewReminderFragment");
        nrf.setMedicationOnLayout(med);
    }

    @Override
    public void setTime(int hourOfDay, int minute) {
        NewReminderFragment newReminderFragment = (NewReminderFragment) getFragmentManager().findFragmentByTag("NewReminderFragment");
        newReminderFragment.setTimeOnLayout(hourOfDay, minute);
    }

    /**
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

    /**
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
        if (id == R.id.nav_reminders) {
            changeFragment(ReminderListFragment.newInstance());
        } else if (id == R.id.nav_medication) {
            changeFragment(MedicationListFragment.newInstance());
        } else if (id == R.id.nav_settings) {
            //TODO: fill inn changefragment to settings fragment
            changeFragment(AccountAdministrationFragment.newInstance());
        } else if (id == R.id.nav_guardian_dashboard) {
            changeFragment(new GuardianDashboardFragment());
        } else if (id == R.id.nav_linking) {
            changeFragment(LinkingFragment.newInstance());
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
    public void onSaveNewReminder(Reminder r) {

        if (r.getIsActive()) {
            // Activate the reminder
            scheduleNotification(getNotification("Take your medication", r), r);
            r.setIsActive(true);
        }
        // Updates the DB
        MySQLiteHelper db = new MySQLiteHelper(this);
        db.updateReminder(r);

        changeFragment(ReminderListFragment.newInstance());
    }

    @Override
    public void onPositiveUnitDialogResult(int unit) {
        String[] units = getResources().getStringArray(R.array.unit_items);
        MedicationStorageFragment sf = (MedicationStorageFragment) getFragmentManager().findFragmentByTag("MedicationStorageFragment");
        sf.setUnitText(units[unit]);
    }

    @Override
    public void onReminderListItemClicked(Reminder reminder) {
        changeFragment(NewReminderFragment.newInstance(reminder));
    }

    @Override
    public void onReminderDeleteButtonClicked(Reminder reminder) {

        // Cancel notification if set
        if(reminder.getIsActive()){
            cancelNotification(reminder.getReminderId());
        }

        // Delete reminder from local database
        MySQLiteHelper db = new MySQLiteHelper(this);
        db.deleteReminder(reminder);
    }

    @Override
    public void onReminderListSwitchClicked(Reminder reminder) {

        if (reminder.getIsActive()) {

            // Cancel the scheduled reminder
            cancelNotification(reminder.getReminderId());
            reminder.setIsActive(false);
        } else {

            // Activate the reminder
            scheduleNotification(getNotification("Take your medication", reminder), reminder);
            reminder.setIsActive(true);
            System.out.println("Reminder: " + reminder.getReminderId() + " was activated");
        }
        // Updates the DB
        MySQLiteHelper db = new MySQLiteHelper(this);
        db.updateReminder(reminder);
    }
}