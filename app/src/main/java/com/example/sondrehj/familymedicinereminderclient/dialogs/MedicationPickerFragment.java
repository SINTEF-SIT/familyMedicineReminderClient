package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.sondrehj.familymedicinereminderclient.database.MedicationListContent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import java.util.List;

public class MedicationPickerFragment extends DialogFragment {

    private OnMedicationPickerDialogResultListener mListener;
    int selectedItem;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final List<Medication> medications = MedicationListContent.ITEMS;
        String[] medicationNames = new String[medications.size()];
        for(int count = 0; count < medicationNames.length; count++) {
            medicationNames[count] = medications.get(count).getName();
        }

        // Set the dialog title
        builder.setTitle("Select Medication")
                // Set choice items to unit_items array
                .setSingleChoiceItems(medicationNames, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        selectedItem = arg1;
                    }
                })
                        // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Set unit TextView to the selected list item
                        System.out.println(selectedItem);
                        mListener.onPositiveMedicationPickerDialogResult(medications.get(selectedItem));
                        System.out.println(medications.get(selectedItem).getName());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    //API >= 23
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMedicationPickerDialogResultListener) {
            mListener = (OnMedicationPickerDialogResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnMedicationPickerDialogResultListener) {
            mListener = (OnMedicationPickerDialogResultListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnUnitDialogResultListener");
        }
    }

    public interface OnMedicationPickerDialogResultListener {
        void onPositiveMedicationPickerDialogResult(Medication med);
    }

}