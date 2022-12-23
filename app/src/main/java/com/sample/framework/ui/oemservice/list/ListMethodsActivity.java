package com.sample.framework.ui.oemservice.list;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.airwatch.sdk.aidl.oem.IOEMService;
import com.airwatch.sdk.aidl.oem.OEMMethod;
import com.sample.airwatchsdk.R;
import com.sample.framework.ui.oemservice.Constants;
import com.sample.framework.ui.oemservice.invoke.InvokeMethodActivity;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class ListMethodsActivity extends AppCompatActivity implements ListMethodsActivityFragment.Callback {

    private static final int REQ_INVOKE_METHOD = 1;
    IOEMService service;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(Constants.LOG_TAG, "oem service connected " + name);
            ListMethodsActivity.this.service = IOEMService.Stub.asInterface(service);
            refreshMethodList();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOG_TAG, "oem service disconnected " + name);
            service = null;
        }
    };

    private void refreshMethodList() {
        getFragment().refresh();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_methods);
    }

    private ListMethodsActivityFragment getFragment() {
        return (ListMethodsActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setClassName("com.airwatch.androidagent", "com.airwatch.agent.enterprise.service.OEMService");

        if (bindService(intent, mServiceConnection, BIND_AUTO_CREATE)) {
            Log.d(Constants.LOG_TAG, "bound to oem service");
        } else {
            Log.d(Constants.LOG_TAG, "not bound to oem service");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
    }

    @Override
    public void onInvokeMethod(OEMMethod method) {
        startActivityForResult(new Intent(this,
                InvokeMethodActivity.class).putExtra(InvokeMethodActivity.EXTRA_METHOD, method), REQ_INVOKE_METHOD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_INVOKE_METHOD:
                onInvokeMethodResult(resultCode, data);
                break;
        }
    }

    private void onInvokeMethodResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            final OEMMethod method = data.getParcelableExtra(InvokeMethodActivity.EXTRA_RESULT_METHOD);
            final String[] args = data.getStringArrayExtra(InvokeMethodActivity.EXTRA_RESULT_ARGS);
            try {

                final String response = service.invoke(method, args);
                new AlertDialog.Builder(this).setTitle(R.string.oem_method_response_alert_title)
                        .setMessage(response).setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } catch (Exception e) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(out));
                final String response = out.toString();

                new AlertDialog.Builder(this).setTitle(R.string.oem_method_response_alert_title)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(response).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

                Log.e(Constants.LOG_TAG, "exception calling " + method.name, e);
            }
        }
    }
}
