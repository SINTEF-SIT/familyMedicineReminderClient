package com.example.sondrehj.familymedicinereminderclient.sync;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;

import java.sql.SQLOutput;
import java.util.ArrayList;

import java.util.List;

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
        Call<List<Reminder>> call = restApi.getUserReminderList(userToSync);
        call.enqueue(new Callback<List<Reminder>>() {
            @Override
            public void onResponse(Call<List<Reminder>> call, Response<List<Reminder>> response) {
                System.out.println("In syncreminders");
                ArrayList<Reminder> dbReminders = db.getReminders();
                for (Reminder serverReminder : response.body()) {

                    System.out.println("in add reminder");
                    db.addReminder(serverReminder);

                    /*boolean changed = false;
                    for (Reminder dbReminder : dbReminders) {

                        // If the two have the same server ID, we know they are the same, and we request
                        // an update. If we made a change, we want to move on to the next serverReminder.
                        if (serverReminder.getReminderServerId() == dbReminder.getReminderServerId()) {
                            dbReminder.setName(serverReminder.getName());
                            dbReminder.setDate(Converter.databaseDateStringToCalendar(serverReminder.getDateString()));
                            dbReminder.setEndDate(Converter.databaseDateStringToCalendar(serverReminder.getEndDateString()));
                            dbReminder.setMedicine(serverReminder.getMedicine());
                            dbReminder.setDosage(serverReminder.getDosage());
                            dbReminder.setIsActive(serverReminder.getIsActive());
                            db.updateReminder(dbReminder);
                            changed = true;
                        }
                        if (changed) continue;
                    }*/
                }
            }

            @Override
            public void onFailure(Call<List<Reminder>> call, Throwable t) {
                System.out.println("Could not retrieve reminders: " + t.getMessage());
            }
        });
        return false;
    }

    public Boolean syncMedications() {
        Call<List<Medication>> call = restApi.getUserMedicationList(userToSync);
        call.enqueue(new Callback<List<Medication>>() {
            @Override
            public void onResponse(Call<List<Medication>> call, Response<List<Medication>> response) {
                System.out.println("In sync medications");
                ArrayList<Reminder> dbMedications = db.getReminders();
                for (Medication serverMedication : response.body()) {
                    System.out.println("in add medications");
                    db.addMedication(serverMedication);
                }
                System.out.println("Finished db, sending intent");
                Intent intent = new Intent();
                intent.setAction("mycyfapp");
                intent.putExtra("action", "syncMedications");
                context.sendBroadcast(intent);
                Toast.makeText(context, "Synchronization finished!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Medication>> call, Throwable t) {
                System.out.println("Could not retrieve medications: " + t.getMessage());
            }
        });
        return true;
    }
}
