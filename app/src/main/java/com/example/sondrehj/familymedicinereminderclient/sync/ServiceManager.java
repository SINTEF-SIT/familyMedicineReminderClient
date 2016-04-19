package com.example.sondrehj.familymedicinereminderclient.sync;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
