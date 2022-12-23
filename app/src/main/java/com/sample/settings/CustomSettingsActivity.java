/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.settings;

import android.content.Intent;

import androidx.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.airwatch.login.ui.settings.SettingsActivity;
import com.airwatch.login.ui.settings.model.CustomHeader;
import com.airwatch.login.ui.settings.views.SdkHeaderCategoryView;
import com.airwatch.ui.activity.OpenSourceLicenseActivity;
import com.sample.airwatchsdk.R;
import com.sample.main.MainLandingActivity;

import java.util.List;

public class CustomSettingsActivity extends SettingsActivity {

    private void startOSLActivity(){
        Intent showLicenseIntent = new Intent(this, OpenSourceLicenseActivity.class);
        String termsAndConditionsUrl = "http://www.air-watch.com/company/privacy-policy";
        showLicenseIntent.putExtra(OpenSourceLicenseActivity.KEY_ACTIVITY_TITLE_NAME, com.airwatch.core.R.string.awsdk_eula_tittle);
        showLicenseIntent.putExtra(OpenSourceLicenseActivity.KEY_ACTIVITY_IS_SIMPLIFIED_ENROLLMENT, true);
        showLicenseIntent.putExtra(OpenSourceLicenseActivity.KEY_OPEN_SOURCE_FILE_URL, termsAndConditionsUrl);
        startActivity(showLicenseIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout){
            logout();
            return true;
        }
        if (item.getItemId() == R.id.menu_change_passcode){
            changePasscode();
            return true;
        }
        if (item.getItemId() == R.id.lock_session){
            lockAndvalidateSession();
            return true;
        }
        if (item.getItemId() == R.id.home){
            startActivity(new Intent(this, MainLandingActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_base_activity, menu);
        return true;
    }

    @Override
    public void addPreferencesHeaders(List<SdkHeaderCategoryView> categoryViewList) {
        SdkHeaderCategoryView oslCategory = new SdkHeaderCategoryView(this);
        oslCategory.setTopShadowVisibility(true);
        oslCategory.setBottomShadowVisibility(true);
        oslCategory.setTitle("Legal");
        CustomHeader tncHeader = new CustomHeader();
        tncHeader.setTitle("Terms and conditions");
        tncHeader.setSummary("A sample list item");
        tncHeader.iconRes = com.airwatch.core.R.drawable.awsdk_username;
        tncHeader.setHeaderClickListener(new CustomHeader.HeaderClickListener() {
            @Override
            public void onHeaderClick(@NonNull CustomHeader header) {
                startOSLActivity();
            }
        });
        oslCategory.addCustomHeaderToCategory(tncHeader);
        categoryViewList.add(oslCategory);

        SdkHeaderCategoryView legalCategory = new SdkHeaderCategoryView(this);
        legalCategory.setTopShadowVisibility(true);
        legalCategory.setBottomShadowVisibility(true);
        legalCategory.setTitle("Read me first");
        CustomHeader openSourceLicense = new CustomHeader();
        openSourceLicense.setTitle("Open source license");
        openSourceLicense.setSummary("A sample list item");
        openSourceLicense.iconRes = com.airwatch.core.R.drawable.awsdk_username;
        openSourceLicense.setHeaderClickListener(new CustomHeader.HeaderClickListener() {
            @Override
            public void onHeaderClick(@NonNull CustomHeader header) {
                startOSLActivity();
            }
        });
        legalCategory.addCustomHeaderToCategory(openSourceLicense);
        CustomHeader privacyPolicy = new CustomHeader();
        privacyPolicy.setTitle("Privacy Policy");
        privacyPolicy.setSummary("A sample list item");
        privacyPolicy.iconRes = com.airwatch.core.R.drawable.awsdk_username;
        privacyPolicy.setHeaderClickListener(new CustomHeader.HeaderClickListener() {
            @Override
            public void onHeaderClick(@NonNull CustomHeader header) {
                startOSLActivity();
            }
        });
        legalCategory.addCustomHeaderToCategory(privacyPolicy);
        categoryViewList.add(legalCategory);
    }
}
