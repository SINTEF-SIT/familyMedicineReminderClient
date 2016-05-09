package com.example.sondrehj.familymedicinereminderclient.jobs;

import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Eirik on 09/05/16.
 */
public class DeleteReminderJob extends Job {

    private static String TAG = "DeleteReminderJob";
    private static final int PRIORITY = 1;
    private Reminder reminder;
    private String userId;
    private String authToken;

    public DeleteReminderJob(Reminder reminder, String userId, String authToken) {
        // This job requires network connectivity,
        // and should be persisted in case the application exits before job is completed.

        super(new Params(PRIORITY)
                .requireNetwork()
                .persist());
        Log.d(TAG, "New delete reminder job posted.");
        this.reminder = reminder;
        this.userId = userId;
        this.authToken = authToken;
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
        MyCyFAPPServiceAPI api = RestService.createRestService(authToken);
        Call<Boolean> call = api.deleteReminder(userId, reminder.getServerId() + ""); //Will get problems here due if serverID = -1
        Response res = call.execute();
        Log.d(TAG, "Delete response: " + res.raw().toString());
        if (res.isSuccessful()) {
            Log.d(TAG, "deleted: ");
            //BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERS));
        } else {
            Log.d(TAG, "Server did not respond to deletion request");
        }
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
