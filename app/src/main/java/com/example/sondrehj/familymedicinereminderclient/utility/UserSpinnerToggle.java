package com.example.sondrehj.familymedicinereminderclient.utility;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.User;

import java.util.ArrayList;

/**
 * Created by sondre on 01/05/2016.
 */
public class UserSpinnerToggle {

    Activity activity;
    Spinner userSpinner;
    ArrayList<User> users;
    private String[] usersAlias;
    private User selectedUser;
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

    public void toggle() {

        if (users.size() != 0) {
            updateSpinnerContent();
            userSpinner.setEnabled(true);
            userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedUser = users.get(position);
                    ((MainActivity) activity).setCurrentUser(selectedUser);
                    Toast.makeText(activity, "TransportUser changed to " + selectedUser.getAlias(), Toast.LENGTH_SHORT).show();
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

    public void updateSpinnerContent() {

        MySQLiteHelper db = new MySQLiteHelper(activity);
        this.users = db.getUsers();
        usersAlias = new String[users.size()];
        for (int userIndex = 0; userIndex < users.size(); userIndex++) {
            usersAlias[userIndex] = users.get(userIndex).getAlias();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                R.layout.simple_spinner_item, usersAlias);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(adapter);
    }

    public void setUserIcon(ImageView userIcon) {
        this.userIcon = userIcon;
    }

    public void showUserActionBar(Boolean flag) {

        if (flag) {
            userSpinner.setVisibility(View.VISIBLE);
            userIcon.setVisibility(View.VISIBLE);
        } else {
            userSpinner.setVisibility(View.INVISIBLE);
            userIcon.setVisibility(View.INVISIBLE);
        }
    }

    public User getSelectedUser() {
        return this.selectedUser;
    }

}
