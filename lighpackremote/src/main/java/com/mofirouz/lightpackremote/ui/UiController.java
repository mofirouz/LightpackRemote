package com.mofirouz.lightpackremote.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
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

        activity.modeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                System.out.println("------selected " + item);
                lightPack.updateMode(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
