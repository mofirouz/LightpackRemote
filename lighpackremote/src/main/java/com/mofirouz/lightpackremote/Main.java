package com.mofirouz.lightpackremote;

import android.app.Activity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.mofirouz.lightpackremote.lightpacklibrary.Lightpack;
import com.mofirouz.lightpackremote.lightpacklibrary.LightpackConnector;

@EActivity(R.layout.activity_main)
public class Main extends Activity {
    private Lightpack lightpack;

    @ViewById
    Switch lightSwitch;

    @AfterViews
    public void afterViews() {
        lightSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lightpack.setLightStatus(isChecked);
                lightSwitch.setChecked(lightpack.getLightStatus());
                lightSwitch.invalidate();
            }
        });

        connectToLightPack();
    }

    @Background
    public void connectToLightPack() {
        try {
            lightpack = LightpackConnector.connect("10.0.0.4", 3636);
            final boolean lightOn = lightpack.getLightStatus();
            lightSwitch.setChecked(lightOn);
            lightSwitch.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isLightpackConnected()) {
            try {
                LightpackConnector.disconnect(lightpack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        finish();
    }

    private boolean isLightpackConnected() {
        return lightpack != null;
    }
}
