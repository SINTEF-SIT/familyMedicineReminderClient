package com.example.sondrehj.familymedicinereminderclient.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.jobs.JobManagerService;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.network.NetworkEventProvider;
import com.path.android.jobqueue.network.NetworkUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import retrofit2.Call;

/**
 *
 * This class is used by JobManager to determine the status of the network. If there is network connectivity,
 * it will attempt to poll the server to make sure it is online. The class maintains two static variables to determine
 * whether a change in the serverStatus occurred. If the server status changed from offline to online, the listener's
 * onNetworkChange method is called. The listener in our case is a JobManager, and the JobManager's onNetworkChange
 * propmts running of the queued jobs.
 *
 */
public class ServerStatusChangeReceiver extends BroadcastReceiver implements NetworkUtil, NetworkEventProvider {
    private static String TAG = "ServerStatusChangeReceiver";
    public NetworkEventProvider.Listener listener;
    private static Boolean previousServerStatus = false;
    private static Boolean currentServerStatus = false;


    @Override
    public void onReceive(Context context, Intent intent) {

        Account acct = MainActivity.getAccount(context);
        if(acct == null) {
            previousServerStatus = currentServerStatus = false;
            return;
        }
        String authToken = AccountManager.get(context).getUserData(acct,"authToken");

        //Sends a HEAD request to the server to determine connectivity
        try {
            Future pollingFuture = Executors.newFixedThreadPool(1).submit(() -> {
                MyCyFAPPServiceAPI api = RestService.createRestService(authToken);
                Call<Void> call = api.sendPollingRequest();
                Boolean result = call.clone().execute().isSuccessful();
                Log.d(TAG, "Poll result: " + result);
                return result;
            });
            while (!pollingFuture.isDone());
            Boolean pollingResult = (Boolean) pollingFuture.get();

            //If we have network connectivity and the polling succeeded, we have connectivity.
            currentServerStatus = ServiceManager.isNetworkAvailable(context) && pollingResult;
            Log.d(TAG, "Previous server status: " + previousServerStatus);
            Log.d(TAG, "Current server status: " + currentServerStatus);
        } catch (Exception e) {
            //If an error occurred, we say that there is no connectivity
            Log.d(TAG, "Exception: " + e.toString());
            currentServerStatus = false;
        }

        //If the status changed from offline to online, we want to notify the listener
        if(currentServerStatus &&  !previousServerStatus) {
            Log.d(TAG, "currentServerStatus:TRUE & previousServerStatus:FALSE");
            JobManagerService.getJobManager(context);
            JobManagerService.changeReceiver.listener.onNetworkChange(currentServerStatus);
        }
        //Update previousServerStatus
        previousServerStatus = currentServerStatus;
    }

    @Override
    public boolean isConnected(Context context) {
        return currentServerStatus;
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
