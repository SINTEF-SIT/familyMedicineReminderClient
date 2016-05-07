package com.example.sondrehj.familymedicinereminderclient.utility;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import java.util.Comparator;

public class ReminderComparator implements Comparator<Reminder> {


    @Override
    public int compare(Reminder r1, Reminder r2) {
        int diff = r1.getDateString().compareTo(r2.getDateString());
        return diff;
    }
}