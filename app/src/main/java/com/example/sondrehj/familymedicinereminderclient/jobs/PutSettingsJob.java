package com.example.sondrehj.familymedicinereminderclient.jobs;

import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.User;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;

public class PutSettingsJob extends Job {
    private static String TAG = "PutSettingsJob";
    private static final int PRIORITY = 1;
    private String gracePeriod;
    private String userId;

    public PutSettingsJob(String userId, String gracePeriod) {
        // This job requires network connectivity,
        // and should be persisted in case the application exits before job is completed.

        super(new Params(PRIORITY)
                .requireNetwork()
                .persist());
        Log.d(TAG, "New Job posted.");
        this.userId = userId;
        this.gracePeriod = gracePeriod;
    }

    @Override
    public void onAdded() {

        // Job has been saved to disk.
        // This is a good place to dispatch a UI event to indicate the job will eventually run.
        // In this example, it would be good to update the UI with the newly posted tweet.
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job is running in background.");
        Log.d(TAG, Thread.currentThread().toString());

        MyCyFAPPServiceAPI api = RestService.createRestService();
        Call<User> call = api.setGracePeriod(userId, gracePeriod);
        call.execute(); //synchronous call
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        throwable.printStackTrace();
        return true;

        // An error occurred in onRun.
        // Return value determines whether this job should retry running (true) or abort (false).
    }

    @Override
    protected void onCancel() {
        // Job has exceeded retry attempts or shouldReRunOnThrowable() has returned false.
    }
}
