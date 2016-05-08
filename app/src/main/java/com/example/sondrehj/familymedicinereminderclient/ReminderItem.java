package com.example.sondrehj.familymedicinereminderclient;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

/**
 * Created by nikolai on 08/05/16.
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
