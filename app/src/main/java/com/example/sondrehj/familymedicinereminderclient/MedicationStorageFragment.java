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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MedicationStorageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MedicationStorageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MedicationStorageFragment newInstance(String param1, String param2) {
        MedicationStorageFragment fragment = new MedicationStorageFragment();
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
        return inflater.inflate(R.layout.fragment_medication_storage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        Button saveMedicationBtn = (Button) view.findViewById(R.id.saveMedicationBtn);
        saveMedicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                // TODO: Add medicine to database

                //Return to MedicationCabinet
                ((MainActivity) getActivity()).changeFragment(new MedicationCabinetFragment());
            }
        });
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
