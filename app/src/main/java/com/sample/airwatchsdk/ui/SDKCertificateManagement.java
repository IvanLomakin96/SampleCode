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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.bizlib.model.CertificateDefinition;
import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.airwatch.sdk.profile.ApplicationProfile;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class SDKCertificateManagement extends AppBaseActivity{

	boolean serviceError = false;
	SDKManager awSDKManager=null;
	String certUuid = null;
	String certThumprint = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cert);
		
		certUuid = null;
		certThumprint = null;

		
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
		
		final TextView textCerts = (TextView) findViewById(R.id.textCerts);
		Button buttonGetCerts = (Button) findViewById(R.id.buttonGetCert);
		buttonGetCerts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError || awSDKManager==null) return;

				certUuid = null;
				certThumprint = null;

				new Thread(new Runnable() {
					public void run() {
						try {
							ApplicationProfile awAppProfile = awSDKManager.getApplicationProfile();
							if (awAppProfile!=null) {
								List<CertificateDefinition> certs = awAppProfile.getCertificates();
								if(certs!=null && certs.size()>0) {
									CertificateDefinition cert = certs.get(0);
									certUuid = cert.getUuid();
									certThumprint = cert.getThumbprint();
								}
							}
							final String receivedCertUuid = certUuid;
							runOnUiThread(new Runnable() {
								public void run() {
									if (receivedCertUuid == null) {
										Calendar c = Calendar.getInstance();
										SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										String formattedDate = df.format(c.getTime());
										textCerts.setText("No Certificates: Last Check Time:"+formattedDate);
									} else {
										textCerts.setText("Certificate: UUID:"+receivedCertUuid+", Thumbprint:"+certThumprint);
									}
									Toast toast = Toast.makeText(getApplicationContext(), "Get Certificate:"+receivedCertUuid, Toast.LENGTH_LONG);
									toast.show();
								}
							});
						} catch (final AirWatchSDKException e) {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast toast = Toast.makeText(getApplicationContext(), "Get Certificate Exception:"+e.getMessage(), Toast.LENGTH_LONG);
									toast.show();
								}
							});
						}
					}
				}).start();
			}
		});
		
		Button buttonInUse = (Button) findViewById(R.id.buttonInUse);
		buttonInUse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError || awSDKManager==null || certUuid==null) return;
				
				new Thread(new Runnable() {
					public void run() {
						try {
							awSDKManager.reportApplicationProfile(certUuid, true);
						} catch(AirWatchSDKException e) {
						} catch(Exception e) {
						}
						runOnUiThread(new Runnable() {
							public void run() {
								if (certUuid!= null) {
									Toast toast = Toast.makeText(getApplicationContext(), "Reported Certificate In Use", Toast.LENGTH_LONG);
									toast.show();
								} else {
									Toast toast = Toast.makeText(getApplicationContext(), "No Certificates to Report", Toast.LENGTH_LONG);
									toast.show();
								}
							}
						});
					}
				}).start();
				
			}
		});
		
		Button buttonNotInUse = (Button) findViewById(R.id.buttonNotInUse);
		buttonNotInUse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(serviceError || awSDKManager==null || certUuid==null) return;
				
				new Thread(new Runnable() {
					public void run() {
						try {
							awSDKManager.reportApplicationProfile(certUuid, false);
						} catch(AirWatchSDKException e) {
						} catch(Exception e) {
						}
						runOnUiThread(new Runnable() {
							public void run() {
								if (certUuid!= null) {
									Toast toast = Toast.makeText(getApplicationContext(), "Reported Certificate Not In Use", Toast.LENGTH_LONG);
									toast.show();
								} else {
									Toast toast = Toast.makeText(getApplicationContext(), "No Certificates to Report", Toast.LENGTH_LONG);
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
