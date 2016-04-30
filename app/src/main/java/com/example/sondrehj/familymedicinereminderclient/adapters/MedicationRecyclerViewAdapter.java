package com.example.sondrehj.familymedicinereminderclient.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.fragments.MedicationListFragment.OnListFragmentInteractionListener;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.example.sondrehj.familymedicinereminderclient.models.Medication} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MedicationRecyclerViewAdapter extends RecyclerView.Adapter<MedicationRecyclerViewAdapter.ViewHolder> {

    private final List<Medication> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context context;

    public MedicationRecyclerViewAdapter(Context context, List<Medication> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_medication_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        // Set the name
        holder.mContentView.setText(mValues.get(position).getName());

        // Set the amount.
        if (mValues.get(position).getCount() % 1 == 0) {
            if (mValues.get(position).getCount() > 1.0) {
                int i = mValues.get(position).getCount().intValue();
                String amount = Integer.toString(i) + " " + mValues.get(position).getUnit() + "s";
                holder.mMedAmountView.setText(amount);
            } else {
                int i = mValues.get(position).getCount().intValue();
                String amount = Integer.toString(i) + " " + mValues.get(position).getUnit();
                holder.mMedAmountView.setText(amount);
            }

        } else {
            if (mValues.get(position).getCount() > 1.0){
                String amount = Double.toString(mValues.get(position).getCount())
                        + " " + mValues.get(position).getUnit() + "s";
                holder.mMedAmountView.setText(amount);
            } else {
                String amount = Double.toString(mValues.get(position).getCount())
                        + " " + mValues.get(position).getUnit();
                holder.mMedAmountView.setText(amount);
            }
        }

        // Set the icon
        switch (holder.mItem.getUnit()) {
            case "mg":case "pill": case "mcg":case "g":
                holder.mMedIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.android_pill));
                break;
            case "inhalation":
                holder.mMedIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.android_lungs));
                break;
            case "ml":case "unit":
                holder.mMedIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.android_bottle));
                break;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onMedicationListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.mDeleteWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    remove(holder.getAdapterPosition());

                    //TODO: remove medication from database (handle notification conflict if medication is attached to reminder)
                    //mListener.onMedicationListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void remove(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mMedAmountView;
        public final TextView mContentView;
        public final FrameLayout mDeleteWrapper;
        public final ImageView mMedIcon;
        public Medication mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMedAmountView = (TextView) view.findViewById(R.id.med_amount_left_text);
            mContentView = (TextView) view.findViewById(R.id.med_content);
            mDeleteWrapper = (FrameLayout) view.findViewById(R.id.med_delete_wrapper);
            mMedIcon = (ImageView) view.findViewById(R.id.med_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
