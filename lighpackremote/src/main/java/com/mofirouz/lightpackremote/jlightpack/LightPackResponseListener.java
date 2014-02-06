package com.mofirouz.lightpackremote.jlightpack;

public interface LightPackResponseListener {
    //TODO: add callbacks here...
    public void onConnectFailure();
    public void onLightsOff();
    public void onLightsOn();
}
