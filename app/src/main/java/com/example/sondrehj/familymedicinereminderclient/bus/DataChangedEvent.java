package com.example.sondrehj.familymedicinereminderclient.bus;

/**
 * Created by Eirik on 25.04.2016.
 */
public class DataChangedEvent {

    public static String REMINDERS = "reminders";
    public static String MEDICATIONS = "medications";

    public String type;

    public DataChangedEvent(String type) {
        this.type = type;
    }
}
