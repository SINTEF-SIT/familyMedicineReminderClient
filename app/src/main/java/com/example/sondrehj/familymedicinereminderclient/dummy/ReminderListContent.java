package com.example.sondrehj.familymedicinereminderclient.dummy;

import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ReminderListContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Reminder> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Reminder> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createReminder());
        }
    }

    private static void addItem(Reminder reminder) {
        ITEMS.add(reminder);
        ITEM_MAP.put(reminder.getOwnerId(), reminder);
    }

    private static Reminder createReminder() {
        return new Reminder("1", "My reminder", "My time");
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
