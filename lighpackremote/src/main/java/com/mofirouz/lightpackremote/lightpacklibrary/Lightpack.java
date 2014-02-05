package com.mofirouz.lightpackremote.lightpacklibrary;

import java.util.Map;

/**
 * Represents a Lightpack box
 */
public class Lightpack {
    private final LightpackComm comm;
    private final String version;

    Lightpack(LightpackComm comm, String version) {
        this.comm = comm;
        this.version = version;
    }

    public LightpackComm getComm() {
        return comm;
    }

    public String getVersion() {
        return version;
    }

    public boolean getLightStatus() {
        Map<String, String> status = comm.getInfo(LightpackCommand.GET_STATUS);

        return ("on".equalsIgnoreCase(status.get(LightpackCommand.GET_STATUS.getResponse())));
    }

    public void setLightStatus(boolean on) {
        String s="off";
        if (on) s = "on";
        comm.sendCommand(LightpackCommand.SET_STATUS, s);
    }


}
