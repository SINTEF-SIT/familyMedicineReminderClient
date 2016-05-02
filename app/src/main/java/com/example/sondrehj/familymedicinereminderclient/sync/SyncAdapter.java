package com.example.sondrehj.familymedicinereminderclient.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;

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
     * This function runs when requestSync is called from anywhere in the
     * android system with the right authority. The functions performs a sync of the content provider.
     * In our use case, the content provider is a stub, and we perform custom syncronization instead.
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

        Log.d(TAG, "Sync is performing");

        String notificationType = extras.getString("notificationType");
        String optionalData = extras.getString("optionalData");
        MyCyFAPPServiceAPI api = RestService.createRestService();
        MySQLiteHelper db = new MySQLiteHelper(getContext());

        Synchronizer synchronizer = new Synchronizer(account.name, api, db, context);
        if (notificationType != null) {
            switch (notificationType) {
                case "remindersChanged":
                    synchronizer.syncReminders();
                    break;
                case "medicationsChanged":
                    synchronizer.syncMedications();
                    break;
                case "linkingRequest":
                    Log.d(TAG, "in switch -> linkingRequest");
                    //incoming linking request from push notification
                    Intent intent = new Intent();
                    intent.setAction("mycyfapp"); //not action, but filter.
                    intent.putExtra("action", "open_dialog");
                    context.sendBroadcast(intent);
                    break;
                case "positiveLinkingResponse":
                    Log.d(TAG, "in switch -> positiveLinkingResponse");
                    Intent intent1 = new Intent();
                    intent1.setAction("mycyfapp");
                    intent1.putExtra("action", "notifyPositiveResultToLinkingFragment");
                    intent1.putExtra("patientID", optionalData);
                    context.sendBroadcast(intent1);
                    break;
                case "negativeLinkingResponse":
                    Log.d(TAG, "in switch -> negativeLinkingResponse");
                    Intent intent2 = new Intent();
                    intent2.setAction("mycyfapp");
                    intent2.putExtra("action", "notifyNegativeResultToLinkingFragment");
                    context.sendBroadcast(intent2);
                    break;
            }
        } else {
            Log.d("SyncAdapter", "notificationType == null");
        }
    }
}