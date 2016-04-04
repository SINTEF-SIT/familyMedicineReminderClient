package com.example.sondrehj.familymedicinereminderclient;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.sondrehj.familymedicinereminderclient.dummy.ReminderListContent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.sqlite.MySQLiteHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewReminderFragment.OnNewReminderInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewReminderFragment extends android.app.Fragment {

    private Switch reminderSwitch;
    private EditText nameEditText;
    private LinearLayout datePickerLayout;
    private TextView dateSetText;
    private LinearLayout timePickerLayout;
    private TextView timeSetText;
    private Reminder reminder;
    private Button saveButton;
    protected Activity mActivity;


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
        View view = inflater.inflate(R.layout.fragment_new_reminder, container, false);

        reminderSwitch = (Switch) view.findViewById(R.id.reminderSwitch);
        datePickerLayout = (LinearLayout) view.findViewById(R.id.datePickerLayout);
        dateSetText = (TextView) view.findViewById(R.id.dateSetText);
        timePickerLayout = (LinearLayout) view.findViewById(R.id.timePickerLayout);
        timeSetText = (TextView) view.findViewById(R.id.timeSetText);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        saveButton = (Button) view.findViewById(R.id.saveButton);

        if (getArguments() != null) {
            Reminder tempReminder = (Reminder) getArguments().get(REMINDER_ARGS);
            nameEditText.setText(tempReminder.getName());
        }

        //sets todays date and time as default
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //month is 0-indexed
        int day = c.get(Calendar.DAY_OF_MONTH);
        String currentTime = String.format("%02d:%02d", hour, minute);
        timeSetText.setText(currentTime);
        String todaysDate = String.format("%02d.%02d.%4d", day, month, year);
        dateSetText.setText(todaysDate);


        datePickerLayout.setOnClickListener(
                new LinearLayout.OnClickListener() {
                    public void onClick(View v) {
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

        saveButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (reminder == null) {
                    createReminder();
                } else {
                    updateReminder();
                }
            }
        });

        return view;
    }

    public void setDateOnLayout(int year, int month, int day) {
        month = month + 1;  //month is 0-indexed
        String dateSet = String.format("%02d.%02d.%4d", day, month, year);
        dateSetText.setText(dateSet);
    }

    public void setReminder(Reminder reminder){
        System.out.println(reminder);
        this.reminder = reminder;
    }

    public void setTimeOnLayout(int hour, int minute) {
        String timeSet = String.format("%02d:%02d", hour, minute);
        timeSetText.setText(timeSet);
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
        reminder.setMedicine(new Medication(1, "1", "Paracetamol", 2.0, "ml"));
        reminder.setUnits("1");
        reminder.setIsActive(reminderSwitch.isActivated());
        ReminderListContent.ITEMS.add(0, reminder);
        mListener.onSaveNewReminder();

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
        mListener.onSaveNewReminder();
        //Update existing reminder in database
        MySQLiteHelper db = new MySQLiteHelper(mActivity);
        db.updateReminder(reminder);
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
        void onSaveNewReminder();
    }

    public Switch getReminderSwitch() {
        return reminderSwitch;
    }

    public EditText getNameEditText() {
        return nameEditText;
    }

    public TextView getDateSetText() {
        return dateSetText;
    }

    public TextView getTimeSetText() {
        return timeSetText;
    }

    public Button getSaveButton() {
        return saveButton;
    }

}
