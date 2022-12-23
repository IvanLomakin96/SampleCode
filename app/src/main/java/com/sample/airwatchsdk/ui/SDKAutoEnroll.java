/*
 * AirWatch Android Software Development Kit
 * Copyright (c) 2017 AirWatch. All rights reserved.
 *
 * Unless required by applicable law, this Software Development Kit and sample
 * code is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. AirWatch expressly disclaims any and all
 * liability resulting from User's use of this Software Development Kit. Any
 * modification made to the SDK must have written approval by AirWatch.
 */
package com.sample.airwatchsdk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.airwatch.sdk.context.SDKContextManager;
import com.airwatch.storage.SDKSecurePreferencesKeys;
import com.sample.airwatchsdk.AirWatchSDKIntentService;
import com.sample.airwatchsdk.AsyncSDKManager;
import com.sample.airwatchsdk.R;

public class SDKAutoEnroll  extends AppCompatActivity implements View.OnClickListener, AsyncSDKManager.Invoker<Bundle> {
    private EditText server, group, user, pass;
    private Button enroll;
    private AsyncSDKManager manager;
    private TextView result;
    private MyReceiver myReceiver = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_enroll);

        enroll = (Button) findViewById(R.id.enroll);
        server = (EditText) findViewById(R.id.server);
        group = (EditText) findViewById(R.id.group);
        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.password);

        result = (TextView) findViewById(R.id.result);

        enroll.setOnClickListener(this);

        this.manager = new AsyncSDKManager();

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, getIntentFilter());
        prepareCredentials();
    }

    private void prepareCredentials() {
        SharedPreferences securePreferences = SDKContextManager.getSDKContext().getSDKSecurePreferences();
        if(securePreferences != null) {
            server.setText(securePreferences.getString(SDKSecurePreferencesKeys.HOST,""));
            group.setText(securePreferences.getString(SDKSecurePreferencesKeys.GROUP_ID,""));
            user.setText(securePreferences.getString(SDKSecurePreferencesKeys.USERNAME,""));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    @Override
    public void onClick(View v) {
        validateInput();
        manager.invoke(this);
    }

    private void validateInput() {
        if(server.getText() == null) {
            server.setText("");
        }
        if(group.getText() == null) {
            group.setText("");
        }
        if(user.getText() == null) {
            user.setText("");
        }
        if(pass.getText() == null) {
            pass.setText("");
        }
    }

    @Override
    public Bundle invoke(SDKManager sdk) throws AirWatchSDKException {
        boolean res = sdk.autoEnroll(server.getText().toString()
                , group.getText().toString()
                , user.getText().toString()
                , pass.getText().toString());
        Bundle resultBundle = new Bundle();
        resultBundle.putBoolean("res", res);
        return resultBundle;
    }

    @Override
    public void onResult(Bundle result, AirWatchSDKException exception) {
        if (result != null) {
            Toast.makeText(this, "call made to agent = " + result.getBoolean("res"), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "exception = " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private IntentFilter getIntentFilter() {
        return new IntentFilter(AirWatchSDKIntentService.ENROLLMENT_FINISH);
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AirWatchSDKIntentService.ENROLLMENT_FINISH.equals(intent.getAction())) {
                String result_text = "Auto Enrollment result is: " +
                        intent.getBooleanExtra(AirWatchSDKIntentService.ENROLLMENT_RESULT, false);
                result.append(result_text);
                result.append(System.getProperty("line.separator"));
            }
        }
    }
}
