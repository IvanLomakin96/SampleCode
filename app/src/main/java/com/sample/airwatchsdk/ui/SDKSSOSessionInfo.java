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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class SDKSSOSessionInfo extends AppBaseActivity {

	boolean serviceError = false;
	SDKManager awSDKManager = null;
	boolean isSSOActivated = false;
	boolean result = false;
	static int authValue = 0;
	String errorStr = null;
	String[] authTypes = null;
	int gracePeriodValue = 0;
	static int remGracePeriod = 0;

	Button btnResetPasscode = null;
	TextView textGracePeriod = null;
	TextView textInfo = null;
	TextView textRemGracePeriod = null;
	TextView textSession = null;
	TextView authTypeValue = null;
	ProgressDialog mSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sso);
		
		authTypes = getResources().getStringArray(R.array.authentication_types);
		
		textGracePeriod = (TextView) findViewById(R.id.textGracePeriodValue);
		textGracePeriod.setText(new StringBuilder().append(0));
		
		authTypeValue = (TextView)findViewById(R.id.textAuthTypeValue);
		
		textRemGracePeriod = (TextView)findViewById(R.id.textRemGracePeriod);
		textRemGracePeriod.setText(new StringBuilder(getString(R.string.remaining_grace_period)).append(" " + remGracePeriod));
		
		btnResetPasscode = (Button) findViewById(R.id.btnResetPasscode);
		btnResetPasscode.setOnClickListener(mButtonClickListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new SDKManagerInitTask().execute();
	}
	
	private View.OnClickListener mButtonClickListener =  new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(serviceError || awSDKManager==null) return;
			
			if(btnResetPasscode == v) {
				//showDlg(DIALOG_SET_PASSCODE);
				Intent intent = new Intent(getApplicationContext(), SDKPasscode.class);
				startActivity(intent);
			} 
		}
	};

	private class SDKManagerInitTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			mSpinner = ProgressDialog.show(SDKSSOSessionInfo.this, getString(R.string.empty), getString(R.string.loading_wait), true);
		}

		@Override
		protected String doInBackground(Void... unused) {
			try {
				awSDKManager = SDKManager.init(getApplicationContext());
				authValue = awSDKManager.getAuthenticationType();

				result = awSDKManager.isSSOSessionValid();				
				gracePeriodValue = awSDKManager.getSSOGracePeriod();
				remGracePeriod = awSDKManager.getSSORemainingGracePeriod();

				return "SUCCESS";
			} catch (final AirWatchSDKException e) {
				serviceError = true;
				errorStr = e.getMessage();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String resultStr) {
			if(mSpinner != null) 
				mSpinner.dismiss();
			
			if(resultStr == null){
				Toast toast = Toast.makeText(SDKSSOSessionInfo.this, errorStr, Toast.LENGTH_LONG);
				toast.show();
				//return;
			}
			
			if (!(gracePeriodValue < 0)) {
				textGracePeriod.setText(new StringBuilder().append(gracePeriodValue));
				textRemGracePeriod.setText(new StringBuilder(getString(R.string.remaining_grace_period)).append(" " + remGracePeriod));
			}
			authTypeValue.setText(authTypes[authValue]);
			
			if(authValue != 2)
				btnResetPasscode.setVisibility(View.GONE);
			else
				btnResetPasscode.setVisibility(View.VISIBLE);
				
		}
	}
}
