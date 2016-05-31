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
 *
 * Singleton class for instantiation and access of the jobManager instance.
 * The jobManager uses a ServerStatusChangeReceiver to determine whether
 * the network status.
 */




public class JobManagerService {

    private static JobManager jobManager;
    public static ServerStatusChangeReceiver changeReceiver;
    /**
     * Singleton jobManager.
     *
     * @return jobManager
     */
    public synchronized static JobManager getJobManager(Context context) {
        if(changeReceiver == null) {
            changeReceiver = new ServerStatusChangeReceiver();
        }
        if (jobManager == null) {
            Configuration configuration = new Configuration.Builder(context)
                    .networkUtil(changeReceiver)
                    .build();
            jobManager = new JobManager(context, configuration);
        }
        return jobManager;
    }
}
