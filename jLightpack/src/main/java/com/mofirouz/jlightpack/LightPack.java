package com.mofirouz.jlightpack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mofirouz.jlightpack.api.LightPackCommand;
import com.mofirouz.jlightpack.api.LightPackResponse.LightPackApiResponse;

/**
 * Represents a LightPack box. This object does *not* hold any state, and therefore every time a method is invoked, a network call is made.
 * (Only exception is the LED count which can be optionally set to be held in an instance of this class)
 *
 * Method calls are non-blocking, and run on a separate non-UI thread.
 * When data is requested, the corresponding callback is made on the listener specific when creating this LightPack.
 */

public class LightPack {
    private final LightPackConnection comm;
    private final String version;

    private int ledCount;

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

    public int getLedCount() {
        return ledCount;
    }

    public void setLedCount(int ledCount) {
        this.ledCount = ledCount;
    }

    public void requestLightStatus() {
        comm.requestInfo(LightPackCommand.GET_STATUS);
    }

    public void updateLightStatus(boolean lightsOn) {
        comm.sendCommand(LightPackCommand.SET_STATUS, lightsOn ? LightPackApiResponse.ON : LightPackApiResponse.OFF);
        requestLightStatus();
    }

    public void requestAllProfiles() {
        comm.requestInfo(LightPackCommand.GET_ALL_PROFILES);
    }

    public void requestCurrentProfile() {
        comm.requestInfo(LightPackCommand.GET_PROFILE);
    }

    public void updateProfile(String profile) {
        comm.sendCommand(LightPackCommand.SET_PROFILE, profile);
        requestCurrentProfile();
    }

    public void requestCurrentMode() {
        comm.requestInfo(LightPackCommand.GET_MODE);
    }

    public void updateMode(String mode) {
        comm.sendCommand(LightPackCommand.SET_MODE, mode.replaceAll(" ","").toLowerCase());
        requestCurrentMode();
    }

    public void requestBrightness() {
        comm.requestInfo(LightPackCommand.GET_BRIGHTNESS);
    }

    public void requestGamma() {
        comm.requestInfo(LightPackCommand.GET_GAMMA);
    }

    public void requestSmoothness() {
        comm.requestInfo(LightPackCommand.GET_SMOOTHNESS);
    }

    public void updateBrightness(int value) {
        comm.sendCommand(LightPackCommand.SET_BRIGHTNESS, String.valueOf(value));
        requestBrightness();
    }

    public void updateGamma(int value) {
        comm.sendCommand(LightPackCommand.SET_GAMMA, String.valueOf(value));
        requestGamma();
    }

    public void updateSmoothness(int value) {
        comm.sendCommand(LightPackCommand.SET_SMOOTHNESS, String.valueOf(value));
        requestSmoothness();
    }

    public void requestFps() {
        comm.requestInfo(LightPackCommand.GET_FPS);
    }

    public void requestLedCount() {
        comm.requestInfo(LightPackCommand.COUNT_LEDS);
    }

    public void requestLedColours() {
        comm.requestInfo(LightPackCommand.GET_COLORS);
    }

    public void updateLedColour(int i, int red, int green, int blue) {
        comm.sendCommand(LightPackCommand.SET_COLOR, getLedColourUpdateCommand(i, red, green, blue));
        requestLedColours();
    }

    /**
     * Multimap of LEDs to RGB Colours. You can pass in
     * however number of Keys and Values, however only the first 'LedCount' numbers are read.
     */
    public void updateLedColours(ArrayListMultimap<Integer, Integer> colours){
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= ledCount; i++) {
            builder.append(getLedColourUpdateCommand(i, colours.get(i).get(0), colours.get(i).get(1), colours.get(i).get(2)));
            builder.append(";");
        }
    }

    public void updateLedColours(int red, int green, int blue) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= ledCount; i++) {
            builder.append(getLedColourUpdateCommand(i, red, green, blue));
            builder.append(";");
        }

        comm.sendCommand(LightPackCommand.SET_COLOR, builder.toString());
        requestLedColours();
    }

    private String getLedColourUpdateCommand(int ledNumber, int red, int green, int blue) {
        return ledNumber + "-" + red + "," + green + "," + blue;
    }
}
