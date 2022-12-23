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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.airwatch.sdk.logger.AWLog;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class SDKLogging extends AppBaseActivity {
	
	boolean serviceError = false;
	SDKManager awSDKManager = null;
	
	EditText etLogMessage = null;
	Spinner spinnerLogLevel = null;
	Button btnSubmit = null;

	String errorStr = null;
	String logMessageString = null;
	
	ProgressDialog mSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logging);

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
		
		etLogMessage = (EditText) findViewById(R.id.logMessage);
		logMessageString = etLogMessage.getText().toString();
		
		spinnerLogLevel = (Spinner) findViewById(R.id.spinLogLevel);
		ArrayAdapter<String> textAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, getResources().getStringArray(R.array.logging_levels));
		textAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		spinnerLogLevel.setAdapter(textAdapter);
		spinnerLogLevel.setOnItemSelectedListener(mSpinnerItemSelected);
		
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(mButtonClickListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private OnItemSelectedListener mSpinnerItemSelected = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	private View.OnClickListener mButtonClickListener =  new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(serviceError || awSDKManager==null) return;
			
			int logLevel = spinnerLogLevel.getSelectedItemPosition();
			logMessageString = etLogMessage.getText().toString();
			switch(logLevel) {
			case 0 : //Log level is 'Verbose'
				AWLog.v(logMessageString);
				Toast.makeText(SDKLogging.this, "Verbose message logged!!", Toast.LENGTH_SHORT).show();
				break;
			case 1 : //Log level is 'Debug'
				AWLog.d(logMessageString);
				Toast.makeText(SDKLogging.this, "Debug message logged!!", Toast.LENGTH_SHORT).show();
				break;
			case 2 : //Log level is 'Info'
				AWLog.i(logMessageString);
				Toast.makeText(SDKLogging.this, "Info message logged!!", Toast.LENGTH_SHORT).show();
				break;
			case 3 : //Log level is 'Warn'
				AWLog.w(logMessageString);
				Toast.makeText(SDKLogging.this, "Warning message logged!!", Toast.LENGTH_SHORT).show();
				break;
			case 4 : //Log level is 'Error'
				AWLog.e(logMessageString);
				Toast.makeText(SDKLogging.this, "Error message logged!!", Toast.LENGTH_SHORT).show();
				break;
			}
			etLogMessage.setText("");
		}
	};
}
