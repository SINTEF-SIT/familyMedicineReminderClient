package com.example.sondrehj.familymedicinereminderclient.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.LinkingRequestEvent;
import com.example.sondrehj.familymedicinereminderclient.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nikolai on 07/04/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static String TAG = "SyncAdapter";
    private Context context;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    /**
     * This function is ran when requestSync is called from anywhere in the
     * android system with the right authority. The functions performs a sync of the data.
     *
     * @param account
     * @param extras
     * @param authority
     * @param provider
     * @param syncResult
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {


        Log.d("Sync", "Sync is performing");

        String notificationType = extras.getString("notificationType");
        MyCyFAPPServiceAPI api = RestService.createRestService();
        Synchronizer synchronizer = new Synchronizer(account.name, api);
        if (notificationType != null) {
            switch (notificationType) {
                case "remindersChanged":
                    synchronizer.syncReminders();
                    break;
                case "medicationsChanged":
                    //syncMedications();
                    break;
                case "linkingRequest":
                    Log.d(TAG, "in switch -> linkingRequest");
                    //incoming linking request from push notification
                    Intent intent = new Intent();
                    intent.setAction("openDialog");
                    intent.putExtra("action", "open_dialog");
                    context.sendBroadcast(intent);
                    break;
                case "positiveLinkingResponse":
                    Log.d(TAG, "in switch -> positiveLinkingResponse");
                    Intent intent1 = new Intent();
                    intent1.setAction("openDialog");
                    intent1.putExtra("action", "notifyPositiveResultToLinkingFragment");
                    context.sendBroadcast(intent1);
                    break;
                case "negativeLinkingResponse":
                    Log.d(TAG, "in switch -> negativeLinkingResponse");
                    Intent intent2 = new Intent();
                    intent2.setAction("openDialog");
                    intent2.putExtra("action", "notifyNegativeResultToLinkingFragment");
                    context.sendBroadcast(intent2);
                    break;
            }
        } else {
            Log.d("SyncAdapter", "notificationType == null");
        }
    }
}