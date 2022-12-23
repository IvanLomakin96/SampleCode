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
import com.airwatch.sdk.profile.PasscodePolicy;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class SDKPasscode extends AppBaseActivity {

	boolean serviceError = false;
	SDKManager awSDKManager = null;
	PasscodePolicy passPolicy = null;
	boolean result = false;
	String resultStr = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passcode);
		
		new Thread(new Runnable() {
			public void run() {
				try {
					awSDKManager = SDKManager.init(getApplicationContext());
					passPolicy = awSDKManager.getPasscodePolicy();
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
		
		Button buttonIsPassReq = (Button) findViewById(R.id.buttonIsPassReq);
		buttonIsPassReq.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError||awSDKManager==null) return;
				if(passPolicy==null) return;
				boolean result = passPolicy.isPasscodeRequired();
				Toast toast = Toast.makeText(getApplicationContext(), "Is Passcode Required:"+result, Toast.LENGTH_LONG);
				toast.show();
			}
		});

		final EditText editPassReqs = (EditText) findViewById(R.id.editPassReqs);
		editPassReqs.setKeyListener(null);
		Button buttonPassReqs = (Button) findViewById(R.id.buttonPassReqs);
		buttonPassReqs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError||awSDKManager==null) return;
				if (passPolicy == null) {
					Toast toast = Toast.makeText(getApplicationContext(), "Unable to Receive Passcode Requirements", Toast.LENGTH_LONG);
					toast.show();
				} else {
					String result = "passscodeComplexity: "+passPolicy.getPassscodeComplexity()+"\n"+
				                    "minPasscodeLength: "+passPolicy.getMinPasscodeLength()+"\n"+
				                    "minComplexChars: "+passPolicy.getMinComplexChars()+"\n"+
				                    "maxPasscodeAge: "+passPolicy.getMaxPasscodeAge()+"\n"+
				                    "passcodeHistory:"+passPolicy.getPasscodeHistory();
					editPassReqs.setText(result);
				}
			}
		});

		final EditText editPasscode = (EditText) findViewById(R.id.editPasscode);
		
		Button buttonSet = (Button) findViewById(R.id.buttonSet);
		buttonSet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError||awSDKManager==null) return;
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							String userPasscode = editPasscode.getText().toString();
							boolean result = awSDKManager.setPasscode(userPasscode);
							resultStr= result?"true":"false";
						} catch (AirWatchSDKException e) {
							resultStr = "Exception";
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast toast = Toast.makeText(getApplicationContext(), "Set Result:"+resultStr, Toast.LENGTH_LONG);
								toast.show();
							}
						});
					}
				}).start();
			}
		});
		
		Button buttonValidate = (Button) findViewById(R.id.buttonValidate);
		buttonValidate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError||awSDKManager==null) return;
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							String userPasscode = editPasscode.getText().toString();
							boolean result = awSDKManager.validatePasscode(userPasscode);
							resultStr= result?"true":"false";
						} catch (AirWatchSDKException e) {
							resultStr = "Exception";
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast toast = Toast.makeText(getApplicationContext(), "Validate Result:"+resultStr, Toast.LENGTH_LONG);
								toast.show();
							}
						});
					}
				}).start();
			}
		});
		
		Button buttonReset = (Button) findViewById(R.id.buttonReSet);
		buttonReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError||awSDKManager==null) return;
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							boolean result = awSDKManager.resetPasscode();
							resultStr= result?"true":"false";
						} catch (AirWatchSDKException e) {
							resultStr = "Exception";
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast toast = Toast.makeText(getApplicationContext(), "Reset Result:"+resultStr, Toast.LENGTH_LONG);
								toast.show();
							}
						});
					}
				}).start();
			}
		});
	}
}
