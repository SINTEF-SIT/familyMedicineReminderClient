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
        this.reminderId = reminder.getReminderId();
        this.serverId = reminder.getServerId();
        this.ownerId = reminder.getOwnerId();
        this.name = reminder.getName();
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
        this.days = Converter.dayArrayToServerDayString(reminder.getDays());
    }

    public int getReminderId() { return reminderId; }

    public void setReminderId(int reminderId) { this.reminderId = reminderId; }

    public int getServerId() { return serverId; }

    public String getOwnerId() { return ownerId; }

    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getEndDate() { return endDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getMedicine() { return medicine; }

    public void setMedicine(int medicine) { this.medicine = medicine; }

    public Double getDosage() { return dosage; }

    public void setDosage(Double dosage) { this.dosage = dosage; }

    public Boolean getActive() { return isActive; }

    public void setActive(Boolean active) { isActive = active; }

    public String getDays() { return days; }

    public void setDays(String days) { this.days = days; }




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
