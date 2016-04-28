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
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link GuardianDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuardianDashboardFragment extends android.app.Fragment implements TitleSupplier {

    //TODO: Implement the logic for this fragment!

    private LinearLayout guardianDashboardLayout;

    //TODO: Refactor this

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

    public static GuardianDashboardFragment newInstance(String param1, String param2) {
        GuardianDashboardFragment fragment = new GuardianDashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    //TODO: Comments is gone? RIP
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

    @Override
    public String getTitle() {
        return "Guardian Dashboard";
    }
}
