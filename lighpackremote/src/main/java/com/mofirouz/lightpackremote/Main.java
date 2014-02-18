package com.mofirouz.lightpackremote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.mofirouz.lightpackremote.jlightpack.ColourUtil;
import com.mofirouz.lightpackremote.jlightpack.LightPack;
import com.mofirouz.lightpackremote.jlightpack.LightPackConnector;
import com.mofirouz.lightpackremote.jlightpack.LightPackResponseListener;
import com.mofirouz.lightpackremote.ui.DeviceResponseListener;
import com.mofirouz.lightpackremote.ui.MainActivityOnRefreshListener;
import com.mofirouz.lightpackremote.ui.UiController;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

@EActivity(R.layout.activity_main)
public class Main extends Activity {
    private static int actionBarDefaultColor = Color.BLACK;
    private static int actionBarOffColor;
    private static int actionBarOnColor;

    public LightPackResponseListener deviceListener;
    public volatile LightPack lightPack;

    public UiController uiController;
    public OnRefreshListener refreshListener;
    public Menu menu;
    public Switch lightSwitch;
    public ArrayAdapter<CharSequence> spinnerAdapter;
    public ArrayAdapter<CharSequence> profileAdapter;

    @Pref
    DevicePrefs_ prefs;

    @ViewById
    public DrawerLayout drawerLayout;

    @ViewById
    public PullToRefreshLayout pullToRefreshLayout;
    @ViewById
    public TextView statusMessage;
    @ViewById
    public RelativeLayout statusMessageLayout;
    @ViewById
    public RelativeLayout mainLayout;
    @ViewById
    public RelativeLayout moodlampLayout;
    @ViewById
    public RelativeLayout ambilightLayout;

    @ViewById
    public Spinner profileSpinner;
    @ViewById
    public Spinner modeSpinner;
    @ViewById
    public SeekBar brightnessSeekbar;
    @ViewById
    public SeekBar gammaSeekbar;
    @ViewById
    public SeekBar smoothnessSeekbar;
    @ViewById
    public TextView fps;

    @ViewById
    public ColorPicker colourPicker;
    @ViewById
    SaturationBar colourSaturationPar;


    @Background
    public void start() {
        enableDrawer(false);

        deviceListener = new DeviceResponseListener(this);
        if (!isDeviceSetup()) {
            noDeviceSetup();
            return;
        }

        if (isLightpackConnected())
            return;

        if (!prefs.getApiKey().get().isEmpty())
            LightPackConnector.connect(prefs.getDeviceAddress().get(), prefs.getDevicePort().get(), prefs.getApiKey().get(), deviceListener);
        else
            LightPackConnector.connect(prefs.getDeviceAddress().get(), prefs.getDevicePort().get(), deviceListener);
    }

    @Background
    public void onConnect(LightPack lightPack) {
        this.lightPack = lightPack;
        //setup initial state before binding UI listeners to avoid double-calls...
        refreshState();

        uiController = new UiController(this, lightPack);
        uiController.setupListeners();
    }

    public boolean isLightpackConnected() {
        return lightPack != null && isDeviceSetup();
    }

    public void onLightpackDisconnect() {
        try {
            LightPackConnector.disconnect(lightPack);
        } catch (Exception e) {}
        lightPack = null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lightSwitch.setChecked(false);
                lightSwitch.setVisibility(View.INVISIBLE);
            }
        });
    }

    public boolean isDeviceSetup() {
        return (!(prefs.getDevicePort().get() == 0 || prefs.getDeviceAddress().get() == null | prefs.getDeviceAddress().get().isEmpty()));
    }

    @UiThread
    public void refreshState() {
        if (!isLightpackConnected()) {
            start();
        } else {
            refreshDeviceState();

            this.menu.findItem(R.id.menu_lightSwitch).setVisible(true);
            pullToRefreshLayout.setRefreshComplete();

            randomizeActionBarColor(false);
        }

    }

    @UiThread
    public void noDeviceSetup() {
        statusMessage.setText(R.string.no_device);
        enableApplicationUi(false);

        pullToRefreshLayout.setRefreshComplete();
    }

    @Background
    public void refreshDeviceState() {
        if (!isLightpackConnected())
            return;

        lightPack.requestLedCount();
        lightPack.requestLightStatus();
        lightPack.requestAllProfiles();
        lightPack.requestCurrentProfile();
        lightPack.requestBrightness();
        lightPack.requestGamma();
        lightPack.requestSmoothness();
    }

// --------------------

    @UiThread
    public void enableDrawer(boolean enabled) {
        if (enabled)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        else
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @UiThread
    public void enableApplicationUi(boolean enable) {
        if (enable) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(actionBarOnColor));
            statusMessageLayout.setVisibility(View.INVISIBLE);
            mainLayout.setVisibility(View.VISIBLE);
            lightSwitch.setVisibility(View.VISIBLE);
            enableDrawer(true);
        } else {
            getActionBar().setBackgroundDrawable(new ColorDrawable(actionBarOffColor));
            statusMessage.setTextColor(Color.GRAY);
            statusMessageLayout.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.INVISIBLE);
            lightSwitch.setChecked(false);
            enableDrawer(false);
        }
    }

    private void randomizeActionBarColor(boolean bypassCheck) {
        if (!bypassCheck)
            if (!isLightpackConnected() || !lightSwitch.isChecked())
                return;

        actionBarOffColor = ColourUtil.generateColor();
        actionBarOnColor = ColourUtil.lighter(actionBarOffColor, 1.5);
        updateActionBarColour();
    }

    @UiThread
    public void changeActionBarColour(int color) {
        actionBarOnColor = color;
        actionBarOffColor = ColourUtil.darker(actionBarOnColor, 1.5);

        updateActionBarColour();
    }

    private void updateActionBarColour() {
        getActionBar().setBackgroundDrawable(new ColorDrawable(actionBarOnColor));

        DefaultHeaderTransformer transformer = (DefaultHeaderTransformer) pullToRefreshLayout.getHeaderTransformer();
        TextView textView = (TextView) transformer.getHeaderView().findViewById(uk.co.senab.actionbarpulltorefresh.library.R.id.ptr_text);
        ViewGroup contentLayout = (ViewGroup) transformer.getHeaderView().findViewById(uk.co.senab.actionbarpulltorefresh.library.R.id.ptr_content);
        transformer.setProgressBarColor(actionBarOffColor);
        transformer.getHeaderView().setBackground(new ColorDrawable(actionBarOnColor));
        textView.setBackground(new ColorDrawable(actionBarOnColor));
        textView.setTextColor(actionBarOffColor);
        contentLayout.setBackground(new ColorDrawable(actionBarOnColor));
    }

// --------------------

    @AfterViews
    public void afterViews() {
        refreshListener = new MainActivityOnRefreshListener(this);
        ActionBarPullToRefresh.from(this)
                            .allChildrenArePullable()
                            .listener(refreshListener)
                            .setup(pullToRefreshLayout);

        pullToRefreshLayout.setRefreshing(true);

        randomizeActionBarColor(true);

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.lights_modes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(spinnerAdapter);

        profileAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        profileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profileSpinner.setAdapter(profileAdapter);

        colourPicker.setShowOldCenterColor(false);
        colourPicker.addSaturationBar(colourSaturationPar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        MenuItem lightSwitchMenu = this.menu.findItem(R.id.menu_lightSwitch);
        LinearLayout layout = (LinearLayout) lightSwitchMenu.getActionView();
        lightSwitch = (Switch) layout.findViewById(R.id.lightSwitch);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        start();
        return true;
    }

    @Background
    @OptionsItem(R.id.action_settings)
    void showSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity_.class);
        startActivity(intent);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (isLightpackConnected()) {
            try {
                onLightpackDisconnect();
            } catch (Exception e) {}
        }
        finish();
    }
}
