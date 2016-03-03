package com.example.sondrehj.familymedicinereminderclient;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerListener mListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // This gets called when the user sets a time
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
