package com.example.sondrehj.familymedicinereminderclient.Models;

/**
 * Created by nikolai on 24/02/16.
 */
public class Reminder {
    String ownerId;
    String name;
    String time;
    MedicationStorage medicine;
    String units;

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

    public MedicationStorage getMedicine() {
        return medicine;
    }

    public void setMedicine(MedicationStorage medicine) {
        this.medicine = medicine;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
