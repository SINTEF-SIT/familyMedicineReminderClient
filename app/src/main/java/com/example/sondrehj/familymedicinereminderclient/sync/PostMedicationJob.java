package com.example.sondrehj.familymedicinereminderclient.sync;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;

public class PostMedicationJob extends Job {
    private static final int PRIORITY = 1;

    private Medication medication;
    private String userId;

    public PostMedicationJob(Medication medication, String userId) {
        // This job requires network connectivity,
        // and should be persisted in case the application exits before job is completed.

        super(new Params(PRIORITY)
                .requireNetwork()
                .persist());
        System.out.println("New job posted");
        this.medication = medication;
        this.userId = userId;
    }
    @Override
    public void onAdded() {
        System.out.println("Medication added");

        // Job has been saved to disk.
        // This is a good place to dispatch a UI event to indicate the job will eventually run.
        // In this example, it would be good to update the UI with the newly posted tweet.
    }
    @Override
    public void onRun() throws Throwable {
        System.out.println("In onRun!");
        System.out.println(Thread.currentThread().toString());

        MyCyFAPPServiceAPI api = RestService.createRestService();
        Call<Medication> call = api.createMedication(userId, medication);
        Medication med = call.execute().body();
        if(med != null) {
            System.out.println(med);
            BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONSENT, med));
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
