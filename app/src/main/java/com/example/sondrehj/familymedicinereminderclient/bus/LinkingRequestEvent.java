package com.example.sondrehj.familymedicinereminderclient.bus;

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
