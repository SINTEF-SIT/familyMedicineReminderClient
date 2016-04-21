package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuardianDashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuardianDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuardianDashboardFragment extends android.app.Fragment {

    private LinearLayout guardianDashboardLayout;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GuardianDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GuardianDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuardianDashboardFragment newInstance(String param1, String param2) {
        GuardianDashboardFragment fragment = new GuardianDashboardFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardian_dashboard, container, false);

        guardianDashboardLayout = (LinearLayout) view.findViewById(R.id.guardianDashboardLayout);
        guardianDashboardLayout.setDividerDrawable(getResources().getDrawable(R.drawable.horizontal_divider));
        guardianDashboardLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        LinearLayout pasientLayout = addPasientToView();
        addDefaultMedicineIcon(pasientLayout);
        addDefaultMedicineIcon(pasientLayout);
        addDefaultMedicineIcon(pasientLayout);
        addDefaultMedicineIcon(pasientLayout);

        return view;
    }

    public LinearLayout addPasientToView() {
        LinearLayout pasientLayout = new LinearLayout(getActivity());
        pasientLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams pasientLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pasientLayout.setLayoutParams(pasientLayoutParams);
        //converting dp to px, because LayoutParams only takes px
        int layoutPaddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        pasientLayout.setPadding(0, 0, 0, layoutPaddingBottom);
        guardianDashboardLayout.addView(pasientLayout);

        TextView pasientNameText = new TextView(getActivity());
        String pasientName = "Sondre Hjetland";
        pasientNameText.setText(pasientName);
        pasientNameText.setTextSize(22);
        pasientNameText.setTextColor(Color.parseColor("#000000"));
        LinearLayout.LayoutParams pasientNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pasientNameText.setLayoutParams(pasientNameParams);
        pasientNameParams.gravity = Gravity.CENTER;
        //converting dp to px, because LayoutParams only takes px
        int pasientNameMarginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        int pasientNameMarginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        pasientNameParams.setMargins(0, pasientNameMarginTop, 0, pasientNameMarginBottom);
        pasientLayout.addView(pasientNameText);

        /*
        LinearLayout medicineLayout = new LinearLayout(getActivity());
        medicineLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams medicineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        medicineLayout.setLayoutParams(medicineLayoutParams);
        medicineLayoutParams.gravity = Gravity.CENTER;
        medicineLayout.setGravity(Gravity.CENTER);
        //converting dp to px, because LayoutParams only takes px
        int layoutMarginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        int layoutMarginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        medicineLayoutParams.setMargins(layoutMarginLeft, 0, layoutMarginRight, 0);
        pasientLayout.addView(medicineLayout);
        */
        GridLayout gridLayout = new GridLayout(getActivity());
        LinearLayout.LayoutParams gridLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        gridLayout.setLayoutParams(gridLayoutParams);
        gridLayoutParams.gravity = Gravity.CENTER;
        gridLayout.setColumnCount(5);
        gridLayout.setRowCount(2);
        pasientLayout.addView(gridLayout);


        return pasientLayout;
    }

    public void addDefaultMedicineIcon(LinearLayout pasientLayout) {
        Button medicineButton = new Button(getActivity());
        medicineButton.setBackgroundResource(R.drawable.questionmark);
        String questionmark = "?";
        medicineButton.setText(questionmark);
        medicineButton.setTextColor(Color.parseColor("#FFFFFF"));
        medicineButton.setTextSize(25);
        medicineButton.setGravity(Gravity.CENTER);
        medicineButton.setPadding(0, 0, 0, 0);    //gets fucked if not padding is set for some reason
        //converting dp to px, because LayoutParams only takes px
        int buttonWidthHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        //LinearLayout.LayoutParams medicineParams = new LinearLayout.LayoutParams(buttonWidthHeight, buttonWidthHeight);
        GridLayout.LayoutParams medicineParams = new GridLayout.LayoutParams();
        medicineParams.width = buttonWidthHeight;
        medicineParams.height = buttonWidthHeight;
        medicineButton.setLayoutParams(medicineParams);
        int buttonMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        medicineParams.setMargins(buttonMargin, buttonMargin, buttonMargin, buttonMargin);

        GridLayout medicineLayout = (GridLayout) pasientLayout.getChildAt(1);
        medicineLayout.addView(medicineButton);
    }

    public void addCheckMarkIcon(LinearLayout pasientLayout) {
        Button medicineButton = new Button(getActivity());
        medicineButton.setBackgroundResource(R.drawable.green_checkmark);
        String checkmark = "✓";
        medicineButton.setText(checkmark);
        medicineButton.setTextColor(Color.parseColor("#FFFFFF"));
        medicineButton.setTextSize(25);
        medicineButton.setGravity(Gravity.CENTER);
        medicineButton.setPadding(0, 0, 0, 0);    //gets fucked if not padding is set for some reason
        //converting dp to px, because LayoutParams only takes px
        int buttonWidthHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams medicineParams = new LinearLayout.LayoutParams(buttonWidthHeight, buttonWidthHeight);
        medicineButton.setLayoutParams(medicineParams);
        int buttonMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        medicineParams.setMargins(buttonMargin, 0, buttonMargin, 0);

        LinearLayout medicineLayout = (LinearLayout) pasientLayout.getChildAt(1);
        medicineLayout.addView(medicineButton);
    }

    public void addRedXIcon(LinearLayout pasientLayout) {
        Button medicineButton = new Button(getActivity());
        medicineButton.setBackgroundResource(R.drawable.red_x);
        String x = "X";
        medicineButton.setText(x);
        medicineButton.setTextColor(Color.parseColor("#FFFFFF"));
        medicineButton.setTextSize(25);
        medicineButton.setGravity(Gravity.CENTER);
        medicineButton.setPadding(0, 0, 0, 0);    //gets fucked if not padding is set for some reason
        //converting dp to px, because LayoutParams only takes px
        int buttonWidthHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams medicineParams = new LinearLayout.LayoutParams(buttonWidthHeight, buttonWidthHeight);
        medicineButton.setLayoutParams(medicineParams);
        int buttonMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        medicineParams.setMargins(buttonMargin, 0, buttonMargin, 0);

        LinearLayout medicineLayout = (LinearLayout) pasientLayout.getChildAt(1);
        medicineLayout.addView(medicineButton);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
        void onFragmentInteraction(Uri uri);
    }
}
