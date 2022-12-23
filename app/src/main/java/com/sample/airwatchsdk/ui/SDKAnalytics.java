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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.airwatch.sdk.configuration.SDKConfigurationKeys;
import com.airwatch.sdk.context.SDKContextManager;
import com.airwatch.sdk.profile.AnalyticsEventQueue;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class SDKAnalytics extends AppBaseActivity {

	boolean serviceError = false;
	boolean analyticsResult = false;
	SDKManager awSDKManager = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analytics);
		
		final AnalyticsEventQueue analyticsQueue = new AnalyticsEventQueue();

		new Thread(new Runnable() {
			public void run() {
				try {
					awSDKManager = SDKManager.init(getApplicationContext());
				} catch (AirWatchSDKException e) {
					serviceError = true;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							String reason = "AirWatch SDK Connection Problem. Please make sure AirWatch MDM Agent 4.0 above is Installed";
							Toast toast = Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG);
							toast.show();
						}
					});
				}
			}
		}).start();

		final EditText editAnyKey = (EditText) findViewById(R.id.editAnyKey);
		final EditText editAnyValue  = (EditText) findViewById(R.id.editAnyValue);

		Button buttonAnyQueue = (Button) findViewById(R.id.buttonAnyQueue);
		buttonAnyQueue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String anyKey = editAnyKey.getText().toString();
				String anyValue = editAnyValue.getText().toString();
				if (anyKey!=null && anyValue!=null) {
					boolean result = analyticsQueue.queueAnalyticsEvent(anyKey, anyValue);
					if(result) {
						Toast toast = Toast.makeText(getApplicationContext(), "Analytics Queued Successfully", Toast.LENGTH_LONG);
						toast.show();
					} else {
						Toast toast = Toast.makeText(getApplicationContext(), "Analytics Queueing Failure", Toast.LENGTH_LONG);
						toast.show();
					}
				}
			}
		});
		
		Button buttonAnyReset = (Button) findViewById(R.id.buttonAnyReset);
		buttonAnyReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String anyKey = editAnyKey.getText().toString();
				String anyValue = editAnyValue.getText().toString();
				if (anyKey!=null && anyValue!=null) {
					boolean result = analyticsQueue.resetAnalyticsEventQueue();
					if(result) {
						Toast toast = Toast.makeText(getApplicationContext(), "Analytics Reset Successfully", Toast.LENGTH_LONG);
						toast.show();
					} else {
						Toast toast = Toast.makeText(getApplicationContext(), "Analytics Reset Failure", Toast.LENGTH_LONG);
						toast.show();
					}
				}
			}
		});
		
		Button buttonAnyReport = (Button) findViewById(R.id.buttonAnyReport);
		buttonAnyReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError || awSDKManager==null || !SDKContextManager.getSDKContext().getSDKConfiguration()
						.getBooleanValue(SDKConfigurationKeys.TYPE_ANALYTICS_SETTINGS, SDKConfigurationKeys.ENABLE_ANALYTICS)) {
					Toast.makeText(getApplicationContext(), "SDK setting disabled or service error.", Toast.LENGTH_LONG).show();
					return;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							analyticsResult = awSDKManager.reportAnalytics(analyticsQueue);
						} catch (AirWatchSDKException e) {
							analyticsResult = false;
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(analyticsResult) {
									Toast toast = Toast.makeText(getApplicationContext(), "Analytics Report Successfully", Toast.LENGTH_LONG);
									toast.show();
								} else {
									Toast toast = Toast.makeText(getApplicationContext(), "Analytics Report Failure", Toast.LENGTH_LONG);
									toast.show();
								}
							}
						});
					}
				}).start();
			}
		});
		
	}
}
