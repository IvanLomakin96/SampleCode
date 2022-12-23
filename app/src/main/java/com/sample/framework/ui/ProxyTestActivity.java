/**
 * Copyright (c) 2014 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in the
 * United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airwatch.gateway.GatewayException;
import com.airwatch.gateway.GatewayManager;
import com.airwatch.gateway.GatewayStatusCodes;
import com.airwatch.gateway.IGatewayStatusListener;
import com.airwatch.gateway.clients.AWOkHttpClient;
import com.airwatch.gateway.clients.AWUrlConnection;
import com.airwatch.sdk.configuration.SDKConfigurationKeys;
import com.airwatch.sdk.context.SDKContextManager;
import com.airwatch.util.Logger;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sample.airwatchsdk.R;
import com.sample.framework.OkHttpStack;
import com.sample.framework.TestWebView;
import com.sample.framework.interfaces.LogsUpdateListener;
import com.sample.framework.logs.LoggerService;
import com.sample.framework.logs.LogsContainer;
import com.sample.framework.retrofit.RetrofitAPIInterface;
import com.sample.framework.retrofit.RetrofitClient;
import com.sample.framework.retrofit.User;
import com.sample.main.AppBaseActivity;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class ProxyTestActivity extends AppBaseActivity implements OnClickListener {

    private static final String TAG = "ProxyTestActivity";
    public static final String ACTION_ERROR = "errorAction";
    private static final int UPDATE_LOGS = 100;
    private static final int PROXY_STARTED = 101;
    private static final int UPDATE_STATUS = 102;
    private static final int UPDATE_URL = 103;

    private EditText mEditUrl = null;
    private TextView mTextLogs, mTextStatus;

    ScrollView mLogsScrollView = null;

    RelativeLayout mWebViewHolder;
    private TestWebView mAwWebView;

    private Context context;
    private GatewayManager mGatewayManager = null;
    private String mUrl, mStatusCode;
    private ProgressDialog mProgressDialog;
    private ImageView imageView;

    private String[] mTestComponentArray = {"AWWebView", "AWUrlConnection", "AWOkHttpClient",
            "Volley using AWOkHttpClient", "Retrofit using AWOkHttpClient", "Picasso using AWOkHttpClient"};

    private ArrayAdapter<String> mTestComponentAdapter;
    private Spinner mSpTestComponent;
    private boolean isAwWebView = true;
    private boolean isAwURLConnection = false;
    private boolean isAwOkHttpClient = false;
    private boolean isAwOkHttpClientwithVolley = false;
    private boolean isAwOkHttpClientwithRetrofit = false;
    private boolean isAwOkHttpClientwithPicasso = false;

    static LogsHandler mHandler = null;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            isAwWebView = (position == 0);
            isAwURLConnection = (position == 1);
            isAwOkHttpClient = (position == 2);
            isAwOkHttpClientwithVolley = (position == 3);
            isAwOkHttpClientwithPicasso = (position == 5);
            if(isAwOkHttpClientwithRetrofit = (position == 4)){
                mEditUrl.setEnabled(false);
                mEditUrl.setText("https://reqres.in/");
            }else {
                mEditUrl.setEnabled(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    static LogsUpdateListener mLogsListener = new LogsUpdateListener() {
        @Override
        public void update() {
            if (mHandler != null)
                mHandler.sendEmptyMessage(UPDATE_LOGS);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ERROR);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(intentFilter));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.proxy_activity);

        context = this;
        LogsContainer.getInstance().setLogsUpdateListener(mLogsListener);
        initViews();
        LoggerService.startService(getApplicationContext());

        if (isTunnellingEnabled()) {
            setupProxy();
        }
    }

    private boolean isTunnellingEnabled() {
        return SDKContextManager.getSDKContext().getSDKConfiguration().
                getBooleanValue(SDKConfigurationKeys.TYPE_APP_TUNNELING, SDKConfigurationKeys.ENABLE_APP_TUNNEL);
    }

    /**
     * this is where we start the proxy manager that handle the proxy setup.
     */
    private void setupProxy() {
        try {
            GatewayManager gatewayManager = GatewayManager.getInstance(getApplicationContext());
            if (!gatewayManager.isRunning()) {
                launchRingDialog();
            } else {
                mTextStatus.setText("Proxy Started. Enter your url");
            }
            gatewayManager.registerGatewayStatusListener(new ProxyListener());
            gatewayManager.autoConfigureProxy();
        } catch (GatewayException e) {
            Logger.e("Getting exception while creating proxy " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new LogsHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(UPDATE_LOGS);
        mHandler.removeMessages(PROXY_STARTED);
        mHandler = null;
    }

    private void initViews() {
        mAwWebView = (TestWebView) findViewById(R.id.act_main_test_result_wv);
        mWebViewHolder = (RelativeLayout) findViewById(R.id.lyt_webview_holder);

        if (mWebViewHolder.findViewById(R.id.act_main_test_result_wv) == null) {
            mWebViewHolder.addView(mAwWebView);
        }

        findViewById(R.id.act_main_start_test_btn).setOnClickListener(this);
        findViewById(R.id.clear_logs).setOnClickListener(this);
        imageView = findViewById(R.id.imageView);
        mEditUrl = (EditText) findViewById(R.id.url_edit_text);
        mEditUrl.setText("");
        mSpTestComponent = (Spinner) findViewById(R.id.act_main_test_component_sp);
        setupSpinners();
        mTextLogs = (TextView) findViewById(R.id.logs_text_view);
        mTextStatus = (TextView) findViewById(R.id.text_status_code);
        mLogsScrollView = (ScrollView) findViewById(R.id.logs_scroll_view);

    }

    private void setupSpinners() {

        mTestComponentAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, mTestComponentArray);
        mTestComponentAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
        mSpTestComponent.setAdapter(mTestComponentAdapter);
        mSpTestComponent.setOnItemSelectedListener(onItemSelectedListener);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.act_main_start_test_btn:
                if (TextUtils.isEmpty(mEditUrl.getText())) {
                    Toast.makeText(this, "Please Enter URL", Toast.LENGTH_LONG).show();
                    break;
                }

                String loadUrl = mEditUrl.getText().toString();
                if ((!loadUrl.startsWith("http://")) && (!loadUrl.startsWith("https://"))) {
                    loadUrl = "http://" + loadUrl;
                    mEditUrl.setText(loadUrl);
                }

                LogsContainer.getInstance().setAllowLogs(true);// Allow logs just before loading the url.
                if (isAwWebView) {
                    imageView.setVisibility(View.GONE);
                    mAwWebView.setVisibility(View.VISIBLE);
                    loadUrlInAwWebView(loadUrl);
                } else if (isAwURLConnection) {
                    loadUrlInAwUrlConnection(loadUrl);
                } else if (isAwOkHttpClient) {
                    loadUrlInAwOkHttpClient(loadUrl);
                } else if (isAwOkHttpClientwithVolley) {
                    loadUrlUsingVolleyWithAWOkHttpClient(loadUrl);
                } else if (isAwOkHttpClientwithRetrofit) {
                    getResponseUsingRetrofitWithAWOkHttpClient(loadUrl);
                } else if (isAwOkHttpClientwithPicasso) {
                    getImageLoadingFromPicasso(loadUrl);
                } else {
                    mTextStatus.setText("Invalid selection");
                    break;
                }

                mTextStatus.setText(getResources().getString(R.string.status));
                break;
            case R.id.clear_logs:
                LogsContainer.getInstance().setAllowLogs(false);
                LogsContainer.getInstance().clearLogs();
                mTextLogs.setText("No Logs to display.");
                mTextStatus.setText(getResources().getString(R.string.status));
                break;
            default:
                break;
        }
    }


    private void loadUrlInAwWebView(String loadUrl) {
        mAwWebView.loadUrl(loadUrl);
    }

    private void loadUrlInAwUrlConnection(final String loadUrl) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL url = new URL(loadUrl);
                    URLConnection connection = AWUrlConnection.openConnection(url);
                    if (connection instanceof HttpURLConnection) {
                        HttpURLConnection httpConnection = (HttpURLConnection) connection;
                        httpConnection.setInstanceFollowRedirects(true);
                        mStatusCode = "Status code AWHttpURLConnection : "
                                + httpConnection.getResponseCode();
                        logResponse(httpConnection.getInputStream());
                        mHandler.sendEmptyMessage(UPDATE_STATUS);
                        Logger.i("Status code AWHttpURLConnection :" + Integer.toString(httpConnection.getResponseCode()));
                    } else {
                        HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                        httpsConnection.setInstanceFollowRedirects(true);
                        mStatusCode = "Status code AWHttpsURLConnection : "
                                + httpsConnection.getResponseCode();
                        logResponse(httpsConnection.getInputStream());
                        mHandler.sendEmptyMessage(UPDATE_STATUS);
                        Logger.i("Status code AWHttpsURLConnection :" + Integer.toString(httpsConnection.getResponseCode()));
                    }

                } catch (Exception e) {
                    Logger.e("Unable to send request URLConnection");
                }
            }

            private void logResponse(InputStream inputStream) throws IOException {
                StringBuilder sb = new StringBuilder();

                byte[] buf = new byte[128];

                while (inputStream.read(buf) != -1) {
                    sb.append(new String(buf));
                }

                Log.e("AirWatch", "AWHTTPURLCONNECTION resp: " + sb.toString());
            }
        }).start();
    }

    private void loadUrlInAwOkHttpClient(final String loadUrl) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(loadUrl)
                            .build();

                    final Response response = AWOkHttpClient.newCall(client, request).execute();
                    mStatusCode = "Status code AWOkHttpClient : " + response.code();
                    mHandler.sendEmptyMessage(UPDATE_STATUS);
                    Logger.i("Status code AWOkHttpClient :" + Integer.toString(response.code()));
                    Logger.i("AWOkHttpClient response: " + response.body().string());
                    response.body().close();
                } catch (Exception e) {
                    Logger.e("Unable to send request AWOkHttpClient");
                }
            }
        }).start();
    }

    /**
     * This method sets {@link OkHttpClient} using AWWrapped class - {@link AWOkHttpClient} as transport layer for Volley.
     * For this it uses abstraction layer, implementation of {@BaseHttpStack}.
     * Refer: {@link OkHttpStack}
     */
    private void loadUrlUsingVolleyWithAWOkHttpClient(final String loadUrl) {
        OkHttpClient client = new OkHttpClient();
        OkHttpStack stack = new OkHttpStack(client);
        RequestQueue requestQueue = Volley.newRequestQueue(context, stack);
        requestQueue.start();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, loadUrl,
                response -> Logger.d(TAG, "Response is: " + response),
                error -> Logger.d(TAG, "Error received: " + error.getMessage())) {
            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                int statusCode = response.statusCode;
                mStatusCode = "Status code: " + statusCode;

                mHandler.sendEmptyMessage(UPDATE_STATUS);
                Logger.i(TAG,"AWOkHttpClient with Volley:" + mStatusCode);
                return super.parseNetworkResponse(response);
            }
        };
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    /**
     * This method refers to Retrofit usage using {@link AWOkHttpClient}. Refer {@link RetrofitClient}
     */
    private void getResponseUsingRetrofitWithAWOkHttpClient(String loadUrl) {
        RetrofitAPIInterface apiInterface = RetrofitClient.getClient(loadUrl).create(RetrofitAPIInterface.class);
        /**
         GET List Resources
         **/
        Call<User> user = apiInterface.getUserInfo();
        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                mStatusCode = "Retrofit Response code: " + response.code();
                mHandler.sendEmptyMessage(UPDATE_STATUS);
                Logger.d(TAG, mStatusCode);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mStatusCode = "Retrofit onFailure";
                mHandler.sendEmptyMessage(UPDATE_STATUS);
                Logger.d(TAG, mStatusCode+": "+t.getMessage());
            }
        });
    }

    /**
     * Integrates OkHttpClient as network stack for Picasso.
     * tested:"https://httpbin.org/image/jpeg"
     */

    private void getImageLoadingFromPicasso(String loadUrl) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        //can use
        OkHttpClient awOkhttpClient = AWOkHttpClient.copyWithDefaults(client);

        //Uses OkHttp3Downloader which consumes OkHttpClient
        Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(awOkhttpClient))
                .build();

        imageView.setVisibility(View.VISIBLE);
        mAwWebView.setVisibility(View.GONE);

        //Load the required url into imageview. Added callback to get the status.
        picasso.load(loadUrl)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        mStatusCode = "Picasso callback: Success";
                        mHandler.sendEmptyMessage(UPDATE_STATUS);
                        Logger.d(TAG, mStatusCode);
                    }

                    @Override
                    public void onError(Exception e) {
                        mStatusCode = "Picasso Error received. "+e.getMessage()
                                +"\nCheck if entered URL returns an Image.\nEg: https://httpbin.org/image/jpeg";
                        mHandler.sendEmptyMessage(UPDATE_STATUS);
                        Logger.d(TAG, mStatusCode+" "+e.getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        try {
            if (mGatewayManager != null) {
                //  mGatewayManager.unregisterGatewayStatusListener(mProxyListener);
                mGatewayManager.stop();
            }
        } catch (GatewayException e) {
            e.printStackTrace();
        }
        LogsContainer.getInstance().clearLogs();
        LogsContainer.getInstance().unregisterListener();
        LoggerService.stopService(getApplicationContext());
        super.onDestroy();
    }


    public class ProxyListener implements IGatewayStatusListener {

        @Override
        public void onError(int magErrorCode) {
            Logger.d("Airwatch::reportMAGError() - errorCode -" + magErrorCode);
        }

        @Override
        public void onStatusChange(int gatewayStatus) {

            switch (gatewayStatus) {
                case GatewayStatusCodes.STATE_CONFIGURING:
                case GatewayStatusCodes.STATE_ESTABLISHING_CONNECTION:
                    Logger.d("Airwatch::onProxyStateChange() state-" + gatewayStatus);
                    break;
                case GatewayStatusCodes.STATE_STARTED:
                    onProxyStartComplete();
                    break;
                case GatewayStatusCodes.STATE_STOPPED:
                    onProxyStopComplete();
                default:
                    break;
            }
        }

        public void onProxyStartComplete() {
            Log.i("Airwatch", "onProxyStartComplete");
            Log.i("Airwatch", "isSuccess");
            mProgressDialog.dismiss();
            mHandler.sendEmptyMessage(PROXY_STARTED);
        }

        public void onProxyStopComplete() {
            Logger.d("Airwatch::onProxyStopComplete()");
        }
    }

    public void launchRingDialog() {
        mProgressDialog = ProgressDialog.show(this, "Please wait ...",
                "Starting Proxy ...", true);
        mProgressDialog.setCancelable(true);
    }

    static class LogsHandler extends Handler {
        WeakReference<ProxyTestActivity> weakRefActivity;

        public LogsHandler(ProxyTestActivity instance) {
            weakRefActivity = new WeakReference<ProxyTestActivity>(instance);
        }

        public void handleMessage(android.os.Message msg) {
            final ProxyTestActivity activity = weakRefActivity.get();
            if (activity != null) {
                if (msg.what == UPDATE_LOGS) {
                    activity.mTextLogs.setText(LogsContainer.getInstance().getLogs());
                    activity.mLogsScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            activity.mLogsScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                } else if (msg.what == UPDATE_STATUS) {
                    activity.mTextStatus.setText(activity.mStatusCode);
                } else if (msg.what == UPDATE_URL) {
                    activity.mEditUrl.setText(activity.mUrl);
                } else if (msg.what == PROXY_STARTED)
                    activity.mTextStatus.setText("Proxy Started. Enter your url");
            }
        }
    }

}