package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.models.Message;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nikolai on 19/05/16.
 */
public class ForgotReminderDialogFragment extends DialogFragment  {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MyCyFAPPServiceAPI api = RestService.createRestService();


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Forgotten reminder");
        builder.setMessage("Your child might have forgotten to take their medicine, reach out to them.");
        builder.setNeutralButton("Dismiss", (DialogInterface dialog, int which) -> {

        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}