package com.mofirouz.jlightpack;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LightPackAnimator {
    public enum AnimationStyle {
        NONE, // No animation running
        SNAKE, // sequentially fade LEDs to black and back to the original colour
        SNAKE_RANDOM, // sequentially fade LEDs to black and back to a random colour
        COLOUR_FADE, // randomly change all colours of all LEDs to the same colour
        RANDOM; // randomly change all colour of all LEDs to different colours
    }

    private final static int MAX_SMOOTHNESS = 255;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Random random = new Random();
    private final LightPack lightPack;

    private volatile AnimationStyle style = AnimationStyle.NONE;
    private int deviceSmoothness = 255;
    private Future animationFuture;

    public LightPackAnimator(LightPack lightPack) {
        this.lightPack = lightPack;
    }

    public void deviceSmoothness(int smoothness) {
        deviceSmoothness = smoothness;
    }

    public void stop() {
        if (animationFuture != null)
            animationFuture.cancel(false);

        style = AnimationStyle.NONE;
        lightPack.updateSmoothness(deviceSmoothness);
    }

    public void snake(final int red, final int green, final int blue) {
        // stop any previous animation
        if (style != AnimationStyle.NONE)
            stop();

        style = AnimationStyle.SNAKE;
        lightPack.updateSmoothness(MAX_SMOOTHNESS);
        lightPack.updateLedColours(0, 0, 0); // make everything black

        animationFuture = executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (int k = 0; k <= lightPack.getLedCount(); k++) {
                    for (int i = 0; i <= lightPack.getLedCount(); i++) {
                        if (k != i) {
                            lightPack.updateLedColour(i, 0, 0, 0);
                        } else {
                            lightPack.updateLedColour(i, red, green, blue);
                        }
                    }
                }

                lightPack.updateLedColours(0, 0, 0); // make everything black
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void randomSnake() {
        if (style != AnimationStyle.NONE)
            stop();

        style = AnimationStyle.SNAKE_RANDOM;

        lightPack.updateSmoothness(MAX_SMOOTHNESS);

        animationFuture = executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int red = random.nextInt(255);
                int green = random.nextInt(255);
                int blue = random.nextInt(255);

                for (int k = 0; k <= lightPack.getLedCount(); k++) {
                    for (int i = 0; i <= lightPack.getLedCount(); i++) {
                        if (k != i) {
                            lightPack.updateLedColour(i, 0, 0, 0);
                        } else {
                            lightPack.updateLedColour(i, red, green, blue);
                        }
                    }
                }

                lightPack.updateLedColours(0, 0, 0); // make everything black
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void fadeColours() {
        // stop any previous animation
        if (style != AnimationStyle.NONE)
            stop();

        style = AnimationStyle.COLOUR_FADE;

        lightPack.updateSmoothness(MAX_SMOOTHNESS);

        animationFuture = executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int red = random.nextInt(255);
                int green = random.nextInt(255);
                int blue = random.nextInt(255);

                float[] hsb = Color.RGBtoHSB(red, green, blue, null);
                Color colour = Color.getHSBColor(hsb[0], hsb[1], 0.5f);

                lightPack.updateLedColours(colour.getRed(), colour.getGreen(), colour.getBlue());
            }
        }, 0, 3, TimeUnit.SECONDS);

    }

    public void random() {
        if (style != AnimationStyle.NONE)
            stop();

        style = AnimationStyle.SNAKE_RANDOM;

        lightPack.updateSmoothness(MAX_SMOOTHNESS);
        lightPack.updateLedColours(0, 0, 0); // make everything black

        animationFuture = executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int led = random.nextInt((lightPack.getLedCount() + 1));
                int red = random.nextInt(255);
                int green = random.nextInt(255);
                int blue = random.nextInt(255);

                lightPack.updateLedColour(led, red, green, blue);
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public AnimationStyle getAnimationStyle() {
        return style;
    }

}
