package com.example.sondrehj.familymedicinereminderclient.jobs;

import android.content.Context;

import com.example.sondrehj.familymedicinereminderclient.sync.ServerStatusChangeReceiver;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.config.Configuration;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.path.android.jobqueue.JobManager;

/**
 * Created by Eirik on 11/05/16.
 */
public class JobManagerService {

    private static JobManager jobManager;
    /**
     * Singleton jobManager.
     *
     * @return jobManager
     */
    public synchronized static JobManager getJobManager(Context context) {
        if (jobManager == null) {
            Configuration configuration = new Configuration.Builder(context)
                    .networkUtil(new ServerStatusChangeReceiver())
                    .build();
            jobManager = new JobManager(context, configuration);
        }
        return jobManager;
    }
}
