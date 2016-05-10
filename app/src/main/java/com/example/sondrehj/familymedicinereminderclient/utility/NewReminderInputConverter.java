package com.example.sondrehj.familymedicinereminderclient.utility;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.fragments.NewReminderFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.models.User2;

import java.util.GregorianCalendar;

/**
 * Created by sondre on 30/04/2016.
 */
public class NewReminderInputConverter {

    private static final String CONTINUOUS_END_DATE = "Continuous";
    Activity activity;

    public NewReminderInputConverter(Activity activity){
        this.activity = activity;
    }

    public Reminder CreateReminderFromInput(EditText nameInput, TextView dateInput, TextView timeInput,
                                            Medication medication, Switch attachMedicationSwitch,
                                            EditText dosageInput, Switch repeatSwitch, int[] selectedDays,
                                            TextView endDateInput, Switch activeSwitch, User2 currentUser) {

        Reminder reminder = new Reminder();
        reminder.setOwnerId(currentUser.getUserId());
        reminder.setName(nameInput.getEditableText().toString());

        // Set start date
        GregorianCalendar cal = Converter.dateStringToCalendar(
                dateInput.getText().toString(),
                timeInput.getText().toString());
        reminder.setDate(cal);

        // Attach medication
        if (attachMedicationSwitch.isChecked() && medication != null && !dosageInput.getText().toString().equals("")) {
            reminder.setMedicine(medication);
            reminder.setDosage(Double.parseDouble(dosageInput.getText().toString()));
        }

        // Non-repeating
        if (!repeatSwitch.isChecked()) {
            reminder.setDays(new int[]{});
        } else { // Repeating
            if (selectedDays.length > 0) {
                reminder.setDays(selectedDays);
            }
            if (endDateInput.getText().toString().equals(CONTINUOUS_END_DATE)) {
                reminder.setEndDate(new GregorianCalendar(9999, 0, 0));
            } else {
                // Set end date
                GregorianCalendar endCal = Converter.dateStringToCalendar(
                        endDateInput.getText().toString(),
                        timeInput.getText().toString());
                reminder.setEndDate(endCal);
            }
        }
        reminder.setIsActive(activeSwitch.isChecked());
        reminder.setServerId(-1);
        return reminder;
    }


    public Reminder UpdateReminderFromInput(EditText nameInput, TextView dateInput, TextView timeInput,
                                            Medication medication, Switch attachMedicationSwitch,
                                            EditText dosageInput, Switch repeatSwitch, int[] selectedDays,
                                            TextView endDateInput, Switch activeSwitch, Reminder reminder, User2 currentUser) {

        //Updates an existing Reminder object
        reminder.setName(nameInput.getText().toString());
        reminder.setOwnerId(currentUser.getUserId());

        // Set start date
        GregorianCalendar cal = Converter.dateStringToCalendar(
                dateInput.getText().toString(),
                timeInput.getText().toString());
        reminder.setDate(cal);

        // Set active
        reminder.setIsActive(activeSwitch.isChecked());

        // Non-repeating
        if (!repeatSwitch.isChecked()) {
            reminder.setDays(new int[]{});
            reminder.setEndDate(null);
        }

        if (selectedDays == null && reminder.getDays().length == 0) {
            reminder.setDays(new int[]{});
        }

        // Repeating
        if (repeatSwitch.isChecked()) {
            if (selectedDays == null && reminder.getDays().length > 1) {
                reminder.setDays(reminder.getDays());
            } else {
                reminder.setDays(selectedDays);
            }
            if (endDateInput.getText().toString().equals(CONTINUOUS_END_DATE)) {
                // Set end date far into the future
                reminder.setEndDate(new GregorianCalendar(9999, 0, 0));
            } else if (!endDateInput.getText().toString().equals(CONTINUOUS_END_DATE)) {
                // Set end date
                GregorianCalendar endCal = Converter.dateStringToCalendar(
                        endDateInput.getText().toString(),
                        timeInput.getText().toString());
                reminder.setEndDate(endCal);
            }
        }

        // Medication
        if (medication != null && attachMedicationSwitch.isChecked() && !dosageInput.getText().toString().equals("")) {
            reminder.setMedicine(medication);
            reminder.setDosage(Double.parseDouble(dosageInput.getText().toString()));
        } else if (!attachMedicationSwitch.isChecked() && medication != null) {
            reminder.setMedicine(null);
            reminder.setDosage(null);
        }
        return reminder;
    }
}
