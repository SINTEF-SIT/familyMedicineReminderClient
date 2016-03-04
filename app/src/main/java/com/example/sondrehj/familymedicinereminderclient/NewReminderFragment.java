package com.example.sondrehj.familymedicinereminderclient;

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

import org.w3c.dom.Text;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewReminderFragment.OnFragmentInteractionListener} interface
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
    private Button saveButton;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NewReminderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewReminderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewReminderFragment newInstance(String param1, String param2) {
        NewReminderFragment fragment = new NewReminderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_reminder, container, false);

        reminderSwitch = (Switch) view.findViewById(R.id.reminderSwitch);
        timePickerButton = (Button) view.findViewById(R.id.timePickerButton);
        datePickerButton = (Button) view.findViewById(R.id.datePickerButton);
        nameText = (TextView) view.findViewById(R.id.nameText);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        saveButton = (Button) view.findViewById(R.id.saveButton);

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        //String currentTime = hour + ":" + minute;
        String currentTime = String.format("%02d:%02d", hour, minute);
        timePickerButton.setText(currentTime);
        //String todaysDate = day + "." + month + "." + year;
        String todaysDate = String.format("%02d.%02d.%4d", day, month, year);
        datePickerButton.setText(todaysDate);

        timePickerButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
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





        return view;
    }

    public void setTimeOnButton(int hour, int minute) {
        String timeSet = hour + ":" + minute;
        timePickerButton.setText(timeSet);
    }

    public void setDateOnButton(int year, int month, int day) {
        String dateSet = day + "." + month + "." + year;
        datePickerButton.setText(dateSet);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onNewReminderFragmentInteraction(uri);
        }
    }

    //pass in the fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNewReminderFragmentInteraction(Uri uri);
    }

    public Switch getReminderSwitch() {
        return reminderSwitch;
    }

    public void setReminderSwitch(Switch reminderSwitch) {
        this.reminderSwitch = reminderSwitch;
    }

    public Button getTimePickerButton() {
        return timePickerButton;
    }

    public void setTimePickerButton(Button timePickerButton) {
        this.timePickerButton = timePickerButton;
    }

    public Button getDatePickerButton() {
        return datePickerButton;
    }

    public void setDatePickerButton(Button datePickerButton) {
        this.datePickerButton = datePickerButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(Button saveButton) {
        this.saveButton = saveButton;
    }
}
