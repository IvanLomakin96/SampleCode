package com.sample.framework.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airwatch.gateway.AWBaseGatewayStatusListener;
import com.airwatch.gateway.GatewayException;
import com.airwatch.gateway.GatewayManager;
import com.airwatch.gateway.clients.AWWebView;
import com.airwatch.gateway.clients.AWWebViewClient;
import com.airwatch.gateway.ui.GatewayBaseActivity;
import com.airwatch.sdk.configuration.SDKConfigurationKeys;
import com.airwatch.sdk.context.SDKContextManager;
import com.sample.airwatchsdk.R;


/**
 * this version shows the use of the default listener and the base sdk activity in tunneling
 */
public class ProxyActivityAWWebView2 extends GatewayBaseActivity {

    public static final String TAG = "AWDemo";

    private Button mGoBtn;
    private AWWebView webView;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        mGoBtn = (Button) findViewById(R.id.go_btn);
        mGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(input.getText()))
                    loadContent();
            }
        });
        webView = (AWWebView) findViewById(R.id.aaa);
        input = (EditText) findViewById(R.id.input_password);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new AWWebViewClient(this));
    }

    private void loadContent(){
        String url = input.getText().toString();
        if (TextUtils.isEmpty(url)){
            Toast.makeText(ProxyActivityAWWebView2.this, "Url please" , Toast.LENGTH_LONG).show();
            return;
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))){
            url = "http://" + url;
        }
        Log.i(TAG, "loading " + url);
        webView.loadUrl(url);
    }

}
