/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.airwatch.core.AirWatchDevice;
import com.airwatch.core.security.CompromiseDetector;
import com.airwatch.safetynet.online.SafetyNetAttestationManager;
import com.airwatch.sdk.AppInfoReader;
import com.airwatch.sdk.context.awsdkcontext.SDKDataModel;
import com.airwatch.sdk.context.awsdkcontext.SDKDataModelImpl;
import com.airwatch.task.TaskQueue;
import com.airwatch.util.ArrayUtils;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class RootStatus extends AppBaseActivity {

    ProgressDialog progress;
    TextView statusText, compromiseText;
    Button checkState, compromiseBtn;

    private int[] statusCode;
    private TextView safetyNetTextView;
    private Button safetyNetButton;
    private CompromiseDetector compromiseChecker = new CompromiseDetector();


    private CompromiseDetector.ResultCallBack callBack = new CompromiseDetector.ResultCallBack() {
        @Override
        public void onResult(CompromiseDetector.CompromiseCheckResult result) {
            compromiseBtn.setEnabled(true);
            compromiseText.setText("isRooted = " + result.isRooted());
            compromiseText.append("\n");
            compromiseText.append("errorCodes = " + result.getErrorCodes().toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_status);
        statusText = findViewById(R.id.statusText);
        checkState = findViewById(R.id.deviceStatus);
        checkState.setOnClickListener(new OnClickListener(
        ) {

            @Override
            public void onClick(View v) {
                new GetRootStatusTask().execute();
            }
        });

        compromiseText = findViewById(R.id.compromise_tv);
        compromiseBtn = findViewById(R.id.compromise_btn);
        compromiseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                compromiseChecker.checkDeviceForCompromise(getApplicationContext(), callBack);
                compromiseBtn.setEnabled(false);

            }
        });

        safetyNetTextView = findViewById(R.id.safetyNetResponse);
        safetyNetButton = findViewById(R.id.safetyNetCheck);
        safetyNetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskQueue.getInstance().post(SafetyNetAttestationManager.SAFETYNET_ONLINE_ATTESTATION_QUEUE, new Runnable() {
                    @Override
                    public void run() {
                        SDKDataModel dataModel = new SDKDataModelImpl(getApplicationContext());
                        String safetyNetApiKey = AppInfoReader.getAppMetaData(getApplicationContext()).getString(AppInfoReader.SAFETYNET_API_KEY);
                        if (!TextUtils.isEmpty(safetyNetApiKey) && !TextUtils.isEmpty(dataModel.getAWSrvUrl()) && !ArrayUtils.isEmpty(dataModel.getApplicationHMACToken())) {
                            SafetyNetAttestationManager safetyNetAttestationManager =
                                    new SafetyNetAttestationManager(getApplicationContext(), dataModel.getAWSrvUrl(), dataModel.getApplicationHMACToken(),
                                            new SafetyNetAttestationManager.SafetyNetAttestationResultCallback() {
                                                @Override
                                                public void onResult(int resultCode) {
                                                    setSafetyNetTextView("SafetyNet Attestation resultCode " + resultCode);
                                                }

                                                @Override
                                                public void onCompromised(int compromisedCode) {
                                                    setSafetyNetTextView("Compromised error code received from console " + compromisedCode);
                                                }
                                            });
                            safetyNetAttestationManager.requestSafetyNetAttestation(safetyNetApiKey);
                            setSafetyNetTextView("Please wait...");
                        } else {
                            setSafetyNetTextView("Invalid parameters");
                        }
                    }
                });
            }
        });
    }

    private void setSafetyNetTextView(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                safetyNetTextView.setText(text);
            }
        });
    }


    private class GetRootStatusTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {


            boolean isRooted = false;
            statusCode = AirWatchDevice.getDeviceStatusCodes();
            if (ArrayUtils.isEmpty(statusCode)) {
                isRooted = false;
            } else {
                isRooted = true;
            }
            return isRooted;


        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(RootStatus.this, "", "Checking for Root Status");

        }

        @Override
        protected void onPostExecute(Boolean result) {
            progress.dismiss();
            statusText.setText(String.valueOf(result));
        }
    }
}