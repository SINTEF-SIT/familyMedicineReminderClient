package com.example.sondrehj.familymedicinereminderclient.adapters;

/**
 * Abstract class extended by {@link HeaderItem} and {@link ListItem}
 */
public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_REMINDER = 1;

    abstract public int getType();
}

