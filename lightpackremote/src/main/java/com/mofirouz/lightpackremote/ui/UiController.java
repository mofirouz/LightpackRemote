package com.mofirouz.lightpackremote.ui;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.mofirouz.lightpackremote.Main;
import com.mofirouz.jlightpack.LightPack;

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

        activity.profileSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                lightPack.updateProfile(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activity.modeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                lightPack.updateMode(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser)
                    return;

                if (seekBar == activity.brightnessSeekbar)
                    lightPack.updateBrightness(progress);
                else if (seekBar == activity.gammaSeekbar)
                    lightPack.updateGamma(progress);
                else if (seekBar == activity.smoothnessSeekbar)
                    lightPack.updateSmoothness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        activity.brightnessSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        activity.gammaSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        activity.smoothnessSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);

        final OnColorChangedListener colorChangedListener = new OnColorChangedListener() {
            @Override
            public void onColorChanged(int colour) {
                activity.lightPack.updateLedColours(Color.red(colour), Color.green(colour), Color.blue(colour));
            }
        };

        activity.colourPicker.setOnColorChangedListener(colorChangedListener);
        activity.colourPicker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                colorChangedListener.onColorChanged(activity.colourPicker.getColor());
            }
        });
    }

}
