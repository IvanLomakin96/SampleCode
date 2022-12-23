/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.core.task.TaskResult;

import com.airwatch.sdk.configuration.BaseConfiguration;
import com.airwatch.sdk.configuration.IOnConfigurationChangeListener;
import com.airwatch.sdk.configuration.SDKConfiguration;
import com.airwatch.sdk.configuration.SDKConfigurationImpl;
import com.airwatch.sdk.context.ISdkFetchSettingsListener;
import com.airwatch.sdk.context.SDKContext;
import com.airwatch.sdk.context.SDKContextManager;
import com.airwatch.task.IFutureCallback;
import com.airwatch.util.Logger;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class SDKConfigurationFetch extends AppBaseActivity implements IOnConfigurationChangeListener{

    private TextView mTextViewResult;
    private EditText mEditText, configVersionTxt;
    private ProgressDialog mDialog;
    private SDKConfiguration mSdkConfiguration;
    private static final String TAG = SDKConfigurationFetch.class.getName();

    private final ISdkFetchSettingsListener mISdkFetchSettingsListener = new ISdkFetchSettingsListener() {
        @Override
        public void onSuccess(final BaseConfiguration configuration) {
            Logger.d("SDKFetchSettingsHelper" ,"listener success notify");

            hideProgress();
            mTextViewResult.setText("SDK fetch from server success:" + "\n" + configuration.toString());
        }

        @Override
        public void onFailure(final TaskResult taskResult) {
            Logger.d("SDKFetchSettingsHelper" ,"failed");
            hideProgress();
            mTextViewResult.setText("SDK fetch from server fail: " + "\n" + taskResult.getPayload().toString());
        }
    };

    private void hideProgress(){
        if (mDialog != null){
            mDialog.dismiss();
        }
    }

    private void showProgress(){
        if (mDialog == null){
            mDialog = new ProgressDialog(this);
            mDialog.setMessage(getString(R.string.loading_wait));
        }
        mDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdkconfiguration_fetch);
        mTextViewResult = findViewById(R.id.tv_sdk_output);
        mEditText = findViewById(R.id.config_type);
        configVersionTxt = findViewById(R.id.config_version);

        addListenerOnButton();
        SDKContext sdkContext = SDKContextManager.getSDKContext();
        if (sdkContext.getCurrentState() == SDKContext.State.IDLE) {
            Toast.makeText(getApplicationContext(), "The sdk context is not init", Toast.LENGTH_LONG).show();
            return;
        }
        mSdkConfiguration = sdkContext.getSDKConfiguration();
        mSdkConfiguration.registerOnConfigurationChangeListener(this);
    }


    private void addListenerOnButton() {
        Button btn_get_sdk_settings_from_db = findViewById(R.id.get_sdk_settings_db);
        Button btn_get_sdk_settings_from_server = findViewById(R.id.fetch_sdk_settings_server);
        Button btn_test_geofence = findViewById(R.id.test_geofence);
        Button btn_app_setting = findViewById(R.id.fetch_app_settings_server);
        Button btn_fetch_compliance_settings = findViewById(R.id.fetch_compliance_settings);

        btn_fetch_compliance_settings.setOnClickListener(v -> {
            Log.d(TAG,"Fetch & Execute Compliance button clicked");
            SDKContextManager.getSDKContext().fetchAndExecuteComplianceSettings(new IFutureCallback<Boolean>() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG,"fetchAndExecuteComplianceSettings exception e"+e.getMessage());
                }

                @Override
                public void onSuccess(Boolean result) {
                    Log.i(TAG,"fetchAndExecuteComplianceSettings result "+result);
                }
            });
        });

        btn_app_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                String configType = (!TextUtils.isEmpty(mEditText.getText()))? mEditText.getText().toString():"32";
                String configVersion = (!TextUtils.isEmpty(configVersionTxt.getText()))? configVersionTxt.getText().toString():"1";
                SDKContextManager.getSDKContext().fetchAppSettingsWithConfigTypeAndVersion(mISdkFetchSettingsListener, configType, configVersion);
            }
        });

        btn_get_sdk_settings_from_server.setOnClickListener(view -> {
            showProgress();
            SDKContextManager.getSDKContext().fetchSDKSettings(mISdkFetchSettingsListener);
        });

        btn_get_sdk_settings_from_db.setOnClickListener(view -> fetchSdkSettingsFromDb());
        btn_test_geofence.setOnClickListener(view -> testGeoFence());
    }


    private void testGeoFence() {
        int count = 0;
        try {
            Log.d("DATABASE", SDKConfigurationImpl.createConfigurationFromDatabase(this).toString());
            ArrayList sdkGeofenceAreas = SDKConfigurationImpl.createConfigurationFromDatabase(this).getGeoFenceAreas();
            //iterate through sdkGeofenceAreaList
            String resultString = "";
            Iterator itr = sdkGeofenceAreas.iterator();

            while (itr.hasNext()) {
                resultString = resultString + "\r\n" + "\r\n" + itr.next().toString();
                count++;
            }

            resultString = resultString + "\r\n\r\n" + "Geofence Area Count: " + count;
            mTextViewResult.setText(resultString);
        }catch (NumberFormatException e) {
            System.err.println("Geofencing not enabled in console");
            mTextViewResult.setText("Geofencing not enabled in console");
        }
    }

    private void fetchSdkSettingsFromDb() {
        final SDKContext sdkContext = SDKContextManager.getSDKContext();

        if (sdkContext.getCurrentState() == SDKContext.State.CONFIGURED) {
            mTextViewResult.setText((mSdkConfiguration != null) ? mSdkConfiguration.toString() : "nothing returned");
        } else {
            mTextViewResult.setText("SDK Configuration is not initialized");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSdkConfiguration.unregisterOnConfigurationChangeListener(this);
    }

    @Override
    public void onConfigurationChanged(Set<String> keySet) {
        String res = "";
        for (String ele : keySet) {
            res += ele + "; ";
        }
        Toast.makeText(getApplicationContext(), "The change key set is: " + res, Toast.LENGTH_LONG).show();
    }
}
