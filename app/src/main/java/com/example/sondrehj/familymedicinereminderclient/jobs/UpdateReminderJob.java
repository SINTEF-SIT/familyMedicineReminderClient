package com.example.sondrehj.familymedicinereminderclient.jobs;

import android.renderscript.RenderScript;

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

/**
 * Created by Eirik on 30.04.2016.
 */
public class UpdateReminderJob extends Job {
    private static final int PRIORITY = 1;

    private Reminder reminder;
    private String userId;
    private TransportReminder preSendTransportReminder;

    public UpdateReminderJob(Reminder reminder, String userId) {
        // This job requires network connectivity,
        // and should be persisted in case the application exits before job is completed.

        super(new Params(PRIORITY)
                .requireNetwork()
                .persist());
        System.out.println("New reminder job posted");
        this.reminder = reminder;
        this.userId = userId;
        this.preSendTransportReminder = new TransportReminder(reminder);

    }

    @Override
    public void onAdded() {
        System.out.println("In reminder job's onAdded");
        // Job has been saved to disk. This means that the job is persisted and the application can fail without
        // consequence for the job queue.
    }

    @Override
    public void onRun() throws Throwable {

        MyCyFAPPServiceAPI api = RestService.createRestService();
        System.out.println("Transreminder before sending: " + preSendTransportReminder);
        Call<TransportReminder> call = api.updateReminder(userId, String.valueOf(preSendTransportReminder.getServerId()), preSendTransportReminder);
        TransportReminder transportReminder = call.execute().body();
        if(transportReminder != null) {
            System.out.println("Received transportreminder: " + transportReminder);
            reminder.updateFromTransportReminder(transportReminder);
            System.out.println(reminder);
            BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERSENT, reminder));
        }
        else {
            System.out.println("reminder returned from server was null");
        }
    }

    @Override
    protected void onCancel() {
        //Here we would normally cancel the request if completion was impossible, but we do not do this
        //Instead we persist the request and try to run it again when the


    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        System.out.println("Exception in reminder job: ");
        throwable.printStackTrace();
        return true;
    }
}
