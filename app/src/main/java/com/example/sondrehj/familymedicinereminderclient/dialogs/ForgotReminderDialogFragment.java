package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ForgotReminderDialogFragment extends DialogFragment  {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Forgotten reminder");
        builder.setMessage("Your child might have forgotten to take their medicine, reach out to them.");
        builder.setNeutralButton("Dismiss", (DialogInterface dialog, int which) -> {
            //empty
        });
        // Create the Dialog object and return it
        return builder.create();
    }
}