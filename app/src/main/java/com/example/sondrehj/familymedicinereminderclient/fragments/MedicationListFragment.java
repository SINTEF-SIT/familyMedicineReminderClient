package com.example.sondrehj.familymedicinereminderclient.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import android.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.bus.BusService;
import com.example.sondrehj.familymedicinereminderclient.bus.DataChangedEvent;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.adapters.MedicationRecyclerViewAdapter;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.dialogs.SetAliasDialog;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.utility.TitleSupplier;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.sql.RowSetEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MedicationListFragment extends android.app.Fragment implements TitleSupplier, SwipeRefreshLayout.OnRefreshListener {

    //TODO: Get patient name on the header, f.ex. Sondre's Medication
    //TODO: Create a warning when trying to delete a medication

    private Boolean busIsRegistered = false;
    private List<Medication> medications = new ArrayList<>();
    private OnListFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeContainer;

    @Bind(R.id.medication_empty)
    TextView emptyView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MedicationListFragment() {
    }

    public static MedicationListFragment newInstance() {
        return new MedicationListFragment();
    }

    @Subscribe
    public void handleMedicationsChangedEvent(DataChangedEvent event) {

        if(event.type.equals(DataChangedEvent.MEDICATIONS)) {
            medications.clear();
            //medications.addAll(new MySQLiteHelper(getActivity()).getMedications());
            medications.addAll(new MySQLiteHelper(getActivity()).getMedicationsByOwnerId(((MainActivity) getActivity()).getCurrentUser().getUserId()));
            MedicationListFragment fragment = (MedicationListFragment) getFragmentManager().findFragmentByTag("MedicationListFragment");
            if (fragment != null) {
                getActivity().runOnUiThread(() -> {
                    fragment.notifyChanged();
                    swipeContainer.setRefreshing(false);
                });
            }
        }
        if (event.type.equals(DataChangedEvent.MEDICATIONS_BY_OWNERID)) {
            medications.clear();
            medications.addAll(new MySQLiteHelper(getActivity()).getMedicationsByOwnerId(((MainActivity) getActivity()).getCurrentUser().getUserId()));
            MedicationListFragment fragment = (MedicationListFragment) getFragmentManager().findFragmentByTag("MedicationListFragment");
            if (fragment != null) {
                fragment.notifyChanged();
            }
        }
    }

    public void notifyChanged() {
        RecyclerView recView = (RecyclerView) getActivity().findViewById(R.id.medication_list);
        if (recView != null) {
            System.out.println(medications);
            recView.getAdapter().notifyDataSetChanged();
            if(medications.size() == 0) {
                Log.d("MedicationListFragment", "size was 0");
                recView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                recView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
            System.out.println("notifychanged called");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        medications.addAll(new MySQLiteHelper(getActivity()).getMedicationsByOwnerId(((MainActivity)getActivity()).getCurrentUser().getUserId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medication_list, container, false);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.medication_refresh_layout);
        RecyclerView recView = (RecyclerView) view.findViewById(R.id.medication_list);
        ButterKnife.bind(this, view);

        // Set listener for swipe refresh
        swipeContainer.setOnRefreshListener(this);

        // Set the adapter
        if (recView != null) {
            Context context = view.getContext();
            recView.setLayoutManager(new LinearLayoutManager(context));
            recView.setAdapter(new MedicationRecyclerViewAdapter(getActivity(), medications, mListener));

            if(medications.size() == 0) {
                Log.d("MedicationListFragment", "size was 0");
                recView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                recView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }
        return view;
    }

    public void deleteMedcation(Medication med, int position){
        medications.remove(med);
        RecyclerView recView = (RecyclerView) getActivity().findViewById(R.id.medication_list);
        notifyChanged();
    }

    @OnClick(R.id.new_medication_fab)
    public void onFloatingActionButtonClick() {
        ((MainActivity) getActivity()).changeFragment(new MedicationStorageFragment());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!busIsRegistered) {
            BusService.getBus().register(this);
            busIsRegistered = true;
        }
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!busIsRegistered) {
            BusService.getBus().register(this);
            busIsRegistered = true;
        }

        if (activity instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        ButterKnife.unbind(this);
        super.onDetach();
        if (busIsRegistered) {
            BusService.getBus().unregister(this);
            busIsRegistered = false;
        }
        mListener = null;
    }

    @Override
    public String getTitle() {
        return "Medications";
    }

    @Override
    public void onRefresh() {
        System.out.println("Called onRefresh");
        Bundle extras = new Bundle();
        extras.putString("notificationType", "medicationsChanged");
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        extras.putString("currentUserId", ((MainActivity) getActivity()).getCurrentUser().getUserId());

        ContentResolver.requestSync(
                MainActivity.getAccount(getActivity()),
                "com.example.sondrehj.familymedicinereminderclient.content",
                extras);
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
    public interface OnListFragmentInteractionListener {
        void onMedicationListFragmentInteraction(Medication medication);
    }
}
