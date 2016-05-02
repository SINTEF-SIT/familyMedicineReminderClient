package com.example.sondrehj.familymedicinereminderclient.sync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.fragments.MedicationListFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.TransportReminder;
import com.example.sondrehj.familymedicinereminderclient.utility.Converter;

import java.sql.SQLOutput;
import java.util.ArrayList;

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
                System.out.println("In syncreminders");
                ArrayList<Reminder> dbReminders = db.getReminders();
                for (TransportReminder serverReminder : response.body()) {

                    //If a reminder is attached to an unsynced medication, we request that the user sync medications first
                    System.out.println("Getting med dependency: " + serverReminder.getMedicine());
                    Medication medDependency = new MySQLiteHelper(context).getSingleMedicationByServerID(serverReminder.getMedicine());
                    System.out.println("Got med dependency: " + medDependency);
                    if(medDependency == null) {
                        Toast.makeText(context, "Some of the reminders need medications that are not yet synchronized. " +
                                "Please synchronize medcations first", Toast.LENGTH_SHORT).show();
                        //We do not return, but merely continue to the next reminder
                        continue;
                    }

                    Boolean updated = false;
                    for (Reminder dbReminder : dbReminders) {
                        System.out.println("DBReminder serverID: " + dbReminder.getServerId());
                        System.out.println("Server reminder ID: " + serverReminder.getServerId());

                        // If the two have the same server ID, we know they are the same, and we request
                        // an update. If we made a change, we want to move on to the next serverReminder.
                        if (serverReminder.getServerId() == dbReminder.getServerId()) {
                            dbReminder.setMedicine(medDependency);
                            System.out.println("Comparing" + serverReminder.getServerId() + " : " + dbReminder.getServerId());
                            dbReminder.updateFromTransportReminder(serverReminder);
                            dbReminder.setName(serverReminder.getName());
                            dbReminder.setDate(Converter.databaseDateStringToCalendar(serverReminder.getDate()));
                            System.out.println("ENd date: " + serverReminder.getEndDate());
                            if(! serverReminder.getEndDate().equals("0")) {
                                dbReminder.setEndDate(Converter.databaseDateStringToCalendar(serverReminder.getEndDate()));
                            } else {
                                dbReminder.setEndDate(null);
                            }

                            dbReminder.setDosage(serverReminder.getDosage());
                            dbReminder.setIsActive(serverReminder.getActive());
                            db.updateReminder(dbReminder);
                            updated = true;
                        }
                    }
                    if(!updated) {
                        System.out.println("not updated ");
                        db.addReminder(new Reminder(serverReminder, medDependency));
                    }
                }

                BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERS));
                System.out.println("Finished db, sending intent");
                Intent intent = new Intent();
                intent.setAction("mycyfapp");
                intent.putExtra("action", "syncReminders");
                context.sendBroadcast(intent);

                Toast.makeText(context, "Reminders synchronized!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<TransportReminder>> call, Throwable t) {
                System.out.println("Could not retrieve reminders: " + t.getMessage());
            }
        });
        return true;
    }

    public Boolean syncMedications() {
        Call<List<Medication>> call = restApi.getUserMedicationList(userToSync);
        call.enqueue(new Callback<List<Medication>>() {
            @Override
            public void onResponse(Call<List<Medication>> call, Response<List<Medication>> response) {
                System.out.println("In sync medications");
                ArrayList<Medication> clientMedications = db.getMedications();
                for (Medication serverMedication : response.body()) {
                    boolean updated = false;
                    for (Medication clientMedication : clientMedications) {
                        if(serverMedication.getServerId() == clientMedication.getServerId()) {
                            System.out.println("Updated med with id " + clientMedication.getServerId());
                            clientMedication.setServerId(serverMedication.getServerId());
                            clientMedication.setName(serverMedication.getName());
                            clientMedication.setUnit(serverMedication.getUnit());
                            clientMedication.setCount(serverMedication.getCount());
                            db.updateMedication(clientMedication);
                            updated = true;
                        }
                    }
                    if(!updated) {
                        db.addMedication(serverMedication);
                    }
                }

                System.out.println("Finished db, sending intent");
                Intent intent = new Intent();
                intent.setAction("mycyfapp");
                intent.putExtra("action", "syncMedications");
                context.sendBroadcast(intent);

                Toast.makeText(context, "Medications synchronized!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Medication>> call, Throwable t) {
                System.out.println("Could not retrieve medications: " + t.getMessage());
            }
        });
        return true;
    }
}
