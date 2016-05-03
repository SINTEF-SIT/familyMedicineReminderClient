package com.example.sondrehj.familymedicinereminderclient.bus;

/**
 * Created by nikolai on 25/04/16.
 */
public class LinkingResponseEvent {
    private String message;
    private String patientID;

    public LinkingResponseEvent() {

    }

    public LinkingResponseEvent(String s) {
        this.message = s;
    }

    public LinkingResponseEvent(String s, String patientID) {
        this.message = s;
        this.patientID = patientID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public void setMessage(String s) {
        message = s;
    }

    public String getMessage() {
        return message;
    }
}