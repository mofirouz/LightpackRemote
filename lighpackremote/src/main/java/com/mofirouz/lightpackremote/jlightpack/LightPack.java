package com.mofirouz.lightpackremote.jlightpack;

import com.mofirouz.lightpackremote.jlightpack.api.LightPackCommand;
import com.mofirouz.lightpackremote.jlightpack.api.LightPackResponse.LightPackApiResponse;

/**
 * Represents a LightPack box. This object does *not* hold any state, and therefore every time a method is invoked, a network call is made.
 *
 * Method calls are non-blocking, and run on a separate non-UI thread.
 * When data is requested, the corresponding callback is made on the listener specific when creating this LightPack.
 */

public class LightPack {
    private final LightPackConnection comm;
    private final String version;

    LightPack(LightPackConnection comm, String version) {
        this.comm = comm;
        this.version = version;
    }

    public LightPackConnection getComm() {
        return comm;
    }

    public String getVersion() {
        return version;
    }

    public void requestLightStatus() {
        comm.requestInfo(LightPackCommand.GET_STATUS);
    }

    public void updateLightStatus(boolean lightsOn) {
        comm.sendCommand(LightPackCommand.SET_STATUS, lightsOn ? LightPackApiResponse.ON : LightPackApiResponse.OFF);
        requestLightStatus();
    }

    public void requestCurrentMode() {
        comm.requestInfo(LightPackCommand.GET_MODE);
    }

    public void updateMode(String mode) {
        comm.sendCommand(LightPackCommand.SET_MODE, mode.replaceAll(" ","").toLowerCase());
        requestCurrentMode();
    }


}
