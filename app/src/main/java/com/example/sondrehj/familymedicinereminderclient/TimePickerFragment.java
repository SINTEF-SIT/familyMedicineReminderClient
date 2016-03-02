package com.example.sondrehj.familymedicinereminderclient;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        String timeSet = hourOfDay + ":" + minute;
        //NewReminderFragment nrf = (NewReminderFragment) getFragmentManager().findFragmentById(R.id.newreminderfrag);
        //nrf.setTimeSetText(hourOfDay, minute);



        //NewReminderFragment nrf = (NewReminderFragment) getFragmentManager().findFragmentById(R.id.newreminderfrag);
        //nrf.getTimePickerButton().setText(timeSet);

        //NewReminderFragment newReminderFragment = NewReminderFragment.newInstance("hei", "hoi");
        //newReminderFragment.timePickerButton.setText(timeSet);
    }

    //onAttach get called when the fragment gets attached to the activity
}
