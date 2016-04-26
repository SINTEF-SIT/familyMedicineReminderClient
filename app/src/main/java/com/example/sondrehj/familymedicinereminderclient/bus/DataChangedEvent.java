package com.example.sondrehj.familymedicinereminderclient.bus;

/**
 * Created by Eirik on 25.04.2016.
 */
public class DataChangedEvent {

    public String type;

    public DataChangedEvent(String type) {
        this.type = type;
    }
}
