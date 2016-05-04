package com.example.sondrehj.familymedicinereminderclient.models;

import java.io.Serializable;

/**
 * Created by sondre on 01/05/2016.
 */
public class User2 implements Serializable {

    String userId; //6 digit alphanumerical
    String alias;

    public User2(String userId, String alias){
        this.userId = userId;
        this.alias = alias;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String toString(){
        return "UserID: " + getUserId() + " | Alias: " + getAlias();
    }

}



