package com.example.sondrehj.familymedicinereminderclient.sync;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
    @Override
    public void onReceive(Context context, Intent intent) {
        if(ServiceManager.isNetworkAvailable(context)) {
            //Toast.makeText(context, "Network available - do stuff!", Toast.LENGTH_LONG).show();
            ContentResolver.setSyncAutomatically(
                    MainActivity.getAccount(context),
                    AUTHORITY,
                    true);
            ContentResolver.requestSync(
                    MainActivity.getAccount(context),
                    AUTHORITY,
                    Bundle.EMPTY);
        }
    }
}
