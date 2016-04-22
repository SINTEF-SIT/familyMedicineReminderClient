package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.sondrehj.familymedicinereminderclient.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectDaysDialogFragment extends DialogFragment {

    private OnDaysDialogResultListener mListener;
    ArrayList<Integer> selectedItems = new ArrayList<>();
    private int[] daysAlreadySelected;

    private static final String DAYS_SELECTED_ARGS = "days-selected";

    public static SelectDaysDialogFragment newInstance(int[] daysAlreadySelected) {
        SelectDaysDialogFragment fragment = new SelectDaysDialogFragment();
        if (daysAlreadySelected != null) {
            Bundle bundle = new Bundle();
            bundle.putIntArray(DAYS_SELECTED_ARGS, daysAlreadySelected);
            fragment.setArguments(bundle);
            fragment.setDaysAlreadySelected(daysAlreadySelected);
        }
        return fragment;
    }

    public void setDaysAlreadySelected(int[] daysAlreadySelected) {
        this.daysAlreadySelected = daysAlreadySelected;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        boolean[] checkedItems = new boolean[]{false, false, false, false, false, false, false};

        if (daysAlreadySelected != null) {
            for (int day : daysAlreadySelected) {
                selectedItems.add(day);
                checkedItems[day] = true;
            }
        }
        else {
            for (int x = 0; x < 7; x++) {
                selectedItems.add(x);
                checkedItems[x] = true;
            }
        }

        System.out.println(Arrays.toString(daysAlreadySelected));


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
    }

}