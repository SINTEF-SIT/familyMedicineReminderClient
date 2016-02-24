package com.example.sondrehj.familymedicinereminderclient.Models;

/**
 * Created by nikolai on 24/02/16.
 */
public class MedicationStorage {
    String ownerId;
    String name;
    Double count;

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

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }
}
