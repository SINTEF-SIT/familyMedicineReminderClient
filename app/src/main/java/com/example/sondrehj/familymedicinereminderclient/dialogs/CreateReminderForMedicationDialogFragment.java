package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.fragments.MedicationListFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

/**
 * Created by sondre on 09/05/2016.
 */
public class CreateReminderForMedicationDialogFragment extends DialogFragment{

    private CreateReminderForMedicationDialogListener mListener;

    public static CreateReminderForMedicationDialogFragment newInstance(Medication medication) {
        CreateReminderForMedicationDialogFragment fragment = new CreateReminderForMedicationDialogFragment();
        if (medication != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("medication", medication);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog title
        builder.setMessage("Do you want to create a reminder for this medicine?")
                // Set the action buttons
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Medication medication = (Medication) getArguments().getSerializable("medication");
                        mListener.onPositiveCreateReminderForMedicationDialogResult(medication);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity) getActivity()).changeFragment(new MedicationListFragment());
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    //API >= 23
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateReminderForMedicationDialogListener) {
            mListener = (CreateReminderForMedicationDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CreateReminderForMedicationDialogListener) {
            mListener = (CreateReminderForMedicationDialogListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement DeleteReminderDialogListener");
        }
    }

    public interface CreateReminderForMedicationDialogListener {
        void onPositiveCreateReminderForMedicationDialogResult(Medication medication);
    }


}
