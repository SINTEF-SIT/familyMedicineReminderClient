package com.example.sondrehj.familymedicinereminderclient.models;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Created by nikolai on 24/02/16.
 */
public class Reminder implements Serializable {
    int reminderId;
    String ownerId;
    String name;
    GregorianCalendar date;
    Medication medicine;
    String units;
    Boolean isActive;

    public Reminder() {

    }

    public Reminder(int reminderId, String ownerId, String name, GregorianCalendar date, Boolean isActive) {
        setReminderId(reminderId);
        setOwnerId(ownerId);
        setName(name);
        setDate(date);
        setIsActive(isActive);
    }

    public int getReminderId(){
        return reminderId;
    }

    public void setReminderId(int id) {
        this.reminderId = id;
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

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public void setIsActive(Boolean b){ this.isActive = b; }

    public boolean getIsActive() { return isActive; }

    public Medication getMedicine() {
        return medicine;
    }

    public void setMedicine(Medication medicine) {
        this.medicine = medicine;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String toString() {
        return ownerId + ", " + name;
    }
}
