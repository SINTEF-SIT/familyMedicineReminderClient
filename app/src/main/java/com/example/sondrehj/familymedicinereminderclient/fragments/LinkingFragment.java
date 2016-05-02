package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.LinkingResponseEvent;
import com.example.sondrehj.familymedicinereminderclient.models.Message;
import com.example.sondrehj.familymedicinereminderclient.models.User;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link LinkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinkingFragment extends android.app.Fragment implements TitleSupplier {

    private boolean busIsRegistered;
    private static String TAG = "LinkingFragment";
    private Context context;

    @Bind(R.id.link_guardian_button) Button linkButton;
    @Bind(R.id.link_guardian_status_icon) ImageView statusIcon;
    @Bind(R.id.link_guardian_status_text) TextView statusText;
    @Bind(R.id.link_guardian_id_helper) TextView idInputHelper;
    @Bind(R.id.link_guardian_id_input) EditText idInput;
    @Bind(R.id.link_patient_infotext) TextView infoText;
    @Bind(R.id.link_patient_id_helper) TextView idHelper;
    @Bind(R.id.link_patient_id) TextView userID;

    public LinkingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LinkingFragment.
     */
    public static LinkingFragment newInstance() {
        LinkingFragment fragment = new LinkingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linking, container, false);

        ButterKnife.bind(this, view); //Binds references to the items in the inflated view.

        Account account = MainActivity.getAccount(context); //Gets a reference to the account
        Log.d(TAG, "Accountname: " + account.name);

        String userRole = AccountManager.get(context).getUserData(account, "userRole"); // Gets the userRole of the specified account.
        Log.d(TAG, "The users role equals: " + userRole +". Hiding elements belonging to the opposite role.");
        if (userRole.equals("patient")) {
            linkButton.setVisibility(View.GONE);
            statusIcon.setVisibility(View.GONE);
            idInputHelper.setVisibility(View.GONE);
            idInput.setVisibility(View.GONE);
            userID.setText(account.name);
        } else {
            infoText.setVisibility(View.GONE);
            idHelper.setVisibility(View.GONE);
            userID.setVisibility(View.GONE);
        }
        return view;
    }

    @OnClick(R.id.link_guardian_button)
    public void tryToLink() {
        if (idInput.getText().length() == 5) { // input validation
            //Give textual feedback about linking
            statusText.setText("Linking with: " + idInput.getText().toString() + "...");
            //Set the color of the status icon to yellow to signalise progress.
            int color = Color.parseColor("#FFEB3B");
            statusIcon.setColorFilter(color);
            linkWithAccount(idInput.getText().toString()); //Start linking progress.
        } else {
            statusText.setText("Enter a 5-digit ID. Try again...");
            clearStatusTextAfterSeconds(3);
        }
    }

    public void linkWithAccount(String idToLinkWith){
        MyCyFAPPServiceAPI api = RestService.createRestService(); //Creates a rest service and save the reference.
        Account account = MainActivity.getAccount(context); //Fetches the user account

        Log.d(TAG, "sendlinkingrequest userID: " + account.name);



        Call<Message> call = api.sendLinkingRequest(account.name, idToLinkWith);
        call.enqueue(new Callback<Message>() {
            /**
             * Callback function when there is a valid response from the server.
             * Validiation is done here, not in onFailure.
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) { //on received message object OK.
                    Message m = response.body();
                    String message = m.getMessage();
                    if (message.equals("Successfully sent linking request to patient.")) {
                        //continue
                    } else {
                        statusText.setText("Could not send linking request to patient.");
                        int color = Color.parseColor("#FF5252");
                        statusIcon.setColorFilter(color);
                    }
                    //TODO: persist linked status to the device.??
                    clearStatusTextAfterSeconds(5);
                } else { //on received message object FAIL.
                    Log.d(TAG, "unsuccessful linking.");
                    int color = Color.parseColor("#FF5252");
                    statusIcon.setColorFilter(color);
                    statusText.setText("Server error, please contact developer.");
                    clearStatusTextAfterSeconds(5);
                }
            }

            /**
             * Callback function when the connection times out or there is not network.
             *
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.d("linking/api", "sendlinkingrequest -> onFailure.");
                statusText.setText("Cannot initiate network call.");
                int color = Color.parseColor("#FF5252");
                statusIcon.setColorFilter(color);
                clearStatusTextAfterSeconds(5);
            }
        });
    }

    @Subscribe
    public void handleLinkingResultByNotification(LinkingResponseEvent event){
        if (event.getMessage().equals("positiveResponse")) {
            statusText.setText("The patient have successfully been linked to this guardian account!");
            int color = Color.parseColor("#388E3C");
            statusIcon.setColorFilter(color);


        } else {
            statusText.setText("The patient has denied the linking request.");
            int color = Color.parseColor("#FFBF360C");
            statusIcon.setColorFilter(color);
        }
        clearStatusTextAfterSeconds(5);
    }

    public void clearStatusTextAfterSeconds(int i) {
        new CountDownTimer(i*1000,1000){

            @Override
            public void onTick(long miliseconds){}

            @Override
            public void onFinish(){
                if (statusText != null)
                    statusText.setText("");
            }
        }.start();
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
        if (!busIsRegistered) {
            BusService.getBus().register(this);
            busIsRegistered = true;
        }
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
        if (!busIsRegistered) {
            BusService.getBus().register(this);
            busIsRegistered = true;
        }
        this.context = context;
    }

    /**
     * Unregisters the bus when the fragment is detached.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (busIsRegistered) {
            BusService.getBus().unregister(this);
            busIsRegistered = false;
        }
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
        return "Settings";
    }
}