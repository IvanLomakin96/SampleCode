package com.sample.main;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.airwatch.login.SDKBaseActivity;
import com.sample.airwatchsdk.R;
import com.sample.settings.CustomSettingsActivity;

/**
 * Created by dipanshug on 10/20/2015.
 */
public class AppBaseActivity extends SDKBaseActivity {

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
        if (item.getItemId() == R.id.setting){
            startActivity(new Intent(this, CustomSettingsActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.home){
            startActivity(new Intent(this, MainLandingActivity.class));
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
    public boolean showWatermark() {
        return true;
    }
}
