package com.example.sondrehj.familymedicinereminderclient;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.models.User;
import com.example.sondrehj.familymedicinereminderclient.sync.ServiceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeFragment.OnWelcomeListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends android.app.Fragment {

    private OnWelcomeListener mListener;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WelcomeFragment.
     */
    public static WelcomeFragment newInstance(Account newAccount) {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_welcome, container, false);
        Button continueButton = (Button) view.findViewById(R.id.continue_button);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContinueButtonPressed(getActivity());
            }
        });

        return view;
    }

    public void onContinueButtonPressed(Context context) {
        final Toast failureToast = Toast.makeText(context,
                "An internet connection is required to create an account. Please try again once " +
                        "you have connected to the internet.",
                Toast.LENGTH_SHORT);

        final ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Creating user");
        progress.setMessage("Please wait while a user is created...");

        if (!ServiceManager.isNetworkAvailable(context)) {
            failureToast.show();
            return;
        }

        //TODO: Replace this section with the section commented below

        //mListener.OnNewAccountCreated("dummy_user_id", "dummy_password");

        //TODO: Uncomment this section. It is commented for development reasons only
        MyCyFAPPServiceAPI service = RestService.createRestService();
        User user = new User();
        Call<User> call = service.createUser(user);
        progress.show();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                String userId = response.body().getUserID();
                String password = response.body().getPassword();
                System.out.println("UserID:" + userId);
                System.out.println("Password:" + password);
                progress.dismiss();

                //TODO: Update with password != null as well
                if (mListener != null) {
                    if (userId != null) {
                        mListener.OnNewAccountCreated(userId, password);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progress.dismiss();
                failureToast.show();
                System.out.println("Could not create user" + t.getMessage());
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWelcomeListener) {
            mListener = (OnWelcomeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    //For devices with low API-level
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnWelcomeListener) {
            mListener = (OnWelcomeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
    public interface OnWelcomeListener {
        void OnNewAccountCreated(String userId, String password);
    }
}
