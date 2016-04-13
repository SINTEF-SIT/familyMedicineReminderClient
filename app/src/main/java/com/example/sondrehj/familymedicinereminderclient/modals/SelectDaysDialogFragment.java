package com.example.sondrehj.familymedicinereminderclient.modals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.sondrehj.familymedicinereminderclient.R;

import java.util.ArrayList;

public class SelectDaysDialogFragment extends DialogFragment {

    private OnDaysDialogResultListener mListener;
    ArrayList<Integer> selectedItems = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        for(int x = 0; x < 7; x++) {
            selectedItems.add(x);
        }
        boolean[] checkedItems = {true, true, true, true, true, true, true};

        // Set the dialog title
        builder.setTitle("Select Days")
                // Set choice items to unit_items array
                .setMultiChoiceItems(R.array.reminder_days, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int chosen, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected item
                            selectedItems.add(chosen);
                        } else {
                            selectedItems.remove(Integer.valueOf(chosen));
                        }
                    }
                })
                        // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Set unit TextView to the selected list item
                        mListener.onPositiveDaysDialogResult(selectedItems);
                        System.out.println(selectedItems);
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
        if (context instanceof OnDaysDialogResultListener) {
            mListener = (OnDaysDialogResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnDaysDialogResultListener) {
            mListener = (OnDaysDialogResultListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnDaysDialogResultListener");
        }
    }

    public interface OnDaysDialogResultListener {
        void onPositiveDaysDialogResult(ArrayList days);
        void onNegativeDaysDialogResult();
    }

}