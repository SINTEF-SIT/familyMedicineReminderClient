package com.example.sondrehj.familymedicinereminderclient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    // This gets called when the user sets a date
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.setDate(year, month, day);
    }

    public interface DatePickerListener {
        public void setDate(int day, int month, int year);
    }

    //onAttach get called when the fragment gets attached to the activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (DatePickerListener) activity;
    }

}
