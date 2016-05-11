package com.example.sondrehj.familymedicinereminderclient.sync;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.fragments.MedicationListFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.TransportReminder;
import com.example.sondrehj.familymedicinereminderclient.notification.NotificationScheduler;
import com.example.sondrehj.familymedicinereminderclient.utility.Converter;

import java.sql.SQLOutput;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Eirik on 19.04.2016.
 */
public class Synchronizer {

    private final MyCyFAPPServiceAPI restApi;
    private final String userToSync;
    private final MySQLiteHelper db;
    private final Context context;

    private final String TAG = "Synchronizer";

    public Synchronizer(String userToSync, MyCyFAPPServiceAPI restApi, MySQLiteHelper db, Context context) {
        this.restApi = restApi;
        this.userToSync = userToSync;
        this.db = db;
        this.context = context;
    }

    public Boolean syncReminders() {
        Call<List<TransportReminder>> call = restApi.getUserReminderList(userToSync);
        call.enqueue(new Callback<List<TransportReminder>>() {
            @Override
            public void onResponse(Call<List<TransportReminder>> call, Response<List<TransportReminder>> response) {
                ArrayList<Reminder> dbReminders = db.getReminders();


                int[] array = new int[dbReminders.size()];
                Arrays.fill(array, 0);

                outerloop:
                for (TransportReminder serverReminder : response.body()) {

                    //If a reminder is attached to an unsynced medication, we request that the user sync medications first
                    Medication medDependency = new MySQLiteHelper(context).getSingleMedicationByServerID(serverReminder.getMedicine());
                    if(medDependency == null && serverReminder.getDosage() != 0) {
                        Toast.makeText(context, "Some of the reminders need medications that are not yet synchronized. " +
                                "Please synchronize medcations first", Toast.LENGTH_SHORT).show();
                        //We do not return, but merely continue to the next reminder
                        continue;
                    }
                    boolean updated = false;

                    for (Reminder dbReminder : dbReminders) {
                        // If the two have the same server ID, we know they are the same, and we request
                        // an update. If we made a change, we want to move on to the next serverReminder.
                        if (serverReminder.getServerId() == dbReminder.getServerId()) {
                            dbReminder.setMedicine(medDependency);
                            dbReminder.updateFromTransportReminder(serverReminder);
                            dbReminder.setName(serverReminder.getName());
                            dbReminder.setDate(Converter.databaseDateStringToCalendar(serverReminder.getDate()));
                            if(! serverReminder.getEndDate().equals("0")) {
                                dbReminder.setEndDate(Converter.databaseDateStringToCalendar(serverReminder.getEndDate()));
                            } else {
                                dbReminder.setEndDate(null);
                            }
                            if(! serverReminder.getTimeTaken().equals("0")) {
                                dbReminder.setTimeTaken(Converter.databaseDateStringToCalendar(serverReminder.getTimeTaken()));
                            } else {
                                dbReminder.setTimeTaken(null);
                            }
                            dbReminder.setDosage(serverReminder.getDosage());
                            dbReminder.setIsActive(serverReminder.getActive());
                            dbReminder.setDays(Converter.serverDayStringToDayArray(serverReminder.getDays()));
                            Reminder reminder = db.updateReminder(dbReminder);
                            Intent intent = new Intent();
                            intent.setAction("mycyfapp");
                            intent.putExtra("action", "scheduleReminder");
                            intent.putExtra("reminder", reminder);
                            context.sendBroadcast(intent);

                            array[dbReminders.indexOf(dbReminder)] = 1;
                            db.updateReminder(dbReminder);
                            updated = true;
                        }
                    }
                    if(!updated) {
                        Reminder reminder = db.addReminder(new Reminder(serverReminder, medDependency));
                        Intent intent = new Intent();
                        intent.setAction("mycyfapp");
                        intent.putExtra("action", "scheduleReminder");
                        intent.putExtra("reminder", reminder);
                        context.sendBroadcast(intent);
                    }
                }

                MySQLiteHelper helper = new MySQLiteHelper(context);
                for(int i = 0; i < array.length; i++) {
                    if(array[i] == 0) {
                        helper.deleteReminder(dbReminders.get(i));
                    }
                }

                //BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERS));
                Intent intent = new Intent();
                intent.setAction("mycyfapp");
                intent.putExtra("action", "syncReminders");
                context.sendBroadcast(intent);

                Toast.makeText(context, "Reminders synchronized!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<TransportReminder>> call, Throwable t) {
                Log.d(TAG, "Failure");
            }
        });
        return true;
    }

    public Boolean syncMedications() {
        Call<List<Medication>> call = restApi.getUserMedicationList(userToSync);
        call.enqueue(new Callback<List<Medication>>() {
            @Override
            public void onResponse(Call<List<Medication>> call, Response<List<Medication>> response) {
                ArrayList<Medication> clientMedications = db.getMedications();
                Log.d(TAG, response.raw().toString());

                int[] array = new int[clientMedications.size()];
                Arrays.fill(array, 0);

                outerloop:
                for (Medication serverMedication : response.body()) {
                    for (Medication clientMedication : clientMedications) {
                        if(serverMedication.getServerId() == clientMedication.getServerId()) {
                            clientMedication.setServerId(serverMedication.getServerId());
                            clientMedication.setName(serverMedication.getName());
                            clientMedication.setUnit(serverMedication.getUnit());
                            clientMedication.setCount(serverMedication.getCount());
                            db.updateMedication(clientMedication);
                            array[clientMedications.indexOf(clientMedication)] = 1;
                            continue outerloop;
                        }

                    }
                    db.addMedication(serverMedication);
                }

                MySQLiteHelper helper = new MySQLiteHelper(context);
                for(int i = 0; i < array.length; i++) {
                    if(array[i] == 0) {
                        helper.deleteMedication(clientMedications.get(i));
                    }
                }

                Intent intent = new Intent();
                intent.setAction("mycyfapp");
                intent.putExtra("action", "syncMedications");
                context.sendBroadcast(intent);
                //BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONS));
                Toast.makeText(context, "Medications synchronized!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Medication>> call, Throwable t) {
                Log.e(TAG, "Could not retrieve medications: " + t.getMessage());
            }
        });
        return true;
    }
}
