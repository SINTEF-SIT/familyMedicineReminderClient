package com.example.sondrehj.familymedicinereminderclient.models;

import java.sql.SQLOutput;

/**
 * Created by nikolai on 24/02/16.
 */
public class Medication {
    String ownerId;
    String name;
    Double count;
    String unit;

    public Medication(String ownerId, String name, Double count, String unit) {
        this.ownerId = ownerId;
        this.name = name;
        this.count = count;
        this.unit = unit;
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

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public String getUnit(){
        return unit;
    }


    public String toString(){
        return "Name: " + this.name + "\n" + "Count: " + this.count + "\n" + "Unit: " + this.unit;
    }
}
