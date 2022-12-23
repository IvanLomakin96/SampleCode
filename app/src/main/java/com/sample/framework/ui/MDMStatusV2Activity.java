package com.sample.framework.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.context.SDKContextManager;
import com.airwatch.sdk.context.awsdkcontext.SDKContextHelper;
import com.airwatch.sdk.context.awsdkcontext.SDKDataModel;
import com.airwatch.sdk.context.awsdkcontext.SDKDataModelImpl;
import com.airwatch.storage.SDKSecurePreferencesKeys;
import com.airwatch.util.ArrayUtils;
import com.airwatch.util.Logger;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

import org.json.JSONObject;

import java.util.Arrays;

public class MDMStatusV2Activity extends AppBaseActivity {

    EditText editTextOG;
    EditText editTextRequestCode;
    EditText editTextHost;
    EditText editTextHmac;
    EditText editTextDeviceId;
    TextView txtViewReuslt;
    private SDKDataModel dataModel;
    private final String TAG = "MDMStatusV2Activity";
    String DEVICE_UID = "deviceUID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdmstatus_v2);
        editTextOG = (EditText)findViewById(R.id.edit_og);
        editTextRequestCode = (EditText)findViewById(R.id.edit_request_code);
        editTextHost = (EditText)findViewById(R.id.edit_host);
        editTextDeviceId = (EditText)findViewById(R.id.edit_device_udid);
        editTextHmac = (EditText)findViewById(R.id.edit_hmac);
        txtViewReuslt = (TextView)findViewById(R.id.txt_result);
        dataModel = new SDKDataModelImpl(SDKContextManager.getSDKContext().getContext());
        editTextHmac.setText(Arrays.toString(dataModel.getApplicationHMACToken()));
        SharedPreferences preferences = SDKContextManager.getSDKContext().getSDKSecurePreferences();

        preferences.getString(SDKSecurePreferencesKeys.GROUP_ID, "");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        editTextHost.setText(preferences.getString(SDKSecurePreferencesKeys.HOST, ""));
        editTextDeviceId.setText(settings.getString(DEVICE_UID, ""));
        editTextOG.setText(preferences.getString(SDKSecurePreferencesKeys.GROUP_ID, ""));
        editTextRequestCode.setText("0");
        editTextHmac.setText(Arrays.toString(dataModel.getApplicationHMACToken()));

    }





    public void requestStatus(View view) {


        SDKContextHelper subject = new SDKContextHelper();
        subject.requestConsoleStatusWithGroupInformation(Integer.valueOf(editTextRequestCode.getText().toString()), this,  editTextHost.getText().toString(), editTextDeviceId.getText().toString(), new SDKContextHelper.AWContextCallBack() {
            @Override
            public void onSuccess(int requestCode, Object result) {
                Logger.d(TAG, "success with request code " + requestCode);
                JSONObject jsonObject = (JSONObject)result;
                txtViewReuslt.setText(jsonObject.toString());

            }

            @Override
            public void onFailed(AirWatchSDKException e) {
                Logger.e(TAG, "Failed to fetch group information", e);
            }
        }, editTextHmac.getText().toString().getBytes());
    }
}
