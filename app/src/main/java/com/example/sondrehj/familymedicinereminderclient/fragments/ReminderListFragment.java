package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.adapters.ReminderListRecyclerViewAdapter;
import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.database.ReminderListContent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnReminderListFragmentInteractionListener}
 * interface.
 */
public class ReminderListFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //TODO: Animation is not triggering when you go to reminder view

    //TODO: On Android 4.1, frequency days are showing even when the reminder is one-time

    private OnReminderListFragmentInteractionListener mListener;
    private Boolean busIsRegistered = false;
    private List<Reminder> reminders = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;

    @Bind(R.id.reminder_empty)
    TextView emptyView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReminderListFragment() { }


    @SuppressWarnings("unused")
    public static ReminderListFragment newInstance() {
        ReminderListFragment fragment = new ReminderListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminders = new MySQLiteHelper(getActivity()).getRemindersByOwnerId(((MainActivity)getActivity()).getCurrentUser().getUserId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);
        RecyclerView recView = (RecyclerView) view.findViewById(R.id.reminder_list);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.reminder_refresh_layout);
        getActivity().setTitle("Reminders");
        ButterKnife.bind(this, view);

        // Set listener for swipe refresh
        swipeContainer.setOnRefreshListener(this);

        // Set the adapter
        if (recView != null) {
            Context context = view.getContext();
            recView.setLayoutManager(new LinearLayoutManager(context));
            recView.setAdapter(new ReminderListRecyclerViewAdapter(context, reminders, mListener));

            if(reminders.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                recView.setVisibility(View.GONE);
            }
            else{
                emptyView.setVisibility(View.GONE);
                recView.setVisibility(View.VISIBLE);
            }
        }

        view.findViewById(R.id.reminder_fab).setOnClickListener( (View v) ->
            ((MainActivity) getActivity()).changeFragment(new NewReminderFragment())
        );
        return view;
    }

    @Subscribe
    public void handleRemindersChangedEvent(DataChangedEvent event) {
        if(event.type.equals(DataChangedEvent.REMINDERS)) {
            System.out.println("In handle data changed event");
            List<Reminder> remindersToAdd = new MySQLiteHelper(getActivity()).getRemindersByOwnerId(((MainActivity) getActivity()).getCurrentUser().getUserId());
            ReminderListFragment fragment = (ReminderListFragment) getFragmentManager().findFragmentByTag("ReminderListFragment");
            BusService.getBus().post(new DataChangedEvent(DataChangedEvent.DASHBOARDCHANGED));
            if (fragment != null) {
                getActivity().runOnUiThread(() -> {
                    reminders.clear();
                    reminders.addAll(remindersToAdd);
                    fragment.notifyChanged();
                    swipeContainer.setRefreshing(false);
                });
            }
        }
    }

    public void notifyChanged() {
        RecyclerView recView = (RecyclerView) getActivity().findViewById(R.id.reminder_list);
        if (recView != null) {
            if(reminders.size() == 0) {
                Log.d("MedicationListFragment", "size was 0");
                recView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                recView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
            System.out.println(reminders);
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
        if (context instanceof OnReminderListFragmentInteractionListener) {
            mListener = (OnReminderListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnReminderListFragmentInteractionListener");
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
        if (activity instanceof OnReminderListFragmentInteractionListener) {
            mListener = (OnReminderListFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString() + " must implement OnReminderListFragmentInteractionListener");
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
        extras.putString("currentUserId", ((MainActivity) getActivity()).getCurrentUser().getUserId());

        ContentResolver.requestSync(
                MainActivity.getAccount(getActivity()),
                "com.example.sondrehj.familymedicinereminderclient.content",
                extras);

        swipeContainer.setRefreshing(false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnReminderListFragmentInteractionListener {

        void onReminderListItemClicked(Reminder reminder);
        void onReminderDeleteButtonClicked(Reminder reminder);
        void onReminderListSwitchClicked(Reminder reminder);
    }
}
