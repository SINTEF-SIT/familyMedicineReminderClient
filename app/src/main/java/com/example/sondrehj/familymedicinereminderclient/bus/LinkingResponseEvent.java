package com.example.sondrehj.familymedicinereminderclient.bus;

/**
 * Created by nikolai on 25/04/16.
 */
public class LinkingResponseEvent {
    private String message;

    public LinkingResponseEvent() {

    }

    public LinkingResponseEvent(String s) {
        message = s;
    }

    public void setMessage(String s) {
        message = s;
    }

    public String getMessage() {
        return message;
    }
}