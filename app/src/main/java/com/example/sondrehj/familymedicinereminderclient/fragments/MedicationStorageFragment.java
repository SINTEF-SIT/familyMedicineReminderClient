package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MedicationListContent;
import com.example.sondrehj.familymedicinereminderclient.dialogs.SelectUnitDialogFragment;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.sync.PostMedicationJob;
import com.example.sondrehj.familymedicinereminderclient.utility.SerializableString;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;
import com.path.android.jobqueue.JobManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedicationStorageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicationStorageFragment extends android.app.Fragment implements TitleSupplier {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MEDICATION = "medication";

    private Medication mMedication;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_medication, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        if (mMedication != null){
            fillTextFields();
        }

        Button saveMedicationBtn = (Button) view.findViewById(R.id.saveMedicationBtn);
        saveMedicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText medicationName = (EditText) getActivity().findViewById(R.id.medicationName);
                EditText medicationAmount = (EditText) getActivity().findViewById(R.id.medicationAmount);
                TextView medicationUnit = (TextView) getActivity().findViewById(R.id.medicationUnit);

                if (medicationName.getText().toString().equals("") || medicationAmount.getText().toString().equals("")
                        ||  medicationUnit.getText().toString().equals("Click to choose")) {
                    Toast toast = Toast.makeText(getActivity(), "All fields must be entered!", Toast.LENGTH_LONG);
                    toast.show();

                } else {
                    if (mMedication == null) {
                        createNewMedication();
                    } else {
                        updateMedication();
                    }
                    //Return to MedicationCabinet
                    ((MainActivity) getActivity()).changeFragment(new MedicationListFragment());
                }
            }
        });

        final FrameLayout unitWrapper = (FrameLayout) view.findViewById(R.id.unitWrapper);
        unitWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getActivity().getFragmentManager();
                SelectUnitDialogFragment test = new SelectUnitDialogFragment();
                test.show(fm, "test");
            }
        });

    }

    public void setUnitText(String unit){
        TextView unitText = (TextView) getActivity().findViewById(R.id.medicationUnit);
        unitText.setText(unit);
    }


    public void fillTextFields(){

        //Fills the TextFields with data from the given medicine object
        EditText medicationName = (EditText) getActivity().findViewById(R.id.medicationName);
        EditText medicationAmount = (EditText) getActivity().findViewById(R.id.medicationAmount);
        TextView medicationUnit = (TextView) getActivity().findViewById(R.id.medicationUnit);

        medicationName.setText(mMedication.getName());
        medicationAmount.setText(Double.toString(mMedication.getCount()));
        medicationUnit.setText(mMedication.getUnit());
    }

    public void createNewMedication(){

        //MedicationStorage input-fields
        EditText medicationName = (EditText) getActivity().findViewById(R.id.medicationName);
        EditText medicationAmount = (EditText) getActivity().findViewById(R.id.medicationAmount);
        TextView medicationUnit = (TextView) getActivity().findViewById(R.id.medicationUnit);

        //Creates a new Medication object with the values of the input-fields
        Medication medication = new Medication(
                0,
                -1,
                "786#13%",
                medicationName.getText().toString(),
                Double.parseDouble(medicationAmount.getText().toString()),
                medicationUnit.getText().toString()
        );

        //Adds the new medicine to MedicationListContent
        //MedicationListFragment.medications.add(0, medication);

        // Adds the medicine to the DB
        MySQLiteHelper db = new MySQLiteHelper(getActivity());
        db.addMedication(medication);
        JobManager jobManager = ((MainActivity) getActivity()).getJobManager();

        String userId = AccountManager.get(getActivity()).getUserData(MainActivity.getAccount(getActivity()), "userId");
        System.out.println("USERID: " + userId);

        jobManager.addJobInBackground(new PostMedicationJob(medication, userId));
        BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONS));

        System.out.println("---------Medication Created---------" + "\n" + medication);
        System.out.println("------------------------------------");

    }

    public void updateMedication() {
        EditText medicationName = (EditText) getActivity().findViewById(R.id.medicationName);
        EditText medicationAmount = (EditText) getActivity().findViewById(R.id.medicationAmount);
        TextView medicationUnit = (TextView) getActivity().findViewById(R.id.medicationUnit);

        //Updates an existing Medication object
        mMedication.setName(medicationName.getText().toString());
        mMedication.setCount(Double.parseDouble(medicationAmount.getText().toString()));
        mMedication.setUnit(medicationUnit.getText().toString());

        // Updates the DB
        MySQLiteHelper db = new MySQLiteHelper(getActivity());
        db.updateMedication(mMedication);

        System.out.println("---------Medication Updated---------" + "\n" + mMedication);
        System.out.println("------------------------------------");
    }

    @Override
    public String getTitle() {
        return "New Medication";
    }
}
