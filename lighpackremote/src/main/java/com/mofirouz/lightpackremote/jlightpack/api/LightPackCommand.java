package com.mofirouz.lightpackremote.jlightpack.api;

/**
 * List of available commands to send to Prismatik.
 */
public enum LightPackCommand {
    LOCK ("lock"),
    UNLOCK ("unlock"),
    GET_STATUS ("getstatus"),
    SET_STATUS ("setstatus"),
    GET_MODE ("getmode"),
    SET_MODE ("setmode");

    //TODO: add more commands here...

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