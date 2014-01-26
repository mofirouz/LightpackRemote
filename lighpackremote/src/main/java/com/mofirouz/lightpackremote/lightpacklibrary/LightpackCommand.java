package com.mofirouz.lightpackremote.lightpacklibrary;

/**
 * 25/01/2014 13:55
 */
public enum LightpackCommand {
    GET_STATUS ("getstatus"),
    SET_STATUS ("setstatus");

    private final String command;
    LightpackCommand(String command) {
        this.command = command;
    }

    String getCommand() {
        return command;
    }
}
