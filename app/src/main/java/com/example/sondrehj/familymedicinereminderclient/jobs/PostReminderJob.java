package com.example.sondrehj.familymedicinereminderclient.jobs;

import android.renderscript.RenderScript;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.models.TransportReminder;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;

public class PostReminderJob extends Job {
    private static final int PRIORITY = 1;

    private Reminder reminder;
    private String userId;
    private String authToken;
    private final String TAG = "PostReminerJob";

    public PostReminderJob(Reminder reminder, String userId, String authToken) {
        // This job requires network connectivity,
        // and should be persisted in case the application exits before job is completed.

        super(new Params(PRIORITY)
                .requireNetwork()
                .persist());
        this.reminder = reminder;
        this.userId = userId;
        this.authToken = authToken;
    }

    @Override
    public void onAdded() {
        // Job has been saved to disk. This means that the job is persisted and the application can fail without
        // consequence for the job queue.
    }

    @Override
    public void onRun() throws Throwable {

        MyCyFAPPServiceAPI api = RestService.createRestService(authToken);
        TransportReminder transReminder = new TransportReminder(reminder);
        Call<TransportReminder> call = api.createReminder(userId, transReminder);
        TransportReminder transportReminder = call.execute().body();
        if(transportReminder != null) {
            reminder.updateFromTransportReminder(transportReminder);
            BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERSENT, reminder));
        }
        else {
            Log.e(TAG, "Transportreminder was null");
        }
    }

    @Override
    protected void onCancel() {
        //Here we would normally cancel the request if completion was impossible, but we do not do this
        //Instead we persist the request and try to run it again when the


    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Exception in reminder job: ");
        throwable.printStackTrace();
        return true;
    }
}
