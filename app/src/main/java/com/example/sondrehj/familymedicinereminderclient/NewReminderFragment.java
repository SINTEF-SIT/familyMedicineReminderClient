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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.sondrehj.familymedicinereminderclient.dummy.ReminderListContent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;


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
    private Button timePickerButton;
    private Button datePickerButton;
    private TextView nameText;
    private EditText nameEditText;
    private Reminder reminder;
    private Button saveBtn;

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
        Bundle bundle = new Bundle();
        bundle.putSerializable(REMINDER_ARGS, reminder);
        fragment.setArguments(bundle);
        fragment.setReminder(reminder);
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
        timePickerButton = (Button) view.findViewById(R.id.timePickerButton);
        datePickerButton = (Button) view.findViewById(R.id.datePickerButton);
        nameText = (TextView) view.findViewById(R.id.nameText);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        saveBtn = (Button) view.findViewById(R.id.new_reminder_save);

        timePickerButton.setOnClickListener(
                new Button.OnClickListener() {public void onClick(View v) {
                        DialogFragment timePickerFragment = new TimePickerFragment();
                        timePickerFragment.show(getFragmentManager(), "timePicker");
                    }
                }
        );

        datePickerButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        DialogFragment datePickerFragment = new DatePickerFragment();
                        datePickerFragment.show(getFragmentManager(), "date picker");
                    }
                }
        );

        saveBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                createReminder();
            }
        });
        return view;
    }

    public void setReminder(Reminder reminder){
        System.out.println(reminder);
        this.reminder = reminder;
    }

    public void setTimeOnButton(int hour, int minute) {
        String timeSet = hour + ":" + minute;
        timePickerButton.setText(timeSet);
    }

    public void setDateOnButton(int year, int month, int day) {
        String dateSet = day + "." + month + "." + year;
        datePickerButton.setText(dateSet);
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
        reminder.setName(nameEditText.getEditableText().toString());
        reminder.setTime(timePickerButton.getText().toString());
        reminder.setMedicine(new Medication("1", "Paracetamol", 2.0, "ml"));
        reminder.setUnits("1");
        ReminderListContent.ITEMS.add(0, reminder);

        mListener.onSaveNewReminder();
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
}
