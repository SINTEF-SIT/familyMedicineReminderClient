package com.example.sondrehj.familymedicinereminderclient.sync;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.bus.LinkingRequestEvent;
import com.example.sondrehj.familymedicinereminderclient.bus.LinkingResponseEvent;
import com.example.sondrehj.familymedicinereminderclient.dialogs.SetAliasDialog;

/**
 * Created by nikolai on 20/04/16.
 */
public class SyncReceiver extends BroadcastReceiver {
    private static String TAG = "SyncReceiver";

    /**
     * When the SyncReceiver receives an intent from the SyncAdapter or elsewhere, it posts the event you
     * wanted to post in the SyncAdapter here instead. It works here because the bus is
     * registered to the process already (in MainActivity). You cannot register the bus to the
     * SyncAdapter.
     *
     * http://stackoverflow.com/questions/5268536/how-does-one-listen-for-progress-from-android-syncadapter
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("received intent");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, extras.getString("action"));
            String action = extras.getString("action");

            if (action.equals("open_dialog")) {
                BusService.getBus().post(new LinkingRequestEvent());
            }
            if (action.equals("notifyPositiveResultToLinkingFragment")) {
                String patientID = extras.getString("patientID");
                BusService.getBus().post(new LinkingResponseEvent("positiveResponse", patientID));
            }
            if (action.equals("notifyNegativeResultToLinkingFragment")) {
                BusService.getBus().post(new LinkingResponseEvent("negativeResponse"));

            }
            if (action.equals("syncMedications")) {
                System.out.println("posted datachanged event");
                BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONS));
            }

            if (action.equals("syncReminders")) {
                System.out.println("posted datachanged event");
                BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERS));
            }
            if (action.equals("medicationSent")) {
                System.out.println("posted medication");
                BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONSENT));
            }
            if (action.equals("scheduleReminder")) {
                System.out.println("Scheduling reminder");
                BusService.getBus().post(new DataChangedEvent(DataChangedEvent.SCHEDULE_REMINDER, intent.getSerializableExtra("reminder")));
            }
        }
    }
}