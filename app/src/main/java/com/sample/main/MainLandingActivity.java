/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.airwatch.util.Logger;
import com.sample.airwatchsdk.BuildConfig;
import com.sample.airwatchsdk.R;
import com.sample.airwatchsdk.ui.MainActivity;
import com.sample.framework.ui.FrameWorkMainActivity;
import com.sample.privacy.PrivacyMainActivity;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;

import java.util.ArrayList;

public class MainLandingActivity extends AppBaseActivity {

    private static final String TAG = "MainLandingActivity";
    private static final String SCHEME = "package";
    private static final int MY_PERMISSIONS_REQUEST = 123;
    private boolean isCheckPermissionNeeded;
    private Snackbar snackbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mSDKBaseActivityDelegate.isAppReady()){
            setContentView(R.layout.activity_main_landing);
            isCheckPermissionNeeded = true;
            TextView app_version = (TextView) findViewById(R.id.app_version);
            if (!TextUtils.isEmpty(BuildConfig.VERSION_NAME)){
                app_version.setText(String.format("Version: " + BuildConfig.VERSION_NAME));
            }
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.sdk_btn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if(v.getId() == R.id.privacy_btn) {
            Intent intent = new Intent(this, PrivacyMainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, FrameWorkMainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCheckPermissionNeeded && isDeviceApiVersionAboveLollipop()) {
            checkAllPermission();
        }
    }

    private boolean isDeviceApiVersionAboveLollipop(){
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    private void checkAllPermission() {

        // get all the permissions from package manager
        ArrayList<String> permissionArray;
        isCheckPermissionNeeded = false;
        permissionArray = new ArrayList<>();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info != null && info.requestedPermissions != null) {
                for (String permission : info.requestedPermissions) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
                            && !isNormalPermissionOnHigherAndroidVersion(permission)
                            && !permission.contains("airwatch")) {
                        permissionArray.add(permission);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG + "unable to get permission info, " + e.getMessage());
        }
        if (!permissionArray.isEmpty()) {
            String[] requestPermissionArray = new String[permissionArray.size()];
            permissionArray.toArray(requestPermissionArray);
            ActivityCompat.requestPermissions(this, requestPermissionArray,
                    MY_PERMISSIONS_REQUEST);
        }
    }

    /**
     * Checks if the permission is one of the requested "normal" (automatically granted by system)
     * permission on a higher Android version. Permissions like these are not granted on older
     * Android versions and hence our logic determines these as "denied" permissions. Example of
     * this kind of permission is "android.permission.FOREGROUND_SERVICE" introduced in API 28
     *
     * @return True if the permission is one of the "normal" permission on higher Android versions.
     */
    private boolean isNormalPermissionOnHigherAndroidVersion(String permission) {
        return permission.equals("android.permission.FOREGROUND_SERVICE");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            showSettingsSnackbar();
                            enableFunction(false);
                            Logger.v(TAG + " permission is denied, cannot continue to use sample app");
                            return;
                        }
                    }
                }
            }
        }

    }

    private void enableFunction(boolean isEnable) {
        findViewById(R.id.sdk_btn).setEnabled(isEnable);
        findViewById(R.id.fw_btn).setEnabled(isEnable);
    }

    private void showSettingsSnackbar() {
        snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.request_all_permission_rationale), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(com.airwatch.core.R.string.awsdk_action_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isCheckPermissionNeeded = true;
                        enableFunction(true);
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts(SCHEME, getPackageName(), null));
                        startActivityForResult(intent, 0);
                    }
                });
        snackbar.show();
    }
}
