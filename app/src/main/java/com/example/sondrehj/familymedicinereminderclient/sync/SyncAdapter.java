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

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
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


        String authToken = AccountManager.get(context).getUserData(MainActivity.getAccount(context), "authToken");
        MyCyFAPPServiceAPI api = RestService.createRestService(authToken);
        String notificationType = extras.getString("notificationType");
        MySQLiteHelper db = new MySQLiteHelper(getContext());

        Synchronizer synchronizer = new Synchronizer(account.name, api, db, context);
        Intent intent = new Intent();
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
                    intent.setAction("mycyfapp"); //not action, but filter.
                    intent.putExtra("action", "open_dialog");
                    context.sendBroadcast(intent);
                    break;
                case "positiveLinkingResponse":
                    Log.d(TAG, "in switch -> positiveLinkingResponse");
                    intent.setAction("mycyfapp");
                    intent.putExtra("action", "notifyPositiveResultToLinkingFragment");
                    context.sendBroadcast(intent);
                    break;
                case "negativeLinkingResponse":
                    Log.d(TAG, "in switch -> negativeLinkingResponse");
                    intent.setAction("mycyfapp");
                    intent.putExtra("action", "notifyNegativeResultToLinkingFragment");
                    context.sendBroadcast(intent);
                    break;
            }
        } else {
            Log.d("SyncAdapter", "notificationType == null");
        }
    }
}