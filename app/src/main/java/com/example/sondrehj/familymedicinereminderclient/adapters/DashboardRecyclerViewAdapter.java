package com.example.sondrehj.familymedicinereminderclient.adapters;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.jobs.UpdateMedicationJob;
import com.example.sondrehj.familymedicinereminderclient.jobs.UpdateReminderJob;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.models.User2;
import com.example.sondrehj.familymedicinereminderclient.notification.NotificationScheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ListItem> mValues;
    private final Context context;
    private ArrayList<User2> users;

    public DashboardRecyclerViewAdapter(
            Context context,
            List<ListItem> mValues,
            ArrayList<User2> users) {
        this.mValues = mValues;
        this.context = context;
        this.users = users;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ListItem.TYPE_HEADER) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_dashboard_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_dashboard_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        if (type == ListItem.TYPE_HEADER) {
            HeaderItem header = (HeaderItem) mValues.get(position);
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;


            for (User2 user : users) {
                if (header.getOwnerID().equals(user.getUserId())) {
                    holder.mHeaderText.setText(user.getAlias());
                    break;
                }
                if (header.getOwnerID().equals(user.getUserId())) {
                    holder.mHeaderText.setText(user.getAlias());
                    break;
                }
            }

        } else {

            ViewHolder holder = (ViewHolder) viewHolder;
            holder.mReminder = ((ReminderItem) mValues.get(position)).getReminder();
            holder.mNameView.setText(holder.mReminder.getName());

            GregorianCalendar cal = holder.mReminder.getDate();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int date = cal.get(Calendar.DATE);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            String timeString = String.format("%02d:%02d", hour, min);
            holder.mDateTimeView.setText(timeString);

            //set medicineicon if medicine is attached
            if (holder.mReminder.getMedicine() != null) {
                // Set the icon
                switch (holder.mReminder.getMedicine().getUnit()) {
                    case "mg":
                    case "pill":
                    case "mcg":
                    case "g":
                        holder.mDashboardIcon.setImageDrawable(
                                context.getResources().getDrawable(R.drawable.android_pill));
                        break;
                    case "inhalation":
                        holder.mDashboardIcon.setImageDrawable(
                                context.getResources().getDrawable(R.drawable.android_lungs));
                        break;
                    case "ml":
                    case "unit":
                        holder.mDashboardIcon.setImageDrawable(
                                context.getResources().getDrawable(R.drawable.android_bottle));
                        break;
                }
            } else {
                holder.mDashboardIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.android_appointment_reminder));
            }

            Account account = MainActivity.getAccount(context);

            if (account.name.equals(holder.mReminder.getOwnerId())) { //this account owns the reminders.
                if (holder.mReminder.getTimeTaken() == null) {
                    if (holder.mReminder.getMedicine() == null) {
                        holder.mButton.setText("Mark as \n done");
                        holder.mButton.setBackgroundColor(Color.parseColor("#FFEE58"));
                    } else {
                        holder.mButton.setText("Mark as taken");
                        holder.mButton.setBackgroundColor(Color.parseColor("#FFEE58"));
                    }
                } else {
                    Calendar calendar = holder.mReminder.getTimeTaken();
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
                    int minutes = calendar.get(Calendar.MINUTE);
                    String time = String.format("%02d:%02d", hours, minutes);
                    holder.mButton.setBackgroundColor(Color.parseColor("#9CCC65"));
                    if (holder.mReminder.getMedicine() != null)
                        holder.mButton.setText("✓ Taken \n" + time);
                    else
                        holder.mButton.setText("✓ Done \n" + time);
                }

                holder.mButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (holder.mReminder.getTimeTaken() == null) {
                            GregorianCalendar gregorianCalendar = new GregorianCalendar();
                            NotificationScheduler ns = new NotificationScheduler(context);
                            holder.mReminder.setTimeTaken(gregorianCalendar);

                            int hour = gregorianCalendar.get(Calendar.HOUR_OF_DAY);
                            int minute = gregorianCalendar.get(Calendar.MINUTE);

                            String time = String.format("%02d:%02d", hour, minute);
                            String authToken = AccountManager.get(context).getUserData(MainActivity.getAccount(context), "authToken");
                            String userId = ((MainActivity) context).getCurrentUser().getUserId();

                            if (holder.mReminder.getMedicine() == null) {
                                holder.mButton.setText("✓ Done \n" + time);
                                holder.mButton.setBackgroundColor(Color.parseColor("#9CCC65"));
                                MySQLiteHelper db = new MySQLiteHelper(context);
                                db.setReminderTimeTaken(holder.mReminder);
                                ((MainActivity) context).getJobManager().addJobInBackground(new UpdateReminderJob(holder.mReminder, userId, authToken));
                            } else {
                                holder.mButton.setText("✓ Taken \n" + time);
                                holder.mButton.setBackgroundColor(Color.parseColor("#9CCC65"));
                                // Update database and server
                                MySQLiteHelper db = new MySQLiteHelper(context);
                                db.setReminderTimeTaken(holder.mReminder);
                                holder.mReminder.getMedicine().setCount(holder.mReminder.getMedicine().getCount() - holder.mReminder.getDosage());
                                db.updateAmountMedication(holder.mReminder.getMedicine());
                                if (holder.mReminder.getMedicine().getCount() < 5) {
                                    ns.publishInstantNotification(ns.getLowOnMedicationNotification(holder.mReminder.getMedicine()));
                                }
                                ((MainActivity) context).getJobManager().addJobInBackground(new UpdateMedicationJob(holder.mReminder.getMedicine(), userId, authToken));
                                ((MainActivity) context).getJobManager().addJobInBackground(new UpdateReminderJob(holder.mReminder, userId, authToken));
                            }
                            ns.cancelNotification(holder.mReminder.getReminderId());
                        }
                    }
                });
            } else { //this account does not own the reminders.
                if (holder.mReminder.getTimeTaken() == null) {
                    if (holder.mReminder.getMedicine() == null) {
                        holder.mButton.setText("Not done");
                        holder.mButton.setBackgroundColor(Color.parseColor("#FFEE58"));
                    } else {
                        holder.mButton.setText("Not taken");
                        holder.mButton.setBackgroundColor(Color.parseColor("#FFEE58"));
                    }
                } else {
                    Calendar calendar = holder.mReminder.getTimeTaken();
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
                    int minutes = calendar.get(Calendar.MINUTE);
                    String time = String.format("%02d:%02d", hours, minutes);
                    holder.mButton.setBackgroundColor(Color.parseColor("#9CCC65"));
                    if (holder.mReminder.getMedicine() != null)
                        holder.mButton.setText("✓ Taken \n" + time);
                    else
                        holder.mButton.setText("✓ Done \n" + time);
                }
            }
        }
    }

    //USE MATERIAL 400 RED: #EF5350 for Reminder NOT DONE and SHOULD BE DONE
    //USE MATERIAL 400 YELLOW: #FFEE58 for reminder NOT DONE and NOT SHOULD BE DONE
    //USE MATERIAL 400 LIGHT GREEN: #9CCC65 for reminder TAKEN and SHOULD BE TAKEN

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void remove(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).getType();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mDateTimeView;
        public final ImageView mDashboardIcon;
        public final Button mButton;
        public Reminder mReminder;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name_text);
            mDateTimeView = (TextView) view.findViewById(R.id.datetime_text);
            mDashboardIcon = (ImageView) view.findViewById(R.id.dashboard_icon);
            mButton = (Button) view.findViewById(R.id.taken_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public String mUserId;
        public TextView mHeaderText;

        public HeaderViewHolder(View view) {
            super(view);
            mView = view;
            mHeaderText = (TextView) view.findViewById(R.id.dashboard_list_header_text);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}