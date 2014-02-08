package com.mofirouz.lightpackremote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.mofirouz.lightpackremote.jlightpack.LightPack;
import com.mofirouz.lightpackremote.jlightpack.LightPackConnector;
import com.mofirouz.lightpackremote.jlightpack.LightPackResponseListener;
import com.mofirouz.lightpackremote.ui.DeviceResponseListener;
import com.mofirouz.lightpackremote.ui.MainActivityOnRefreshListener;
import com.mofirouz.lightpackremote.ui.UiController;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

@EActivity(R.layout.activity_main)
public class Main extends Activity {
    public LightPackResponseListener deviceListener;
    public LightPack lightPack;

    public UiController uiController;
    public OnRefreshListener refreshListener;
    public Menu menu;
    public Switch lightSwitch;
    public ArrayAdapter<CharSequence> spinnerAdapter;

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
    public Spinner modeSpinner;

    @Background
    public void start() {
        enableDrawer(false);

        deviceListener = new DeviceResponseListener(this);
        connectToLightPack(deviceListener);

        //setup initial state before binding UI listeners to avoid double-calls...
        refreshState();

        uiController = new UiController(this, lightPack);
        uiController.setupListeners();
    }

    public void connectToLightPack(LightPackResponseListener listener) {
        if (!isDeviceSetup()) {
            noDeviceSetup();
            return;
        }

        if (isLightpackConnected())
            return;

        lightPack = LightPackConnector.connect(prefs.getDeviceAddress().get(), prefs.getDevicePort().get(), listener);
    }

    public boolean isLightpackConnected() {
        return lightPack != null;
    }

    public boolean isDeviceSetup() {
        return (!(prefs.getDevicePort().get() == 0 || prefs.getDeviceAddress().get() == null | prefs.getDeviceAddress().get().isEmpty()));
    }

    @UiThread
    public void refreshState() {
        if (!isLightpackConnected()) {
            pullToRefreshLayout.setRefreshComplete();
            return;
        }

        lightPack.requestLightStatus();
        this.menu.findItem(R.id.menu_lightSwitch).setVisible(true);
        pullToRefreshLayout.setRefreshComplete();
    }

    @UiThread
    public void noDeviceSetup() {
        statusMessage.setText(R.string.no_device);
        enableApplicationUi(false);

        pullToRefreshLayout.setRefreshComplete();
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
            statusMessageLayout.setVisibility(View.INVISIBLE);
            mainLayout.setVisibility(View.VISIBLE);
            enableDrawer(true);
        } else {
            statusMessage.setTextColor(Color.GRAY);
            statusMessageLayout.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.INVISIBLE);
            enableDrawer(false);
        }
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

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.lights_modes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(spinnerAdapter);
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
                LightPackConnector.disconnect(lightPack);
            } catch (Exception e) {}
        }
        finish();
    }
}
