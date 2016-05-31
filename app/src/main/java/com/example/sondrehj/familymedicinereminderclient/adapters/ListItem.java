package com.example.sondrehj.familymedicinereminderclient.adapters;

/**
 *
 */
public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_REMINDER = 1;

    abstract public int getType();
}

