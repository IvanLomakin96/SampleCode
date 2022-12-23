/*
 * AirWatch Android Software Development Kit
 * Copyright (c) 2015 AirWatch. All rights reserved.
 *
 * Unless required by applicable law, this Software Development Kit and sample
 * code is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. AirWatch expressly disclaims any and all
 * liability resulting from User's use of this Software Development Kit. Any
 * modification made to the SDK must have written approval by AirWatch.
 */
package com.sample.airwatchsdk.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.airwatch.bizlib.model.CertificateDefinition;
import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.ProxyType;
import com.airwatch.sdk.SDKManager;
import com.airwatch.sdk.SecureAppInfo;
import com.airwatch.sdk.profile.ApplicationProfile;
import com.airwatch.sdk.profile.LoggingProfile;
import com.airwatch.sdk.profile.PasscodePolicy;
import com.airwatch.sdk.profile.RestrictionPolicy;
import com.sample.airwatchsdk.R;
import com.sample.main.ApiAdapter;
import com.sample.main.AppBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDKManagerAPIs extends AppBaseActivity {

    ApiAdapter mainList = null;
    SDKManager awSDKManager = null;
    RestrictionPolicy restrictionPolicy = null;
    PasscodePolicy passcodePolicy = null;
    SecureAppInfo appInfo = null;
    boolean serviceError = false;
    Map<String, String> map = new HashMap<>();
    String[] title_array = null;
    String certUuid = null;
    String certThumprint = null;
    ListView listView = null;
    String customSettingsString = null;
    LoggingProfile profile = null;

    String URL = "http://us8spkcd.us8.airwlab.com";
    String USERNAME = "us8\\imauser";
    String PASSWORD = "P@ssw0rd";

    //region API Name Constants
    private static final String API_VERSION = "API Version";
    private static final String DEVICE_UID = "Device UID";
    private static final String API_PERMISSION = "API Permission";
    private static final String ENROLLMENT_STATUS = "Enrollment Status";
    private static final String SERVER_NAME = "Server Name";
    private static final String GROUP_ID = "Group ID";
    private static final String AUTHENTICATION_TYPE = "Authentication Type";
    private static final String AUTHENTICATE_USER = "Authenticate User";
    private static final String PASSWORD_REQUIRED = "Is Passcode Required";
    private static final String PASSWORD_COMPLEXITY = "Passcode Complexity";
    private static final String MIN_PASSCODE_LENGTH = "Min Passcode Length";
    private static final String MIN_COMPLEX_CHAR_LENGTH = "Min Complex Char Length";
    private static final String MAXIMUM_PASSCODE_AGE = "Maximum Passcode Age";
    private static final String PASSCODE_HISTORY = "Passcode History";
    private static final String IS_COMPLIANT = "Is Compliant";
    private static final String IS_COMPROMISED = "Is Compromised";
    private static final String IS_ENTERPRISE = "Is Enterprise";
    private static final String BLUETOOTH_ALLOWED = "Bluetooth Allowed";
    private static final String CAMERA_ALLOWED = "Camera Allowed";
    private static final String OFFLINE_MODE_ALLOWED = "Offline Mode Allowed";
    private static final String COPY_AND_CUT = "Prevent Copy and Cut";
    private static final String WIFI = "Wifi SSID Allowed";
    private static final String APPLICATION_PROFILE_ID = "Application Profile ID";
    private static final String APPLICATION_PROFILE_NAME = "Application Profile Name";
    private static final String DND_ENABLE_STATUS = "DND Enable Status";
    private static final String DND_STATUS_HAS_BEEN_SET = "DND Status Has Been Set";
    private static final String SSO_SESSION_VALID = "Is SSO Session Valid";
    private static final String SSO_ENABLED = "Is SSO Enabled";
    private static final String SSO_GRACE_PERIOD = "SSO Grace Period";
    private static final String DND_STATUS = "DND Status";
    private static final String WORKSPACE_EXIT_MODE = "Workspace Exit Mode";
    private static final String ANCHOR_TYPE = "Anchor Type";
    private static final String CONSOLE_VERSION = "Console Version";
    private static final String INTEGRATED_AUTH_ENABLED = "Is Integrated Auth Enabled";
    private static final String DOMAINS = "Domains Supported";
    private static final String LG_CONFIGURE_MODE = "LG Configure Mode";
    private static final String SHARED_DEVICE_MODE = "Is Shared Device Mode Enabled";
    private static final String SHARED_DEVICE_CHECKOUT_STATUS = "Shared Device Checkout Status";
    private static final String SESSION_TOKEN = "Session Token";
    private static final String CUSTOM_SETTINGS = "Custom Settings";
    private static final String PROFILE_GROUPS = "Profile Groups";
    private static final String LOGGING_ENABLE_STATUS = "Logging Enable Status";
    private static final String LOGGING_LEVEL = "Logging Level";
    private static final String SEND_OVER_WIFI = "Send Over Wifi Only";
    private static final String LAST_CHECKIN_TIME = "Last Checkin Time";
    private static final String ALLOWED_WIFI_SSID = "Allowed WiFi SSID";
    private static final String GET_CERTIFICATE = "Get Certificate";
    private static final String GET_KERBEROS_TOKEN = "Get Kerberos Token";
    private static final String GET_CERTIFICATE_KEYS = "Get Certificate Keys";
    private static final String APPLICATION_CONFIG_SETTING = "Application Config Setting";
    private static final String SSO_STATUS = "SSO Status";
    private static final String ENROLLMENT_USERNAME = "Enrollment Username";
    //endregion

    public void setup(){
        if (awSDKManager == null){
            try{
                awSDKManager = SDKManager.init(getApplicationContext());
                serviceError = false;
            }catch (AirWatchSDKException e){
                serviceError = true;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_manager_api);
        title_array = getResources().getStringArray(R.array.api_list_items);
        mainList = new ApiAdapter(this, R.array.api_list_items, map);
        listView = (ListView) findViewById(R.id.apilist);
        listView.setAdapter(mainList);
    }

    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listView != null && mainList != null) {
                    mainList.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SDKManager.isServiceConnected()){
            setup();
            for (String s : title_array) {
                getResponse(s);
            }
        }
    }

    public void getResponse(final String title) {
        new Thread(new Runnable() {
            public void run() {
                String response = null;
                try {
                    if (serviceError){
                        awSDKManager = SDKManager.init(getApplicationContext());
                    }
                    //region Initialize SDK manager, profile, policies and other variables
                    StringBuilder builder;
                    //endregion
                    //region Handle cases
                    switch (title) {
                        //region API Version
                        case API_VERSION:
                            response = "" + awSDKManager.getAPIVersion();
                            break;
                        //endregion
                        //region Device UID
                        case DEVICE_UID:
                            response = "" + awSDKManager.getDeviceUid();
                            break;
                        //endregion
                        //region Enrollment Username
                        case ENROLLMENT_USERNAME:
                            response = awSDKManager.getEnrollmentUsername();
                            break;
                        //endregion
                        //region API Permission
                        case API_PERMISSION:
                            response = "" + awSDKManager.hasAPIPermission();
                            break;
                        //endregion
                        //region Enrollment Status
                        case ENROLLMENT_STATUS:
                            response = "" + awSDKManager.isEnrolled();
                            break;
                        //endregion
                        //region Server Name
                        case SERVER_NAME:
                            response = "" + awSDKManager.getServerName();
                            break;
                        //endregion
                        //region Group ID
                        case GROUP_ID:
                            response = "" + awSDKManager.getGroupId();
                            break;
                        //endregion
                        //region Authentication Type
                        case AUTHENTICATION_TYPE:
                            response = "" + awSDKManager.getAuthenticationType();
                            break;
                        //endregion
                        //region Authenticate User
                        case AUTHENTICATE_USER:
                            appInfo = awSDKManager.getSecureAppInfo();
                            response = "" + awSDKManager.authenticateUser(appInfo.getEnrollmentUsername(), appInfo.getEnrollmentPassword());
                            break;
                        //endregion
                        //region Is Passcode Required
                        case PASSWORD_REQUIRED:
                            passcodePolicy = awSDKManager.getPasscodePolicy();
                            response = "" + passcodePolicy.isPasscodeRequired();
                            break;
                        //endregion
                        //region Passcode Complexity
                        case PASSWORD_COMPLEXITY:
                            passcodePolicy = awSDKManager.getPasscodePolicy();
                            int complexity = passcodePolicy.getPassscodeComplexity();
                            switch (complexity) {
                                case 1:
                                    response = "Numeric";
                                    break;
                                case 2:
                                    response = "Alpha numeric";
                                    break;
                                default:
                                    response = "Unknown";
                                    break;
                            }
                            break;
                        //endregion
                        //region Min Passcode Length
                        case MIN_PASSCODE_LENGTH:
                            passcodePolicy = awSDKManager.getPasscodePolicy();
                            response = "" + passcodePolicy.getMinPasscodeLength();
                            break;
                        //endregion
                        //region Min Complex Char Length
                        case MIN_COMPLEX_CHAR_LENGTH:
                            passcodePolicy = awSDKManager.getPasscodePolicy();
                            response = "" + passcodePolicy.getMinComplexChars();
                            break;
                        //endregion
                        //region Maximum Passcode Age
                        case MAXIMUM_PASSCODE_AGE:
                            passcodePolicy = awSDKManager.getPasscodePolicy();
                            response = "" + passcodePolicy.getMaxPasscodeAge() + " day(s)";
                            break;
                        //endregion
                        //region Passcode History
                        case PASSCODE_HISTORY:
                            passcodePolicy = awSDKManager.getPasscodePolicy();
                            response = "" + passcodePolicy.getPasscodeHistory();
                            break;
                        //endregion
                        //region Compliant Status
                        case IS_COMPLIANT:
                            response = "" + awSDKManager.isCompliant();
                            break;
                        //endregion
                        //region Compromise Status
                        case IS_COMPROMISED:
                            response = "" + awSDKManager.isCompromised();
                            break;
                        //endregion
                        //region Enterprise Status
                        case IS_ENTERPRISE:
                            response = "" + awSDKManager.isEnterprise();
                            break;
                        //endregion
                        //region Bluetooth
                        case BLUETOOTH_ALLOWED:
                            restrictionPolicy = awSDKManager.getRestrictionPolicy();
                            response = "" + restrictionPolicy.allowBluetooth();
                            break;
                        //endregion
                        //region Camera
                        case CAMERA_ALLOWED:
                            restrictionPolicy = awSDKManager.getRestrictionPolicy();
                            response = "" + restrictionPolicy.allowCamera();
                            break;
                        //endregion
                        //region Offline Mode
                        case OFFLINE_MODE_ALLOWED:
                            restrictionPolicy = awSDKManager.getRestrictionPolicy();
                            response = "" + restrictionPolicy.allowOfflineMode();
                            break;
                        //endregion
                        //region Copy and Cut
                        case COPY_AND_CUT:
                            restrictionPolicy = awSDKManager.getRestrictionPolicy();
                            response = "" + restrictionPolicy.preventCopyAndCutActions();
                            break;
                        //endregion
                        //region WiFi SSID
                        case WIFI:
                            response = "" + awSDKManager.isAllowedWiFiSSID();
                            break;
                        //endregion
                        //region Application Profile ID
                        case APPLICATION_PROFILE_ID:
                            response = "" + awSDKManager.getApplicationProfile().getProfileId();
                            break;
                        //endregion
                        //region Application Profile Name
                        case APPLICATION_PROFILE_NAME:
                            response = "" + awSDKManager.getApplicationProfile().getName();
                            break;
                        //endregion
                        //region Get Certificate
                        case GET_CERTIFICATE:
                            certUuid = null;
                            certThumprint = null;
                            ApplicationProfile awAppProfile = awSDKManager.getApplicationProfile();
                            if (awAppProfile != null) {
                                List<CertificateDefinition> certs = awAppProfile.getCertificates();
                                if (certs != null && certs.size() > 0) {
                                    CertificateDefinition cert = certs.get(0);
                                    certUuid = cert.getUuid();
                                    certThumprint = cert.getThumbprint();
                                }
                            }
                            final String receivedCertUuid = certUuid;
                            if (receivedCertUuid == null) {
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String formattedDate = df.format(c.getTime());
                                response = "No Certificates: Last Check Time:" + formattedDate;
                                map.put(title, response);
                            } else {
                                response = "Certificate: UUID:" + receivedCertUuid + ", Thumbprint:" + certThumprint;
                                map.put(title, response);
                            }
                            break;
                        //endregion
                        //region DND Enable Status
                        case DND_ENABLE_STATUS:
                            response = "" + awSDKManager.isDNDEnabled();
                            break;
                        //endregion
                        //region DND Status Has Been Set
                        case DND_STATUS_HAS_BEEN_SET:
                            response = "" + awSDKManager.isDNDStatusSet();
                            break;
                        //endregion
                        //region Is SSO Session Valid
                        case SSO_SESSION_VALID:
                            response = "" + awSDKManager.isSSOSessionValid();
                            break;
                        //endregion
                        //region Is SSO Enabled
                        case SSO_ENABLED:
                            response = "" + awSDKManager.isSSOEnabled();
                            break;
                        //endregion
                        //region SSO Grace Period
                        case SSO_GRACE_PERIOD:
                            response = "" + awSDKManager.getSSOGracePeriod() + " minute(s)";
                            break;
                        //endregion
                        //region DND Status
                        case DND_STATUS:
                            int dndStatus = awSDKManager.getAnchorAppStatus().getDNDStatus();
                            switch (dndStatus){
                                case 0:
                                    response = "DND Status is not set";
                                    break;
                                case 1:
                                    response = "DND Status is set";
                                    break;
                                default:
                                    response = "Unknown";
                                    break;
                            }
                            break;
                        //endregion
                        //region Workspace Exit Mode
                        case WORKSPACE_EXIT_MODE:
                            int mode = awSDKManager.getAnchorAppStatus().getWorkspaceExitMode();
                            switch (mode){
                                case 0:
                                    response = "Lock & Exit";
                                    break;
                                case 1:
                                    response = "Exit with Notification";
                                    break;
                            }
                            break;
                        //endregion
                        //region Anchor Type
                        case ANCHOR_TYPE:
                            int anchorType = awSDKManager.getAnchorAppStatus().getAnchorType();
                            switch (anchorType){
                                case 0:
                                    response = "Agent";
                                    break;
                                case 1:
                                    response = "Workspace";
                                    break;
                                default:
                                    response = "Unknown";
                                    break;
                            }
                            break;
                        //endregion
                        //region Console Version
                        case CONSOLE_VERSION:
                            response = "" + awSDKManager.getConsoleVersion();
                            break;
                        //endregion
                        //region Is Integrated Auth Enabled
                        case INTEGRATED_AUTH_ENABLED:
                            int status = awSDKManager.getIntegratedAuthenticationProfile().getIntegratedAuthEnabled();
                            switch (status) {
                                case 0:
                                    response = "IntegratedAuthentication status is not set on AirWatch";
                                    break;
                                case 1:
                                    response = "IntegratedAuthentication status is set on AirWatch";
                                    break;
                                default:
                                    response = "IntegratedAuthentication status is unknown on AirWatch";
                                    break;
                            }
                            break;
                        //endregion
                        //region Domains Supported
                        case DOMAINS:
                            String domains = awSDKManager.getIntegratedAuthenticationProfile().getDomains();
                            if (domains == null || domains.length() == 0) {
                                response = "Domain is empty";
                            } else {
                                response = "" + domains;
                            }
                            break;
                        //endregion
                        //region LG Configure Mode
                        case LG_CONFIGURE_MODE:
                            response = "" + awSDKManager.getSharedDeviceStatus().getLGConfigureMode();
                            break;
                        //endregion
                        //region Shared Device Mode
                        case SHARED_DEVICE_MODE:
                            response = "" + awSDKManager.getSharedDeviceStatus().isSharedDeviceModeEnabled();
                            break;
                        //endregion
                        //region Shared Device Checkout Status
                        case SHARED_DEVICE_CHECKOUT_STATUS:
                            response = "" + awSDKManager.getSharedDeviceStatus().getcheckoutStatus();
                            break;
                        //endregion
                        //region Custom Settings
                        case CUSTOM_SETTINGS:
                            customSettingsString = awSDKManager.getCustomSettings();
                            response = "";
                            if (null != customSettingsString && customSettingsString.trim().length() != 0) {
                                response += "------- Custom Profile : -------\n";
                                response += customSettingsString;
                                response += "\n------- ### End ### -------\n\n";
                            } else {
                                response += "N/A";
                            }
                            break;
                        //endregion
                        //region Profile Groups
                        case PROFILE_GROUPS:
                            List<String> groups = awSDKManager.getAllProfileGroups();
                            if (groups != null){
                                builder = new StringBuilder();
                                for (String s : groups) {
                                    builder.append(s + "\n");
                                }
                                response = builder.toString();
                            }
                            else{
                                response = "Profile Groups are empty";
                            }
                            break;
                        //endregion
                        //region Logging Enable Status
                        case LOGGING_ENABLE_STATUS:
                            profile = awSDKManager.getLoggingSettings();
                            response = "" + profile.getLoggingStatus();
                            break;
                        //endregion
                        //region Logging Level
                        case LOGGING_LEVEL:
                            profile = awSDKManager.getLoggingSettings();
                            /* The Logging levels used by Android.
                            Android Values :: Error:6; Warning:5; Information:4; Debug:3.
                             */
                            switch(profile.getLoggingLevel()) {
                                case 6 : response = "Error"; break;
                                case 5 : response = "Warning"; break;
                                case 4 : response = "Information"; break;
                                case 3 : response = "Debug"; break;
                            }
                            break;
                        //endregion
                        //region Send Over Wifi Only
                        case SEND_OVER_WIFI:
                            profile = awSDKManager.getLoggingSettings();
                            response = "" + profile.getSendOverWifiOnly();
                            break;
                        //endregion
                        //region Last Checkin Time
                        case LAST_CHECKIN_TIME:
                            String strDateFormat = "EEE, dd MMM yyyy HH:mm:ss z";
                            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                            response = sdf.format(awSDKManager.getDeviceLastCheckinTime());
                            break;
                        //endregion
                        //region Allowed WiFi SSID
                        case ALLOWED_WIFI_SSID:
                            response = "" + awSDKManager.isAllowedWiFiSSID();
                            break;
                        //endregion
                        //region Get Kerberos Token
                        case GET_KERBEROS_TOKEN:
                            response = null;
                            String token = getResources().getString(R.string.str_kerberos_token) + " ";

                            if (response != null && response.length() != 0) {
                                response = token + response;
                            } else {
                                response = getResources().getString(R.string.str_kerberos_error);
                            }
                            break;
                        //endregion
                        //region Get Certificate Keys
                        case GET_CERTIFICATE_KEYS:
                            if (serviceError || awSDKManager == null) return;

                            response = awSDKManager.getMagCertificateKeys();
                            StringBuilder str = new StringBuilder();

                            if (response != null && response.length() > 0) {
                                String[] certs = response.split(",");
                                for (String cert : certs) {
                                    if (cert != null && cert.length() > 0) {
                                        str.append(cert).append("\n");
                                    }
                                }
                                response = getResources().getString(R.string.str_cert_keys_found) + "\n" + str.toString();
                            } else {
                                response = getResources().getString(R.string.str_cert_keys_not_available);
                            }
                            break;
                        //endregion
                        //region Application Config Setting
                        case APPLICATION_CONFIG_SETTING:
                            Map<String, String> configSetting = awSDKManager.getApplicationConfigSetting();
                            if (configSetting == null || configSetting.size() == 0) {
                                response = "Config Setting is empty";
                            } else {
                                builder = new StringBuilder();
                                for (Map.Entry<String, String> entry : configSetting.entrySet()) {
                                    builder.append(entry.getKey() + ", " + entry.getValue() + "\n");
                                }
                                response = builder.toString();
                            }
                            break;
                        //endregion
                        //region SSO Status
                        case SSO_STATUS:
                            response = "" + awSDKManager.getSSOStatus();
                            break;
                        //endregion
                    }
                    serviceError = false;
                    //endregion
                } catch (AirWatchSDKException e) {
                    response = "not available";
                    serviceError = true;
                } finally {
                    //region Update result
                    if (response != null){
                        map.put(title, response);
                        updateUI();
                    }
                    //endregion
                }
            }
        }).start();
    }

    public void handleClick(View v) {
        if (SDKManager.isServiceConnected()){
            TextView title = (TextView) v.getTag();
            final String code = title.getText().toString();
            getResponse(code);
        }
    }
}
