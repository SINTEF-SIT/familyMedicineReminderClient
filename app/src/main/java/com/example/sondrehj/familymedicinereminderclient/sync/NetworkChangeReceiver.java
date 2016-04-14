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
 * TODO: extend WakefulBroadcastReceiver?
 */
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
        if(checkInternet(context)) {
            //Toast.makeText(context, "Network available - do stuff!", Toast.LENGTH_LONG).show();
            ContentResolver.setSyncAutomatically(
                    MainActivity.getAccount(),
                    AUTHORITY,
                    true);
            ContentResolver.requestSync(
                    MainActivity.getAccount(),
                    AUTHORITY,
                    Bundle.EMPTY);
        }
    }

    /**
     * Runs the helper ServiceManager to check whether the network is available or not.
     * @param context
     * @return
     */
    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }
}
