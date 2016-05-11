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
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import java.util.ArrayList;

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
     * <p>
     * http://stackoverflow.com/questions/5268536/how-does-one-listen-for-progress-from-android-syncadapter
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, extras.getString("action"));
            String action = extras.getString("action");
            switch (action) {
                case "open_dialog":
                    BusService.getBus().post(new LinkingRequestEvent());
                    break;
                case "notifyPositiveResultToLinkingFragment":
                    BusService.getBus().post(new LinkingResponseEvent("positiveResponse", extras.getString("patientID")));
                    break;
                case "notifyNegativeResultToLinkingFragment":
                    BusService.getBus().post(new LinkingResponseEvent("negativeResponse"));
                    break;
                case "syncMedications":
                    BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONS));
                    break;
                case "syncReminders":
                    BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERS));
                    break;
                case "medicationSent":
                    BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONSENT));
                    break;
                case "scheduleReminder":
                    BusService.getBus().post(new DataChangedEvent(DataChangedEvent.SCHEDULE_REMINDER, intent.getSerializableExtra("reminder")));
                    break;
                default:
                    break;
            }
        }
        return;
    }
}
