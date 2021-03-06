package com.mofirouz.lightpackremoteplus.ui;

import android.view.View;
import android.widget.ArrayAdapter;

import com.mofirouz.lightpackremoteplus.Main;
import com.mofirouz.lightpackremoteplus.R;
import com.mofirouz.jlightpack.LightPack;
import com.mofirouz.jlightpack.LightPackResponseListener;
import com.mofirouz.jlightpack.api.LightPackCommand;
import com.mofirouz.jlightpack.api.LightPackResponse.LightPackApiResponse;

public class DeviceResponseListener implements LightPackResponseListener {
    private final Main activity;

    public DeviceResponseListener (Main activity) {
        this.activity = activity;
    }

    @Override
    public void onConnect(LightPack lightPack) {
        activity.onConnect(lightPack);
    }

    @Override
    public void onConnectFailure() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.statusMessage.setText(R.string.connection_error);
                activity.pullToRefreshLayout.setRefreshComplete();

                activity.enableApplicationUi(false);
                activity.onLightpackDisconnect();
            }
        });
    }

    @Override
    public void onError(LightPackCommand command, Exception e) {
        onConnectFailure();
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
                activity.lightSwitch.setVisibility(View.VISIBLE);
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

    @Override
    public void onProfiles(final String[] profiles) {
        final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) activity.profileSpinner.getAdapter();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.addAll(profiles);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onProfileSelection(final String profile) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < activity.profileSpinner.getCount(); i++)
                    if (profile.equalsIgnoreCase(activity.profileAdapter.getItem(i).toString()))
                        activity.profileSpinner.setSelection(i);
            }
        });

        activity.lightPack.requestCurrentMode();
        activity.lightPack.requestBrightness();
        activity.lightPack.requestGamma();
        activity.lightPack.requestSmoothness();
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

        activity.lightPack.requestLedColours();
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

        activity.lightPack.requestFps();
    }

    @Override
    public void onBrightnessUpdate(final int brightness) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.brightnessSeekbar.setProgress(brightness);
            }
        });
    }

    @Override
    public void onGammaUpdate(final int gamma) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.gammaSeekbar.setProgress(gamma);
            }
        });
    }

    @Override
    public void onSmoothnessUpdate(final int smoothness) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.smoothnessSeekbar.setProgress(smoothness);
                activity.lightPackAnimator.deviceSmoothness(smoothness);
            }
        });
    }

    @Override
    public void onFpsUpdate(final double fps) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.fps.setText(activity.getString(R.string.fps) + " " + fps + activity.getString(R.string.api_update_notice));
            }
        });
    }

    @Override
    public void onLedCountUpdate(int leds) {
        activity.lightPack.setLedCount(leds);
    }

    @Override
    public void onLedColourUpdate(int led, int red, int green, int blue) {
//        final int c = Color.argb(255, red, green, blue);
//
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (c != activity.colourPicker.getColor())
//                    activity.colourPicker.setColor(c);
//
//                activity.changeActionBarColour(c);
//            }
//        });
    }
}
