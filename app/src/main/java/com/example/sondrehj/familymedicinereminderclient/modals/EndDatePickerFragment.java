package com.example.sondrehj.familymedicinereminderclient.modals;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EndDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private EndDatePickerListener mListener;
    private static int yearAlreadySet;
    private static int monthAlreadySet;
    private static int dayAlreadySet;
    public String startDate;

    private static final String START_DATE_ARGS = "reminder";

    public static EndDatePickerFragment newInstance (String startDate) {
        EndDatePickerFragment fragment = new EndDatePickerFragment();
        if (startDate != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(START_DATE_ARGS, startDate);
            fragment.setArguments(bundle);
            fragment.setStartDate(startDate);
        }
        return fragment;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String startDate = (String) getArguments().getSerializable(START_DATE_ARGS);
            setStartDate(startDate);
        }

        System.out.println(startDate);
        if (yearAlreadySet == 0 && monthAlreadySet == 0 && dayAlreadySet == 0) {
            // Use today's date as the default date in the picker

            String[] date = startDate.split("\\W+");

            GregorianCalendar gc = new GregorianCalendar(
                    Integer.parseInt(date[2]),
                    Integer.parseInt(date[1]) - 1,
                    Integer.parseInt(date[0]) + 1
            );

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog
            DatePickerDialog dp = new DatePickerDialog(getActivity(), this, year, month, day);
            // Disables dates back in time
            Calendar cal = Calendar.getInstance();
            dp.getDatePicker().setMinDate(gc.getTimeInMillis());
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
        mListener.setEndDate(year, month, day);
    }

    public interface EndDatePickerListener {
        public void setEndDate(int day, int month, int year);
    }

    //onAttach get called when the fragment gets attached to the activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (EndDatePickerListener) activity;
    }

}
