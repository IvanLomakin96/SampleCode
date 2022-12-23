/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.airwatch.crypto.MasterKeyManager;
import com.airwatch.sdk.context.SDKContext;
import com.airwatch.sdk.context.SDKContextManager;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

public class FWKeyManager extends AppBaseActivity {

    private EditText mEditText;
    private TextView mTextViewEncrypt, mTextViewDecrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwkey_manager);
        mEditText = (EditText) findViewById(R.id.et_encrypt);
        mTextViewEncrypt = (TextView) findViewById(R.id.tv_encrypt);
        mTextViewDecrypt = (TextView) findViewById(R.id.tv_decrypt);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_encrypt) {
            encrypt();
        } else if (v.getId() == R.id.btn_decrypt) {
            decrypt();
        }
    }

    // demonstrating encryption
    private void encrypt(){

        SDKContext sdkContextManager = SDKContextManager.getSDKContext();
        MasterKeyManager masterKeyManager = sdkContextManager.getKeyManager();
        String encryptedString = masterKeyManager.encryptAndEncodeString(mEditText.getText().toString());
        mTextViewEncrypt.setText(encryptedString);

    }

    // demonstrating decryption
    private void decrypt(){

        SDKContext sdkContextManager = SDKContextManager.getSDKContext();
        MasterKeyManager masterKeyManager = sdkContextManager.getKeyManager();
        String decryptedString = masterKeyManager.decodeAndDecryptString(mTextViewEncrypt.getText().toString());
        mTextViewDecrypt.setText(decryptedString);

    }
}
