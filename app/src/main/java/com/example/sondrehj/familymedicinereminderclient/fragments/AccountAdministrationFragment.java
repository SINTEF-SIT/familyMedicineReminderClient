package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sondrehj.familymedicinereminderclient.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AccountAdministrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountAdministrationFragment extends android.app.Fragment {

    public static int SNOOZE_TIME;


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_administration, container, false);
    }
}
