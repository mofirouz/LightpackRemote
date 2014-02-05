package com.mofirouz.lightpackremote.lightpacklibrary;

/**
 * 25/01/2014 13:55
 */
public enum LightpackCommand {
    LOCK ("lock", "lock"),
    UNLOCK ("unlock", "lock"),
    GET_STATUS ("getstatus", "status"),
    SET_STATUS ("setstatus", "status");

    private final String command;
    private final String response;

    LightpackCommand(String command, String response) {
        this.command = command;
        this.response = response;
    }

    String getCommand() {
        return command;
    }
    String getResponse() { return response; }
}
