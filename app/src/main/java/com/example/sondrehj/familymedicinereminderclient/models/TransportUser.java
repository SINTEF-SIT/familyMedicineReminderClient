package com.example.sondrehj.familymedicinereminderclient.models;

import java.util.List;

/**
 * Created by nikolai on 24/02/16.
 */
public class TransportUser {
    String userID; //6 digit alphanumerical
    String username; //optional
    String password;
    String userRole;
    List<TransportUser> guardians;
    List<TransportUser> children;

    public TransportUser() {
    }

    public TransportUser(String userRole) {
        this.userRole = userRole;
    }

    public TransportUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getUserRole() { return userRole; }

    public List<TransportUser> getGuardians() {
        return guardians;
    }

    public void setGuardians(List<TransportUser> guardians) {
        this.guardians = guardians;
    }

    public List<TransportUser> getChildren() {
        return children;
    }

    public void setChildren(List<TransportUser> children) {
        this.children = children;
    }

    public String toString(){
        return "userID: " + userID + ", username: " + username + ", password: " + password +
                ", guardians: " + guardians + ", children: " + children +".";
    }
}
