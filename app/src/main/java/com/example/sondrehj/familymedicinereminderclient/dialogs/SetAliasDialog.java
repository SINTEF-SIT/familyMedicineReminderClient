package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.sondrehj.familymedicinereminderclient.R;

public class SetAliasDialog extends DialogFragment {

    private OnSetAliasDialogListener mListener;


    private String userId;
    private static final String USER_ID_ARG = "userId";

    public static SetAliasDialog newInstance(String userId) {
        SetAliasDialog fragment = new SetAliasDialog();
        if (userId != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(USER_ID_ARG, userId);
            fragment.setArguments(bundle);
            fragment.setUserId(userId);
        }
        return fragment;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alias_dialog, null);

        EditText alias_edit_text = (EditText) view.findViewById(R.id.alias_dialog_set_alias);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Set alias", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onPositiveSetAliasDialog(alias_edit_text.getText().toString(), userId);
                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                })
                .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onPositiveSetAliasDialog(userId, userId);
                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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
        void onPositiveSetAliasDialog(String alias, String userId);
    }
}