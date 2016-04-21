package com.example.sondrehj.familymedicinereminderclient.bus;


import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by nikolai on 20/04/16.
 */
public class BusService {

    private static Bus bus;
    /**
     * Singleton event bus.
     *
     * @return bus
     */
    public synchronized static Bus getBus() {
        if (bus == null) {
            bus = new Bus(ThreadEnforcer.ANY);
        }
        return bus;
    }
}
