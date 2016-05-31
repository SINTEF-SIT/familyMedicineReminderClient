package com.example.sondrehj.familymedicinereminderclient.jobs;

import android.util.Log;
import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import android.util.Log;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;

public class PostMedicationJob extends Job {
    private static String TAG = "PostMedicationJob";
    private static final int PRIORITY = 1;
    private Medication medication;
    private String userId;
    private String authToken;

    public PostMedicationJob(Medication medication, String userId, String authToken) {
        // This job requires network connectivity,
        // and should be persisted in case the application exits before job is completed.

        super(new Params(PRIORITY)
                .requireNetwork()
                .persist());
        Log.d(TAG, "New Job posted.");
        this.medication = medication;
        this.userId = userId;
        this.authToken = authToken;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Medication added.");
        // Job has been saved to disk.
        // This is a good place to dispatch a UI event to indicate the job will eventually run.
        // In this example, it would be good to update the UI with the newly posted tweet.
    }

    /*
    * When this method runs, we attempt to send a request to the server. If an error is thrown,
    * the method will run again because shouldReRunOnThrowable returns true.
    *
    **/

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job is running in background.");
        Log.d(TAG, Thread.currentThread().toString());
        MyCyFAPPServiceAPI api = RestService.createRestService(authToken);
        Call<Medication> call = api.createMedication(userId, medication);
        Medication med = call.execute().body(); //medication retrieved from server
        if(med != null) {
            medication.setServerId(med.getServerId());
            BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONSENT, medication));
        }
        else {
            Log.d(TAG, "Medication returned from the server was null.");
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