package com.example.sondrehj.familymedicinereminderclient.modals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.sondrehj.familymedicinereminderclient.R;

public class SelectUnitDialogFragment extends DialogFragment {

    private OnUnitDialogResultListener mListener;
    int selectedItem;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog title
        builder.setTitle("Select Unit")
                // Set choice items to unit_items array
                .setSingleChoiceItems(R.array.unit_items, -1, new DialogInterface.OnClickListener() {
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
                        mListener.onPositiveUnitDialogResult(selectedItem);
                        System.out.println(selectedItem);
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
        if (context instanceof OnUnitDialogResultListener) {
            mListener = (OnUnitDialogResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnUnitDialogResultListener) {
            mListener = (OnUnitDialogResultListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnUnitDialogResultListener");
        }
    }

    public interface OnUnitDialogResultListener {
        void onPositiveUnitDialogResult(int unit);
        void onNegativeUnitDialogResult();
    }

}