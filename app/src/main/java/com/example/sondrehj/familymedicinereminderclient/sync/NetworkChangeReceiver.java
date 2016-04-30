package com.example.sondrehj.familymedicinereminderclient.sync;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.path.android.jobqueue.network.NetworkEventProvider;
import com.path.android.jobqueue.network.NetworkUtil;

import retrofit2.Call;
import retrofit2.Response;

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
