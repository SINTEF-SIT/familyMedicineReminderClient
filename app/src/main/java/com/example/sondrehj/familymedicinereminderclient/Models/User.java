package com.example.sondrehj.familymedicinereminderclient.models;

import java.util.List;

/**
 * Created by nikolai on 24/02/16.
 */
public class User {
    String userID; //6 digit alphanumerical
    String username; //optional
    String password;
    String userRole;
    List<User> guardians;
    List<User> children;

    public User() {
    }

    public User(String userRole) {
        this.userRole = userRole;
    }

    public User(String username, String password) {
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

    public List<User> getGuardians() {
        return guardians;
    }

    public void setGuardians(List<User> guardians) {
        this.guardians = guardians;
    }

    public List<User> getChildren() {
        return children;
    }

    public void setChildren(List<User> children) {
        this.children = children;
    }

    public String toString(){
        return "userID: " + userID + ", username: " + username + ", password: " + password +
                ", guardians: " + guardians + ", children: " + children +".";
    }
}
