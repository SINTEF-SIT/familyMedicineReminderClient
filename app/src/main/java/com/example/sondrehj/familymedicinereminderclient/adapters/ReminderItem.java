package com.example.sondrehj.familymedicinereminderclient.adapters;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

/**
 * A holder class for {@link DashboardRecyclerViewAdapter}.
 */
public class ReminderItem extends ListItem {

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    private Reminder reminder;

    @Override
    public int getType(){
        return TYPE_REMINDER;
    }
}
