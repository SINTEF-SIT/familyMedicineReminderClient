package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class AttachReminderDialogFragment extends DialogFragment {

    private AttachReminderDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog title
        builder.setMessage("Do you want to set a reminder for this medicine?")
                // Set the action buttons
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onPositiveAttachReminderDialogResult();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //nothing happens
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    //API >= 23
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AttachReminderDialogListener) {
            mListener = (AttachReminderDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof AttachReminderDialogListener) {
            mListener = (AttachReminderDialogListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement AttachReminderDialogListener");
        }
    }

    public interface AttachReminderDialogListener {
        void onPositiveAttachReminderDialogResult();
    }

}
    

