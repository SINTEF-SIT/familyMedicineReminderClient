package com.example.sondrehj.familymedicinereminderclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.ReminderListFragment.OnReminderListFragmentInteractionListener;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Reminder} and makes a call to the
 * specified {@link OnReminderListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ReminderListRecyclerViewAdapter extends RecyclerView.Adapter<ReminderListRecyclerViewAdapter.ViewHolder> {

    private final List<Reminder> mValues;
    private final OnReminderListFragmentInteractionListener mListener;
    private final Context context;

    public ReminderListRecyclerViewAdapter(Context context, List<Reminder> items, OnReminderListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_reminder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mReminder = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mSwitch.setChecked(mValues.get(position).getIsActive());

        GregorianCalendar cal = mValues.get(position).getDate();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);


        String timeString = String.format("%02d:%02d", hour, min);
        String dateString = String.format("%02d.%02d.%4d", date, month, year);
        String timeDateString;

        // Text for a repeating reminder
        if(holder.mReminder.getDays().length > 0){
            String daySelectString = mListener.ReminderListGetSelectedDaysText(holder.mReminder.getDays());
            holder.mDateTimeView.setText(timeString);
            holder.mDaySelectView.setText(Html.fromHtml(daySelectString));
        }
        // Text for a non-repeating reminder
        else {
            holder.mDateTimeView.setText(timeString);
            holder.mDaySelectView.setText("(" + dateString + ")");
        }


        holder.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != mListener) {
                    mListener.onReminderListSwitchClicked(holder.mReminder);
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onReminderListItemClicked(holder.mReminder);
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
        public final Switch mSwitch;
        public final TextView mDaySelectView;
        public Reminder mReminder;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name_text);
            mDateTimeView = (TextView) view.findViewById(R.id.datetime_text);
            mSwitch = (Switch) view.findViewById(R.id.reminder_switch);
            mDaySelectView = (TextView) view.findViewById(R.id.day_select_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
