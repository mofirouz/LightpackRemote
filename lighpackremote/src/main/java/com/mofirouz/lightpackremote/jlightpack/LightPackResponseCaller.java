package com.mofirouz.lightpackremote.jlightpack;

import com.mofirouz.lightpackremote.jlightpack.api.LightPackCommand;
import com.mofirouz.lightpackremote.jlightpack.api.LightPackResponse;
import com.mofirouz.lightpackremote.jlightpack.api.LightPackResponse.LightPackApiResponse;

import java.util.Map;

public class LightPackResponseCaller {
    private final LightPackResponseListener listener;
    public LightPackResponseCaller(LightPackResponseListener lightPackResponseListener) {
        this.listener = lightPackResponseListener;
    }

    public void callback(Map<LightPackCommand, LightPackResponse> responses) {
        for (LightPackCommand command : responses.keySet()) {
            LightPackResponse response = responses.get(command);

            switch (command) {
                case GET_STATUS:
                    if (response.getApiResponse() == LightPackApiResponse.ON) listener.onLightsOn();
                    else listener.onLightsOff();
                    break;

                case GET_MODE:
                    if (response.getApiResponse() == LightPackApiResponse.MOODLAMP) listener.onMoodlamp();
                    else listener.onAmbilight();
                    break;

            }
        }
    }
}
