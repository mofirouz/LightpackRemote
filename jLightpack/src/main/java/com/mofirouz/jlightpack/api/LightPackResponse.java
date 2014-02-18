package com.mofirouz.jlightpack.api;

import java.util.Arrays;
import java.util.List;

/**
 * List of responses received back from Prismatik.
 *
 * Standard API responses are in the LightPackApiResponse,
 * whilst dynamic responses will be stored in 'unrecognizedResponses' as type 'UNKNOWN'
 */

public class LightPackResponse {
    public enum LightPackApiResponse {
        OK,
        SUCCESS,
        FAIL,
        ERROR,
        BUSY,
        IDLE,
        ON,
        OFF,
        AMBILIGHT,
        MOODLAMP,
        /** Dynamic responses. Make sure you looked up UnrecognizedResponses for the whole list */
        UNKNOWN;
    }

    private final LightPackApiResponse apiResponse;
    private final String[] unrecognizedResponses;

    public LightPackResponse(LightPackApiResponse apiResponse) {
        this(apiResponse, "");
    }

    public LightPackResponse(LightPackApiResponse apiResponse, String... unrecognizedResponses) {
        this.apiResponse = apiResponse;
        this.unrecognizedResponses = unrecognizedResponses;
    }

    public LightPackApiResponse getApiResponse() {
        return apiResponse;
    }

    public List<String> getUnrecognizedResponses() {
        return Arrays.asList(unrecognizedResponses);
    }

    public static LightPackResponse parseResponse(String raw) {
        LightPackApiResponse apiResponse = LightPackApiResponse.UNKNOWN;
        try {
            apiResponse = LightPackApiResponse.valueOf(raw.toUpperCase());
        } catch (Exception e) {}

        return new LightPackResponse(apiResponse, raw.split(";"));
    }
}
