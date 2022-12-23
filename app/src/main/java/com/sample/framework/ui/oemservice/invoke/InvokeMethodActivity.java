package com.sample.framework.ui.oemservice.invoke;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.airwatch.sdk.aidl.oem.OEMMethod;
import com.sample.airwatchsdk.R;

import java.util.List;


public class InvokeMethodActivity extends AppCompatActivity implements InvokeMethodActivityFragment.Callback {

    public static final String EXTRA_METHOD = "method";
    public static final String EXTRA_RESULT_METHOD = "method";
    public static final String EXTRA_RESULT_ARGS = "args";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoke_method);
    }

    @Override
    public OEMMethod getMethod() {
        return getIntent().getParcelableExtra(EXTRA_METHOD);
    }

    @Override
    public void onInvoke(List<String> args) {
        final Intent data = new Intent();
        data.putExtra(EXTRA_RESULT_METHOD, getMethod());
        data.putExtra(EXTRA_RESULT_ARGS, args.toArray(new String[args.size()]));
        setResult(RESULT_OK, data);
        finish();
    }
}
