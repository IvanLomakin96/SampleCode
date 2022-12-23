/*
 * Copyright (c) 2017. AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */
package com.sample.privacy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.airwatch.privacy.AWPrivacyCallback;
import com.airwatch.privacy.AWPrivacyConfig;
import com.airwatch.privacy.AWPrivacyContent;
import com.airwatch.privacy.AWPrivacyController;
import com.airwatch.privacy.AWPrivacyPermissionType;
import com.airwatch.privacy.AWPrivacyResult;


import java.util.ArrayList;


public class PrivacyHelper {

    private Context context = null;
    private SharedPreferences preferences = null;
    private final String PRIVACY_PREF = "PRIVACYPREF";
    AWPrivacyController awPrivacyController = AWPrivacyController.INSTANCE;

    public PrivacyHelper(@NonNull final Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PRIVACY_PREF, Context.MODE_PRIVATE);
        awPrivacyController.initialize(preferences);
    }

    public AWPrivacyConfig getPrivacyConfig() {
        AWPrivacyConfig awPrivacyConfig = new AWPrivacyConfig();
        // retrieved policy allowed metrics
        // set analytics flag based on policy allowed metrics first. If not defined then use SDK analytics flag

        // use analytics flag
        awPrivacyConfig.setAllowFeatureAnalytics(true);


        // if customer privacy policy defined then use that
        awPrivacyConfig.setCustPrivacyInfo("Customer Privacy Policy", "www.vmware.com");

        //Get default app data
        ArrayList<AWPrivacyContent> appData = awPrivacyController.getAppDataDefaults(context);

        //Add app specific items to app data collected

        AWPrivacyContent appBrowsingHistory = new AWPrivacyContent();
        appBrowsingHistory.setTitle("Browsing History");
        appBrowsingHistory.setSummary("Browsing hstory text");
        //appBrowsingHistory.setId(R.drawable.ic_browsing_history);
        appData.add(appBrowsingHistory);

        awPrivacyConfig.setAppData(appData);

        // define permissions
        ArrayList<AWPrivacyContent> appPermissions = new ArrayList<>();

        AWPrivacyContent appPerCam = new AWPrivacyContent();
        appPerCam.setTitle("Camera"); // camera per
        appPerCam.setSummary("Camera required for taking pics");
        appPerCam.setId(awPrivacyController.getPermissionResource(AWPrivacyPermissionType.PERMISSION_CAMERA));
        appPermissions.add(appPerCam);

        AWPrivacyContent appPerPhone = new AWPrivacyContent();
        appPerPhone.setTitle("Phone");
        appPerPhone.setSummary("Phone permission reqd for call");
        appPerPhone.setId(awPrivacyController.getPermissionResource(AWPrivacyPermissionType.PERMISSION_PHONE));
        appPermissions.add(appPerPhone);

        AWPrivacyContent appPerLoc = new AWPrivacyContent();
        appPerLoc.setTitle("Location");
        appPerLoc.setSummary("Location Permission reqd for GPS");
        appPerLoc.setId(awPrivacyController.getPermissionResource(AWPrivacyPermissionType.PERMISSION_LOCATION_SERVICES));
        appPermissions.add(appPerLoc);

        AWPrivacyContent appPerNet = new AWPrivacyContent();
        appPerNet.setTitle("Network");
        appPerNet.setSummary("Required for networking");
        appPerNet.setId(awPrivacyController.getPermissionResource(AWPrivacyPermissionType.PERMISSION_NETWORK));
        appPermissions.add(appPerNet);

        AWPrivacyContent appPerStorage = new AWPrivacyContent();
        appPerStorage.setTitle("Storage");
        appPerStorage.setSummary("To save Files");
        appPerStorage.setId(awPrivacyController.getPermissionResource(AWPrivacyPermissionType.PERMISSION_STORAGE));
        appPermissions.add(appPerStorage);

        AWPrivacyContent appPerHardware = new AWPrivacyContent();
        appPerHardware.setTitle("Hardware");
        appPerHardware.setSummary("To access MIC etc");
        appPerHardware.setId(awPrivacyController.getPermissionResource(AWPrivacyPermissionType.PERMISSION_FINGERPRINT));
        appPermissions.add(appPerHardware);
        awPrivacyConfig.setAppName("Sample App");
        awPrivacyConfig.setAppPermissions(appPermissions);
        return awPrivacyConfig;

    }

    public void previewPrivacy() {
        awPrivacyController.previewPrivacy(context, getPrivacyConfig(), new AWPrivacyCallback() {
            @Override
            public void onComplete(AWPrivacyResult result) {
                // App speific actions
            }
        });
    }

    public Intent getPrivacyIntent(AWPrivacyCallback callback) {
        if(awPrivacyController.getConsentRequired(getPrivacyConfig())) {
            return awPrivacyController.getPrivacyIntent(context,getPrivacyConfig(),callback);
        }
        else
            return null;
    }
}