package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sondrehj.familymedicinereminderclient.HeaderItem;
import com.example.sondrehj.familymedicinereminderclient.ListItem;
import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.ReminderItem;
import com.example.sondrehj.familymedicinereminderclient.adapters.DashboardRecyclerViewAdapter;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.models.User2;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.Bind;

public class DashboardListFragment extends android.app.Fragment implements TitleSupplier, SwipeRefreshLayout.OnRefreshListener {

    private OnDashboardListFragmentInteractionListener mListener;
    private Boolean busIsRegistered = false;
    private List<Reminder> todaysReminders = new ArrayList<>();
    private LinkedHashMap<String,List<Reminder>> todaysRemindersSortedByUser = new LinkedHashMap<>();
    private List<ListItem> todaysRemindersForAdapter = new ArrayList<>();
    private SwipeRefreshLayout.OnRefreshListener refreshListener = this;
    @Bind(R.id.dashboard_list) RecyclerView recView;
    @Bind(R.id.reminder_refresh_layout) SwipeRefreshLayout swipeContainer;

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
        RecyclerView recView = (RecyclerView) view.findViewById(R.id.dashboard_list);

        if (recView != null) {
            Context context = view.getContext();
            recView.setLayoutManager(new LinearLayoutManager(context));
            recView.setAdapter(new DashboardRecyclerViewAdapter(context, todaysRemindersForAdapter, mListener, new MySQLiteHelper(getActivity()).getUsers()));
        }

        return view;
    }

    @Subscribe
    public void handleDashboardChangedEvent(DataChangedEvent event) {

        if (event.type.equals(DataChangedEvent.DASHBOARDCHANGED)) {
            todaysRemindersForAdapter.clear();
            todaysReminders.clear();
            todaysRemindersSortedByUser.clear();

            todaysReminders.addAll(new MySQLiteHelper(getActivity()).getTodaysReminders());
            todaysRemindersSortedByUser = setTodaysRemindersSortedByUser(todaysReminders);
            todaysRemindersForAdapter.addAll(createTodaysRemindersFromTreeMap(todaysRemindersSortedByUser));
            DashboardListFragment fragment = (DashboardListFragment) getFragmentManager().findFragmentByTag("DashboardListFragment");
            if (fragment != null) {
                fragment.notifyChanged();
            }
        }
    }

    public void notifyChanged() {
        RecyclerView recView = (RecyclerView) getActivity().findViewById(R.id.dashboard_list);
        if (recView != null) {
            recView.getAdapter().notifyDataSetChanged();
            System.out.println("notifychanged called");
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
            Collections.sort(userReminders, new CustomComparator());
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
    public String getTitle() {
        return "Today's reminders";
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

    public class CustomComparator implements Comparator<Reminder> {
        @Override
        public int compare(Reminder o1, Reminder o2) {
            int time1 = o1.getDate().get(Calendar.HOUR_OF_DAY) + o1.getDate().get(Calendar.MINUTE);
            int time2 = o2.getDate().get(Calendar.HOUR_OF_DAY) + o2.getDate().get(Calendar.MINUTE);
            return time1 - time2;
        }
    }

}
