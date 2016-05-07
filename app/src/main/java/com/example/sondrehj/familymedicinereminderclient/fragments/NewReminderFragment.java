package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.dialogs.TimePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.DatePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.EndDatePickerFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.MedicationPickerFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.SelectDaysDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.jobs.PostMedicationJob;
import com.example.sondrehj.familymedicinereminderclient.jobs.PostReminderJob;
import com.example.sondrehj.familymedicinereminderclient.jobs.UpdateReminderJob;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import com.example.sondrehj.familymedicinereminderclient.utility.Converter;
import com.example.sondrehj.familymedicinereminderclient.utility.NewReminderInputValidator;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;
import com.example.sondrehj.familymedicinereminderclient.utility.NewReminderInputConverter;


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

    //days of the week
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
    public static final String REMINDER_UPDATE = "reminder-update";
    public static final String REMINDER_INSERT = "reminder-insert";
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
        weekDayTextViewArray = new TextView[]{Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday};

        // Hide layouts which are opened with switches.
        chooseMedicationGroup.setVisibility(View.GONE);
        chooseDosageGroup.setVisibility(View.GONE);
        repeatSwitch.setOnCheckedChangeListener((CompoundButton repeatButton, boolean isChecked) -> {
            if (isChecked) {
                endDatePickerGroup.setVisibility(View.VISIBLE);
                endDatePickerGroup.setOnClickListener((View v) -> {
                    EndDatePickerFragment endDate = EndDatePickerFragment.newInstance(currentStartDate);
                    endDate.show(getFragmentManager(), "endDatePicker");
                });
                chooseDaysPickerGroup.setVisibility(View.VISIBLE);
                chooseDaysPickerGroup.setOnClickListener((View v) -> {
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
                });
            } else {
                selectedDays = new int[]{0, 1, 2, 3, 4, 5, 6};
                endDatePickerGroup.setVisibility(View.GONE);
                chooseDaysPickerGroup.setVisibility(View.GONE);
            }
        });

        attachMedicationSwitch.setOnCheckedChangeListener((CompoundButton medicationSwitch, boolean isChecked) -> {
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
        });
        fillFields();
        if (reminder != null) {
            getActivity().setTitle("Edit reminder");
        }
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
        NewReminderInputValidator inputValidator = new NewReminderInputValidator(
                getActivity(), nameInput, dateInput, timeInput, attachMedicationSwitch,
                attachMedicationSwitch, dosageInput, repeatSwitch, endDateInput);
        if (reminder == null) {
            if (inputValidator.validateAllFields())
                createReminder();
        } else {
            if (inputValidator.validateAllFields())
                updateReminder();
        }
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public void createReminder() {
        String userId = AccountManager.get(getActivity()).getUserData(MainActivity.getAccount(getActivity()), "userId");
        String authToken = AccountManager.get(getActivity()).getUserData(MainActivity.getAccount(getActivity()), "authToken");

        NewReminderInputConverter vr = new NewReminderInputConverter(getActivity());
        Reminder reminder = vr.CreateReminderFromInput(
                nameInput, dateInput, timeInput,
                medication, attachMedicationSwitch,
                dosageInput, repeatSwitch, selectedDays,
                endDateInput, activeSwitch, ((MainActivity) getActivity()).getCurrentUser());

        //Add reminder to database
        executeDatabaseReminderAction(reminder, REMINDER_INSERT);
        ((MainActivity) getActivity()).getJobManager().addJobInBackground(new PostReminderJob(reminder, ((MainActivity) getActivity()).getCurrentUser().getUserId(), authToken));
        mListener.onSaveNewReminder(reminder);
    }

    public void updateReminder() {
        String userId = AccountManager.get(getActivity()).getUserData(MainActivity.getAccount(getActivity()), "userId");
        String authToken = AccountManager.get(getActivity()).getUserData(MainActivity.getAccount(getActivity()), "authToken");
        System.out.println("In newreminder update: " + reminder);

        NewReminderInputConverter vr = new NewReminderInputConverter(getActivity());
        reminder = vr.UpdateReminderFromInput(
                nameInput, dateInput, timeInput,
                medication, attachMedicationSwitch,
                dosageInput, repeatSwitch, selectedDays,
                endDateInput, activeSwitch, reminder, ((MainActivity) getActivity()).getCurrentUser());

        System.out.println("In newreminder update (after converter): " + reminder);


        // Update existing reminder in database
        executeDatabaseReminderAction(reminder, REMINDER_UPDATE);

        ((MainActivity) getActivity()).getJobManager().addJobInBackground(new UpdateReminderJob(reminder, ((MainActivity) getActivity()).getCurrentUser().getUserId(), authToken));

        mListener.onSaveNewReminder(reminder);
    }

    public void setBoldOnSelectedDays(int[] selectedDays) {
        for (int i = 0; i < 7; i++) {
            boolean inList = false;
            for (int day : selectedDays) {
                if (i == day) {
                    inList = true;
                    weekDayTextViewArray[i].setTypeface(null, Typeface.BOLD);
                    weekDayTextViewArray[i].setTextColor(Color.GRAY);
                    break;
                }
            }
            if (!inList) {
                weekDayTextViewArray[i].setTypeface(null, Typeface.NORMAL);
                weekDayTextViewArray[i].setTextColor(Color.parseColor("#DDDDDD"));
            }
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

    public boolean executeDatabaseReminderAction(Reminder r, String action){
        MySQLiteHelper db = new MySQLiteHelper(getActivity());
        String toastText;
        switch (action){
            case REMINDER_INSERT:
                db.addReminder(r);
                toastText = "Reminder created";
                break;
            case REMINDER_UPDATE:
                db.updateReminder(r);
                toastText = "Reminder updated";
                break;
            default:
                return false;
        }
        Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        return true;

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
            //TODO: Medication field lacks onClickListener
            chooseMedicationGroup.setOnClickListener(new View.OnClickListener() {
                @Override
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

    public void setDaysOnLayout(ArrayList selectedItems) {
        selectedDays = new int[selectedItems.size()];

        // Convert to the expected format for GregorianCalendar
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedDays[i] = ((Integer) selectedItems.get(i) + 1) % 7;
        }
        Arrays.sort(selectedDays);
        System.out.println("Hello: " + Arrays.toString(selectedDays));
        setBoldOnSelectedDays(selectedDays);
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
        return "New reminder";
    }

    public interface OnNewReminderInteractionListener {
        void onSaveNewReminder(Reminder r);
    }
}
