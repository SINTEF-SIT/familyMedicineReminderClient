package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerListener mListener;
    private static int yearAlreadySet;
    private static int monthAlreadySet;
    private static int dayAlreadySet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (yearAlreadySet == 0 && monthAlreadySet == 0 && dayAlreadySet == 0) {
            // Use today's date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog
            DatePickerDialog dp = new DatePickerDialog(getActivity(), this, year, month, day);
            // Disables dates back in time
            Calendar cal = Calendar.getInstance();
            //dp.getDatePicker().setMinDate(cal.getTimeInMillis());
            // Returns the instance of DatePickerDialog
            return dp;
        }
        //if date already has been set, return the DatePicker with the already set date
        return new DatePickerDialog(getActivity(), this, yearAlreadySet, monthAlreadySet, dayAlreadySet);
    }

    // This gets called when the user sets a date
    public void onDateSet(DatePicker view, int year, int month, int day) {
        yearAlreadySet = year;
        monthAlreadySet = month;
        dayAlreadySet = day;
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
