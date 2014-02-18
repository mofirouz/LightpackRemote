package com.mofirouz.lightpackremote;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface DevicePrefs {
    String getDeviceAddress();
    int getDevicePort();
    String getApiKey();
}
