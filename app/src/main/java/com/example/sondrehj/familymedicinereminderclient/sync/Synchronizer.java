package com.example.sondrehj.familymedicinereminderclient.sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.TransportReminder;
import com.example.sondrehj.familymedicinereminderclient.utility.Converter;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


    //Method that fetches the reminders from the server and
    public Boolean syncReminders() {
        //The reminders of userToSync are fetched from the server
        Call<List<TransportReminder>> call = restApi.getUserReminderList(userToSync);
        call.enqueue(new Callback<List<TransportReminder>>() {
            @Override
            public void onResponse(Call<List<TransportReminder>> call, Response<List<TransportReminder>> response) {
                ArrayList<Reminder> dbReminders = db.getRemindersByOwnerId(userToSync);

                //To determine which reminders on the client are not present in the data set received by the server, we
                //create and array of 0. If there are entries that are still 0 at the end of the method,
                //we know that the corresponding reminder is deleted on the server
                int[] array = new int[dbReminders.size()];
                Arrays.fill(array, 0);

                //A loop comparing the data set received by the server with the one present in the local database
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

                            //If an update was made, we need to reschedule the reminder
                            Intent intent = new Intent();
                            intent.setAction("mycyfapp");
                            intent.putExtra("action", "scheduleReminder");
                            intent.putExtra("reminder", reminder);
                            context.sendBroadcast(intent);

                            //We update the deletion list
                            array[dbReminders.indexOf(dbReminder)] = 1;
                            db.updateReminder(dbReminder);
                            updated = true;
                        }
                    }

                    //If a serverReminder was not updated, we know that this must be a new reminder
                    //We add it to the local database and schedule it
                    if(!updated) {
                        Reminder reminder = db.addReminder(new Reminder(serverReminder, medDependency));
                        Intent intent = new Intent();
                        intent.setAction("mycyfapp");
                        intent.putExtra("action", "scheduleReminder");
                        intent.putExtra("reminder", reminder);
                        context.sendBroadcast(intent);
                    }
                }

                //We traverse the deletion list and delete the reminders that were
                //absent in the server data set.
                MySQLiteHelper helper = new MySQLiteHelper(context);
                for(int i = 0; i < array.length; i++) {
                    if(array[i] == 0) {
                        helper.deleteReminder(dbReminders.get(i));
                    }
                }

                //We broadcast an intent caught by SyncReceiver to be able to communicate with the main thread
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

    //This method works similarly to syncReminders()
    public Boolean syncMedications() {
        Call<List<Medication>> call = restApi.getUserMedicationList(userToSync);
        call.enqueue(new Callback<List<Medication>>() {
            @Override
            public void onResponse(Call<List<Medication>> call, Response<List<Medication>> response) {
                ArrayList<Medication> clientMedications = db.getMedicationsByOwnerId(userToSync);
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
