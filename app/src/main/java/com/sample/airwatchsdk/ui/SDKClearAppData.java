/*
 * AirWatch Android Software Development Kit
 * Copyright (c) 2013 AirWatch. All rights reserved.
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
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sample.airwatchsdk.AirWatchSDKSampleApp;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class SDKClearAppData extends AppBaseActivity {

	private static int value = 0;
	private TextView textCounterValue;
	private TextView textInfo;
	private Button buttonUpCounter;
	private String infoStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clear_app_data);
		
		IntentFilter filter = new IntentFilter(AirWatchSDKSampleApp.CLEAR_APP_DATA_BROADCAST);
		LocalBroadcastManager.getInstance(this).registerReceiver(onClearAppDataCommandReceived, filter);
		
		textCounterValue = (TextView) findViewById(R.id.textCounterValue);
		textCounterValue.setText(new StringBuilder().append(value));
		
		textInfo = (TextView)findViewById(R.id.textInfo);
		infoStr = textInfo.getText().toString();
		textInfo.setText(infoStr + value);
		
		buttonUpCounter = (Button) findViewById(R.id.buttonUpCounter);
		buttonUpCounter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				value++;
				textCounterValue.setText(new StringBuilder().append(value));
				textInfo.setText(infoStr + value);
			}
		});		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onClearAppDataCommandReceived);
	}
	
	private BroadcastReceiver onClearAppDataCommandReceived = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent i) {
			clearAppData();
		}
	};
	
	public void clearAppData() {
		value = 0;
		textCounterValue.setText(new StringBuilder().append(value));
		textInfo.setText("Clear AppData command received. Resetting the counter");
	}
}
