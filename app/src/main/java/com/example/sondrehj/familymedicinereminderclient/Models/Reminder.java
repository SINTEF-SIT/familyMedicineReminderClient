package com.example.sondrehj.familymedicinereminderclient.models;

/**
 * Created by nikolai on 24/02/16.
 */
public class Reminder {
    String ownerId;
    String name;
    String time;
    Medication medicine;
    String units;

    public Reminder(String ownerId, String name, String time) {
        setOwnerId(ownerId);
        setName(name);
        setTime(time);
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

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
