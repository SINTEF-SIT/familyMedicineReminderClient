package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.dialogs.TimePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.database.ReminderListContent;
import com.example.sondrehj.familymedicinereminderclient.dialogs.DatePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.EndDatePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.MedicationPickerFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.SelectDaysDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.utility.Converter;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewReminderFragment.OnNewReminderInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewReminderFragment extends android.app.Fragment implements TitleSupplier {

    //TODO: Suggestion: If a medication is attached, put name of medication as name. Name is not needed if it's a reminder to take the medicine

    //TODO: When you get sent to NewReminder from the dialog after creating a medicine, the medicine should be attached

    //TODO: Crash if you attach a medicine and there is no medicines to choose from, and press OK

    //TODO: Remove one of the "reminder is active" switches. There's one in Reminder and one in NewReminder

    @Bind(R.id.reminder_edit_group_end_date) LinearLayout endDatePickerGroup;
    @Bind(R.id.reminder_edit_group_choose_days) LinearLayout chooseDaysPickerGroup;
    @Bind(R.id.reminder_edit_group_choose_medication) LinearLayout chooseMedicationGroup;
    @Bind(R.id.reminder_edit_group_choose_dosage) LinearLayout chooseDosageGroup;

    @Bind(R.id.reminder_edit_active_switch) Switch activeSwitch;
    @Bind(R.id.reminder_edit_medication_switch) Switch attachMedicationSwitch;
    @Bind(R.id.reminder_edit_repeat_switch) Switch repeatSwitch;

    @Bind(R.id.reminder_edit_name_input) EditText nameInput;
    @Bind(R.id.reminder_edit_date_input) TextView dateInput;
    @Bind(R.id.reminder_edit_time_input) TextView timeInput;
    @Bind(R.id.reminder_edit_chosen_end_date) TextView endDateInput;
    @Bind(R.id.reminder_edit_chosen_medication) TextView attachedMedicationInput;
    @Bind(R.id.reminder_edit_dosage_text) TextView dosageText;
    @Bind(R.id.reminder_edit_dosage_input) EditText dosageInput;
    @Bind(R.id.reminder_edit_dosage_unit) TextView dosageUnit;

    //days of the week if you didn't know - cpt. obvious
    @Bind(R.id.reminder_edit_chosen_chosen_days_2) TextView Monday;
    @Bind(R.id.reminder_edit_chosen_chosen_days_3) TextView Tuesday;
    @Bind(R.id.reminder_edit_chosen_chosen_days_4) TextView Wednesday;
    @Bind(R.id.reminder_edit_chosen_chosen_days_5) TextView Thursday;
    @Bind(R.id.reminder_edit_chosen_chosen_days_6) TextView Friday;
    @Bind(R.id.reminder_edit_chosen_chosen_days_0) TextView Saturday;
    @Bind(R.id.reminder_edit_chosen_chosen_days_1) TextView Sunday;
    public TextView[] weekDayTextViewArray;

    private Reminder reminder;
    private Medication medication;
    private int[] selectedDays;
    protected Activity mActivity;
    private String currentStartDate;
    private static final String CONTINUOUS_END_DATE = "Continuous";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REMINDER_ARGS = "reminder";
    private OnNewReminderInteractionListener mListener;

    public NewReminderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param reminder The reminder object of the new reminder
     * @return A new instance of fragment NewReminderFragment.
     */
    public static NewReminderFragment newInstance(Reminder reminder) {
        NewReminderFragment fragment = new NewReminderFragment();
        if (reminder != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(REMINDER_ARGS, reminder);
            fragment.setArguments(bundle);
            fragment.setReminder(reminder);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reminder = (Reminder) getArguments().getSerializable(REMINDER_ARGS);
            setReminder(reminder);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_reminder, container, false);
        ButterKnife.bind(this, view);
        weekDayTextViewArray = new TextView[] {Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday};

        // Hide layouts which are opened with switches.
        chooseMedicationGroup.setVisibility(View.GONE);
        chooseDosageGroup.setVisibility(View.GONE);

        repeatSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton repeatButton, boolean isChecked) {
                        if (isChecked) {
                            endDatePickerGroup.setVisibility(View.VISIBLE);
                            endDatePickerGroup.setOnClickListener(
                                    new LinearLayout.OnClickListener() {
                                        public void onClick(View v) {
                                            EndDatePickerFragment endDate = EndDatePickerFragment.newInstance(currentStartDate);
                                            endDate.show(getFragmentManager(), "endDatePicker");
                                        }
                                    });

                            chooseDaysPickerGroup.setVisibility(View.VISIBLE);
                            chooseDaysPickerGroup.setOnClickListener(new LinearLayout.OnClickListener() {
                                public void onClick(View v) {

                                    if (selectedDays != null) {

                                        // Finds the corresponding list index of each item in selectedDays
                                        int[] selectedItems = Converter.selectedDaysToSelectedItems(selectedDays);

                                        // Creates a new SelectDaysDialogFragment where the selected days are checked.
                                        SelectDaysDialogFragment selectDaysDialogFragment = SelectDaysDialogFragment.newInstance(selectedItems);
                                        selectDaysDialogFragment.show(getFragmentManager(), "selectdayslist");
                                    } else {
                                        SelectDaysDialogFragment selectDaysDialogFragment = new SelectDaysDialogFragment();
                                        selectDaysDialogFragment.show(getFragmentManager(), "selectdayslist");
                                    }
                                }
                            });
                        } else {
                            selectedDays = new int[]{0, 1, 2, 3, 4, 5, 6};
                            endDatePickerGroup.setVisibility(View.GONE);
                            chooseDaysPickerGroup.setVisibility(View.GONE);
                        }
                    }
                }
        );

        attachMedicationSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton medicationSwitch, boolean isChecked) {
                        if (isChecked) {
                            chooseMedicationGroup.setVisibility(View.VISIBLE);
                            chooseDosageGroup.setVisibility(View.VISIBLE);
                            chooseDosageGroup.setEnabled(true);
                            enableMedicationField(true);
                            if (medication != null) {
                                enableDosageField(true);
                            }
                        } else {
                            chooseMedicationGroup.setVisibility(View.GONE);
                            enableMedicationField(false);
                            enableDosageField(false);
                        }
                    }
                }
        );
        fillFields();
        return view;
    }

    @OnClick(R.id.reminder_edit_group_date)
    public void onChooseDateGroupClick() {
        DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.reminder_edit_group_time)
    public void onChooseTimeGroupClick() {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    @OnClick(R.id.reminder_edit_save_button)
    public void onSaveButtonClick() {
        if (reminder == null) {
            if (validateName() && validateDateAndTime() && validateMedication() && validateEndDate())
                createReminder();
        } else {
            if (validateName() && validateDateAndTime() && validateMedication() && validateEndDate())
                updateReminder();
        }
    }

    public void fillFields() {

        final Calendar c;
        // Checks if a reminder is passed to the fragment
        if (getArguments() != null) {
            activeSwitch.setChecked(reminder.getIsActive());
            nameInput.setText(reminder.getName());
            c = reminder.getDate();

            // Set the repeat fields if the reminder is repeating.
            if (reminder.getDays().length >= 1) {
                repeatSwitch.setChecked(true);
                selectedDays = reminder.getDays();
                setBoldOnSelectedDays(selectedDays);

                GregorianCalendar endCal = reminder.getEndDate();
                int year = endCal.get(Calendar.YEAR);
                int month = endCal.get(Calendar.MONTH) + 1; //month is 0-indexed
                final int day = endCal.get(Calendar.DAY_OF_MONTH);

                // Checks if the reminder is Continuous
                if (year == 9998) {
                    endDateInput.setText(CONTINUOUS_END_DATE);
                } else {
                    String date = String.format("%02d.%02d.%4d", day, month, year);
                    endDateInput.setText(date);
                }

            } else {
                selectedDays = new int[]{0, 1, 2, 3, 4, 5, 6};
                setBoldOnSelectedDays(selectedDays);
                repeatSwitch.setChecked(false);
                endDatePickerGroup.setVisibility(View.GONE);
                chooseDaysPickerGroup.setVisibility(View.GONE);
            }
            // Set the medicine fields if the reminder got a medicine attached.
            if (reminder.getMedicine() != null) {
                medication = reminder.getMedicine();
                attachMedicationSwitch.setChecked(true);
                attachedMedicationInput.setText(reminder.getMedicine().getName());
                dosageInput.setText(reminder.getDosage().toString());
                dosageUnit.setText(reminder.getMedicine().getUnit());
                enableDosageField(true);
            }
        } else { // Set today's date and time as default if a reminder is not provided
            c = Calendar.getInstance();
            selectedDays = new int[]{0, 1, 2, 3, 4, 5, 6};
            setBoldOnSelectedDays(selectedDays);
            repeatSwitch.setChecked(false);
            endDatePickerGroup.setVisibility(View.GONE);
            chooseDaysPickerGroup.setVisibility(View.GONE);
        }

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //month is 0-indexed
        final int day = c.get(Calendar.DAY_OF_MONTH);
        String time = String.format("%02d:%02d", hour, minute);
        String date = String.format("%02d.%02d.%4d", day, month, year);
        currentStartDate = date;
        dateInput.setText(date);
        timeInput.setText(time);
    }

    public void setBoldOnSelectedDays(int[] selectedDays) {

        for(int i = 0; i < 7; i ++) {
            boolean inList = false;
            for (int day : selectedDays) {
                if (i == day) {
                    inList = true;
                    weekDayTextViewArray[i].setTypeface(null, Typeface.BOLD);
                    weekDayTextViewArray[i].setTextColor(Color.GRAY);
                    break;
                }
            } if(!inList) {
                weekDayTextViewArray[i].setTypeface(null, Typeface.NORMAL);
                weekDayTextViewArray[i].setTextColor(Color.parseColor("#DDDDDD"));
            }
        }

        /*
        for(int i : selectedDays) {
            switch (i){
                case 0:
                    Saturday.setTypeface(Saturday.getTypeface(), Typeface.BOLD);
                    Saturday.setTextColor(Color.DKGRAY);
                    break;
                case 1:
                    Sunday.setTypeface(Sunday.getTypeface(), Typeface.BOLD);
                    Sunday.setTextColor(Color.DKGRAY);
                    break;
                case 2:
                    Monday.setTypeface(Monday.getTypeface(), Typeface.BOLD);
                    Monday.setTextColor(Color.DKGRAY);
                    break;
                case 3:
                    Tuesday.setTypeface(Tuesday.getTypeface(), Typeface.BOLD);
                    Tuesday.setTextColor(Color.DKGRAY);
                    break;
                case 4:
                    Wednesday.setTypeface(Wednesday.getTypeface(), Typeface.BOLD);
                    Wednesday.setTextColor(Color.DKGRAY);
                    break;
                case 5:
                    Thursday.setTypeface(Thursday.getTypeface(), Typeface.BOLD);
                    Thursday.setTextColor(Color.DKGRAY);
                    break;
                case 6:
                    Friday.setTypeface(Friday.getTypeface(), Typeface.BOLD);
                    Friday.setTextColor(Color.DKGRAY);
                    break;
            }
        }
        */
    }

    public void enableDosageField(boolean enable) {
        if (enable) {
            chooseDosageGroup.setVisibility(View.VISIBLE);
            dosageInput.setEnabled(true);
            dosageInput.setTextColor(Color.GRAY);
            dosageUnit.setTextColor(Color.GRAY);
            dosageText.setTextColor(Color.BLACK);
        } else {
            chooseDosageGroup.setVisibility(View.GONE);
            dosageInput.setEnabled(false);
            dosageInput.setTextColor(Color.parseColor("#DDDDDD"));
            dosageText.setTextColor(Color.parseColor("#DDDDDD"));
            dosageUnit.setTextColor(Color.parseColor("#DDDDDD"));
        }
    }

    public void enableMedicationField(boolean enable) {

        if (enable) {
            chooseMedicationGroup.setVisibility(View.VISIBLE);
            chooseMedicationGroup.setOnClickListener(new LinearLayout.OnClickListener() {
                public void onClick(View v) {
                    MedicationPickerFragment medicationPickerFragment = new MedicationPickerFragment();
                    medicationPickerFragment.show(getFragmentManager(), "medicationPickerFragment");
                }
            });
        } else {
            chooseMedicationGroup.setOnClickListener(null);
            chooseMedicationGroup.setVisibility(View.GONE);
        }
    }

    public void setDateOnLayout(int year, int month, int day) {
        month = month + 1;  //month is 0-indexed
        String dateSet = String.format("%02d.%02d.%4d", day, month, year);
        dateInput.setText(dateSet);
        currentStartDate = dateSet;
    }

    public void setEndDateOnLayout(int year, int month, int day) {
        month = month + 1;  //month is 0-indexed
        String dateSet = String.format("%02d.%02d.%4d", day, month, year);
        endDateInput.setText(dateSet);
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public void setMedicationOnLayout(Medication med) {
        this.medication = med;
        attachedMedicationInput.setText(med.getName());
        dosageInput.setText("");
        dosageUnit.setText(med.getUnit());
        enableDosageField(true);
    }

    public void setTimeOnLayout(int hour, int minute) {
        String timeSet = String.format("%02d:%02d", hour, minute);
        timeInput.setText(timeSet);
    }

    public void setDaysOnLayout(ArrayList selectedItems) {
        selectedDays = new int[selectedItems.size()];

        // Convert to the expected format for GregorianCalendar
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedDays[i] = ((Integer) selectedItems.get(i) + 2) % 7;
        }
        Arrays.sort(selectedDays);
        setBoldOnSelectedDays(selectedDays);
    }

    public void createReminder() {
        Reminder reminder = new Reminder();
        reminder.setOwnerId("temp");
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
        reminder.setReminderServerId(-1);

        System.out.println("----------Reminder Created----------" + "\n" + reminder);
        System.out.println("------------------------------------");

        //Add reminder to database
        MySQLiteHelper db = new MySQLiteHelper(mActivity);
        db.addReminder(reminder);
        Toast.makeText(getActivity(), "Reminder created", Toast.LENGTH_LONG).show();

        mListener.onSaveNewReminder(reminder);
    }

    public void updateReminder() {
        //Updates an existing Reminder object
        reminder.setName(nameInput.getText().toString());

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

        System.out.println("----------Reminder Updated----------" + "\n" + reminder);
        System.out.println("------------------------------------");

        // Update existing reminder in database
        MySQLiteHelper db = new MySQLiteHelper(mActivity);
        db.updateReminder(reminder);
        Toast.makeText(getActivity(), "Reminder was updated", Toast.LENGTH_LONG).show();

        mListener.onSaveNewReminder(reminder);
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
                Toast toast = Toast.makeText(getActivity(), "Chosen date and time is before today's date", Toast.LENGTH_LONG);
                toast.show();
                return false;
            } else {
                return true;
            }
        }
        Toast toast = Toast.makeText(getActivity(), "Date field is empty", Toast.LENGTH_LONG);
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
                    Toast toast = Toast.makeText(getActivity(), "Chosen end date is before start date", Toast.LENGTH_LONG);
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
            Toast toast = Toast.makeText(getActivity(), "Please enter a reminder name", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        return true;
    }

    private boolean validateMedication() {
        if (attachMedicationSwitch.isChecked()) {
            if (attachedMedicationInput.getText().toString().equals("Click to choose")) {
                Toast toast = Toast.makeText(getActivity(), "Please choose a medication", Toast.LENGTH_LONG);
                toast.show();
                return false;
            } else {
                if (dosageInput.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getActivity(), "Please enter a dosage", Toast.LENGTH_LONG);
                    toast.show();
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }


    //API Level >= 23
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewReminderInteractionListener) {
            mListener = (OnNewReminderInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //API Level < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        if (activity instanceof OnNewReminderInteractionListener) {
            mListener = (OnNewReminderInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ButterKnife.unbind(this);
        mListener = null;
    }

    @Override
    public String getTitle() {
        return "New Reminders";
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNewReminderInteractionListener {
        void onSaveNewReminder(Reminder r);
    }
}
