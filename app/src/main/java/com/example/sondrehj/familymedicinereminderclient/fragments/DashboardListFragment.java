package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.adapters.HeaderItem;
import com.example.sondrehj.familymedicinereminderclient.adapters.ListItem;
import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.adapters.ReminderItem;
import com.example.sondrehj.familymedicinereminderclient.adapters.DashboardRecyclerViewAdapter;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardListFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "DashboardListFragment";
    private OnDashboardListFragmentInteractionListener mListener;
    private Boolean busIsRegistered = false;
    private List<Reminder> todaysReminders = new ArrayList<>();
    private LinkedHashMap<String,List<Reminder>> todaysRemindersSortedByUser = new LinkedHashMap<>();
    private List<ListItem> todaysRemindersForAdapter = new ArrayList<>();
    private SwipeRefreshLayout.OnRefreshListener refreshListener = this;
    @Bind(R.id.today_empty) TextView emptyView;


    public DashboardListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WelcomeFragment.
     */
    public static DashboardListFragment newInstance() {
        DashboardListFragment fragment = new DashboardListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        todaysReminders.addAll(new MySQLiteHelper(getActivity()).getTodaysReminders());
        todaysRemindersSortedByUser = setTodaysRemindersSortedByUser(todaysReminders);
        todaysRemindersForAdapter.addAll(createTodaysRemindersFromTreeMap(todaysRemindersSortedByUser));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        ButterKnife.bind(this, view);
        RecyclerView recView = (RecyclerView) view.findViewById(R.id.dashboard_list);
        getActivity().setTitle("Dashboard");

        if (recView != null) {
            Context context = view.getContext();
            recView.setLayoutManager(new LinearLayoutManager(context));
            recView.setAdapter(new DashboardRecyclerViewAdapter(context, todaysRemindersForAdapter, mListener, new MySQLiteHelper(getActivity()).getUsers()));
        }

        if(todaysReminders.size() == 0) {
            Log.d(TAG, "size was 0");
            recView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        return view;
    }

    @Subscribe
    public void handleDashboardChangedEvent(DataChangedEvent event) {

        if (event.type.equals(DataChangedEvent.DASHBOARDCHANGED)) {
            Log.d("DashboardListFragment", "in Dashboardchanged");
            todaysRemindersForAdapter.clear();
            todaysReminders.clear();
            todaysRemindersSortedByUser.clear();

            todaysReminders.addAll(new MySQLiteHelper(getActivity()).getTodaysReminders());
            todaysRemindersSortedByUser = setTodaysRemindersSortedByUser(todaysReminders);
            todaysRemindersForAdapter.addAll(createTodaysRemindersFromTreeMap(todaysRemindersSortedByUser));
            DashboardListFragment fragment = (DashboardListFragment) getFragmentManager().findFragmentByTag("DashboardListFragment");
            if (fragment != null) {
                getActivity().runOnUiThread(() -> {
                    fragment.notifyChanged();
                    //swipeContainer.setRefreshing(false);
                });
            }
        }
    }

    public void notifyChanged() {
        RecyclerView recView = (RecyclerView) getActivity().findViewById(R.id.dashboard_list);
        if (recView != null) {
            recView.getAdapter().notifyDataSetChanged();
            Log.d(TAG,"Notify changed called");
        }

        if(todaysReminders.size() == 0) {
            Log.d(TAG, "size was 0");
            recView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private List<ListItem> createTodaysRemindersFromTreeMap(LinkedHashMap<String, List<Reminder>> todaysRemindersSortedByUser){
        List<ListItem> mItems;

        mItems = new ArrayList<>();
        for (String userId : todaysRemindersSortedByUser.keySet()) {
            HeaderItem header = new HeaderItem();
            header.setOwnerID(userId);
            mItems.add(header);
            for (Reminder reminder : todaysRemindersSortedByUser.get(userId)) {
                ReminderItem item = new ReminderItem();
                item.setReminder(reminder);
                mItems.add(item);
            }
        }
        return mItems;
    }

    private LinkedHashMap<String, List<Reminder>> setTodaysRemindersSortedByUser(List<Reminder> todaysReminders) {
        LinkedHashMap<String, List<Reminder>> mItems = new LinkedHashMap<>();
        String userId = MainActivity.getAccount(getActivity()).name;

        ArrayList<String> users = new ArrayList<>();
        for (Reminder reminder:todaysReminders) {
            if(!users.contains(reminder.getOwnerId())){
                if(reminder.getOwnerId().equals(userId)){
                    users.add(0, reminder.getOwnerId());
                } else {
                    users.add(reminder.getOwnerId());
                }
            }
        }

        for (String user : users){
            ArrayList<Reminder> userReminders = new ArrayList<>();
            for (Reminder reminder : todaysReminders){
                if(reminder.getOwnerId().equals(user)){
                    userReminders.add(reminder);
                }
            }
            mItems.put(user, userReminders);
        }
        return mItems;
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
    public void onRefresh() {
        Bundle extras = new Bundle();
        extras.putString("notificationType", "remindersChanged");
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(
                MainActivity.getAccount(getActivity()),
                "com.example.sondrehj.familymedicinereminderclient.content",
                extras);
    }

    public interface OnDashboardListFragmentInteractionListener {

    }
}
