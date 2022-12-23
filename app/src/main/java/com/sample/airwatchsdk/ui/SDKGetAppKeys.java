/*
 * AirWatch Android Software Development Kit
 * Copyright (c) 2015 AirWatch. All rights reserved.
 *
 * Unless required by applicable law, this Software Development Kit and sample
 * code is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. AirWatch expressly disclaims any and all
 * liability resulting from User's use of this Software Development Kit. Any
 * modification made to the SDK must have written approval by AirWatch.
*/
package com.sample.airwatchsdk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.airwatch.sdk.AirWatchSDKConstants;
import com.airwatch.util.Logger;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SDKGetAppKeys extends AppBaseActivity {
    EditText appKey;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_app_keys);
        appKey = (EditText) findViewById(R.id.appKeyEditText);
        ArrayList<String> packageName = getAllPackageName();
        initializeListView(packageName);
    }

    private void initializeListView(final ArrayList<String> packageList) {
        mListView = (ListView) findViewById(R.id.appKeylistView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.main_list_item,
                R.id.main_list_item_text, packageList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                showAlertDialog(getKey(packageList.get(position)));
            }
        });
    }

    private ArrayList<String> getAllPackageName() {
        ArrayList<String> packageName = new ArrayList<String>();
        PackageManager pm = this.getPackageManager();
        List<PackageInfo> packageList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        for (PackageInfo packageItem : packageList) {
            packageName.add(packageItem.packageName);
        }
        Collections.sort(packageName);
        return packageName;
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SDKGetAppKeys.this);
        builder.setMessage(message)
                .setTitle("APP Key Base64")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog promptDialog = builder.create();
        promptDialog.show();
    }

    private String getKey(String packageName) {
        final PackageManager pm = this.getPackageManager();
        try {
            Signature[] sigArray = pm.getPackageInfo(packageName, 64).signatures;
            PublicKey appKey = CertificateFactory.getInstance("X509").
                    generateCertificate(new ByteArrayInputStream
                            (sigArray[0].toByteArray())).getPublicKey();
            Log.e(AirWatchSDKConstants.TAG, Base64.encodeToString(sigArray[0].toByteArray(), Base64.DEFAULT));
            return Base64.encodeToString(appKey.getEncoded(), Base64.DEFAULT);
        } catch (Exception e) {
            Logger.e("Unable to get package name");
        }
        return "Unable to find the key";
    }

    public void getPublicKey(View view) {
        String key = getKey(appKey.getText().toString());
        showAlertDialog(key);
    }
}



