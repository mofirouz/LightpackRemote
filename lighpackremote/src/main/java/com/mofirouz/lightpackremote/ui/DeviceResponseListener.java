package com.mofirouz.lightpackremote.ui;

import android.graphics.Color;
import android.view.View;

import com.mofirouz.lightpackremote.Main;
import com.mofirouz.lightpackremote.R;
import com.mofirouz.lightpackremote.jlightpack.LightPackResponseListener;

public class DeviceResponseListener implements LightPackResponseListener {
    private final Main activity;

    public DeviceResponseListener (Main activity) {
        this.activity = activity;
    }

    @Override
    public void onConnectFailure() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.statusMessage.setText(R.string.connection_error);
                activity.statusMessage.setTextColor(Color.RED);
                activity.statusMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onLightsOff() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.lightSwitch.setChecked(false);
                activity.lightSwitch.invalidate();

                activity.statusMessage.setText(R.string.off_device);
                activity.statusMessage.setTextColor(Color.GRAY);
                activity.statusMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onLightsOn() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.lightSwitch.setChecked(true);
                activity.lightSwitch.invalidate();

                activity.statusMessage.setVisibility(View.INVISIBLE);
            }
        });

    }
}
