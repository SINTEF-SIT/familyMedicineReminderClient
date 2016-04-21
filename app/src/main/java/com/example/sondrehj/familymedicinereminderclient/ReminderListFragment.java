package com.example.sondrehj.familymedicinereminderclient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sondrehj.familymedicinereminderclient.dummy.ReminderListContent;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnReminderListFragmentInteractionListener}
 * interface.
 */
public class ReminderListFragment extends android.app.Fragment {

    private OnReminderListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReminderListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ReminderListFragment newInstance() {
        ReminderListFragment fragment = new ReminderListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);
        RecyclerView recView = (RecyclerView) view.findViewById(R.id.reminder_list);
        // Set the adapter
        if (recView != null) {
            Context context = view.getContext();
            recView.setLayoutManager(new LinearLayoutManager(context));
            //recView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            recView.setAdapter(new ReminderListRecyclerViewAdapter(context, ReminderListContent.ITEMS, mListener));
        }

        view.findViewById(R.id.reminder_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragment(NewReminderFragment.newInstance(null));
            }
        });
        return view;
    }

    //API Level >= 23
    @Override
     public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReminderListFragmentInteractionListener) {
            mListener = (OnReminderListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    //API Level < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnReminderListFragmentInteractionListener) {
            mListener = (OnReminderListFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnReminderListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onReminderListItemClicked(Reminder reminder);
        void onNewReminderButtonClicked();
        void onReminderDeleteButtonClicked(Reminder reminder);
        void onReminderListSwitchClicked(Reminder reminder);
    }
}
