package com.example.sondrehj.familymedicinereminderclient.playservice;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * GCM Push Notification receiver. Handles the received messages in onMessageReceived().
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    // Content provider authority
    public static final String AUTHORITY = "com.example.sondrehj.familymedicinereminderclient.content";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String notificationType = data.getString("notification-action");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Notification type: " + notificationType);


        final Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction("mycyfapp");
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.putExtra("notification-action", notificationType);

        sendBroadcast(notificationIntent);

        Bundle extras = new Bundle();
        extras.putString("notificationType", notificationType);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(
                MainActivity.getAccount(getApplicationContext()),
                AUTHORITY,
                extras);

        //TODO: Put the above code for synchronization in a separate class, so we can reuse it

    }
}