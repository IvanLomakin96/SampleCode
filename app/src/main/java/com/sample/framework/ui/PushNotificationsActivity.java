/*
 * Copyright (c) 2016. AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.notification.PushNotificationManager;
import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.util.Logger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sample.airwatchsdk.R;
import com.sample.notification.FCMMessagingService;


public class PushNotificationsActivity extends Activity {

    private final String AGENT_SENDER_ID = "450360282757";
    private EditText senderId;
    private TextView regId;
    private Button registerBtn;
    private static final String TAG = "PushNotificationsActivi";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notifications);
        senderId = findViewById(R.id.sender_id_field);
        senderId.setText(AGENT_SENDER_ID);
        regId = findViewById(R.id.reg_id);
        registerBtn = findViewById(R.id.register_btn);
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);

    }

    public void onRegisterButtonPressed(View view) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.getResult() != null && !TextUtils.isEmpty(task.getResult().getToken())) {
                String token = task.getResult().getToken();
                regId.setText(String.format(getString(R.string.registration_id), token));
                try {
                    PushNotificationManager.getInstance(this.getApplicationContext()).registerToken(token);
                } catch (AirWatchSDKException e) {
                    Logger.w(TAG, "onRegisterButtonPressed: Registering FCM token", e);
                    sharedPreferences.edit().putString(FCMMessagingService.FCM_PENDING_REGISTRATION_TOKEN, token).apply();
                }
            } else if (task.getException() != null) {
                regId.setText(String.format(getString(R.string.error_while_registering), task.getException().getMessage()));
            }

            registerBtn.setEnabled(true);
        });
    }
}
