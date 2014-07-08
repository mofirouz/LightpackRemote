package com.mofirouz.lightpackremoteplus;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface DevicePrefs {
    String getDeviceAddress();
    int getDevicePort();
    String getApiKey();
}
