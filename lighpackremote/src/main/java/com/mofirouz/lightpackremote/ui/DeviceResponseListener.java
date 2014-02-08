package com.mofirouz.lightpackremote.ui;

import android.view.View;

import com.mofirouz.lightpackremote.Main;
import com.mofirouz.lightpackremote.R;
import com.mofirouz.lightpackremote.jlightpack.LightPackResponseListener;
import com.mofirouz.lightpackremote.jlightpack.api.LightPackResponse.LightPackApiResponse;

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
                activity.pullToRefreshLayout.setRefreshComplete();

                activity.enableApplicationUi(false);
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
                activity.enableApplicationUi(false);
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

                activity.enableApplicationUi(true);
            }
        });

        activity.lightPack.requestCurrentMode();
    }

    public void onMoodlamp() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < activity.spinnerAdapter.getCount(); i++)
                    if (LightPackApiResponse.MOODLAMP.name().equalsIgnoreCase(activity.spinnerAdapter.getItem(i).toString()))
                        activity.modeSpinner.setSelection(i);

                activity.ambilightLayout.setVisibility(View.INVISIBLE);
                activity.moodlampLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    public void onAmbilight() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < activity.spinnerAdapter.getCount(); i++)
                    if (LightPackApiResponse.AMBILIGHT.name().equalsIgnoreCase(activity.spinnerAdapter.getItem(i).toString()))
                        activity.modeSpinner.setSelection(i);


                activity.moodlampLayout.setVisibility(View.INVISIBLE);
                activity.ambilightLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
