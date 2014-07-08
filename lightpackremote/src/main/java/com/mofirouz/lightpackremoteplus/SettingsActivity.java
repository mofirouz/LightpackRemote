package com.mofirouz.lightpackremoteplus;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.NumberRule.NumberType;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mofirouz.lightpackremoteplus.R.id;
import com.mofirouz.lightpackremoteplus.R.layout;

@EActivity(layout.settings_layout)
public class SettingsActivity extends Activity implements ValidationListener {
    private Validator validator;

    @Pref
    DevicePrefs_ prefs;

    Menu menu;

    @Required (order = 1)
    @ViewById
    EditText ip_field;

    @Required (order = 2)
    @NumberRule (order = 3, type = NumberType.INTEGER, message = "Invalid Device Port")
    @ViewById
    EditText port_field;

    @ViewById
    EditText apikey_field;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        this.menu = menu;
        return true;
    }

    @AfterViews
    public void onCreate() {
        validator = new Validator(this);
        validator.setValidationListener(this);

        ip_field.setText(prefs.getDeviceAddress().get());
        apikey_field.setText(prefs.getApiKey().get());
        if (prefs.getDevicePort().get() != 0)
            port_field.setText(String.valueOf(prefs.getDevicePort().get()));
    }

    @Background
    @OptionsItem(id.saveSettings)
    void saveSettings() {
        validator.validate();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Main_.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onValidationSucceeded() {
        prefs.getDeviceAddress().put(ip_field.getText().toString().trim());
        prefs.getDevicePort().put(Integer.parseInt(port_field.getText().toString().trim()));
        prefs.getApiKey().put(apikey_field.getText().toString().trim());

        onBackPressed();
    }

    @UiThread
    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();
        failedView.requestFocus();
        ((EditText) failedView).setError(message);
    }
}