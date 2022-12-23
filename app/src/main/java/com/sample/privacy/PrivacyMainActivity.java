/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.privacy;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.airwatch.privacy.AWPrivacyCallback;
import com.airwatch.privacy.AWPrivacyController;
import com.airwatch.privacy.AWPrivacyResult;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;
import com.sample.main.MainListAdapter;

public class PrivacyMainActivity extends AppBaseActivity implements ListView.OnItemClickListener {

    public static final String TAG = "AWPrivacyDemo";

    private ListView mListView;
    private PrivacyHelper privacyHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_main);
        mListView = (ListView) findViewById(R.id.privacy_list);
        mListView.setAdapter(new MainListAdapter(this,R.array.privacy_list_items));
        mListView.setOnItemClickListener(this);
        privacyHelper = new PrivacyHelper(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = null;
        switch (position) {
            case 0:
                Intent privacyIntent = privacyHelper.getPrivacyIntent(new AWPrivacyCallback() {
                    @Override
                    public void onComplete(AWPrivacyResult result) {
                        // App speific actions
                    }
                });
                if(privacyIntent == null) {
                    Snackbar.make(view, "Privacy already Accepted", Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                }
                else {
                    startActivity(privacyIntent);
                }
                break;
            case 1:
                privacyHelper.previewPrivacy();
                break;
            case 2:
                AWPrivacyController.INSTANCE.reset(); // After reset getPrivacyIntent will always return as not null

                Snackbar.make(view, "Privacy module resetted, after this getConsent will show privacy screens again", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;

            default:
                break;
        }

    }


}