package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
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
    //TODO: Crash if you attach a medicine and there is no medicines to choose from, and press OK

    private LinearLayout newReminderLayout;
    private LinearLayout endDatePickerLayout;
    private LinearLayout repeatLayout;
    private LinearLayout daysLayout;
    private LinearLayout daysListLayout;
    private LinearLayout timePickerLayout;
    private LinearLayout datePickerLayout;


    // ----- MEDICATION ----- //
    private LinearLayout chooseMedicationWrapper;
    private TextView chooseDosageText;
    private EditText dosageTextField;
    private TextView dosageUnitText;
    private TextView chooseMedicationText;
    private TextView chosenMedicationTextView;

    private TextView endDatePickedText;
    private TextView endDatePickerText;
    private TextView dateSetText;
    private TextView selectDaysText;
    private TextView daysSelectedFromListText;
    private TextView timeSetText;
    private TextView howOftenText;
    private TextView daysPickedText;
    private TextView chosenMedicationText;
    private TextView chosenDosageText;
    private EditText nameEditText;
    private Switch repeatSwitch;
    private Switch reminderSwitch;
    private Switch medicationSwitch;
    private NumberPicker numberPicker;
    private Button saveButton;
    private Reminder reminder;
    private Medication medication;
    private int[] selectedDays;
    protected Activity mActivity;
    private String currentStartDate;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_reminder, container, false);

        newReminderLayout = (LinearLayout) view.findViewById(R.id.newReminderLayout);
        reminderSwitch = (Switch) view.findViewById(R.id.reminderSwitch);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        datePickerLayout = (LinearLayout) view.findViewById(R.id.datePickerLayout);
        dateSetText = (TextView) view.findViewById(R.id.dateSetText);
        timePickerLayout = (LinearLayout) view.findViewById(R.id.timePickerLayout);
        timeSetText = (TextView) view.findViewById(R.id.timeSetText);
        repeatLayout = (LinearLayout) view.findViewById(R.id.repeatLayout);
        repeatSwitch = (Switch) view.findViewById(R.id.repeatSwitch);
        medicationSwitch = (Switch) view.findViewById(R.id.medicationSwitch);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        // MEDICATION
        chooseDosageText = (TextView) view.findViewById(R.id.chooseDosage);
        dosageTextField = (EditText) view.findViewById(R.id.dosage);
        dosageUnitText = (TextView) view.findViewById(R.id.dosageUnit);
        chooseMedicationText = (TextView) view.findViewById(R.id.chooseMedication);
        chosenMedicationTextView = (TextView) view.findViewById(R.id.chosenMedication);
        chooseMedicationWrapper = (LinearLayout) view.findViewById(R.id.chooseMedicationWrapper);

        datePickerLayout.setOnClickListener(
                new LinearLayout.OnClickListener() {
                    public void onClick(View v) {
                        //TODO: Valider at man ikke kan sette en dato tilbake i tid? Kan bli kluss
                        DialogFragment datePickerFragment = new DatePickerFragment();
                        datePickerFragment.show(getFragmentManager(), "datePicker");
                    }
                });

        timePickerLayout.setOnClickListener(
                new LinearLayout.OnClickListener() {
                    public void onClick(View v) {
                        DialogFragment timePickerFragment = new TimePickerFragment();
                        timePickerFragment.show(getFragmentManager(), "timePicker");
                    }
                });

        // ---------- REMINDER ---------- //

        //choose interval layout with number picker
        daysLayout = new LinearLayout(getActivity());
        daysLayout.setOrientation(LinearLayout.HORIZONTAL);
        //converting dp to px, because LayoutParams only takes px
        int layoutHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams daysLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, layoutHeight);
        daysLayout.setLayoutParams(daysLayoutParams);
        int layoutMarginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int layoutMarginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        daysLayoutParams.setMargins(layoutMarginLeft, 0, layoutMarginRight, 0);

        howOftenText = new TextView(getActivity());
        String howOftenString = "How often?";
        howOftenText.setText(howOftenString);
        LinearLayout.LayoutParams howOftenParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        howOftenText.setLayoutParams(howOftenParams);
        howOftenParams.gravity = Gravity.CENTER;
        howOftenText.setTextSize(22);
        howOftenText.setTextColor(Color.parseColor("#000000"));

        daysPickedText = new TextView(getActivity());
        String daysPickedString = "Interval";
        daysPickedText.setText(daysPickedString);
        LinearLayout.LayoutParams daysPickedParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        daysPickedText.setLayoutParams(daysPickedParams);
        daysPickedParams.gravity = Gravity.CENTER;  //layout:gravity
        daysPickedText.setGravity(Gravity.RIGHT);       //gravity
        daysPickedText.setTextSize(22);
        daysPickedText.setTextColor(Color.parseColor("#8a000000"));

        numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(30);
        numberPicker.setValue(1);
        LinearLayout.LayoutParams numberPickerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        numberPicker.setLayoutParams(numberPickerParams);
        numberPickerParams.gravity = Gravity.CENTER;

        daysLayout.addView(howOftenText);
        daysLayout.addView(daysPickedText);
        daysLayout.setVisibility(View.GONE);
        numberPicker.setVisibility(View.GONE);

        //choose which days layout with list
        daysListLayout = new LinearLayout(getActivity());
        daysListLayout.setOrientation(LinearLayout.HORIZONTAL);
        daysListLayout.setLayoutParams(daysLayoutParams);

        selectDaysText = new TextView(getActivity());
        final String selectDaysString = "Select days";
        selectDaysText.setText(selectDaysString);
        selectDaysText.setLayoutParams(howOftenParams);
        selectDaysText.setTextSize(18);
        selectDaysText.setTextColor(Color.parseColor("#000000"));

        daysSelectedFromListText = new TextView(getActivity());
        String daysSelectedFromListString = "Mo, Tu, We, Th, Fr, Sa, Su";
        daysSelectedFromListText.setText(daysSelectedFromListString);
        daysSelectedFromListText.setLayoutParams(daysPickedParams);
        daysSelectedFromListText.setGravity(Gravity.RIGHT);       //gravity
        daysSelectedFromListText.setTextSize(14);
        daysSelectedFromListText.setTextColor(Color.parseColor("#8a000000"));

        daysListLayout.addView(selectDaysText);
        daysListLayout.addView(daysSelectedFromListText);
        daysListLayout.setVisibility(View.GONE);

        endDatePickerLayout = new LinearLayout(getActivity());
        endDatePickerLayout.setOrientation(LinearLayout.HORIZONTAL);
        endDatePickerLayout.setLayoutParams(daysLayoutParams);
        endDatePickerLayout.setVisibility(View.GONE);

        endDatePickerText = new TextView(getActivity());
        String endDatePickerString = "End date";
        endDatePickerText.setText(endDatePickerString);
        endDatePickerText.setLayoutParams(howOftenParams);
        endDatePickerText.setTextSize(18);
        endDatePickerText.setTextColor(Color.parseColor("#000000"));

        endDatePickedText = new TextView(getActivity());
        String endDatePickedString = "Continuous";
        endDatePickedText.setText(endDatePickedString);
        endDatePickedText.setLayoutParams(daysPickedParams);
        endDatePickedText.setGravity(Gravity.RIGHT);       //gravity
        endDatePickedText.setTextSize(14);
        endDatePickedText.setTextColor(Color.parseColor("#8a000000"));

        endDatePickerLayout.addView(endDatePickerText);
        endDatePickerLayout.addView(endDatePickedText);

        newReminderLayout.addView(daysLayout, 8);
        newReminderLayout.addView(numberPicker, 9);
        newReminderLayout.addView(endDatePickerLayout, 10);
        newReminderLayout.addView(daysListLayout, 11);
        //TODO: Fix visibility

        repeatSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton repeatButton, boolean isChecked) {
                        if (isChecked) {
                            endDatePickerLayout.setVisibility(View.VISIBLE);
                            endDatePickerLayout.setOnClickListener(
                                    new LinearLayout.OnClickListener() {
                                        public void onClick(View v) {
                                            EndDatePickerFragment endDate = EndDatePickerFragment.newInstance(currentStartDate);
                                            endDate.show(getFragmentManager(), "endDatePicker");
                                        }
                                    });

                            daysListLayout.setVisibility(View.VISIBLE);
                            daysListLayout.setOnClickListener(new LinearLayout.OnClickListener() {
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
                            endDatePickerLayout.setVisibility(View.GONE);
                            daysLayout.setVisibility(View.GONE);
                            numberPicker.setVisibility(View.GONE);
                            daysListLayout.setVisibility(View.GONE);
                        }
                    }
                }
        );

        medicationSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton repeatButton, boolean isChecked) {
                        if (isChecked) {
                            enableMedicationField(true);
                            if (medication != null) {
                                enableDosageField(true);
                            }
                        } else {
                            enableMedicationField(false);
                            enableDosageField(false);
                        }
                    }
                }
        );
        saveButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if (reminder == null) {
                    if (validateName() && validateDateAndTime() && validateMedication() && validateEndDate())
                        createReminder();
                } else {
                    if (validateName() && validateDateAndTime() && validateMedication() && validateEndDate())
                        updateReminder();
                }
            }
        });
        fillFields();
        return view;
    }

    public void fillFields() {

        final Calendar c;
        // Checks if a reminder is passed to the fragment
        if (getArguments() != null) {
            reminderSwitch.setChecked(reminder.getIsActive());
            nameEditText.setText(reminder.getName());
            c = reminder.getDate();

            // Set the repeat fields if the reminder is repeating.
            if (reminder.getDays().length >= 1) {
                repeatSwitch.setChecked(true);
                daysSelectedFromListText.setText(
                        Html.fromHtml(Converter.daysArrayToSelectedDaysText(reminder.getDays())));
                selectedDays = reminder.getDays();

                GregorianCalendar endCal = reminder.getEndDate();
                int year = endCal.get(Calendar.YEAR);
                int month = endCal.get(Calendar.MONTH) + 1; //month is 0-indexed
                final int day = endCal.get(Calendar.DAY_OF_MONTH);

                // Checks if the reminder is Continuous
                if (year == 9998) {
                    endDatePickedText.setText("Continuous");
                } else {
                    String date = String.format("%02d.%02d.%4d", day, month, year);
                    endDatePickedText.setText(date);
                }

            } else {
                selectedDays = new int[]{0, 1, 2, 3, 4, 5, 6};
                daysSelectedFromListText.setText(
                        Html.fromHtml(Converter.daysArrayToSelectedDaysText(new int[]{0, 1, 2, 3, 4, 5, 6})));
            }
            // Set the medicine fields if the reminder got a medicine attached.
            if (reminder.getMedicine() != null) {
                medication = reminder.getMedicine();
                medicationSwitch.setChecked(true);
                chosenMedicationTextView.setText(reminder.getMedicine().getName());
                dosageTextField.setText(reminder.getDosage().toString());
                dosageUnitText.setText(reminder.getMedicine().getUnit());
                enableDosageField(true);
            }

        }
        // Set today's date and time as default if a reminder is not provided
        else {
            c = Calendar.getInstance();
            selectedDays = new int[]{0, 1, 2, 3, 4, 5, 6};
            daysSelectedFromListText.setText(
                    Html.fromHtml(Converter.daysArrayToSelectedDaysText(new int[]{0, 1, 2, 3, 4, 5, 6})));
        }

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //month is 0-indexed
        final int day = c.get(Calendar.DAY_OF_MONTH);
        String time = String.format("%02d:%02d", hour, minute);
        String date = String.format("%02d.%02d.%4d", day, month, year);
        currentStartDate = date;
        timeSetText.setText(time);
        dateSetText.setText(date);
    }

    public void enableDosageField(boolean enable) {

        if (!enable) {
            chooseDosageText.setTextColor(Color.parseColor("#dddddd"));
            dosageTextField.setTextColor(Color.parseColor("#dddddd"));
            dosageTextField.setEnabled(false);
            dosageUnitText.setTextColor(Color.parseColor("#dddddd"));
        } else {
            chooseDosageText.setTextColor(Color.parseColor("#000000"));
            dosageTextField.setTextColor(Color.parseColor("#8a000000"));
            dosageUnitText.setTextColor(Color.parseColor("#8a000000"));
            dosageTextField.setEnabled(true);
        }

    }

    public void enableMedicationField(boolean enable) {

        if (enable) {
            chooseMedicationText.setTextColor(Color.parseColor("#000000"));
            chosenMedicationTextView.setTextColor(Color.parseColor("#8a000000"));
            chooseMedicationWrapper.setOnClickListener(new LinearLayout.OnClickListener() {
                public void onClick(View v) {
                    MedicationPickerFragment medicationPickerFragment = new MedicationPickerFragment();
                    medicationPickerFragment.show(getFragmentManager(), "medicationPickerFragment");
                }
            });
        } else {
            chooseMedicationText.setTextColor(Color.parseColor("#dddddddd"));
            chosenMedicationTextView.setTextColor(Color.parseColor("#dddddddd"));
            chooseMedicationWrapper.setOnClickListener(null);
        }

    }

    public void setDateOnLayout(int year, int month, int day) {
        month = month + 1;  //month is 0-indexed
        String dateSet = String.format("%02d.%02d.%4d", day, month, year);
        dateSetText.setText(dateSet);
        currentStartDate = dateSet;
    }

    public void setEndDateOnLayout(int year, int month, int day) {
        month = month + 1;  //month is 0-indexed
        String dateSet = String.format("%02d.%02d.%4d", day, month, year);
        endDatePickedText.setText(dateSet);
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public void setMedicationOnLayout(Medication med) {
        this.medication = med;
        chosenMedicationTextView.setText(med.getName());
        dosageTextField.setText("");
        dosageUnitText.setText(med.getUnit());
        enableDosageField(true);
    }

    public void setTimeOnLayout(int hour, int minute) {
        String timeSet = String.format("%02d:%02d", hour, minute);
        timeSetText.setText(timeSet);
    }

    public void setDaysOnLayout(ArrayList selectedItems) {

        selectedDays = new int[selectedItems.size()];

        // Convert to the expected format for GregorianCalendar
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedDays[i] = ((Integer) selectedItems.get(i) + 2) % 7;
        }
        Arrays.sort(selectedDays);
        daysSelectedFromListText.setText(
                Html.fromHtml(Converter.daysArrayToSelectedDaysText(selectedDays)));
    }

    public void createReminder() {
        Reminder reminder = new Reminder();
        reminder.setOwnerId("temp");
        reminder.setName(nameEditText.getEditableText().toString());

        // Set start date
        GregorianCalendar cal = Converter.dateStringToCalendar(
                dateSetText.getText().toString(),
                timeSetText.getText().toString());
        reminder.setDate(cal);

        // Attach medication
        if (medicationSwitch.isChecked() && medication != null && !dosageTextField.getText().toString().equals("")) {
            reminder.setMedicine(medication);
            reminder.setDosage(Double.parseDouble(dosageTextField.getText().toString()));
        }

        // Non-repeating
        if (!repeatSwitch.isChecked()) {
            reminder.setDays(new int[]{});
        }
        // Repeating
        else {
            if (selectedDays.length > 0) {
                reminder.setDays(selectedDays);
            }
            if (endDatePickedText.getText().toString().equals("Continuous")) {
                reminder.setEndDate(new GregorianCalendar(9999, 0, 0));
            } else if (!endDatePickedText.getText().toString().equals("Continuous")) {

                // Set end date
                GregorianCalendar endCal = Converter.dateStringToCalendar(
                        endDatePickedText.getText().toString(),
                        timeSetText.getText().toString());
                reminder.setEndDate(endCal);
            }
        }
        reminder.setIsActive(reminderSwitch.isChecked());
        reminder.setReminderServerId(-1);
        ReminderListContent.ITEMS.add(0, reminder);

        System.out.println("----------Reminder Created----------" + "\n" + reminder);
        System.out.println("------------------------------------");

        //Add reminder to database
        MySQLiteHelper db = new MySQLiteHelper(mActivity);
        db.addReminder(reminder);

        mListener.onSaveNewReminder(reminder);
    }

    public void updateReminder() {
        //Updates an existing Reminder object
        reminder.setName(nameEditText.getText().toString());

        // Set start date
        GregorianCalendar cal = Converter.dateStringToCalendar(
                dateSetText.getText().toString(),
                timeSetText.getText().toString());
        reminder.setDate(cal);

        // Set active
        reminder.setIsActive(reminderSwitch.isChecked());

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
            if (endDatePickedText.getText().toString().equals("Continuous")) {
                // Set end date far into the future
                reminder.setEndDate(new GregorianCalendar(9999, 0, 0));
            } else if (!endDatePickedText.getText().toString().equals("Continuous")) {
                // Set end date
                GregorianCalendar endCal = Converter.dateStringToCalendar(
                        endDatePickedText.getText().toString(),
                        timeSetText.getText().toString());
                reminder.setEndDate(endCal);
            }
        }

        // Medication
        if (medication != null && medicationSwitch.isChecked() && !dosageTextField.getText().toString().equals("")) {
            reminder.setMedicine(medication);
            reminder.setDosage(Double.parseDouble(dosageTextField.getText().toString()));
        } else if (!medicationSwitch.isChecked() && medication != null) {
            reminder.setMedicine(null);
            reminder.setDosage(null);
        }

        System.out.println("----------Reminder Updated----------" + "\n" + reminder);
        System.out.println("------------------------------------");

        // Update existing reminder in database
        MySQLiteHelper db = new MySQLiteHelper(mActivity);
        db.updateReminder(reminder);

        mListener.onSaveNewReminder(reminder);
    }

    private boolean validateDateAndTime() {

        if (!dateSetText.getText().toString().equals("")) {

            GregorianCalendar setDate = Converter.dateStringToCalendar(
                    dateSetText.getText().toString(),
                    timeSetText.getText().toString()
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
            if (!endDatePickedText.getText().toString().equals("Continuous")) {

                // End date
                GregorianCalendar endCal = Converter.dateStringToCalendar(
                        endDatePickedText.getText().toString(),
                        timeSetText.getText().toString()
                );

                // Start date
                GregorianCalendar setDate = Converter.dateStringToCalendar(
                        dateSetText.getText().toString(),
                        timeSetText.getText().toString()
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
        if (nameEditText.getText().toString().equals("")) {
            Toast toast = Toast.makeText(getActivity(), "Please enter a reminder name", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        return true;
    }

    private boolean validateMedication() {

        if (medicationSwitch.isChecked()) {
            if (chosenMedicationTextView.getText().toString().equals("Click to choose")) {
                Toast toast = Toast.makeText(getActivity(), "Please choose a medication", Toast.LENGTH_LONG);
                toast.show();
                return false;
            } else {
                if (dosageTextField.getText().toString().equals("")) {
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
