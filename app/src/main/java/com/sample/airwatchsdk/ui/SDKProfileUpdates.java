/**
 * 
 */
package com.sample.airwatchsdk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.airwatch.util.Logger;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

import java.util.List;

/**
 * @author mkeener
 *
 */
public class SDKProfileUpdates extends AppBaseActivity {

	private static final String TAG = "SDKProfileUpdates";
	private SDKManager awSDKManager;
	private EditText mProfileTypeEditText;
	private EditText mProfileUUIDEditText;
	private TextView mProfileGroupsText;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_update);

		mProfileTypeEditText = (EditText) findViewById(R.id.editTextProfileType);
		mProfileUUIDEditText = (EditText) findViewById(R.id.editTextUUIDProfile);
		mProfileGroupsText = (TextView) findViewById(R.id.textProfiles);
		
		registerSDK();
		
		handleProfileUpdate(getIntent());
	}
	
	private void registerSDK(){
		new Thread(new Runnable() {
			public void run() {
				try {
					awSDKManager = SDKManager.init(getApplicationContext());
				} catch (AirWatchSDKException e) {
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
	}
	
	public void getProfileOnClick(View v){
		boolean useType = false;
		String textVal; 

		textVal = mProfileUUIDEditText.getText().toString();
		
		useType = (textVal == null || textVal.length() < 1);
		
		if(useType){
			textVal = mProfileTypeEditText.getText().toString();
		}
		
		getProfile(useType, textVal);
	}
	
	public void registerProfileListenerOnClick(View v){
		final String text = mProfileTypeEditText.getText().toString();
		
		if(text == null || text.length() == 0){
			Toast.makeText(this, "No profile type to register.", Toast.LENGTH_LONG).show();
			return;
		}

		try {
			final boolean success = awSDKManager.registerProfileListener(text);
			String message = "";
			if(success){
				message = text + " profile type was registered for updates.";
			} else {
				message = "Failed to register the type " + text;
			}
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		} catch (AirWatchSDKException e) {
			Logger.e(TAG, "AirWatchSDKException has occurred");
		}
	}
	
	private void getProfile(final boolean useType, final String text) {
		final String json;
		try {
			if(useType){
				if(awSDKManager == null) return;
				List<String> profiles = awSDKManager.getProfileJSONByType(text);
				if(profiles == null || profiles.size() == 0){
					json = "No profile of " + text + " type found.";
				} else {
					StringBuffer sb = new StringBuffer();
					for (String s : profiles) {
						sb.append(s).append("\n");
					}
					json = new String(sb);
				}
			} else {
				json = awSDKManager.getProfileJSONbyUUID(text);
			}

			mProfileGroupsText.setText(json);
		} catch (AirWatchSDKException e) {
			Logger.e(TAG, "AirWatchSDKException has occurred");
		}
	}

	@Override
	protected void onNewIntent(Intent i){
		super.onNewIntent(i);

		handleProfileUpdate(i);
	}
	
	private void handleProfileUpdate(Intent i){
		if(i != null){			
			String profileUpdateString = i.getStringExtra("PROFILE_TYPE");
			
			if(profileUpdateString != null && profileUpdateString.length() > 0){
				getProfile(true, profileUpdateString);
			}
		}
	}
}
