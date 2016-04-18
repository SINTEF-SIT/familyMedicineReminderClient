package com.example.sondrehj.familymedicinereminderclient;

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

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LinkingFragment.OnLinkingFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LinkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinkingFragment extends android.app.Fragment{

    private OnLinkingFragmentInteractionListener mListener;

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
        ButterKnife.bind(this, view);
        getActivity().setTitle("Linking");
        //TODO: retrieve user type (guardian or patient). Use this to build the interface.
        if (false) {
            linkButton.setVisibility(View.GONE);
            statusIcon.setVisibility(View.GONE);
            idInputHelper.setVisibility(View.GONE);
            idInput.setVisibility(View.GONE);
            //// TODO: retrieve userID from this user.
            userID.setText("replace with id");
        } else {
            infoText.setVisibility(View.GONE);
            idHelper.setVisibility(View.GONE);
            userID.setVisibility(View.GONE);
        }
        return view;
    }

    @OnClick(R.id.link_guardian_button)
    public void tryToLink() {
        // input validation
        if (idInput.getText().length() == 5) {
            // give textual feedback about linking
            statusText.setText("Linking with: " + idInput.getText().toString() + "...");
            // set the color of the status icon to yellow to give feedback
            int color = Color.parseColor("#FFFDD835");
            statusIcon.setColorFilter(color);

            linkWithAccount(idInput.getText().toString());
        } else {
            statusText.setText("Enter a 5-digit ID. Try again...");
            clearStatusTextAfterSeconds(3);
        }
    }

    public void linkWithAccount(String idToLinkWith){
        Log.d("Linking", "initiating linking with account ID " + idToLinkWith + ".");
        MyCyFAPPServiceAPI api = RestService.createRestService();

        //TODO: fetch this user's ID to send with the request.

        Call<User> call = api.sendLinkingRequest("12345", idToLinkWith);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    Log.d("linking/api", "successful linking.");
                    //TODO: persist linked status to the device.
                    int color = Color.parseColor("#FF64DD17");
                    statusIcon.setColorFilter(color);
                } else {
                    Log.d("linking/api", "unsuccessful linking.");
                    int color = Color.parseColor("#FFBF360C");
                    statusIcon.setColorFilter(color);
                    statusText.setText("Linking error, try again later.");
                    clearStatusTextAfterSeconds(3);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("linking/api", "onFailure.");
                statusText.setText("Cannot initiate network call.");
                int color = Color.parseColor("#FFBF360C");
                statusIcon.setColorFilter(color);
                clearStatusTextAfterSeconds(8);
            }
        });
    }

    public void clearStatusTextAfterSeconds(int i) {
        new CountDownTimer(i*1000,1000){

            @Override
            public void onTick(long miliseconds){}

            @Override
            public void onFinish(){
                statusText.setText("");
            }
        }.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLinkingFragmentInteractionListener) {
            mListener = (OnLinkingFragmentInteractionListener) context;
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

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
    public interface OnLinkingFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
