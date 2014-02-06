package com.mofirouz.lightpackremote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
    public final OnRefreshListener refreshListener = new MainActivityOnRefreshListener();
    public Menu menu;
    public Switch lightSwitch;

    @Pref
    DevicePrefs_ prefs;

    @ViewById
    public PullToRefreshLayout pullToRefreshLayout;
    @ViewById
    public TextView statusMessage;

    @Background
    public void start() {
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
        if (!isLightpackConnected())
            return;

        lightPack.requestLightStatus();
        this.menu.findItem(R.id.menu_lightSwitch).setVisible(true);
    }

    @UiThread
    public void noDeviceSetup() {
        statusMessage.setText(R.string.no_device);
        statusMessage.setTextColor(Color.RED);
        statusMessage.setVisibility(View.VISIBLE);
    }

// --------------------

    @AfterViews
    public void afterViews() {
        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(refreshListener)
                .setup(pullToRefreshLayout);
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
