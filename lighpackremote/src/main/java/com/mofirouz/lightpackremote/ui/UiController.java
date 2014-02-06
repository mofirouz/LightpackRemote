package com.mofirouz.lightpackremote.ui;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mofirouz.lightpackremote.Main;
import com.mofirouz.lightpackremote.jlightpack.LightPack;

public class UiController {
    private final Main activity;
    private final LightPack lightPack;

    public UiController(Main activity, LightPack lightPack) {
        this.activity = activity;
        this.lightPack = lightPack;
    }

    public void setupListeners() {
        activity.lightSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lightPack.updateLightStatus(isChecked);
            }
        });
    }

}
