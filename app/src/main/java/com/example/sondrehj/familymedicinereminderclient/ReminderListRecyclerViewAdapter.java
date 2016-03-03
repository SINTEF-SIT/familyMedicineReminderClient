package com.example.sondrehj.familymedicinereminderclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.ReminderListFragment.OnReminderListFragmentInteractionListener;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Reminder} and makes a call to the
 * specified {@link OnReminderListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ReminderListRecyclerViewAdapter extends RecyclerView.Adapter<ReminderListRecyclerViewAdapter.ViewHolder> {

    private final List<Reminder> mValues;
    private final ReminderListFragment.OnReminderListFragmentInteractionListener mListener;
    private final Context context;

    public ReminderListRecyclerViewAdapter(Context context, List<Reminder> items, OnReminderListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_reminder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mReminder = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mDateTimeView.setText(mValues.get(position).getTime());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)context).changeFragment(NewReminderFragment.newInstance("", ""));

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onReminderListFragmentInteraction(holder.mReminder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mDateTimeView;
        public Reminder mReminder;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name_text);
            mDateTimeView = (TextView) view.findViewById(R.id.datetime_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
