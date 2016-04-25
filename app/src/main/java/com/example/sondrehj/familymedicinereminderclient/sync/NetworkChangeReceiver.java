package com.example.sondrehj.familymedicinereminderclient.sync;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;

/**
 * Created by nikolai on 08/04/16.
 */

/**
 * A class that extends the BroadcastReceiver class, and implements the function onReceive.
 * It functions to notify of network changes on the device.
 *
 * */
public class NetworkChangeReceiver extends BroadcastReceiver {

    // Content provider authority
    public static final String AUTHORITY = "com.example.sondrehj.familymedicinereminderclient.content";
    /**
     * onReceive is triggered when network state changes, it is configured in the AndroidManifest.xml.
     * When change is noticed, it should trigger a sync in the SyncAdapter framework.
     * @param context
     * @param intent
     */

    //TODO: Fix bug that appears when turning internet on and off (in welcomefragment, but possibly everywhere)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ServiceManager.isNetworkAvailable(context)) {
            try {
                ContentResolver.setSyncAutomatically(
                    MainActivity.getAccount(context),
                    AUTHORITY,
                    true);
                ContentResolver.requestSync(
                    MainActivity.getAccount(context),
                    AUTHORITY,
                    Bundle.EMPTY);
            } catch (Exception e) { //TODO: runs even when the application is in the background.
                Toast.makeText(context, "Make a user account by restarting the application.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
