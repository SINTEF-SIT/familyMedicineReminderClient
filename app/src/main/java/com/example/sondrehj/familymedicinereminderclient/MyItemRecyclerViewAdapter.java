package com.example.sondrehj.familymedicinereminderclient;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.MedicationListFragment.OnListFragmentInteractionListener;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.example.sondrehj.familymedicinereminderclient.models.Medication} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Medication> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context context;

    public MyItemRecyclerViewAdapter(Context context, List<Medication> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medication_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        //Check if the amount got decimals.
        if (mValues.get(position).getCount() % 1 == 0){
            int i = mValues.get(position).getCount().intValue();
            String amount = Integer.toString(i) + " " + mValues.get(position).getUnit();
            holder.mIdView.setText(amount);
        } else {
            String amount = Double.toString(mValues.get(position).getCount())
                    + " " + mValues.get(position).getUnit();
            holder.mIdView.setText(amount);
        }

        holder.mContentView.setText(mValues.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Changes view to MedicationStorage
                ((MainActivity)context).changeFragment(new MedicationStorageFragment());

                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Medication mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
