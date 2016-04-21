package com.example.sondrehj.familymedicinereminderclient.bus;

/**
 * Created by nikolai on 20/04/16.
 */
public class LinkingRequestEvent {
    public final String message;
    public final String response;

    public LinkingRequestEvent() {
        this.message = "linkingRequestEvent";
        this.response = null;
    }

    public LinkingRequestEvent(String response) {
        this.message = "linkingRequestEvent";
        this.response = response;
    }

    @Override
    public String toString() {
        return message;
    }
}
