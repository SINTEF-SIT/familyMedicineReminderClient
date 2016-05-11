package com.example.sondrehj.familymedicinereminderclient.bus;

/**
 * Created by Eirik on 25.04.2016.
 */
public class DataChangedEvent {

    public static String REMINDERS = "reminders";
    public static String MEDICATIONS = "medications";
    public static String MEDICATIONSENT = "medicationSent";
    public static String REMINDERSENT = "reminderSent";
    public static String SCHEDULE_REMINDER = "scheduleReminder";
    public String type;
    public Object data;

    public DataChangedEvent(String type) {
        this.type = type;
    }

    public DataChangedEvent(String type, Object data) {
        this.type = type;
        this.data = data;
    }
}
