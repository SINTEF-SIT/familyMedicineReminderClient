package com.example.sondrehj.familymedicinereminderclient.sync;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.sqlite.MySQLiteHelper;

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

    public Synchronizer(String userToSync, MyCyFAPPServiceAPI restApi, MySQLiteHelper db) {
        this.restApi = restApi;
        this.userToSync = userToSync;
        this.db = db;
    }

    public Boolean syncReminders() {
        Call<List<Reminder>> call = restApi.getUserReminderList(userToSync);
        call.enqueue(new Callback<List<Reminder>>() {
            @Override
            public void onResponse(Call<List<Reminder>> call, Response<List<Reminder>> response) {
                System.out.println("In syncreminders");
                ArrayList<Reminder> dbReminders = db.getReminders();
                for(Reminder serverReminder : response.body()) {
                    boolean changed = false;
                    for (Reminder dbReminder : dbReminders) {

                        // If the two have the same server ID, we know they are the same, and we request
                        // an update. If we made a change, we want to move on to the next serverReminder.
                        if (serverReminder.getReminderServerId() == dbReminder.getReminderServerId()) {
                            dbReminder.setName(serverReminder.getName());
                            dbReminder.setDate(serverReminder.getDate());
                            dbReminder.setEndDate(serverReminder.getEndDate());
                            dbReminder.setMedicine(serverReminder.getMedicine());
                            dbReminder.setDosage(serverReminder.getDosage());
                            dbReminder.setIsActive(serverReminder.getIsActive());
                            db.updateReminder(dbReminder);
                            changed = true;
                        }
                        if (changed) continue;
                    }
                    if (changed) continue;
                    else {
                        db.addReminder(serverReminder);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Reminder>> call, Throwable t) {

            }
        });
        return false;
    }

    public Boolean syncMedications() {
        return true;
    }








}
