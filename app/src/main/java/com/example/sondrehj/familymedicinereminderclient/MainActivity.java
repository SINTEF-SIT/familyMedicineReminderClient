package com.example.sondrehj.familymedicinereminderclient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.bus.LinkingRequestEvent;
import com.example.sondrehj.familymedicinereminderclient.dialogs.AttachReminderDialogFragment;
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
import com.example.sondrehj.familymedicinereminderclient.notification.NotificationScheduler;
import com.example.sondrehj.familymedicinereminderclient.playservice.RegistrationIntentService;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.sync.DataPublisher;
import com.example.sondrehj.familymedicinereminderclient.sync.NetworkChangeReceiver;
import com.example.sondrehj.familymedicinereminderclient.sync.ServerStatusChangeReceiver;
import com.example.sondrehj.familymedicinereminderclient.sync.SyncReceiver;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewReminderFragment.OnNewReminderInteractionListener,
        ReminderListFragment.OnReminderListFragmentInteractionListener, MedicationListFragment.OnListFragmentInteractionListener,
        TimePickerFragment.TimePickerListener, DatePickerFragment.DatePickerListener, SelectUnitDialogFragment.OnUnitDialogResultListener,
        SelectDaysDialogFragment.OnDaysDialogResultListener,
        EndDatePickerFragment.EndDatePickerListener, MedicationPickerFragment.OnMedicationPickerDialogResultListener, WelcomeFragment.OnWelcomeListener,
        AttachReminderDialogFragment.AttachReminderDialogListener {

    private static String TAG = "MainActivity";
    private SyncReceiver syncReceiver;
    NotificationManager manager;
    private NotificationScheduler notificationScheduler;

    /**
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        //get the accountmanager
        Account account = MainActivity.getAccount(this);


        //Checks if there are accounts on the device. If there aren't, the user is redirected to the welcomeFragment.
        if (account == null) {
            changeFragment(new WelcomeFragment());
            //disables drawer and navigation in welcomeFragment.
            drawer.setDrawerLockMode(drawer.LOCK_MODE_LOCKED_CLOSED);
            //hides ActionBarDrawerToggle
            toggle.setDrawerIndicatorEnabled(false);
        } else {
            ContentResolver.setIsSyncable(account, "com.example.sondrehj.familymedicinereminderclient.content", 1);
            ContentResolver.setSyncAutomatically(account, "com.example.sondrehj.familymedicinereminderclient.content", true);
            changeFragment(new MedicationListFragment());
            //Enables drawer and menu-button
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

        }

        //Sets repeating creation of a Job Manager that will check for upload jobs
        this.manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        //Creates a polling service that checks for server health
        Intent intent = new Intent(this, ServerStatusChangeReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, -2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, new GregorianCalendar().getTimeInMillis(), 60000, pendingIntent);


        // NotificationScheduler
        this.notificationScheduler = new NotificationScheduler(this);

        //Default account settings
        SharedPreferences sharedPrefs = getSharedPreferences("AccountSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor;
        if (!sharedPrefs.contains("initialized")) {
            editor = sharedPrefs.edit();
            // Indicate that the default shared prefs have been set
            editor.putBoolean("initialized", true);
            // Set default values
            editor.putInt("snoozeDelay", 5); // 5 minutes
            editor.putInt("gracePeriod", 30);// 30 minutes
            editor.putBoolean("reminderSwitch", true);
            editor.putBoolean("notificationSwitch", true);
            editor.apply();
        }

        System.out.println(getIntent().toString());

        // The items inside the grey area of the drawer.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Enforce rotation-lock.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //TODO: Look more at how database changes can be broadcasted to the system
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (intent != null) {
            onNewIntent(intent);
            setIntent(null);
        }
    }

    /**
     * Gets the instantiazed account of the system, used with the SyncAdapter and
     * ContentResolver, might have to be moved sometime.
     *
     * @return
     */

    public static Account getAccount(Context context) {
        Account[] accountArray = AccountManager.get(context).getAccountsByType("com.example.sondrehj.familymedicinereminderclient");
        if (accountArray.length >= 1) {
            return accountArray[0];
        }
        return null;
    }

    /**
     * Registering the SyncReceiver to receive intents from the SyncAdapter, we need this
     * because the Bus cannot register to the SyncAdapter (it is another process altogether).
     * Registering the activity to the event bus.
     */
    @Override
    public void onResume() {
        super.onResume();
        BusService.getBus().register(this);
        syncReceiver = new SyncReceiver();
        IntentFilter intentFilter = new IntentFilter("mycyfapp");
        registerReceiver(syncReceiver, intentFilter);
    }

    /**
     * Unregister the activity from the bus.
     * Unregister the receiver so that intents aren't received when the application is paused.
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        BusService.getBus().unregister(this);
        unregisterReceiver(syncReceiver);
    }

    /**
     * Handle a LinkingRequest sent to the patient on the bus. Opens a LinkingDialogFragment
     * which asks the patient wether it wants to link itself with a guardian account upon request.
     *
     * Flow:
     * 1. Guardian presses link
     * 2. Guardian sends rest call to the server
     * 3. Server sends notification to patient
     * 4. MyGCMListener asks for sync with notificationType in extras bundle
     * 5. SyncAdapter sends a intent to the SyncReceiver.
     * 6. SyncReceiver sends a LinkingRequestEvent out on the event bus which MainActivity is registered to.
     * 7. MainActivity of patient handles incoming linking request
     *
     * @param event
     */
    @Subscribe
    public void handleLinkingRequest(LinkingRequestEvent event) {
        Log.d(TAG, "Handled linking request by opening Linking Dialog.");
        FragmentManager fm = getSupportFragmentManager();
        LinkingDialogFragment linkingDialogFragment = new LinkingDialogFragment();
        linkingDialogFragment.show(fm, "linking_request_fragment");
    }

    @Subscribe
    public void handleMedicationPostedRequest(DataChangedEvent event) {
        if (event.type.equals(DataChangedEvent.MEDICATIONSENT)) {
            Medication medication = (Medication) event.data;
            System.out.println("Medication about to be saved: " + medication);
            new MySQLiteHelper(this).updateMedication(medication);
        }
    }

    @Subscribe
    public void handleReminderPostedRequest(DataChangedEvent event) {
        if (event.type.equals(DataChangedEvent.REMINDERSENT)) {
            Reminder reminder = (Reminder) event.data;
            System.out.println("Reminder about to be saved: " + reminder);
            new MySQLiteHelper(this).updateReminder(reminder);
        }
    }

    /**
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

    public JobManager getJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .networkUtil(new ServerStatusChangeReceiver())
                .build();
        return new JobManager(this, configuration);
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
        //change the header to which fragment you are on
        TitleSupplier titleSupplier = (TitleSupplier) fragment;
        setTitle(titleSupplier.getTitle());
    }

    /**
     * Called when a notification is clicked. If the intent contains a reminder with a medication attached,
     * the amount of "units" decreases by the given dosage.
     *
     * @param intent the intent instance created by getNotification(String content, Reminder reminder)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Reminder reminder = (Reminder) intent.getSerializableExtra("notification-reminder");
        String notificationAction = intent.getStringExtra("notification-action");

        if(notificationAction != null) {
            switch (notificationAction) {
                case "notificationRegular":
                    notificationScheduler.handleNotificationMainClick(reminder);
                    break;
                case "notificationSnooze":
                    notificationScheduler.handleNotificationSnoozeClick(reminder);
                    break;
                case "medicationChanged":
                    Bundle extras = new Bundle();
                    extras.putString("notificationType", notificationAction);
                    extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                    extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

                    ContentResolver.requestSync(
                            MainActivity.getAccount(getApplicationContext()),
                            "com.example.sondrehj.familymedicinereminderclient.content",
                            extras);
                    break;
            }
        }
        setIntent(null);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
     * TODO: Disallow usage of the application before the user fixes this error.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    public void deleteAllApplicationData(){

        // Wipe the local database
        this.deleteDatabase("familymedicinereminderclient.db");
        // Wipe account settings stored by SharedPreferences
        this.getSharedPreferences("AccountSettings", 0).edit().clear().commit();
        // TODO: clear all pendingIntents in AlarmManager
        // TODO: wipe server data & account manager
    }

    /**
     * Function called by WelcomeFragment to save/add account to the AccountManager and
     * fetch a gcm token which is sent to the server and associated with the user.
     * @param userId
     * @param password
     * @param userRole
     */

    @Override
    public void OnNewAccountCreated(String userId, String password, String userRole) {
        Account newAccount = new Account(userId, "com.example.sondrehj.familymedicinereminderclient");
        AccountManager manager = AccountManager.get(this);
        boolean saved = manager.addAccountExplicitly(newAccount, password, null);
        if (saved) {
            manager.setUserData(newAccount, "passtoken", password);
            manager.setUserData(newAccount, "userId", userId);
            manager.setUserData(newAccount, "userRole", userRole);
            System.out.println(newAccount.toString());
            System.out.println(AccountManager.get(getApplicationContext()).getUserData(newAccount, "userRole"));
        }

        //Enables drawer and menu-button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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
            // Schedule the notification
            notificationScheduler.scheduleNotification(
                    notificationScheduler.getNotification("Take your medication", r), r);
        }
        BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERS));
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
        if (reminder.getIsActive()) {
            notificationScheduler.cancelNotification(reminder.getReminderId());
        }

        // Delete reminder from local database
        MySQLiteHelper db = new MySQLiteHelper(this);
        db.deleteReminder(reminder);
    }

    @Override
    public void onReminderListSwitchClicked(Reminder reminder) {

        if (reminder.getIsActive()) {

            // Cancel the scheduled reminder
            notificationScheduler.cancelNotification(reminder.getReminderId());
            reminder.setIsActive(false);
        } else {

            // Activate the reminder
            notificationScheduler.scheduleNotification(
                    notificationScheduler.getNotification("Take your medication", reminder), reminder);
            reminder.setIsActive(true);
            System.out.println("Reminder: " + reminder.getReminderId() + " was activated");
        }
        // Updates the DB
        MySQLiteHelper db = new MySQLiteHelper(this);
        db.updateReminder(reminder);
    }

    @Override
    public void onPositiveAttachReminderDialogResult() {
        changeFragment(NewReminderFragment.newInstance(null));
    }
}