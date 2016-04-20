package com.example.sondrehj.familymedicinereminderclient.sync;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.example.sondrehj.familymedicinereminderclient.LinkingFragment;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.LinkingRequestEvent;
import com.example.sondrehj.familymedicinereminderclient.modals.LinkingDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.models.User;

import java.sql.SQLOutput;
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

    public Synchronizer(String userToSync, MyCyFAPPServiceAPI restApi) {
        this.restApi = restApi;
        this.userToSync = userToSync;
    }

    public Boolean syncReminders() {
        Call<List<Reminder>> call = restApi.getUserReminderList(userToSync);
        call.enqueue(new Callback<List<Reminder>>() {
            @Override
            public void onResponse(Call<List<Reminder>> call, Response<List<Reminder>> response) {
                System.out.println("In syncreminders");
                for(Reminder reminder : response.body()) {
                    System.out.println(reminder.toString());
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
