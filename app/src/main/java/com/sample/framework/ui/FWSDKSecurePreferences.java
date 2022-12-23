/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 *  This product is protected by copyright and intellectual property laws in  the United Sta tes and other countries as well as by international treaties.
 *  AirWatch products may be covered by one or more patents listed at
 *  http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.airwatch.sdk.context.SDKContextManager;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FWSDKSecurePreferences extends AppBaseActivity {
    @BindView(R.id.key)
    EditText preferenceKey;

    @BindView(R.id.value)
    EditText preferenceValue;

    @BindView(R.id.list)
    ListView keyValueList;


    private SharedPreferences sdkSecurePreferences;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwsecure_preferences);
        ButterKnife.bind(this);
        sdkSecurePreferences = SDKContextManager.getSDKContext().getSDKSecurePreferences();
        Map<String, ?> stringMap = sdkSecurePreferences.getAll();
        List<String> keys = new ArrayList<>(stringMap.keySet());
        adapter = new ListAdapter(this, keys);
        adapter.addData(stringMap);
        keyValueList.setAdapter(adapter);

    }

    @OnClick(R.id.add_button)
    public void onAddKey() {
        String key = preferenceKey.getText().toString();
        String value = preferenceValue.getText().toString();
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            sdkSecurePreferences.edit().putString(key, value).apply();
            Map<String, ?> stringMap = sdkSecurePreferences.getAll();
            adapter.addData(stringMap);
            preferenceKey.setText("");
            preferenceValue.setText("");
            Toast.makeText(getApplicationContext(), "Value Saved", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter both and value", Toast.LENGTH_LONG).show();
        }
    }

}
