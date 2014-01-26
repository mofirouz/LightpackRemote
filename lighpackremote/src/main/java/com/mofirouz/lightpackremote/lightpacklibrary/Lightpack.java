package com.mofirouz.lightpackremote.lightpacklibrary;

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
        comm.sendCommand(LightpackCommand.GET_STATUS+"\n");
        String[] status = comm.readResponse().split(":");

        return (status.length > 1 && status[1].equalsIgnoreCase("on"));
    }

    public void setLightStatus(boolean on)
    {
        String s="off";
        if (on) s = "on";
        comm.sendCommand(LightpackCommand.SET_STATUS.getCommand() + ":" + s + "\n");
        comm.readResponse();
    }


}
