package com.example.sondrehj.familymedicinereminderclient.utility;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.User2;

import java.util.ArrayList;

/**
 * This class contains the functionality of the {@link Spinner} in the action bar. The class got
 * methods for updating the spinner content, setting the listener and hiding/showing the spinner.
 */
public class UserSpinnerToggle {

    Activity activity;
    Spinner userSpinner;
    ArrayList<User2> users;
    private String[] usersAlias;
    private User2 selectedUser;
    private Toolbar toolbar;
    private boolean initialized;
    private LinearLayout drawerHeader;
    private ImageView userIcon;
    public static String[] barColors = new String[]{"#30cbff", "#009688", "#4CAF50", "#FF9800", "#9C27B0"};

    public UserSpinnerToggle(Activity activity, Spinner userSpinner) {
        MySQLiteHelper db = new MySQLiteHelper(activity);
        this.users = db.getUsers();
        this.activity = activity;
        this.userSpinner = userSpinner;
        this.toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        NavigationView nav_view = (NavigationView) activity.findViewById(R.id.nav_view);
        this.drawerHeader = (LinearLayout) nav_view.getHeaderView(0).findViewById(R.id.drawer_header);
    }

    /**
     * Toggles the user spinner. This include setting the onchangelistener, updating the content of
     * the spinner and changing the views to show the correct data based on the user selected.
     */
    public void toggle() {

        if (users.size() != 0) {
            updateSpinnerContent();
            userSpinner.setEnabled(true);
            userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedUser = users.get(position);
                    ((MainActivity) activity).setCurrentUser(selectedUser);
                    toolbar.setBackgroundColor(Color.parseColor(barColors[position % 4]));
                    drawerHeader.setBackgroundColor(Color.parseColor(barColors[position % 4]));
                    BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONS));
                    BusService.getBus().post(new DataChangedEvent(DataChangedEvent.REMINDERS));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }
            });
        }
        if (users.size() == 1 || users.size() == 0) {
            userSpinner.setEnabled(false);
        }
        if (users.size() > 1) {
            userSpinner.setEnabled(true);
        }
    }

    /**
     * Updates the content of the user spinner. The method retrieves all the saved {@link User2}
     * from the database and append their aliases to the adapter.
     */
    public void updateSpinnerContent() {

        MySQLiteHelper db = new MySQLiteHelper(activity);
        this.users = db.getUsers();
        usersAlias = new String[users.size()];
        for (int userIndex = 0; userIndex < users.size(); userIndex++) {
            usersAlias[userIndex] = users.get(userIndex).getAlias();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                R.layout.custom_spinner_item, usersAlias);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        userSpinner.setAdapter(adapter);
    }

    /**
     * Hides or shows the user action bar. The bar should be hidden for patients and shown
     * for the guardians.
     *
     * @param flag A boolean representing the visibility.
     */
    public void showUserActionBar(Boolean flag) {

        if (flag) {
            userSpinner.setVisibility(View.VISIBLE);
            userIcon.setVisibility(View.VISIBLE);
        } else {
            userSpinner.setVisibility(View.INVISIBLE);
            userIcon.setVisibility(View.INVISIBLE);
        }
    }

    public void setUserIcon(ImageView userIcon) {
        this.userIcon = userIcon;
    }


    public User2 getSelectedUser() {
        return this.selectedUser;
    }
}
