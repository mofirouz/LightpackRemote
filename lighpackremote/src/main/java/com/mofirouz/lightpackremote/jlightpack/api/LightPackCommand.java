package com.mofirouz.lightpackremote.jlightpack.api;

/**
 * List of available commands to send to Prismatik.
 */
public enum LightPackCommand {
    API_KEY("apikey"),
    LOCK ("lock"),
    UNLOCK ("unlock"),
    GET_STATUS ("getstatus"),
    SET_STATUS ("setstatus"),
    GET_ALL_PROFILES("getprofiles"),
    GET_PROFILE("getprofile"),
    SET_PROFILE("setprofile"),
    GET_MODE ("getmode"),
    SET_MODE ("setmode"),
    GET_BRIGHTNESS("getbrightness"),
    SET_BRIGHTNESS("setbrightness"),
    GET_GAMMA("getgamma"),
    SET_GAMMA("setgamma"),
    GET_SMOOTHNESS("getsmoothness"),
    SET_SMOOTHNESS("setsmoothness"),
    COUNT_LEDS("getcountleds"),
    GET_FPS("getfps");

    private final String command;

    LightPackCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static LightPackCommand getCommand(String value) {
        return LightPackCommand.valueOf(value.toUpperCase());
    }
}