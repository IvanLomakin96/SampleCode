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
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;


public class SDKAuthentication extends AppBaseActivity {

	boolean serviceError = false;
	SDKManager awSDKManager = null;
	int authType = -1;
	String authTypeStr = null;
	String authResultStr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		
		new Thread(new Runnable() {
			public void run() {
				try {
					awSDKManager = SDKManager.init(getApplicationContext());
				} catch (final AirWatchSDKException e) {
					serviceError = true;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
							toast.show();
						}
					});
				}
			}
		}).start();

		Button buttonAuthType = (Button) findViewById(R.id.buttonAuthType);
		buttonAuthType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError || awSDKManager==null) return;
				new Thread(new Runnable() {
					public void run() {
						try {
							authType = awSDKManager.getAuthenticationType();
						} catch(AirWatchSDKException e) {
							authTypeStr = e.getMessage();
						}
						if (authType ==0)
							authTypeStr = "No Authentication Required";
						else if(authType==1)
							authTypeStr = "1 (Passcode Authentication Required)";
						else if(authType==2)
							authTypeStr = "2 (Username and Password Authentication Required)";
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast toast = Toast.makeText(getApplicationContext(), authTypeStr, Toast.LENGTH_LONG);
								toast.show();
							}
						});

					}
				}).start();
			}
		});
		
		final EditText editUser = (EditText) findViewById(R.id.editUser);
		final EditText editPwd  = (EditText) findViewById(R.id.editPwd);

		Button buttonAuth = (Button) findViewById(R.id.buttonAuth);
		buttonAuth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError)return;
				new Thread(new Runnable() {
					@Override
					public void run() {
						String userName = editUser.getText().toString();
						String userPassword = editPwd.getText().toString();
						try {
							final boolean result = awSDKManager.authenticateUser(userName, userPassword);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast toast = Toast.makeText(getApplicationContext(), "Authentication:"+result, Toast.LENGTH_LONG);
									toast.show();
								}
							});
						} catch (AirWatchSDKException e) {
							authResultStr = e.getMessage();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast toast = Toast.makeText(getApplicationContext(), "Authentication:"+authResultStr, Toast.LENGTH_LONG);
									toast.show();
								}
							});
						}
					}
				}).start();
			}
		});
		
	}
}
