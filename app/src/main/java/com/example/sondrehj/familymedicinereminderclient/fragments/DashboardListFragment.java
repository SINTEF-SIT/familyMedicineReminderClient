package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.accounts.Account;
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

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.adapters.DashboardRecyclerViewAdapter;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class DashboardListFragment extends android.app.Fragment implements TitleSupplier, SwipeRefreshLayout.OnRefreshListener {

    private OnDashboardListFragmentInteractionListener mListener;
    private Boolean busIsRegistered = false;
    private List<Reminder> todaysReminders = new ArrayList<>();
    private SwipeRefreshLayout.OnRefreshListener refreshListener = this;
    @Bind(R.id.dashboard_list) RecyclerView recView;
    @Bind(R.id.reminder_refresh_layout) SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        todaysReminders.addAll(new MySQLiteHelper(getActivity()).getTodaysReminders());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);

        // Set listener for swipe refresh
        //swipeContainer.setOnRefreshListener(this);

        // Set the adapter
        if (recView != null) {
            Context context = view.getContext();
            recView.setLayoutManager(new LinearLayoutManager(context));


            //http://stackoverflow.com/questions/34848401/divide-elements-on-groups-in-recyclerview
            //http://stackoverflow.com/questions/34848401/divide-elements-on-groups-in-recyclerview
            //http://stackoverflow.com/questions/34848401/divide-elements-on-groups-in-recyclerview
            //http://stackoverflow.com/questions/34848401/divide-elements-on-groups-in-recyclerview
            //http://stackoverflow.com/questions/34848401/divide-elements-on-groups-in-recyclerview
            //http://stackoverflow.com/questions/34848401/divide-elements-on-groups-in-recyclerview
            //TODO: hent ut alle reminders med getTodaysReminders() og sorter etter ownerID.
            //TODO: Problem at itemsInGroup er i intervaller atm

            //splits the recyclerview into sections
            RecyclerView.ItemDecoration recViewItemDecoration = new RecyclerView.ItemDecoration() {

                private int textSize = 50;
                private int groupSpacing = 100;
                private int itemsInGroup = 1;

                private Paint paint = new Paint();
                {
                    paint.setTextSize(textSize);
                }

                @Override
                public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

                    Account account = MainActivity.getAccount(context);
                    String accountName = account.name;

                    //c.drawText("Kid", 0, 0 ,paint);

                    for (int i = 0; i < parent.getChildCount(); i++) {
                        //System.out.println(parent.getChildCount());
                        System.out.println(i);
                        View view = parent.getChildAt(i);
                        int position = parent.getChildAdapterPosition(view);
                        if (position % itemsInGroup == 0) {
                            c.drawText("Kid " + (position / itemsInGroup + 1), view.getLeft(),
                                    view.getTop() - groupSpacing / 2 + textSize / 3, paint);
                        }
                    }
                }

                //this method creates the space between the groups
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    if (parent.getChildAdapterPosition(view) % itemsInGroup == 0) {
                        outRect.set(0, groupSpacing, 0, 0);
                    }
                }
            };

            recView.addItemDecoration(recViewItemDecoration);
            recView.setAdapter(new DashboardRecyclerViewAdapter(context, todaysReminders, mListener));
        }

        return view;
    }

    @Subscribe
    public void handleDashboardChangedEvent(DataChangedEvent event) {

        if (event.type.equals(DataChangedEvent.DASHBOARDCHANGED)) {
            todaysReminders.clear();
            //medications.addAll(new MySQLiteHelper(getActivity()).getMedications());
            todaysReminders.addAll((new MySQLiteHelper(getActivity()).getTodaysReminders()));
            System.out.println();
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
}
