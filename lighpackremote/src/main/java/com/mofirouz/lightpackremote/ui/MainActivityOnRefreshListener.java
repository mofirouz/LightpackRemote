package com.mofirouz.lightpackremote.ui;

import android.view.View;

import com.mofirouz.lightpackremote.Main;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class MainActivityOnRefreshListener implements OnRefreshListener {
    private final Main activity;

    public MainActivityOnRefreshListener(Main activity) {
        this.activity = activity;
    }

    @Override
    public void onRefreshStarted(View view) {
        activity.refreshState();

    }
}
