/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.airwatch.feedback.FeedbackListner;
import com.airwatch.ui.activity.FeedbackActivity;
import com.sample.airwatchsdk.R;
import com.sample.airwatchsdk.ui.SDKManagedChainActivity;
import com.sample.framework.ui.framework_ui.NetworkUtils;
import com.sample.framework.ui.oemservice.list.ListMethodsActivity;
import com.sample.framework.ui.framework_ui.FrameWorkUIMainActivity;
import com.sample.framework.ui.framework_ui.QRCodeScanner;
import com.sample.main.AppBaseActivity;
import com.sample.main.MainListAdapter;

public class FrameWorkMainActivity extends AppBaseActivity implements ListView.OnItemClickListener {

    public static final String TAG = "AWDemo";

    private ListView mListView;

    private static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fram_work_main);
        mListView = findViewById(R.id.samples_list);
        mListView.setAdapter(new MainListAdapter(this,R.array.framework_main_list_items));
        mListView.setOnItemClickListener(this);

        // registering for feedback
        FeedbackListner.getInstance(getApplicationContext()).registerForFeedback();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(getApplicationContext(), FWKeyManager.class);
                break;
            case 1:
                intent=new Intent(getApplicationContext(),FWFileKeyManager.class);
                break;
            case 2:
                intent = new Intent(getApplicationContext(), FWAppSecurePreferences.class);
                break;
            case 3:
                intent = new Intent(getApplicationContext(), FWSDKSecurePreferences.class);
                break;
            case 4:
                intent = new Intent(getApplicationContext(), ProxyTestActivity.class);
                break;
            case 5:
                intent = new Intent(getApplicationContext(), ProxyActivityAWWebView2.class);
                break;
            case 6:
                intent = new Intent(getApplicationContext(), SDKConfigurationFetch.class);
                break;
            case 7:
                intent = new Intent(getApplicationContext(), FrameWorkUIMainActivity.class);
                break;
            case 8:
                intent = new Intent(getApplicationContext(), QRCodeScanner.class);
                break;
            case 9:
                intent =  new Intent(getApplicationContext(),FeedbackActivity.class);
                break;
            case 10:
                break;
            case 11:
                intent = new Intent(getApplicationContext(), ListMethodsActivity.class);
                break;
            case 12:
                intent = new Intent(getApplicationContext(), NetworkUtils.class);
                break;
            case 13:
                intent = new Intent(getApplicationContext(), IntegratedAuthActivity.class);
                break;
            case 14:
                intent = new Intent(getApplicationContext(), SDKManagedChainActivity.class);
                break;
            case 15:
                intent = new Intent(getApplicationContext(), UniqueIdActivity.class);
                break;
            case 16:
                intent = new Intent(getApplicationContext(), PushNotificationsActivity.class);
                break;
            case 17:
                intent = new Intent(getApplicationContext(), DLPActivity.class);
                break;
            case 18:
                intent = new Intent(getApplicationContext(), MDMStatusV2Activity.class);
                break;
            case 19:
                intent = new Intent(getApplicationContext(), SSLPinningStatusActivity.class);
                break;
            case 20:
                intent = new Intent(getApplicationContext(), RevocationCheckActivity.class);
                break;
            case 21:
                intent = new Intent(getApplicationContext(), ComplianceCheckActivity.class);
                break;
            case 22:
                intent = new Intent(getApplicationContext(), RootStatus.class);
                break;
            case 23:
                intent = new Intent(getApplicationContext(), MutualTLSActivity.class);
                break;
            case 24:
                intent = new Intent(getApplicationContext(), AppPermissionActivity.class);
                break;
            case 25:
                intent = new Intent(getApplicationContext(), AWClientExampleActivity.class);
            default:
                break;
        }
        if (intent != null)
            startActivity(intent);
    }

    private void showProgress(String message){
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(true);
        }
        mProgressDialog.setProgressStyle(R.style.SimplifiedEnrollmentDialogTheme);
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
    }

    private void hideProgress(){
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }
}