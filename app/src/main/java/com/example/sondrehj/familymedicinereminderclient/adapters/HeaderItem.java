package com.example.sondrehj.familymedicinereminderclient.adapters;

/**
 * A holder class for {@link DashboardRecyclerViewAdapter}.
 */
public class HeaderItem extends ListItem {

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    private String ownerID;

    @Override
    public int getType(){
        return TYPE_HEADER;
    }
}