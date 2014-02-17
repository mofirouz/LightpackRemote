package com.mofirouz.lightpackremote.jlightpack;

import com.mofirouz.lightpackremote.jlightpack.api.LightPackCommand;

public interface LightPackResponseListener {
    //TODO: add callbacks here...
    public void onConnect(LightPack lightPack);
    public void onConnectFailure();
    public void onError(LightPackCommand command, Exception e);
    public void onLightsOff();
    public void onLightsOn();
    public void onProfiles(String[] profiles);
    public void onProfileSelection(String profile);
    public void onAmbilight();
    public void onMoodlamp();
    public void onBrightnessUpdate(int brightness);
    public void onGammaUpdate(int gamma);
    public void onSmoothnessUpdate(int smoothness);
    public void onFpsUpdate(double fps);
    public void onLedCountUpdate(int leds);

}
