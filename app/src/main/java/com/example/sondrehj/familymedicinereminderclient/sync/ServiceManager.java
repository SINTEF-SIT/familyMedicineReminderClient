package com.example.sondrehj.familymedicinereminderclient.sync;

import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by nikolai on 08/04/16.
 */
public class ServiceManager extends ContextWrapper {

    /**
     * A wrapper around the context which contains references to the connectivityManager and more.
     *
     * @param base
     */
    public ServiceManager(Context base) {
        super(base);
    }

    /**
     * Returns whether network is available on the device or not.
     *
     * @return boolean
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
