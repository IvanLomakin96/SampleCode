/*
 * Copyright (c) 2018 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in  the United States
 * and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airwatch.revocationcheck.CertificateRole;
import com.airwatch.revocationcheck.RevocationCheckCallback;
import com.airwatch.revocationcheck.RevocationCheckConfig;
import com.airwatch.revocationcheck.RevocationCheckManager;
import com.airwatch.revocationcheck.RevocationCheckResponse;
import com.airwatch.revocationcheck.RevocationCheckTrustManager;
import com.airwatch.sdk.context.SDKContextManager;
import com.airwatch.task.TaskQueue;
import com.airwatch.util.IOUtils;
import com.airwatch.util.Logger;
import com.sample.airwatchsdk.R;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class RevocationCheckActivity extends AppCompatActivity implements RevocationCheckCallback {

    private static final String TAG = "RevocationCheckActivity";
    private static final int READ_REQUEST_CODE = 42;

    ToggleButton checkTypeToggle;
    Spinner strictnessSpinner;
    ToggleButton nonceToggle;
    ToggleButton trustStoreToggle;
    Button setTLSButton;
    Button setSMIMEButton;
    TextView certNameTv;
    TextView lastRevocationView;
    Spinner aiaSpinner;
    EditText ocspUrlEt;
    Uri certUri;
    Button doCheckButton;
    Button doSettingsFetchButton;
    Button hitUrlButton;
    EditText hitUrlEt;

    RevocationCheckManager revocationCheckManager;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        revocationCheckManager.registerRevocationCheckCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        revocationCheckManager.unregisterRevocationCheckCallback(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revocation_check);
        aiaSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ocsp_aia_setting, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aiaSpinner.setAdapter(adapter);

        strictnessSpinner = findViewById(R.id.strictnessSpinner);
        ArrayAdapter<CharSequence> strictnessAdapter = ArrayAdapter.createFromResource(this,
                R.array.ocsp_strictness_setting, android.R.layout.simple_spinner_item);
        strictnessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        strictnessSpinner.setAdapter(strictnessAdapter);

        checkTypeToggle = findViewById(R.id.checkTypeToggle);
        nonceToggle = findViewById(R.id.nonceToggle);
        trustStoreToggle = findViewById(R.id.trustStoreToggle);
        setTLSButton = findViewById(R.id.tlsButton);
        setSMIMEButton = findViewById(R.id.smimeButton);
        certNameTv = findViewById(R.id.certNameTv);
        ocspUrlEt = findViewById(R.id.ocspUrlEt);
        doCheckButton = findViewById(R.id.doCheckButton);
        doSettingsFetchButton = findViewById(R.id.doSettingsFetch);
        hitUrlEt = findViewById(R.id.hitUrlEt);
        hitUrlButton = findViewById(R.id.hitUrlButton);
        lastRevocationView = findViewById(R.id.tv_last_revocation_response);

        hitUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitUrl(hitUrlEt.getText().toString());
            }
        });

        doSettingsFetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SDKContextManager.getSDKContext().fetchSDKSettings(null);
            }
        });
        doCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRevocationCheck();
            }
        });

        Button certSelectButton = (Button) findViewById(R.id.fileOpenButton);
        certSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });

        setTLSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTLSSettings();
            }
        });

        setSMIMEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSMIMESettings();
            }
        });

        revocationCheckManager = SDKContextManager.getSDKContext().getRevocationCheckManager();
    }

    private void doRevocationCheck() {
        X509Certificate cert = getCertificateFromFile();
        if (cert != null) {
            RevocationCheckResponse response = revocationCheckManager.checkForRevocation(CertificateRole.S_MIME, new X509Certificate[]{cert});

            lastRevocationView.setText(response != null ? response.toString() : "Revocation Check not executed");
        }
    }

    private void setSMIMESettings() {
        revocationCheckManager.setRevocationCheckConfig(CertificateRole.S_MIME, getConfigFromInput());
    }

    private void setTLSSettings() {
        revocationCheckManager.setRevocationCheckConfig(CertificateRole.TLS, getConfigFromInput());
    }

    private RevocationCheckConfig getConfigFromInput() {
        RevocationCheckConfig.Builder builder = new RevocationCheckConfig.Builder();
        builder.setEnabled(RevocationCheckConfig.REVOCATION_CHECK_ENABLED_OCSP);
        builder.setRevocationCheckUseAia(aiaSpinner.getSelectedItemPosition());
        builder.setRevocationStrictness(strictnessSpinner.getSelectedItemPosition());
        if (checkTypeToggle.getText().toString().equalsIgnoreCase("leaf")) {
            builder.setRevocationCheckType(RevocationCheckConfig.REVOCATION_CHECK_TYPE_LEAF_CERT_ONLY);
        } else {
            builder.setRevocationCheckType(RevocationCheckConfig.REVOCATION_CHECK_TYPE_ENTIRE_CHAIN);
        }
        if (nonceToggle.getText().toString().equalsIgnoreCase("disabled")) {
            builder.setEnforceNonce(RevocationCheckConfig.ENFORCE_NONCE_DISABLED);
        } else {
            builder.setEnforceNonce(RevocationCheckConfig.ENFORCE_NONCE_ENABLED);
        }
        if (trustStoreToggle.getText().toString().equalsIgnoreCase("sdk")) {
            builder.setTrustStorePreference(RevocationCheckConfig.SDK_PROFILE_TRUST_STORE);
        } else {
            builder.setTrustStorePreference(RevocationCheckConfig.DEVICE_TRUST_STORE_AND_SDK_PROFILE_TRUST_STORE);
        }
        String ocspUrl = ocspUrlEt.getText().toString();
        if (!TextUtils.isEmpty(ocspUrl)) {
            builder.setOCSPResponderUrl(ocspUrl);
        }
        return builder.build();
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                certUri = resultData.getData();
                Log.i(TAG, "Uri: " + certUri.toString());
                Cursor returnCursor =
                        getContentResolver().query(certUri, null, null, null, null);
                returnCursor.moveToFirst();
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                certNameTv.setText(returnCursor.getString(nameIndex));
                IOUtils.closeQuietly(returnCursor);
            }
        }
    }

    private X509Certificate getCertificateFromFile() {
        if (certUri == null) {
            Toast.makeText(this, "No or invalid cert", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            return (X509Certificate) fact.generateCertificate(getContentResolver().openInputStream(certUri));

        } catch (FileNotFoundException | CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void hitUrl(final String urlString) {
        TaskQueue queue = TaskQueue.getInstance();
        queue.post("OCSP", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                URL url = null;
                HttpsURLConnection urlConnection;
                try {
                    url = new URL(urlString);
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    RevocationCheckTrustManager revocationCheckTrustManager = new RevocationCheckTrustManager(
                            revocationCheckManager,
                            null);
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{revocationCheckTrustManager}, null);
                    SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                    urlConnection.setSSLSocketFactory(socketFactory);
                    Logger.d(TAG, "response code: " + urlConnection.getResponseCode());
                } catch (MalformedURLException | NoSuchAlgorithmException | KeyManagementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

    }

    @Override
    public void onRevocationCheckValidationFailed(int role, @NotNull RevocationCheckResponse response) {
        Log.e(TAG, "Revocation Validation Failed with role: " + role + " and cert: " + response.getCertSubject());
    }
}