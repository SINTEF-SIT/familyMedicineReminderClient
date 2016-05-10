package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.accounts.AccountManager;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;

import com.example.sondrehj.familymedicinereminderclient.dialogs.CreateReminderForMedicationDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.LinkingDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.dialogs.SelectUnitDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.jobs.UpdateMedicationJob;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.jobs.PostMedicationJob;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedicationStorageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicationStorageFragment extends android.support.v4.app.Fragment {

    //TODO: Not really clear for the user that this is a cabinet where you list how many pills you possess in total

    //TODO: When you add a medicine, ask the user if they want to set a reminder and set them to NewReminder

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MEDICATION = "medication";
    private Medication mMedication;

    @Bind(R.id.medication_storage_medication_input) EditText medicationNameInput;
    @Bind(R.id.medication_storage_amount_input) EditText medicationAmountInput;
    @Bind(R.id.medication_storage_unit_input) TextView medicationUnitInput;

    public MedicationStorageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param medication Parameter 1.
     * @return A new instance of fragment MedicationStorageFragment.
     */
    public static MedicationStorageFragment newInstance(Medication medication) {
        MedicationStorageFragment fragment = new MedicationStorageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDICATION, medication);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMedication = (Medication) getArguments().getSerializable(ARG_MEDICATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_medication, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mMedication != null) {
            fillTextFields();
            getActivity().setTitle("Edit medication");
        }
    }

    @OnClick(R.id.medication_storage_group_unit)
    public void onUnitGroupClick() {
        FragmentManager fm = getActivity().getFragmentManager();
        SelectUnitDialogFragment selectUnitDialogFragment = new SelectUnitDialogFragment();
        selectUnitDialogFragment.show(fm, "test");
    }

    @OnClick(R.id.medication_storage_save_button)
    public void onMedicationStorageSaveButton() {
        if (medicationNameInput.getText().toString().equals("")
                || medicationAmountInput.getText().toString().equals("")
                || medicationUnitInput.getText().toString().equals("Click to choose")) {
            Toast toast = Toast.makeText(getActivity(), "All fields must be entered!", Toast.LENGTH_LONG);
            toast.show();
        } else {
            if (mMedication == null) {
                createNewMedication();
                //CreateReminderForMedicationDialogFragment crm = CreateReminderForMedicationDialogFragment.newInstance(newMedication);
                //crm.show(getFragmentManager(), "create_reminder_for_medication");
                //Return to MedicationCabinet
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                ((MainActivity) getActivity()).changeFragment(new MedicationListFragment());
            } else {
                updateMedication();
                //Return to MedicationCabinet
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                ((MainActivity) getActivity()).changeFragment(new MedicationListFragment());
            }
        }
    }


    public void setUnitText(String unit) {
        medicationUnitInput.setText(unit);
    }

    public void fillTextFields() {
        //Fills the TextFields with data from the given medicine object
        medicationNameInput.setText(mMedication.getName());
        medicationAmountInput.setText(Double.toString(mMedication.getCount()));
        medicationUnitInput.setText(mMedication.getUnit());
    }

    public Medication createNewMedication() {

        String userId = AccountManager.get(getActivity()).getUserData(MainActivity.getAccount(getActivity()), "userId");
        String authToken = AccountManager.get(getActivity()).getUserData(MainActivity.getAccount(getActivity()), "authToken");
        Medication medication = new Medication(
                0,
                -1,
                ((MainActivity)getActivity()).getCurrentUser().getUserId(),
                medicationNameInput.getText().toString(),
                Double.parseDouble(medicationAmountInput.getText().toString()),
                medicationUnitInput.getText().toString()
        );

        // Adds the medicine to the DB
        MySQLiteHelper db = new MySQLiteHelper(getActivity());
        db.addMedication(medication);

        ((MainActivity) getActivity()).getJobManager().addJobInBackground(new PostMedicationJob(medication, ((MainActivity) getActivity()).getCurrentUser().getUserId(), authToken));
        BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONS));
        return medication;

    }

    public void updateMedication() {
        //Fetches the userId
        String userId = AccountManager.get(getActivity()).getUserData(MainActivity.getAccount(getActivity()), "userId");
        String authToken = AccountManager.get(getActivity()).getUserData(MainActivity.getAccount(getActivity()), "authToken");

        //Updates an existing Medication object
        mMedication.setName(medicationNameInput.getText().toString());
        mMedication.setCount(Double.parseDouble(medicationAmountInput.getText().toString()));
        mMedication.setUnit(medicationUnitInput.getText().toString());

        // Updates the DB
        MySQLiteHelper db = new MySQLiteHelper(getActivity());
        db.updateMedication(mMedication);

        ((MainActivity) getActivity()).getJobManager().addJobInBackground(new UpdateMedicationJob(mMedication, ((MainActivity) getActivity()).getCurrentUser().getUserId(), authToken));
        BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONS));
    }
}
