package com.sample.framework.ui.framework_ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.util.NetworkAccessValidator;
import com.airwatch.util.NetworkAccessControlUtil;
import com.airwatch.util.NetworkUtility;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;


public class NetworkUtils extends AppBaseActivity {

    TextView mNetworkAccessValid;

    private BroadcastReceiver mAccessValidityChangeReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"Network access validity changed",Toast.LENGTH_LONG).show();
            mNetworkAccessValid.setText(String.valueOf(NetworkAccessControlUtil.isNetworkAccessAllowed()));
        }
    };

    //TODO: refractor the list names
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_utils);

        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(mAccessValidityChangeReceiver,
                new IntentFilter(NetworkAccessControlUtil.NETWORK_ACCESS_VALIDITY_CHANGED));

        NetworkAccessValidator networkAccessControlUtil=new NetworkAccessValidator();

        TextView enabled=(TextView)findViewById(R.id.nac_enabled);
        enabled.setText(String.valueOf(networkAccessControlUtil.isNetworkAccessControlEnabled()));

        TextView cellular=(TextView)findViewById(R.id.cellular);
        int cellular_setting=networkAccessControlUtil.getCellularSetting();
        if(cellular_setting== NetworkAccessValidator.CellularSetting.NEVER)
            cellular.setText("Never");
        else if(cellular_setting== NetworkAccessValidator.CellularSetting.WHEN_NOT_ROAMING)
            cellular.setText("When not roaming");
        else
            cellular.setText("Always");

        TextView wifi=(TextView)findViewById(R.id.wifi);
        wifi.setText("Always");

        mNetworkAccessValid =(TextView)findViewById(R.id.isNetworkAccessValid);
        mNetworkAccessValid.setText(String.valueOf(NetworkAccessControlUtil.isNetworkAccessAllowed()));


        Button deviceConnection = (Button) findViewById(R.id.deviceConnection);
        deviceConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean connected = NetworkUtility.isDeviceConnectedToNetwork(getApplicationContext());
                CharSequence connection = connected ? "" : "not ";
                CharSequence text = "The Device is " + connection + "connected to network";
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Button deviceConnectionMode = (Button) findViewById(R.id.connectionMode);
        deviceConnectionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = NetworkUtility.getNetworkConnectionMode(getApplicationContext());
                CharSequence connection;
                switch(mode){
                    case 1:
                        connection = "Wifi";
                        break;
                    case 2:
                        connection = "Cellular";
                        break;
                    case 3:
                        connection = "roaming";
                        break;
                    default:
                        connection = "no internet";
                }

                CharSequence text = "The Device is in " + connection + " mode";
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Button ipAddressButton = (Button) findViewById(R.id.ipAddress);
        ipAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = NetworkUtility.getCurrentIpAddress(getApplicationContext());
                CharSequence text = "The IP address for this device is: " + ipAddress;
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_network_utils, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).
                unregisterReceiver(mAccessValidityChangeReceiver);
    }
}
