package com.example.sondrehj.familymedicinereminderclient.models;

import com.example.sondrehj.familymedicinereminderclient.database.MySQLiteHelper;
import com.example.sondrehj.familymedicinereminderclient.utility.Converter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by nikolai on 24/02/16.
 */
public class Reminder implements Serializable {
    int reminderId;
    int serverId;
    String ownerId;
    String name;
    GregorianCalendar date;
    GregorianCalendar endDate;
    Medication medicine;
    Double dosage;
    Boolean isActive;
    int[] days;
    GregorianCalendar timeTaken;

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

    public Reminder(TransportReminder transportReminder, Medication med) {
        setServerId(transportReminder.getServerId());
        setOwnerId(transportReminder.getOwnerId());
        setName(transportReminder.getName());
        setDate(Converter.databaseDateStringToCalendar(transportReminder.getDate()));
        setIsActive(transportReminder.getActive());
        setDays(Converter.serverDayStringToDayArray(transportReminder.getDays()));
        if (med != null) {
            setMedicine(med);
            setDosage(transportReminder.getDosage());
        }

        if(transportReminder.endDate.equals("0")){
            setEndDate(null);
        } else {
            setEndDate(Converter.databaseDateStringToCalendar(transportReminder.getEndDate()));
        }

        if(transportReminder.timeTaken.equals("0")) {
            setTimeTaken(null);
        } else {
            setTimeTaken(Converter.databaseDateStringToCalendar(transportReminder.timeTaken));
        }

    }

    public int getReminderId(){
        return reminderId;
    }

    public void setReminderId(int id) {
        this.reminderId = id;
    }

    public int getServerId(){
        return serverId;
    }

    public void setServerId(int id) {
        this.serverId = id;
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

    public GregorianCalendar getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(GregorianCalendar timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getDateString(){
        System.out.println(date);
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1; //month is 0-indexed
        final int day = date.get(Calendar.DAY_OF_MONTH);
        String time = String.format("%02d:%02d", hour, minute);
        String date = String.format("%02d.%02d.%4d", day, month, year);
        return date + ". Time: " + time;
    }

    public String getEndDateString(){

        if(endDate != null) {
            int hour = endDate.get(Calendar.HOUR_OF_DAY);
            int minute = endDate.get(Calendar.MINUTE);
            int year = endDate.get(Calendar.YEAR);
            int month = endDate.get(Calendar.MONTH) + 1; //month is 0-indexed
            final int day = endDate.get(Calendar.DAY_OF_MONTH);
            String time = String.format("%02d:%02d", hour, minute);
            String date = String.format("%02d.%02d.%4d", day, month, year);
            return date + ". Time: " + time;
        }
        return "Not set";
    }


    public String toString() {

        String reminder_string =
                "ServerID: " + getServerId() + "\n" +
                " Name: " + getName() + "\n" +
                " Date: " + getDateString()  + "\n" +
                " End-date: " + getEndDateString() + "\n" +
                " Days: " + Arrays.toString(getDays()) + "\n" +
                " Active: " + getIsActive() + "\n";
        if (getMedicine() != null) {
            reminder_string += " Medication: " + getMedicine().getName() + "\n" +
                    " Dosage: " + getDosage() + "\n";
        }
        return reminder_string;
    }

    public void updateFromTransportReminder(TransportReminder transportReminder) {
        this.serverId = transportReminder.serverId;
        this.name = transportReminder.getName();
        this.date = Converter.databaseDateStringToCalendar(transportReminder.getDate());
        if(transportReminder.getEndDate().equals("0")) {   //TODO: Fix this
            this.endDate = null;
        }
        else if(transportReminder.getEndDate().equals("continuous")) {
            this.endDate = new GregorianCalendar(9998,11,31,0,0);
        }
        else {
            this.endDate = Converter.databaseDateStringToCalendar(transportReminder.getEndDate());
        }
        this.days = Converter.serverDayStringToDayArray(transportReminder.getDays());
        this.isActive = transportReminder.getActive();
    }
}
