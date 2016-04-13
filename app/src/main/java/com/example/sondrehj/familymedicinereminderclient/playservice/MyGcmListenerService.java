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
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);


        final Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        ContentResolver.requestSync(
                MainActivity.getAccount(),
                AUTHORITY,
                Bundle.EMPTY);
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */
    }
}