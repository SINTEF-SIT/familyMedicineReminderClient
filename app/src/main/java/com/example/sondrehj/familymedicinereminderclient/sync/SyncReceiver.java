package com.example.sondrehj.familymedicinereminderclient.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.LinkingRequestEvent;
import com.example.sondrehj.familymedicinereminderclient.bus.LinkingResponseEvent;
import com.squareup.otto.Bus;

/**
 * Created by nikolai on 20/04/16.
 */
public class SyncReceiver extends BroadcastReceiver {

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
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d("SyncReceiver", extras.getString("action"));
            if (extras.getString("action").equals("open_dialog")) {
                BusService.getBus().post(new LinkingRequestEvent());
            }
            if (extras.getString("action").equals("notifyPositiveResultToLinkingFragment")) {
                BusService.getBus().post(new LinkingResponseEvent("positiveResponse"));
            }
            if (extras.getString("action").equals("notifyNegativeResultToLinkingFragment")) {
                BusService.getBus().post(new LinkingResponseEvent("negativeResponse"));
            }
        }
    }
}