package com.mofirouz.jlightpack;

import com.mofirouz.jlightpack.api.LightPackCommand;

public class LightPackAnimationTest implements LightPackResponseListener {
    public static void main(String[] args) {
        new LightPackAnimationTest();
    }

    private LightPack lightPack;
    private LightPackAnimator animator;

    private LightPackAnimationTest() {
        LightPackConnector.connect("10.0.0.4", 3636, "oou8oo89", this);
    }

    @Override
    public void onConnect(LightPack lightPack) {
        this.lightPack = lightPack;
        lightPack.updateLightStatus(true);
        lightPack.requestLedCount();

    }

    @Override
    public void onConnectFailure() {

    }

    @Override
    public void onError(LightPackCommand command, Exception e) {

    }

    @Override
    public void onLightsOff() {

    }

    @Override
    public void onLightsOn() {
    }

    @Override
    public void onProfiles(String[] profiles) {

    }

    @Override
    public void onProfileSelection(String profile) {

    }

    @Override
    public void onAmbilight() {

    }

    @Override
    public void onMoodlamp() {

    }

    @Override
    public void onBrightnessUpdate(int brightness) {

    }

    @Override
    public void onGammaUpdate(int gamma) {

    }

    @Override
    public void onSmoothnessUpdate(int smoothness) {

    }

    @Override
    public void onFpsUpdate(double fps) {

    }

    @Override
    public void onLedCountUpdate(int leds) {
        lightPack.setLedCount(leds);
        animator = new LightPackAnimator(lightPack);
//        animator.snake(238, 80, 50);
        animator.fadeColours();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        animator.randomSnake();
    }

    @Override
    public void onLedColourUpdate(int led, int red, int green, int blue) {

    }
}
