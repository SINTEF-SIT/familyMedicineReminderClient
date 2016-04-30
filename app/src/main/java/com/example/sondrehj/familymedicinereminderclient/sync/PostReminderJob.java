package com.example.sondrehj.familymedicinereminderclient.sync;

import android.renderscript.RenderScript;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;

/**
 * Created by Eirik on 30.04.2016.
 */
public class PostReminderJob extends Job {
    private static final int PRIORITY = 1;

    private Reminder reminder;
    private String userId;


    public PostReminderJob(Reminder reminder, String userId) {
        // This job requires network connectivity,
        // and should be persisted in case the application exits before job is completed.

        super(new Params(PRIORITY)
                .requireNetwork()
                .persist());
        System.out.println("New reminder job posted");
        this.reminder = reminder;
        this.userId = userId;
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
        Call<Reminder> call = api.createReminder(userId, reminder);
        Reminder reminder = call.execute().body(); //medication retrieved from server
        if(reminder != null) {
            System.out.println(reminder);
            reminder.setServerId(reminder.getServerId());  //To retain the reference to this medication, we add the server id to it
            BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONSENT, reminder));
        }
        else {
            System.out.println("med returned from server was null");
        }
    }

    @Override
    protected void onCancel() {
        //Here we would normally cancel the request if completion was impossible, but we do not do this
        //Insted we persist the request and try to run it again when the

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        System.out.println("Exception in reminder job: ");
        throwable.printStackTrace();
        return true;
    }
}
