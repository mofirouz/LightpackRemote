package com.mofirouz.lightpackremote.jlightpack;

import com.mofirouz.lightpackremote.jlightpack.api.BadApiKeyException;
import com.mofirouz.lightpackremote.jlightpack.api.LightPackCommand;
import com.mofirouz.lightpackremote.jlightpack.api.LightPackResponse;
import com.mofirouz.lightpackremote.jlightpack.api.LightPackResponse.LightPackApiResponse;

import java.util.Map;

public class LightPackResponseCaller {
    private final LightPackResponseListener listener;
    public LightPackResponseCaller(LightPackResponseListener lightPackResponseListener) {
        this.listener = lightPackResponseListener;
    }

    public void onError(LightPackCommand command, Exception e) {
        listener.onError(command, e);
    }

    public void callback(Map<LightPackCommand, LightPackResponse> responses) {
        for (LightPackCommand command : responses.keySet()) {
            LightPackResponse response = responses.get(command);

            switch (command) {
                case API_KEY:
                    if (response.getApiResponse() != LightPackApiResponse.OK) listener.onError(command, new BadApiKeyException());

                case GET_STATUS:
                    if (response.getApiResponse() == LightPackApiResponse.ON) listener.onLightsOn();
                    else listener.onLightsOff();
                    break;

                case GET_ALL_PROFILES:
                    listener.onProfiles(response.getUnrecognizedResponses().toArray(new String[0])); // todo: verify this...
                    break;

                case GET_PROFILE:
                    listener.onProfileSelection(response.getUnrecognizedResponses().get(0)); // todo: verify this...
                    break;

                case GET_MODE:
                    if (response.getApiResponse() == LightPackApiResponse.MOODLAMP) listener.onMoodlamp();
                    else listener.onAmbilight();
                    break;

                case GET_BRIGHTNESS:
//                    listener.onBrightnessUpdate(Integer.parseInt(response.getUnrecognizedResponses().get(0)));
                    break;

                case GET_GAMMA:
//                    listener.onGammaUpdate(Integer.parseInt(response.getUnrecognizedResponses().get(0)));
                    break;

                case GET_SMOOTHNESS:
//                    listener.onSmoothnessUpdate(Integer.parseInt(response.getUnrecognizedResponses().get(0)));
                    break;

                case GET_FPS:
                    listener.onFpsUpdate(Double.parseDouble(response.getUnrecognizedResponses().get(0)));
                    break;

                case COUNT_LEDS:
                    listener.onLedCountUpdate(Integer.parseInt(response.getUnrecognizedResponses().get(0)));
                    break;
            }
        }
    }
}
