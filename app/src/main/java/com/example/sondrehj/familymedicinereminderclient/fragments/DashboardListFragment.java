package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.adapters.DashboardRecyclerViewAdapter;
import com.example.sondrehj.familymedicinereminderclient.adapters.ReminderListRecyclerViewAdapter;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;

import java.util.ArrayList;
import java.util.List;

public class DashboardListFragment extends android.app.Fragment implements TitleSupplier, SwipeRefreshLayout.OnRefreshListener {

    private OnDashboardListFragmentInteractionListener mListener;
    private Boolean busIsRegistered = false;
    private List<Reminder> reminders = new ArrayList<>();
    private SwipeRefreshLayout.OnRefreshListener refreshListener = this;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminders.addAll(new MySQLiteHelper(getActivity()).getReminders());
        System.out.println(new MySQLiteHelper(getActivity()).getReminders());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        RecyclerView recView = (RecyclerView) view.findViewById(R.id.dashboard_list);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.reminder_refresh_layout);

        // Set listener for swipe refresh
        //swipeContainer.setOnRefreshListener(this);


        // Set the adapter
        if (recView != null) {
            Context context = view.getContext();
            recView.setLayoutManager(new LinearLayoutManager(context));
            recView.setAdapter(new DashboardRecyclerViewAdapter(context, reminders, mListener));
        }

        return view;
    }

    //API Level >= 23
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!busIsRegistered) {
            BusService.getBus().register(this);
            busIsRegistered = true;
        }
        if (context instanceof OnDashboardListFragmentInteractionListener) {
            mListener = (OnDashboardListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDashboardListFragmentInteractionListener");
        }
    }

    //API Level < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!busIsRegistered) {
            BusService.getBus().register(this);
            busIsRegistered = true;
        }
        if (activity instanceof OnDashboardListFragmentInteractionListener) {
            mListener = (OnDashboardListFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString() + " must implement OnDashboardListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (busIsRegistered) {
            BusService.getBus().unregister(this);
            busIsRegistered = false;
        }
        mListener = null;
    }

    @Override
    public String getTitle() {
        return "Dashboard";
    }


    @Override
    public void onRefresh() {
        Bundle extras = new Bundle();
        extras.putString("notificationType", "remindersChanged");
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(
                MainActivity.getAccount(getActivity()),
                "com.example.sondrehj.familymedicinereminderclient.content",
                extras);

        swipeContainer.setRefreshing(false);
    }


    public interface OnDashboardListFragmentInteractionListener {

    }
}
