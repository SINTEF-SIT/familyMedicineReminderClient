package com.example.sondrehj.familymedicinereminderclient.utility;

import java.io.Serializable;

/**
 * Created by Eirik on 29.04.2016.
 */
public class SerializableString implements Serializable {

    String str;

    public SerializableString(String str) {
        this.str = str;
    }

    public String getString() {
        return str;
    }

    public void setString(String str) {
        this.str = str;
    }


}
