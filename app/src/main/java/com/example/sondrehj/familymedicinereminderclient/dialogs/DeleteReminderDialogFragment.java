package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

public class DeleteReminderDialogFragment extends DialogFragment {

    private DeleteReminderDialogListener mListener;

    public static DeleteReminderDialogFragment newInstance(Reminder reminder, int position) {
        DeleteReminderDialogFragment fragment = new DeleteReminderDialogFragment();
        if (reminder != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("reminder", reminder);
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog title
        builder.setMessage("Delete this reminder?")
                // Set the action buttons
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Reminder reminder = (Reminder) getArguments().getSerializable("reminder");
                        int position = getArguments().getInt("position");
                        mListener.onPositiveDeleteReminderDialogResult(reminder, position);
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
        if (context instanceof DeleteReminderDialogListener) {
            mListener = (DeleteReminderDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DeleteReminderDialogListener) {
            mListener = (DeleteReminderDialogListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement DeleteReminderDialogListener");
        }
    }

    public interface DeleteReminderDialogListener {
        void onPositiveDeleteReminderDialogResult(Reminder reminder, int position);
    }

}

