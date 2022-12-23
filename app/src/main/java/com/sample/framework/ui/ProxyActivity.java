package com.sample.framework.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.airwatch.gateway.GatewayException;
import com.airwatch.gateway.GatewayManager;
import com.sample.main.AppBaseActivity;

/**
 * Authored by
 * Subham Tyagi
 * on 3/14/2015.
 * <p/>
 * TODO: Add a class header comment!
 */
public abstract class ProxyActivity extends AppBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        try {
            GatewayManager gatewayManager = GatewayManager.getInstance(getApplicationContext());
            if (gatewayManager.isRunning()) {

            } else {
                launchRingDialog();
            }

        } catch (GatewayException e) {
            Log.d("TestApp", e.getMessage());
        }
    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, "Please wait ...",
                "Starting proxy ...", true);
        ringProgressDialog.setCancelable(true);
    }

    protected abstract void showUI();

}
