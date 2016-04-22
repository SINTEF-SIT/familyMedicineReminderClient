package com.example.sondrehj.familymedicinereminderclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.fragments.ReminderListFragment.OnReminderListFragmentInteractionListener;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.utility.Converter;

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mReminder = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mSwitch.setChecked(mValues.get(position).getIsActive());

        GregorianCalendar cal = mValues.get(position).getDate();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);


        String timeString = String.format("%02d:%02d", hour, min);
        String dateString = Converter.dayIndexToDayString(dayOfWeek)
                        + ". " + String.format("%02d", date)
                        + ". " + Converter.monthIndexToMonthString(month) + ".";

        // Text for a repeating reminder
        if(holder.mReminder.getDays().length > 0){
            if(holder.mReminder.getEndDate().get((Calendar.YEAR)) == 9998) {
                holder.mDateText.setText("Continuous");
            } else {
                holder.mDateText.setText(dateString);
            }
            String daySelectString = Converter.daysArrayToSelectedDaysText(holder.mReminder.getDays());
            holder.mDaysText.setText(Html.fromHtml(daySelectString));
            holder.mDateTimeView.setText(timeString);
        }
        // Text for a non-repeating reminder
        else {
            holder.mDateTimeView.setText(timeString);
            holder.mDateText.setText(dateString);
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

        holder.mDeleteWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(holder.getAdapterPosition());
                mListener.onReminderDeleteButtonClicked(holder.mReminder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void remove(int position){
        mValues.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mDateTimeView;
        public final Switch mSwitch;
        public final TextView mDateText;
        public final FrameLayout mDeleteWrapper;
        public final TextView mDaysText;
        public Reminder mReminder;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name_text);
            mDateTimeView = (TextView) view.findViewById(R.id.datetime_text);
            mSwitch = (Switch) view.findViewById(R.id.reminder_switch);
            mDateText = (TextView) view.findViewById(R.id.reminder_item_date_text);
            mDeleteWrapper = (FrameLayout) view.findViewById(R.id.delete_wrapper);
            mDaysText = (TextView) view.findViewById(R.id.days_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
