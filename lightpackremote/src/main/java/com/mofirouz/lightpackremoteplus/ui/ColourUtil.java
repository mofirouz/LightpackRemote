package com.mofirouz.lightpackremoteplus.ui;


import android.graphics.Color;

import java.util.Random;

public class ColourUtil {
    /**
     * Make a color darker.
     *
     * @param color     Color to make darker.
     * @param fraction  Darkness fraction.
     * @return          Darker color.
     */
    public static int darker (int color, double fraction)
    {
        int red   = (int) Math.round (Color.red(color)   * (1.0 - fraction));
        int green = (int) Math.round (Color.green(color) * (1.0 - fraction));
        int blue  = (int) Math.round (Color.blue(color)  * (1.0 - fraction));

        if (red   < 0) red   = 0; else if (red   > 255) red   = 255;
        if (green < 0) green = 0; else if (green > 255) green = 255;
        if (blue  < 0) blue  = 0; else if (blue  > 255) blue  = 255;

        int alpha = Color.alpha(color);

        return Color.argb (alpha, red, green, blue);
    }

    /**
     * Make a color lighter.
     *
     * @param color     Color to make lighter.
     * @param fraction  Darkness fraction.
     * @return          Lighter color.
     */
    public static int lighter (int color, double fraction)
    {
        int red   = (int) Math.round (Color.red(color)   * (1.0 + fraction));
        int green = (int) Math.round (Color.green(color) * (1.0 + fraction));
        int blue  = (int) Math.round (Color.blue(color)  * (1.0 + fraction));

        if (red   < 0) red   = 0; else if (red   > 255) red   = 255;
        if (green < 0) green = 0; else if (green > 255) green = 255;
        if (blue  < 0) blue  = 0; else if (blue  > 255) blue  = 255;

        int alpha = Color.alpha(color);

        return Color.argb(alpha, red, green, blue);
    }

    public static int generateColor() {
        Random random = new Random();

        final float hue = random.nextInt(360);
        final float saturation = (random.nextInt(7000) + 2000) / 10000f;
        final float luminance = 0.3f;
        float[] hsv = {hue, saturation, luminance};
        return Color.HSVToColor(hsv);
    }
}
