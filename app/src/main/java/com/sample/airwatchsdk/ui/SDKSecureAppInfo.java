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
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.airwatch.sdk.SecureAppInfo;
import com.sample.airwatchsdk.R;
import com.sample.main.ApiAdapter;
import com.sample.main.AppBaseActivity;

import java.util.HashMap;
import java.util.Map;

public class SDKSecureAppInfo extends AppBaseActivity{
    ApiAdapter mainList = null;
    SDKManager awSDKManager = null;
    SecureAppInfo appInfo = null;
    boolean serviceError = false;
    Map<String, String> map = new HashMap<>();
    String[] title_array = null;
    ListView listView = null;

    //region API Name Constants
    private static final String USERNAME = "Enrollment Username";
    private static final String PASSWORD = "Enrollment Password";
    private static final String HMAC_TOKEN = "HMAC Token";
    private static final String HMAC_TOKEN_REREGISTER = "HMAC Token Reregister";
    private static final String USER_ID = "User ID";
    //endregion

    public void setup(){
        if (awSDKManager == null){
            try{
                awSDKManager = SDKManager.init(getApplicationContext());
                serviceError = false;
            }catch (AirWatchSDKException e){
                serviceError = true;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_appinfo);
        title_array = getResources().getStringArray(R.array.secure_app_info_list_items);
        mainList = new ApiAdapter(this, R.array.secure_app_info_list_items, map);
        listView = (ListView) findViewById(R.id.apilist);
        listView.setAdapter(mainList);
    }

    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listView != null && mainList != null) {
                    mainList.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SDKManager.isServiceConnected()){
            setup();
            for (String s : title_array) {
                getResponse(s);
            }
        }
    }

    public void getResponse(final String title) {
        new Thread(new Runnable() {
            public void run() {
                String response = null;
                try {
                    //region Initialize SDK manager, profile, policies and other variables
                    if (serviceError){
                        awSDKManager = SDKManager.init(getApplicationContext());
                    }
                    appInfo = awSDKManager.getSecureAppInfo();
                    //endregion
                    //region Handle cases
                    switch (title) {
                        //region Username
                        case USERNAME:
                            response = "" + appInfo.getEnrollmentUsername();
                            break;
                        //endregion
                        //region Password
                        case PASSWORD:
                            response = "" + appInfo.getEnrollmentPassword();
                            break;
                        //endregion
                        //region HMAC Token
                        case HMAC_TOKEN:
                            response = "" + appInfo.register("HARDCODEDAPPID", false);
                            break;
                        //endregion
                        //region HMAC Token Reregister
                        case HMAC_TOKEN_REREGISTER:
                            response = "" + appInfo.register("TESTAPPID", true);
                            break;
                        //endregion
                        //region User ID
                        case USER_ID:
                            response = "" + appInfo.getUserID();
                            break;
                        //endregion
                    }
                    //endregion
                    serviceError = false;
                } catch (AirWatchSDKException e) {
                    response = "not available";
                    serviceError = true;
                } finally {
                    //region Update result
                    map.put(title, response);
                    updateUI();
                    //endregion
                }
            }
        }).start();
    }

    public void handleClick(View v) {
        TextView title = (TextView) v.getTag();
        final String code = title.getText().toString();
        getResponse(code);
    }
}
