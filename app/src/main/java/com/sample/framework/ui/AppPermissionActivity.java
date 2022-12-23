/*
 * Copyright (c) 2018 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airwatch.util.ApplicationInfoUtility;
import com.airwatch.util.ApplicationInformation;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class AppPermissionActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_permission);

        final EditText etPackageName = findViewById(R.id.etPackageName);
        Button btnGetAppInfo = findViewById(R.id.btnGetAppInfo);
        final TextView tvResult = findViewById(R.id.tvResult);

        btnGetAppInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = etPackageName.getText().toString().trim();
                ApplicationInformation appInfo = ApplicationInfoUtility.getApplicationInformation(packageName);

                String result = "Result:\n" + "App Installed: " + appInfo.getAppInstalled() + "\n"
                        + "App Type: " + appInfo.getApplicationType().toString() + "\n"
                        + "Signature Validity: " + appInfo.getSignatureValidity().toString();

                tvResult.setText(result);
            }
        });
    }
}