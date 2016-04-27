package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.dialogs.DeleteAllDataDialogFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AccountAdministrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountAdministrationFragment extends android.app.Fragment implements TitleSupplier {


    @Bind(R.id.account_group_guardian) LinearLayout guardianGroup;

    @Bind(R.id.account_personal_year_input) EditText yearEditText;
    @Bind(R.id.account_personal_reminder_switch) Switch reminderSwitch;

    @Bind(R.id.account_general_notification_switch) Switch notificationSwitch;


    @Bind(R.id.account_settings_save_button) Button saveButton;
    @Bind(R.id.account_delete_data_button) Button deleteDataButton;


    public AccountAdministrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountAdministrationFragment.
     */
    public static AccountAdministrationFragment newInstance() {
        AccountAdministrationFragment fragment = new AccountAdministrationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_administration, container, false);
        ButterKnife.bind(this, view);
        fillTextFields();
        guardianGroup.setVisibility(View.GONE);
        return view;
    }

    @OnClick(R.id.account_settings_save_button)
    public void onSaveButtonClick() {
        updateAccountInformation();
        Toast.makeText(getActivity(), "Settings updated", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.account_delete_data_button)
    public void onDeleteButtonClick() {
        FragmentManager fm = getActivity().getFragmentManager();
        DeleteAllDataDialogFragment daf = new DeleteAllDataDialogFragment();
        daf.show(fm, "delete");
    }

    public boolean updateAccountInformation(){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("AccountSettings", Context.MODE_PRIVATE).edit();
        editor.putInt("yearOfBirth", Integer.parseInt(yearEditText.getText().toString()));
        editor.apply();
        return true;
    }

    public boolean fillTextFields(){

        SharedPreferences prefs = getActivity().getSharedPreferences("AccountSettings", Context.MODE_PRIVATE);
        int snoozeTime = prefs.getInt("snoozeTime", 180000) / 60000;
        int yearOfBirth = prefs.getInt("yearOfBirth", 2000);

        yearEditText.setText(Integer.toString(yearOfBirth));
        return true;
    }

    @Override
    public String getTitle() {
        return "Settings";
    }
}
