package com.example.sondrehj.familymedicinereminderclient.models;

import com.example.sondrehj.familymedicinereminderclient.utility.Converter;
import java.io.Serializable;
import java.util.Arrays;

public class TransportReminder implements Serializable {

    int reminderId;
    int serverId;
    String ownerId;
    String name;
    String date;
    String endDate;
    int medicine;
    Double dosage;
    Boolean isActive;
    String days;

    public TransportReminder(Reminder reminder) {
        this.reminderId = reminder.reminderId;
        this.serverId = reminder.serverId;
        this.ownerId = reminder.ownerId;
        this.name = reminder.name;
        this.date = Converter.calendarToDatabaseString(reminder.getDate());

        //End date may be null if the reminder is one-time or continuous
        if(reminder.getEndDate() != null) {
            this.endDate = Converter.calendarToDatabaseString(reminder.getEndDate());
        } else {
            this.endDate = "0";
        }

        //Medicine may be null if the reminder is for a doctor's appointment or other stuff!
        if (reminder.medicine != null) {
            this.medicine = reminder.getMedicine().getServerId();
            this.dosage = reminder.dosage;
        } else {
            this.medicine = -1;
            this.dosage = 0.0;
        }
        this.isActive = reminder.isActive;
        this.days = Converter.daysArrayToDatabaseString(reminder.getDays());
    }

    public String toString() {
        return  " Transportreminder: " + "\n" +
                " ServerID: " + serverId + "\n" +
                " Name: " + name + "\n" +
                " Date: " + date  + "\n" +
                " End-date: " + endDate + "\n" +
                " Days: " + days + "\n" +
                " Active: " + isActive + "\n" +
                " Medication: " + medicine + "\n" +
                " Dosage: " + dosage + "\n";
    }
}
