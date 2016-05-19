package com.example.sondrehj.familymedicinereminderclient.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;

public class DatabaseReceiver extends BroadcastReceiver {

    public Context context;

    /**
     * Clears the time taken column for reminders every day at 00:00.
     *
     * @param context {@link com.example.sondrehj.familymedicinereminderclient.MainActivity}.
     * @param intent intent passed by AlarmManager
     */
    public void onReceive(Context context, Intent intent) {
        Log.d("DatabaseReceiver","TIME TAKEN WAS CLEARED");
        new MySQLiteHelper(context).resetTimeTaken();
        BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERS));
    }
}

