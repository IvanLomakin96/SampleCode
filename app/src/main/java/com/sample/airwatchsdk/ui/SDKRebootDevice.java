package com.sample.airwatchsdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class SDKRebootDevice extends AppBaseActivity {

	boolean serviceError = false;
	SDKManager awSDKManager = null;
	String responseStr = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reboot_device);
		
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
		
		Button buttonYes = (Button) findViewById(R.id.buttonYes);
		buttonYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError || awSDKManager==null) return;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							awSDKManager.rebootDevice();
						} catch(AirWatchSDKException e) {
							responseStr = e.getMessage();
						}
					}
				}).start();
			}
		});
		
		Button buttonNope = (Button) findViewById(R.id.buttonNo);
		buttonNope.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError || awSDKManager==null) return;
				new Thread(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						if(intent!=null)
							startActivity(intent);
					}
				}).start();
			}
		});
	}
}
