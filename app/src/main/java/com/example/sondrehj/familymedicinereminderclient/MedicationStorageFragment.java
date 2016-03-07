package com.example.sondrehj.familymedicinereminderclient;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.sondrehj.familymedicinereminderclient.dummy.MedicationListContent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import java.io.Serializable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MedicationStorageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MedicationStorageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicationStorageFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MEDICATION = "medication";

    // TODO: Rename and change types of parameters
    private Medication mMedicaiton;

    private OnFragmentInteractionListener mListener;

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
    // TODO: Rename and change types and number of parameters
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
            mMedicaiton = (Medication) getArguments().getSerializable(ARG_MEDICATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medication_storage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        if (mMedicaiton != null){
            fillTextFields();
        }

        Button saveMedicationBtn = (Button) view.findViewById(R.id.saveMedicationBtn);
        saveMedicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mMedicaiton == null) {
                   createNewMedication();
                } else {
                    updateMedication();
                }
                //Return to MedicationCabinet
                ((MainActivity) getActivity()).changeFragment(new MedicationListFragment());
            }
        });
    }

    public void fillTextFields(){

        //Fills the TextFields with data from the given medicine object
        EditText medicationName = (EditText) getActivity().findViewById(R.id.medicationName);
        EditText medicationAmount = (EditText) getActivity().findViewById(R.id.medicationAmount);
        Spinner medicationUnit = (Spinner) getActivity().findViewById(R.id.medicationUnit);

        medicationName.setText(mMedicaiton.getName());
        medicationAmount.setText(Double.toString(mMedicaiton.getCount()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.unit_items, android.R.layout.simple_spinner_item);
        int spinnerPosition = adapter.getPosition(mMedicaiton.getUnit());
        medicationUnit.setSelection(spinnerPosition);
    }

    public void createNewMedication(){

        //MedicationStorage input-fields
        EditText medicationName = (EditText) getActivity().findViewById(R.id.medicationName);
        EditText medicationAmount = (EditText) getActivity().findViewById(R.id.medicationAmount);
        Spinner medicationUnit = (Spinner) getActivity().findViewById(R.id.medicationUnit);

        //Creates a new Medication object with the values of the input-fields
        Medication medication = new Medication(
                "786#13%",
                medicationName.getText().toString(),
                Double.parseDouble(medicationAmount.getText().toString()),
                medicationUnit.getSelectedItem().toString()
        );

        //Adds the new medicine to MedicationListContent
        MedicationListContent.ITEMS.add(0, medication);
        // TODO: Add new medicine to database
    }

    public void updateMedication(){

        EditText medicationName = (EditText) getActivity().findViewById(R.id.medicationName);
        EditText medicationAmount = (EditText) getActivity().findViewById(R.id.medicationAmount);
        Spinner medicationUnit = (Spinner) getActivity().findViewById(R.id.medicationUnit);

        //Updates an existing Medication object
        mMedicaiton.setName(medicationName.getText().toString());
        mMedicaiton.setCount(Double.parseDouble(medicationAmount.getText().toString()));
        mMedicaiton.setUnit(medicationUnit.getSelectedItem().toString());
        // TODO: Update existing medicine in database
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMedicationStorageFragmentInteraction(uri);
        }
    }

    //API >= 23
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

    //API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onMedicationStorageFragmentInteraction(Uri uri);
        public void addMedicationToMedicationList(Medication medication);
    }
}
