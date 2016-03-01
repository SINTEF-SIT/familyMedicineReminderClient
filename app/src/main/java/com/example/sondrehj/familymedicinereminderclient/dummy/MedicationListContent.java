package com.example.sondrehj.familymedicinereminderclient.dummy;

import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MedicationListContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Medication> ITEMS = new ArrayList<Medication>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Medication> ITEM_MAP = new HashMap<String, Medication>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Medication medication) {
        ITEMS.add(medication);
        ITEM_MAP.put(medication.getOwnerId(), medication);
    }

    private static Medication createDummyItem(int position) {
        return new Medication("$€%%", "MDMA", 0.5, "mg");
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
