package com.sample.airwatchsdk.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.sample.airwatchsdk.AirWatchSDKSampleApp;
import com.sample.airwatchsdk.AsyncSDKManager;
import com.sample.airwatchsdk.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class SDKAppConfigActivity extends Activity implements AsyncSDKManager.Invoker<Bundle> {
    private TextView textView;
    private BroadcastReceiver receiver;
    private AsyncSDKManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_app_config);
        super.onCreate(savedInstanceState);

        this.textView = (TextView) findViewById(R.id.text_app_config);

        this.receiver = new AppConfigReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AirWatchSDKSampleApp.APP_CONFIG_CHANGE_BROADCAST));

        this.manager = new AsyncSDKManager();
        manager.invoke(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public Bundle invoke(SDKManager sdk) throws AirWatchSDKException {
        return sdk.getApplicationConfiguration();
    }

    @Override
    public void onResult(Bundle result, AirWatchSDKException exception) {
        if(exception != null){
            exception.printStackTrace();
        }else{
            setBundleText(result);
        }
    }

    class AppConfigReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(AirWatchSDKSampleApp.APP_CONFIG_CHANGE_BROADCAST.equals(action)){
                onAppConfigChange(context, intent);
            }
        }
    }

    private void onAppConfigChange(Context context, Intent intent) {
        Toast.makeText(context, "Application Configuration received", Toast.LENGTH_LONG).show();
        final Bundle bundle = intent.getBundleExtra(AirWatchSDKSampleApp.APP_CONFIG_CHANGE_BROADCAST_EXTRA_CONFIG);
        setBundleText(bundle);
    }

    private void setBundleText(Bundle bundle) {
        final List<?> keys = bundle != null ? new ArrayList<>(bundle.keySet()) : Collections.emptyList();
        textView.setText(keys.toString());
    }
}
