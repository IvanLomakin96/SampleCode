package com.sample.airwatchsdk.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.context.awsdkcontext.SDKContextHelper;
import com.airwatch.sdk.context.awsdkcontext.chain.ManagedAppChain;
import com.airwatch.sdk.context.awsdkcontext.handlers.GatewayConfigurationHandler;
import com.airwatch.sdk.context.awsdkcontext.handlers.SDKBaseHandler;
import com.sample.airwatchsdk.R;

import java.util.ArrayList;
import java.util.List;

public class SDKManagedChainActivity extends AppCompatActivity implements
        SDKContextHelper.AWContextCallBack,
        SDKBaseHandler.HandlerProgressCallback {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdkmanaged_chain);

        textView = (TextView) findViewById(R.id.textView2);
        Button button = (Button) findViewById(R.id.btn_start);

        final ManagedAppChain managedAppChain =
                new ManagedAppChain(this,(ManagedAppChain.SDKContextDataCollector) getApplicationContext(),this);

        addGatewaySetup(managedAppChain);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managedAppChain.process();
            }
        });
    }

    private void addGatewaySetup(ManagedAppChain managedAppChain) {
        SDKBaseHandler gatewayHandler = new GatewayConfigurationHandler(managedAppChain.getLocalCallBack(),
                (SDKContextHelper.AppDetails) getApplicationContext());

        List handlers = new ArrayList<SDKBaseHandler>();
        handlers.add(gatewayHandler);
        managedAppChain.addHandlerChain(handlers);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        textView.append("success");
        textView.append(System.getProperty("line.separator"));

    }

    @Override
    public void onFailed(AirWatchSDKException e) {
        textView.append("failed "+e);
        textView.append(System.getProperty("line.separator"));

    }

    @Override
    public void onHandlerProgress(int total, int current, String handlerName) {
        String s[] = handlerName.split("\\.");
        textView.append(String.format(" %d/%d handler %s ",total,current, s[s.length-1]));
        textView.append(System.getProperty("line.separator"));

    }
}
