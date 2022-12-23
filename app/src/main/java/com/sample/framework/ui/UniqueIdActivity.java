/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 *  This product is protected by copyright and intellectual property laws in  the United Sta tes and other countries
 *  as well as by international treaties.
 *  AirWatch products may be covered by one or more patents listed at
 *  http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airwatch.keymanagement.AWKeyUtils;
import com.airwatch.util.ByteArrayUtils;
import com.sample.airwatchsdk.R;

/**
 * Created by jmara on 8/22/2016.
 */
public class UniqueIdActivity extends AppCompatActivity {

    private EditText passcodeField;
    private TextView uniqueIdLabel;
    private TextView uniqueIdResult;
    private Button getUniqueId;
    private Button generateUniqueId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unique_device_id);
        getSupportActionBar().setTitle(R.string.unique_id);
        passcodeField = (EditText) findViewById(R.id.enter_passcode);
        uniqueIdLabel = (TextView) findViewById(R.id.unique_id_display_label);
        uniqueIdResult = (TextView) findViewById(R.id.unique_id_output_field);
        getUniqueId = (Button) findViewById(R.id.get_unique_id_btn) ;
        generateUniqueId = (Button) findViewById(R.id.generate_unique_id_btn);
        uniqueIdLabel.setVisibility(View.INVISIBLE);
        uniqueIdResult.setVisibility(View.INVISIBLE);
    }

    public void getUniqueIdButtonOnClick(View view){
        if (AWKeyUtils.hasAwUniqueUidV4(this)){
            uniqueIdLabel.setText(R.string.fetching);
            uniqueIdLabel.setVisibility(View.VISIBLE);
            generateUniqueId.setEnabled(false);
            getUniqueId.setEnabled(false);
            fetchUniquiUid();
        } else {
            uniqueIdLabel.setText(R.string.no_unique_id_present);
            uniqueIdLabel.setVisibility(View.VISIBLE);
        }
    }

    private void fetchUniquiUid(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return AWKeyUtils.getAwUniqueUidV4(UniqueIdActivity.this);
            }

            @Override
            protected void onPostExecute(String s) {
                uniqueIdLabel.setText(R.string.unique_id);
                uniqueIdResult.setText(s);
                uniqueIdResult.setVisibility(View.VISIBLE);
                generateUniqueId.setEnabled(true);
                getUniqueId.setEnabled(true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void generateUniqueIdButtonOnClick(View view){
        if (!TextUtils.isEmpty(passcodeField.getText().toString())){
            uniqueIdLabel.setText(R.string.generated_unique_id_is);
            uniqueIdLabel.setVisibility(View.VISIBLE);
            uniqueIdResult.setVisibility(View.VISIBLE);
            Editable editText = passcodeField.getEditableText();
            char[] charArray = new char[editText.length()];
            editText.getChars(0, editText.length(), charArray, 0);
            uniqueIdResult.setText(ByteArrayUtils.toBase64(AWKeyUtils.generateAwUniqueUidV4(this, charArray)));
        } else {
            passcodeField.setError(getString(R.string.passcode_required));
            uniqueIdLabel.setVisibility(View.INVISIBLE);
            uniqueIdResult.setVisibility(View.INVISIBLE);
        }
    }

    public void clearUniqueIdButtonOnClick(View view) {
        AWKeyUtils.clearAwUniqueUidV4(this);
    }

    public void hasUniqueIdButtonOnClick(View view) {
        uniqueIdLabel.setText(R.string.has_unique_id);
        uniqueIdResult.setText(""+AWKeyUtils.hasAwUniqueUidV4(this));
        uniqueIdLabel.setVisibility(View.VISIBLE);
        uniqueIdResult.setVisibility(View.VISIBLE);
    }
}
