package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.sondrehj.familymedicinereminderclient.models.Medication;

public class DeleteMedicationDialogFragment extends DialogFragment {

    private DeleteMedicationDialogListener mListener;

    public static DeleteMedicationDialogFragment newInstance(Medication medication, int position) {
        DeleteMedicationDialogFragment fragment = new DeleteMedicationDialogFragment();
        if (medication != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("medication", medication);
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
        builder.setMessage("Delete this medicine?")
                // Set the action buttons
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Medication medication = (Medication) getArguments().getSerializable("medication");
                        int position = getArguments().getInt("position");
                        mListener.onPositiveDeleteMedicationDialogResult(medication, position);
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
        if (context instanceof DeleteMedicationDialogListener) {
            mListener = (DeleteMedicationDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DeleteMedicationDialogListener) {
            mListener = (DeleteMedicationDialogListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement DeleteReminderDialogListener");
        }
    }

    public interface DeleteMedicationDialogListener {
        void onPositiveDeleteMedicationDialogResult(Medication medication, int position);
    }

}


