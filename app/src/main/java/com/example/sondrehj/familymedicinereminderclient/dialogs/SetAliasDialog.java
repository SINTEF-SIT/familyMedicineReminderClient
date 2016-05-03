package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.example.sondrehj.familymedicinereminderclient.R;

public class SetAliasDialog extends DialogFragment {

    private OnSetAliasDialogListener mListener;
   // int selectedItem;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.alias_dialog, null))
                // Add action buttons
                .setPositiveButton("Set alias", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    //API >= 23
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSetAliasDialogListener) {
            mListener = (OnSetAliasDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnSetAliasDialogListener) {
            mListener = (OnSetAliasDialogListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement AttachReminderDialogListener");
        }
    }

    public interface OnSetAliasDialogListener {
        void onPositiveUnitDialogResult(int unit);
    }
}