/*
 * Copyright (c) $2018 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.gateway.clients.AWOkHttpClient;
import com.airwatch.util.Logger;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sample.airwatchsdk.R;
import com.sample.framework.OkHttpStack;
import com.sample.framework.retrofit.RetrofitAPIInterface;
import com.sample.framework.retrofit.RetrofitClient;
import com.sample.framework.retrofit.User;
import com.sample.main.AppBaseActivity;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;

/*
 * From 19.6 SDK, its required to use AW Wrapped classes for making Http requests.
 * This is needed for authenticating the requests to the local proxy from Android 10 onwards.
 *
 * This activity provides sample code using AWOkHttpClient for REST APIs like Volley Retrofit and Picasso.
 * */
public class AWClientExampleActivity extends AppBaseActivity implements View.OnClickListener {

    private String TAG = AWClientExampleActivity.class.getName();
    private String status = "No Updates";

    private Context context;
    private Handler mHandler;
    private TextView statusMessage;
    private ImageView imageView;
    private EditText urlEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aw_restapis_example_layout);
        context = this;
        statusMessage = findViewById(R.id.status);
        urlEditor = findViewById(R.id.urleditor);
        imageView = findViewById(R.id.imageView);

        findViewById(R.id.volleybtn).setOnClickListener(this);
        findViewById(R.id.retrofitbtn).setOnClickListener(this);
        findViewById(R.id.picassobtn).setOnClickListener(this);

        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.volleybtn:
                if (TextUtils.isEmpty(urlEditor.getText())) {
                    Toast.makeText(context, "Please Enter URL", Toast.LENGTH_LONG).show();
                    break;
                }
                String loadUrl = urlEditor.getText().toString();
                if ((!loadUrl.startsWith("http://")) && (!loadUrl.startsWith("https://"))) {
                    loadUrl = "http://" + loadUrl;
                    urlEditor.setText(loadUrl);
                }
                loadUrlUsingVolleyWithAWOkHttpClient(loadUrl);
                break;
            case R.id.retrofitbtn:
                getResponseUsingRetrofitWithAWOkHttpClient();
                break;
            case R.id.picassobtn:
                getImageLoadingFromPicasso();
                break;
            default:
                Logger.d(TAG, "No implementation defined.");
        }
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
                status = "Status code: " + statusCode;
                Logger.d(TAG, status);
                mHandler.post(() -> statusMessage.setText(status));
                return super.parseNetworkResponse(response);
            }
        };
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    /**
     * This method refers to Retrofit usage using {@link AWOkHttpClient}. Refer {@link RetrofitClient}
     */
    private void getResponseUsingRetrofitWithAWOkHttpClient() {
        RetrofitAPIInterface apiInterface = RetrofitClient.getClient("https://reqres.in/").create(RetrofitAPIInterface.class);
        /**
         GET List Resources
         **/
        Call<User> user = apiInterface.getUserInfo();
        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                Log.d(TAG, "User onResponse " + response.code());
                status = "Retrofit Response code: " + response.code();
                mHandler.post(() -> statusMessage.setText(status));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "User onResponse onFailure" + t.getMessage());
                status = "Failure while Using Retrofit With AWOkHttpClient ";
                mHandler.post(() -> statusMessage.setText(status));
            }
        });
    }

    /**
     * Integrates OkHttpClient as network stack for Picasso.
     */
    private void getImageLoadingFromPicasso() {
        OkHttpClient client = new OkHttpClient();
        //can use
        OkHttpClient awOkhttpClient = AWOkHttpClient.copyWithDefaults(client);

        //Uses OkHttp3Downloader which consumes OkHttpClient
        Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(awOkhttpClient))
                .build();

        //Load the required url into imageview. Added callback to get the status.
        picasso.load("https://httpbin.org/image/jpeg")
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        status = "Picasso callback: Success";
                        mHandler.post(() -> statusMessage.setText(status));
                    }

                    @Override
                    public void onError(Exception e) {
                        status = "Picasso callback: Error received " + e.getMessage();
                        mHandler.post(() -> statusMessage.setText(status));
                    }
                });
    }

}
