package com.example.sondrehj.familymedicinereminderclient.jobs;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit2.Call;

/**
 * Created by Eirik on 02.05.2016.
 */
public class UpdateMedicationJob extends Job {

        private static final int PRIORITY = 1;

        private Medication medication;
        private String userId;

        public UpdateMedicationJob(Medication medication, String userId) {
            // This job requires network connectivity,
            // and should be persisted in case the application exits before job is completed.

            super(new Params(PRIORITY)
                    .requireNetwork()
                    .persist());
            System.out.println("New medication job posted");
            this.medication = medication;
            this.userId = userId;
        }
        @Override
        public void onAdded() {
            System.out.println("In medication job's onAdded");

            // Job has been saved to disk.
            // This is a good place to dispatch a UI event to indicate the job will eventually run.
            // In this example, it would be good to update the UI with the newly posted tweet.
        }
        @Override
        public void onRun() throws Throwable {
            System.out.println("In medication onRun!");

            MyCyFAPPServiceAPI api = RestService.createRestService();
            Call<Medication> call = api.updateMedication(userId, String.valueOf(medication.getServerId()), medication);
            Medication med = call.execute().body(); //medication retrieved from server
            if(med != null) {
                System.out.println(med);
                medication.setName(med.getName());
                medication.setCount(med.getCount());
                medication.setUnit(med.getUnit());
                BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONSENT, medication));
            }
            else {
                System.out.println("med returned from server was null");
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
