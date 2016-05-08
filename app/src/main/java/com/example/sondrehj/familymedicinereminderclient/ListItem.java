package com.example.sondrehj.familymedicinereminderclient;

/**
 * Created by nikolai on 08/05/16.
 */
public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_REMINDER = 1;

    abstract public int getType();
}

