package com.example.sondrehj.familymedicinereminderclient;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.dummy.MedicationListContent;
import com.example.sondrehj.familymedicinereminderclient.dummy.ReminderListContent;
import com.example.sondrehj.familymedicinereminderclient.modals.EndDatePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.modals.MedicationPickerFragment;
import com.example.sondrehj.familymedicinereminderclient.modals.SelectDaysDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.sqlite.MySQLiteHelper;


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
public class NewReminderFragment extends android.app.Fragment {

    private LinearLayout newReminderLayout;
    private LinearLayout endDatePickerLayout;
    private LinearLayout repeatLayout;
    private LinearLayout daysLayout;
    private LinearLayout daysListLayout;
    private FrameLayout timePickerLayout;
    private FrameLayout datePickerLayout;


    // ----- MEDICATION ----- //
    private FrameLayout chooseMedicationWrapper;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REMINDER_ARGS = "reminder";

    // TODO: Rename and change types of parameters

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
    // TODO: Rename and change types and number of parameters
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
        View view = inflater.inflate(R.layout.fragment_new_reminder2, container, false);

        newReminderLayout = (LinearLayout) view.findViewById(R.id.newReminderLayout);
        reminderSwitch = (Switch) view.findViewById(R.id.reminderSwitch);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        datePickerLayout = (FrameLayout) view.findViewById(R.id.datePickerLayout);
        dateSetText = (TextView) view.findViewById(R.id.dateSetText);
        timePickerLayout = (FrameLayout) view.findViewById(R.id.timePickerLayout);
        timeSetText = (TextView) view.findViewById(R.id.timeSetText);
        repeatLayout = (LinearLayout) view.findViewById(R.id.repeatLayout);
        repeatSwitch = (Switch) view.findViewById(R.id.repeatSwitch);
        medicationSwitch = (Switch) view.findViewById(R.id.medicationSwitch);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        // MEDICATION
        chooseDosageText = (TextView)view.findViewById(R.id.chooseDosage);
        dosageTextField = (EditText)view.findViewById(R.id.dosage);
        dosageUnitText = (TextView)view.findViewById(R.id.dosageUnit);
        chooseMedicationText = (TextView)view.findViewById(R.id.chooseMedication);
        chosenMedicationTextView = (TextView)view.findViewById(R.id.chosenMedication);
        chooseMedicationWrapper = (FrameLayout) view.findViewById(R.id.chooseMedicationWrapper);




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
        String selectDaysString = "Select days";
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


        repeatSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton repeatButton, boolean isChecked) {
                        if (isChecked) {

                            /*
                            daysLayout.setVisibility(View.VISIBLE);
                            daysLayout.setOnClickListener(new LinearLayout.OnClickListener() {
                                public void onClick(View v) {
                                    if (numberPicker.getVisibility() == View.GONE) {
                                        numberPicker.setVisibility(View.VISIBLE);
                                        String everyDay = "Every day";
                                        daysPickedText.setText(everyDay);

                                        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                            @Override
                                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                                if (newVal == 1) {
                                                    String newValString = "Every day";
                                                    daysPickedText.setText(newValString);
                                                } else {

                                                    String newValString = "Every " + newVal + " day";
                                                    daysPickedText.setText(newValString);
                                                }
                                            }
                                        });
                                    } else if (numberPicker.getVisibility() == View.VISIBLE) {
                                        numberPicker.setVisibility(View.GONE);
                                    }
                                }
                            }); */

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
                                    //TODO: Når man åpner en ny dialog (for andre gang) er dagene man har krysset av, fjernet
                                    SelectDaysDialogFragment selectDaysDialogFragment = new SelectDaysDialogFragment();
                                    /*
                                    int[] selectedDays2 = new int[selectedDays.length];
                                    for (int i = 0; i < selectedDays.length; i++) {
                                        selectedDays2[i] = selectedDays[i];
                                    }
                                    Bundle daysAlreadyChecked = new Bundle();
                                    daysAlreadyChecked.putIntArray("alreadySelected", selectedDays2);
                                    selectDaysDialogFragment.setArguments(daysAlreadyChecked);
                                    */
                                    selectDaysDialogFragment.show(getFragmentManager(), "selectdayslist");
                                }
                            });

                        } else {
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
                            chooseMedicationText.setTextColor(Color.parseColor("#000000"));
                            chosenMedicationTextView.setTextColor(Color.parseColor("#8a000000"));
                            chooseMedicationWrapper.setOnClickListener(new LinearLayout.OnClickListener() {
                                public void onClick(View v) {
                                    MedicationPickerFragment medicationPickerFragment = new MedicationPickerFragment();
                                    medicationPickerFragment.show(getFragmentManager(), "medicationPickerFragment");
                                }
                            });
                            if(medication != null) {
                                chooseDosageText.setTextColor(Color.parseColor("#000000"));
                                dosageTextField.setTextColor(Color.parseColor("#8a000000"));
                                dosageTextField.setEnabled(true);
                                dosageUnitText.setTextColor(Color.parseColor("#8a000000"));
                            }
                        } else {
                            chooseMedicationText.setTextColor(Color.parseColor("#dddddddd"));
                            chosenMedicationTextView.setTextColor(Color.parseColor("#dddddddd"));
                            chooseMedicationWrapper.setOnClickListener(null);
                            if(medication != null) {
                                chooseDosageText.setTextColor(Color.parseColor("#dddddd"));
                                dosageTextField.setTextColor(Color.parseColor("#dddddd"));
                                dosageTextField.setEnabled(false);
                                dosageUnitText.setTextColor(Color.parseColor("#dddddd"));
                            }

                        }
                    }
                }
        );

        saveButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (nameEditText.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getActivity(), "Name must be entered!", Toast.LENGTH_LONG);
                    toast.show();
                } else {

                    if (reminder == null) {
                        createReminder();
                    } else {
                        updateReminder();
                    }
                }
            }
        });
        fillFields();
        return view;
    }

    public void fillFields() {

        // Sets today's date and time as default
        final Calendar c;
        if (getArguments() != null) {
            reminderSwitch.setChecked(reminder.getIsActive());
            nameEditText.setText(reminder.getName());
            c = reminder.getDate();
            System.out.println(reminder.getEndDate());
            if (reminder.getDays().length >= 1) {
                repeatSwitch.setChecked(true);
                daysSelectedFromListText.setText(
                        Html.fromHtml(mListener.newReminderListGetSelectedDaysText(reminder.getDays())));
                GregorianCalendar endCal = reminder.getEndDate();
                int year = endCal.get(Calendar.YEAR);
                int month = endCal.get(Calendar.MONTH) + 1; //month is 0-indexed
                final int day = endCal.get(Calendar.DAY_OF_MONTH);

                if (year == 9998) {
                    endDatePickedText.setText("Continuous");
                } else {
                    String date = String.format("%02d.%02d.%4d", day, month, year);
                    endDatePickedText.setText(date);
                }
            }
        } else {
            c = Calendar.getInstance();
            daysSelectedFromListText.setText(
                    Html.fromHtml(mListener.newReminderListGetSelectedDaysText(new int[]{0,1,2,3,4,5,6})));
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

    public void setMedicationOnLayout(Medication med){
        this.medication = med;
        chosenMedicationTextView.setText(med.getName());
        chooseDosageText.setTextColor(Color.parseColor("#000000"));
        dosageTextField.setTextColor(Color.parseColor("#8a000000"));
        dosageTextField.setEnabled(true);
        dosageUnitText.setTextColor(Color.parseColor("#8a000000"));
        dosageUnitText.setText(med.getUnit());

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
                Html.fromHtml(mListener.newReminderListGetSelectedDaysText(selectedDays)));
    }


    public void createReminder() {
        Reminder reminder = new Reminder();
        reminder.setOwnerId("temp");
        reminder.setName(nameEditText.getEditableText().toString());

        String[] date = dateSetText.getText().toString().split("\\W+");
        String[] time = timeSetText.getText().toString().split(":");

        GregorianCalendar cal = new GregorianCalendar(
                Integer.parseInt(date[2]),      //Year
                Integer.parseInt(date[1]) - 1,  //Month
                Integer.parseInt(date[0]),      //Date
                Integer.parseInt(time[0]),      //Hour
                Integer.parseInt(time[1]));     //Minute
        reminder.setDate(cal);
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
                String[] endDate = endDatePickedText.getText().toString().split("\\W+");
                GregorianCalendar endCal = new GregorianCalendar(
                        Integer.parseInt(endDate[2]),      //Year
                        Integer.parseInt(endDate[1]) - 1,  //Month
                        Integer.parseInt(endDate[0]),      //Date
                        Integer.parseInt(time[0]),      //Hour
                        Integer.parseInt(time[1]));     //Minute
                reminder.setEndDate(endCal);
            }
        }
        reminder.setMedicine(new Medication(1, "1", "Paracetamol", 2.0, "ml"));
        reminder.setUnits("1");
        reminder.setIsActive(reminderSwitch.isChecked());
        reminder.setReminderServerId(-1);
        ReminderListContent.ITEMS.add(0, reminder);
        mListener.onSaveNewReminder(reminder);

        //Add reminder to database
        MySQLiteHelper db = new MySQLiteHelper(mActivity);
        db.addReminder(reminder);
    }

    public void updateReminder() {
        //Updates an existing Reminder object
        reminder.setName(nameEditText.getText().toString());

        String[] date = dateSetText.getText().toString().split("\\W+");
        String[] time = timeSetText.getText().toString().split(":");

        GregorianCalendar cal = new GregorianCalendar(
                Integer.parseInt(date[2]),      //Year
                Integer.parseInt(date[1]) - 1,  //Month
                Integer.parseInt(date[0]),      //Date
                Integer.parseInt(time[0]),      //Hour
                Integer.parseInt(time[1]));     //Minute
        reminder.setDate(cal);
        reminder.setIsActive(reminderSwitch.isChecked());

        // Non-repeating
        if (!repeatSwitch.isChecked()) {
            System.out.println("NOT CHECKED");
            reminder.setDays(new int[]{});
            reminder.setEndDate(null);
        }

        if (selectedDays == null && reminder.getDays().length == 0) {
            reminder.setDays(new int[]{});
        }

        // Repeating
        if (repeatSwitch.isChecked()) {
            System.out.println("CHECKED");
            if (selectedDays == null && reminder.getDays().length > 1) {
                reminder.setDays(reminder.getDays());
            } else {
                reminder.setDays(selectedDays);
            }
            if (endDatePickedText.getText().toString().equals("Continuous")) {
                reminder.setEndDate(new GregorianCalendar(9999, 0, 0));
            } else if (!endDatePickedText.getText().toString().equals("Continuous")) {
                String[] endDate = endDatePickedText.getText().toString().split("\\W+");
                GregorianCalendar endCal = new GregorianCalendar(
                        Integer.parseInt(endDate[2]),      //Year
                        Integer.parseInt(endDate[1]) - 1,  //Month
                        Integer.parseInt(endDate[0]),      //Date
                        Integer.parseInt(time[0]),      //Hour
                        Integer.parseInt(time[1]));     //Minute
                reminder.setEndDate(endCal);
            }
        }

        mListener.onSaveNewReminder(reminder);
        // Update existing reminder in database
        MySQLiteHelper db = new MySQLiteHelper(mActivity);
        db.updateReminder(reminder);
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
        // TODO: Update argument type and name
        void onSaveNewReminder(Reminder r);

        String newReminderListGetSelectedDaysText(int[] reminder_days);
    }

}
