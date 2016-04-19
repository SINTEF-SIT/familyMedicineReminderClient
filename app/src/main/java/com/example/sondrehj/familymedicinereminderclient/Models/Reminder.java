package com.example.sondrehj.familymedicinereminderclient.models;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Created by nikolai on 24/02/16.
 */
public class Reminder implements Serializable {
    int reminderId;
    int reminderServerId;
    String ownerId;
    String name;
    GregorianCalendar date;
    GregorianCalendar endDate;
    Medication medicine;
    Double dosage;
    Boolean isActive;
    int[] days;


    public Reminder() {

    }

    public Reminder(int reminderId, String ownerId, String name, GregorianCalendar date, Boolean isActive, int[] days) {
        setReminderId(reminderId);
        setOwnerId(ownerId);
        setName(name);
        setDate(date);
        setIsActive(isActive);
        setDays(days);
    }

    public int getReminderId(){
        return reminderId;
    }

    public void setReminderId(int id) {
        this.reminderId = id;
    }

    public int getReminderServerId(){
        return reminderServerId;
    }

    public void setReminderServerId(int id) {
        this.reminderServerId = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public void setIsActive(Boolean b){ this.isActive = b; }

    public boolean getIsActive() { return isActive; }

    public Medication getMedicine() { return medicine; }

    public void setMedicine(Medication medicine) { this.medicine = medicine; }

    public Double getDosage() {
        return dosage;
    }

    public void setDosage(Double dosage) {
        this.dosage = dosage;
    }

    public void setDays(int[] days){ this.days = days; }

    public int[] getDays() { return days; }


    public String toString() {
        return ownerId + ", " + name;
    }
}
