package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.dialogs.DeleteAllDataDialogFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

import com.example.sondrehj.familymedicinereminderclient.models.User;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;



/**
 * TODO: Send user preferences to the server.
 *
 */
public class AccountAdministrationFragment extends android.app.Fragment implements TitleSupplier {

    //private boolean busIsRegistered;
    private static String TAG = "AccountAdministrationFragment";
    private Context context;

    @Bind(R.id.account_group_personal) LinearLayout personalGroup;
    @Bind(R.id.account_group_personal_year) LinearLayout personalYearGroup;
    @Bind(R.id.account_group_general) LinearLayout generalGroup;
    @Bind(R.id.account_group_guardian) LinearLayout guardianGroup;
    @Bind(R.id.account_group_sync) LinearLayout syncGroup;

    @Bind(R.id.account_personal_year_input) EditText yearInput;
    @Bind(R.id.account_personal_reminder_switch) Switch reminderSwitch;

    @Bind(R.id.account_general_notification_switch) Switch notificationSwitch;
    @Bind(R.id.account_general_snooze_minutes_value) TextView snoozeMinuteValue;
    @Bind(R.id.account_general_snooze_minutes_text) TextView snoozeMinuteText;
    @Bind(R.id.account_general_snooze_seekbar) SeekBar snoozeSeekBar;

    @Bind(R.id.account_guardian_grace_minutes_value) TextView graceMinuteValue;
    @Bind(R.id.account_guardian_grace_minutes_text) TextView graceMinuteText;
    @Bind(R.id.account_guardian_grace_seekbar) SeekBar graceSeekBar;

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
        View view = inflater.inflate(R.layout.fragment_account_administration, container, false);
        ButterKnife.bind(this, view);


        Account account = MainActivity.getAccount(context); //Gets a reference to the account.
        String userRole = AccountManager.get(context)
                .getUserData(account, "userRole"); // Gets the userRole of the specified account.

        Log.d(TAG, "The users (" + account.name + ") role equals: " + userRole +
                ". Hiding elements belonging to the opposite role.");

        snoozeSeekBar.setMax(29); //set maximum value of seek bar.
        graceSeekBar.setMax(59);

        snoozeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress+1 == 1) {
                    snoozeMinuteText.setText("min");
                } else {
                    snoozeMinuteText.setText("mins");
                }
                snoozeMinuteValue.setText((progress+1) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        graceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress+1 == 1) {
                    graceMinuteText.setText("min");
                } else {
                    graceMinuteText.setText("mins");
                }
                graceMinuteValue.setText((progress+1) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        personalYearGroup.setVisibility(View.GONE); //hides year input.
        syncGroup.setVisibility(View.GONE); //hides syncing options.

        if (userRole.equals("patient")) {
            guardianGroup.setVisibility(View.GONE); // hides guardian options if user is a patient.
        }
        fillTextFields(); //fill the text fields with data saved in sharedprefs.
        return view;
    }

    @OnClick(R.id.account_settings_save_button)
    public void onSaveButtonClick() {
        updateAccountInformation();
        Toast.makeText(getActivity(), "Settings updated.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.account_delete_data_button)
    public void onDeleteButtonClick() {
        FragmentManager fm = getActivity().getFragmentManager();
        DeleteAllDataDialogFragment daf = new DeleteAllDataDialogFragment();
        daf.show(fm, "delete");
    }

    public boolean updateAccountInformation() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("AccountSettings",
                Context.MODE_PRIVATE).edit();
        editor.putBoolean("reminderSwitch", reminderSwitch.isChecked()) ;
        editor.putBoolean("notificationSwitch", notificationSwitch.isChecked());
        editor.putInt("snoozeDelay", Integer.parseInt(snoozeMinuteValue.getText().toString()));
        Log.d("t", "" + Integer.parseInt(snoozeMinuteValue.getText().toString()));
        if (graceMinuteValue.getVisibility() == View.GONE){
            editor.putInt("gracePeriod", Integer.parseInt(graceMinuteValue.getText().toString()));
            MyCyFAPPServiceAPI api = RestService.createRestService();
            Call<User> call = api.setGracePeriod(MainActivity.getAccount(context).name,
                    graceMinuteValue.getText().toString());
        }
        editor.apply();
        return true;
    }

    public void fillTextFields() {
        SharedPreferences prefs = getActivity().getSharedPreferences("AccountSettings",
                Context.MODE_PRIVATE);
        reminderSwitch.setChecked(prefs.getBoolean("reminderSwitch", true));
        notificationSwitch.setChecked(prefs.getBoolean("notificationSwitch", true));
        snoozeMinuteValue.setText(prefs.getInt("snoozeDelay", 5) + "");
        snoozeSeekBar.setProgress(prefs.getInt("snoozeDelay", 4)-1);
        graceMinuteValue.setText(prefs.getInt("gracePeriod", 5) + "");
        graceSeekBar.setProgress(prefs.getInt("gracePeriod", 4)-1);
    }

    /**
     * Register this fragment to the event bus.
     * Saves a reference to the application context.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (!busIsRegistered) {
            BusService.getBus().register(this);
            busIsRegistered = true;
        }*/
        this.context = context;
    }

    /**
     * Provides compatibility for api levels below 23
     *
     * @param context
     */
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        /*if (!busIsRegistered) {
            BusService.getBus().register(this);
            busIsRegistered = true;
        }*/
        this.context = context;
    }

    /**
     * Unregisters the bus when the fragment is detached.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        /*if (busIsRegistered) {
            BusService.getBus().unregister(this);
            busIsRegistered = false;
        }*/
    }

    /**
     * Unbinds references to objects in the view.
     */
    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public String getTitle() {
        return "Linking";
    }
}
