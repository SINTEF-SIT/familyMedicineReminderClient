package com.example.sondrehj.familymedicinereminderclient.playservice;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * GCM Push Notification receiver. Handles the received messages in onMessageReceived().
 *
 * This GcmListenerService listens for notifications from the Google Cloud Messaging Service.
 *
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private static final String AUTHORITY = "com.example.sondrehj.familymedicinereminderclient.content";

    /**
     * Called when message is received. Posts a notification to the device if the device is killed.
     * If the device is running, an appropriate action will be taken based on the notification type.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String notificationType = data.getString("notification-action");
        String optionalData = data.getString("data");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Notification type: " + notificationType);
        Log.d(TAG, "Data (optional): " + optionalData);

        //Intent for notification
        final Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction("mycyfapp");
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.putExtra("notification-action", notificationType);
        notificationIntent.putExtra("currentUserId", optionalData);
        sendBroadcast(notificationIntent);

        //Bundle that is passed to the synchronization
        Bundle extras = new Bundle();
        extras.putString("notificationType", notificationType);
        extras.putString("optionalData", optionalData);
        extras.putString("currentUserId", optionalData);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        //Performs a sync with the extras bundle
        ContentResolver.requestSync(
                MainActivity.getAccount(getApplicationContext()),
                AUTHORITY,
                extras);
    }
}