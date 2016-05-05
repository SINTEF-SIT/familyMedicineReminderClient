package com.example.sondrehj.familymedicinereminderclient.database;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */




public class ReminderListContent {

    /**
     * An array of sample (dummy) items.
     */

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Reminder> ITEM_MAP = new HashMap<>();

    static {

        String mysString = "Sde";
        // Add some sample items.
        //for (int i = 1; i <= COUNT; i++) {
        //addItem(createReminder());
        //}
    }

    //private static void addItem(Reminder reminder) {
    //    ITEMS.add(new Reminder(0, "0", "Morning pill", "08:00"));
    //    ITEMS.add(new Reminder(1, "1", "Afternoon pill", "15:00"));
    //    ITEMS.add(new Reminder(2, "2", "Evening pill", "20:00"));
    //}

    private static Reminder createReminder() {
        return new Reminder(0, "1", "My reminder", new GregorianCalendar(2016, 5, 10), true, new int[]{1, 2});
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
}
