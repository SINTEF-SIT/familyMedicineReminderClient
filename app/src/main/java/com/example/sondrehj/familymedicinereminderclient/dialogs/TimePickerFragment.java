package com.example.sondrehj.familymedicinereminderclient.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerListener mListener;
    private static int hourAlreadySet;
    private static int minuteAlreadySet;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (hourAlreadySet == 0 && minuteAlreadySet == 0) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE) + 5;
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }
        return new TimePickerDialog(getActivity(), this, hourAlreadySet, minuteAlreadySet, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // This gets called when the user sets a time
        hourAlreadySet = hourOfDay;
        minuteAlreadySet = minute;
        mListener.setTime(hourOfDay, minute);
    }

    public interface TimePickerListener {
        public void setTime(int hourOfDay, int minute);
    }

    //onAttach get called when the fragment gets attached to the activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (TimePickerListener) activity;
    }
}
