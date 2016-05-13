package com.example.sondrehj.familymedicinereminderclient.utility;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by sondre on 30/04/2016.
 */
public class NewReminderInputValidator {

    private static final String CONTINUOUS_END_DATE = "Continuous";
    Activity activity;
    TextView dateInput;
    TextView timeInput;
    TextView endDateInput;
    TextView attachedMedicationInput;
    EditText nameInput;
    EditText dosageInput;
    Switch repeatSwitch;
    Switch attachMedicationSwitch;

    public NewReminderInputValidator(Activity activity, EditText nameInput, TextView dateInput,
                                     TextView timeInput, Switch attachMedicationSwitch, TextView attachedMedicationInput,
                                     EditText dosageInput, Switch repeatSwitch, TextView endDateInput) {
        this.activity = activity;
        this.dateInput = dateInput;
        this.timeInput = timeInput;
        this.endDateInput = endDateInput;
        this.attachedMedicationInput = attachedMedicationInput;
        this.nameInput = nameInput;
        this.dosageInput = dosageInput;
        this.repeatSwitch = repeatSwitch;
        this.attachMedicationSwitch = attachMedicationSwitch;
    }

    public boolean validateAllFields(){
        return validateName() && validateDateAndTime() && validateMedication() && validateEndDate();
    }

    private boolean validateDateAndTime() {

        if (!dateInput.getText().toString().equals("")) {

            GregorianCalendar setDate = Converter.dateStringToCalendar(
                    dateInput.getText().toString(),
                    timeInput.getText().toString()
            );
            //TODO: If you want to edit something on a reminder, this validation deny you from saving it, if the date is back in time
            Calendar currentDate = Calendar.getInstance();
            if (setDate.before(currentDate)) {
                Toast toast = Toast.makeText(activity, "The selected time and date is in the past.", Toast.LENGTH_LONG);
                toast.show();
                return false;
            } else {
                return true;
            }
        }
        Toast toast = Toast.makeText(activity, "Date field is empty", Toast.LENGTH_LONG);
        toast.show();
        return false;
    }

    private boolean validateEndDate() {
        if (repeatSwitch.isChecked()) {
            if (!endDateInput.getText().toString().equals(CONTINUOUS_END_DATE)) {

                // End date
                GregorianCalendar endCal = Converter.dateStringToCalendar(
                        endDateInput.getText().toString(),
                        timeInput.getText().toString()
                );

                // Start date
                GregorianCalendar setDate = Converter.dateStringToCalendar(
                        dateInput.getText().toString(),
                        timeInput.getText().toString()
                );

                if (endCal.before(setDate)) {
                    Toast toast = Toast.makeText(activity, "Chosen end date is before start date", Toast.LENGTH_LONG);
                    toast.show();
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

    private boolean validateName() {
        if (nameInput.getText().toString().equals("")) {
            Toast toast = Toast.makeText(activity, "Please enter a reminder name", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        return true;
    }

    private boolean validateMedication() {
        if (attachMedicationSwitch.isChecked()) {
            if (attachedMedicationInput.getText().toString().equals("Click to choose")) {
                Toast toast = Toast.makeText(activity, "Please choose a medication", Toast.LENGTH_LONG);
                toast.show();
                return false;
            } else {
                if (dosageInput.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(activity, "Please enter a dosage", Toast.LENGTH_LONG);
                    toast.show();
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }


}
