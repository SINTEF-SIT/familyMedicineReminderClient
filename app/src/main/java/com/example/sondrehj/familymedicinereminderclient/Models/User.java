package com.example.sondrehj.familymedicinereminderclient.Models;

/**
 * Created by nikolai on 24/02/16.
 */
public class User {
    String id; //6 digit alphanumerical
    String username; //optional

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
