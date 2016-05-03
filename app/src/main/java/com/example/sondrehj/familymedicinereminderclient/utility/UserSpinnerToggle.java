package com.example.sondrehj.familymedicinereminderclient.utility;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.User2;

import java.util.ArrayList;

/**
 * Created by sondre on 01/05/2016.
 */
public class UserSpinnerToggle {

    Activity activity;
    Spinner userSpinner;
    ArrayList<User2> users;
    private String[] usersAlias;
    private User2 selectedUser;
    private TextView userActionBarTextView;
    private boolean firstUse;

    public UserSpinnerToggle(Activity activity, Spinner userSpinner) {
        MySQLiteHelper db = new MySQLiteHelper(activity);
        this.users = db.getUsers();
        this.activity = activity;
        this.userSpinner = userSpinner;
    }

    public void toggle() {

        if(users.size() != 0) {
            updateSpinnerContent();
            setListener();
        }
    }

    private void setListener() {
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedUser = users.get(position);
                ((MainActivity) activity).setCurrentUser(selectedUser);
                Toast.makeText(activity, "User changed to " + selectedUser.getAlias(), Toast.LENGTH_SHORT).show();
                userActionBarTextView.setText(selectedUser.getAlias());
                BusService.getBus().post(new DataChangedEvent(DataChangedEvent.MEDICATIONS_BY_OWNERID));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void updateUserActionBarTextView(){
        userActionBarTextView.setText(((MainActivity) activity).getCurrentUser().getAlias());
    }

    public void updateSpinnerContent() {

        MySQLiteHelper db = new MySQLiteHelper(activity);
        this.users = db.getUsers();

        usersAlias = new String[users.size()];
        for (int userIndex = 0; userIndex < users.size(); userIndex++) {
            usersAlias[userIndex] = users.get(userIndex).getAlias();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, usersAlias);
        userSpinner.setAdapter(adapter);

    }

    public User2 getSelectedUser() {
        return this.selectedUser;
    }

    public void setUserActioBarTextView(TextView textView) {
        this.userActionBarTextView = textView;
    }

}
